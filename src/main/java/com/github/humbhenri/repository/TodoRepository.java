package com.github.humbhenri.repository;

import com.github.humbhenri.domain.Todo;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Todo entity.
 */
@SuppressWarnings("unused")
public interface TodoRepository extends JpaRepository<Todo,Long> {

    @Query("select todo from Todo todo where todo.user.login = ?#{principal.username}")
    List<Todo> findByUserIsCurrentUser();

}
