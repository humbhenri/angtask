package com.github.humbhenri.service;

import com.github.humbhenri.domain.Todo;
import com.github.humbhenri.repository.TodoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * Service Implementation for managing Todo.
 */
@Service
@Transactional
public class TodoService {

    private final Logger log = LoggerFactory.getLogger(TodoService.class);
    
    @Inject
    private TodoRepository todoRepository;
    
    /**
     * Save a todo.
     * 
     * @param todo the entity to save
     * @return the persisted entity
     */
    public Todo save(Todo todo) {
        log.debug("Request to save Todo : {}", todo);
        Todo result = todoRepository.save(todo);
        return result;
    }

    /**
     *  Get all the todos.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Todo> findAll(Pageable pageable) {
        log.debug("Request to get all Todos");
        Page<Todo> result = todoRepository.findAll(pageable); 
        return result;
    }

    /**
     *  Get one todo by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Todo findOne(Long id) {
        log.debug("Request to get Todo : {}", id);
        Todo todo = todoRepository.findOne(id);
        return todo;
    }

    /**
     *  Delete the  todo by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Todo : {}", id);
        todoRepository.delete(id);
    }
}
