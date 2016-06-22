package com.github.humbhenri.web.rest;

import com.github.humbhenri.AngtaskApp;
import com.github.humbhenri.domain.Todo;
import com.github.humbhenri.repository.TodoRepository;
import com.github.humbhenri.service.TodoService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the TodoResource REST controller.
 *
 * @see TodoResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AngtaskApp.class)
@WebAppConfiguration
@IntegrationTest
public class TodoResourceIntTest {

    private static final String DEFAULT_BODY = "A";
    private static final String UPDATED_BODY = "B";

    private static final LocalDate DEFAULT_CREATED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATED = LocalDate.now(ZoneId.systemDefault());

    private static final Boolean DEFAULT_DONE = false;
    private static final Boolean UPDATED_DONE = true;

    @Inject
    private TodoRepository todoRepository;

    @Inject
    private TodoService todoService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restTodoMockMvc;

    private Todo todo;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TodoResource todoResource = new TodoResource();
        ReflectionTestUtils.setField(todoResource, "todoService", todoService);
        this.restTodoMockMvc = MockMvcBuilders.standaloneSetup(todoResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        todo = new Todo();
        todo.setBody(DEFAULT_BODY);
        todo.setCreated(DEFAULT_CREATED);
        todo.setDone(DEFAULT_DONE);
    }

    @Test
    @Transactional
    public void createTodo() throws Exception {
        int databaseSizeBeforeCreate = todoRepository.findAll().size();

        // Create the Todo

        restTodoMockMvc.perform(post("/api/todos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(todo)))
                .andExpect(status().isCreated());

        // Validate the Todo in the database
        List<Todo> todos = todoRepository.findAll();
        assertThat(todos).hasSize(databaseSizeBeforeCreate + 1);
        Todo testTodo = todos.get(todos.size() - 1);
        assertThat(testTodo.getBody()).isEqualTo(DEFAULT_BODY);
        assertThat(testTodo.getCreated()).isEqualTo(DEFAULT_CREATED);
        assertThat(testTodo.isDone()).isEqualTo(DEFAULT_DONE);
    }

    @Test
    @Transactional
    public void checkBodyIsRequired() throws Exception {
        int databaseSizeBeforeTest = todoRepository.findAll().size();
        // set the field null
        todo.setBody(null);

        // Create the Todo, which fails.

        restTodoMockMvc.perform(post("/api/todos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(todo)))
                .andExpect(status().isBadRequest());

        List<Todo> todos = todoRepository.findAll();
        assertThat(todos).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTodos() throws Exception {
        // Initialize the database
        todoRepository.saveAndFlush(todo);

        // Get all the todos
        restTodoMockMvc.perform(get("/api/todos?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(todo.getId().intValue())))
                .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY.toString())))
                .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED.toString())))
                .andExpect(jsonPath("$.[*].done").value(hasItem(DEFAULT_DONE.booleanValue())));
    }

    @Test
    @Transactional
    public void getTodo() throws Exception {
        // Initialize the database
        todoRepository.saveAndFlush(todo);

        // Get the todo
        restTodoMockMvc.perform(get("/api/todos/{id}", todo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(todo.getId().intValue()))
            .andExpect(jsonPath("$.body").value(DEFAULT_BODY.toString()))
            .andExpect(jsonPath("$.created").value(DEFAULT_CREATED.toString()))
            .andExpect(jsonPath("$.done").value(DEFAULT_DONE.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingTodo() throws Exception {
        // Get the todo
        restTodoMockMvc.perform(get("/api/todos/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTodo() throws Exception {
        // Initialize the database
        todoService.save(todo);

        int databaseSizeBeforeUpdate = todoRepository.findAll().size();

        // Update the todo
        Todo updatedTodo = new Todo();
        updatedTodo.setId(todo.getId());
        updatedTodo.setBody(UPDATED_BODY);
        updatedTodo.setCreated(UPDATED_CREATED);
        updatedTodo.setDone(UPDATED_DONE);

        restTodoMockMvc.perform(put("/api/todos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedTodo)))
                .andExpect(status().isOk());

        // Validate the Todo in the database
        List<Todo> todos = todoRepository.findAll();
        assertThat(todos).hasSize(databaseSizeBeforeUpdate);
        Todo testTodo = todos.get(todos.size() - 1);
        assertThat(testTodo.getBody()).isEqualTo(UPDATED_BODY);
        assertThat(testTodo.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testTodo.isDone()).isEqualTo(UPDATED_DONE);
    }

    @Test
    @Transactional
    public void deleteTodo() throws Exception {
        // Initialize the database
        todoService.save(todo);

        int databaseSizeBeforeDelete = todoRepository.findAll().size();

        // Get the todo
        restTodoMockMvc.perform(delete("/api/todos/{id}", todo.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Todo> todos = todoRepository.findAll();
        assertThat(todos).hasSize(databaseSizeBeforeDelete - 1);
    }
}
