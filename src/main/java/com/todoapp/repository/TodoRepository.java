package com.todoapp.repository;

import com.todoapp.domain.Todo;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Todo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TodoRepository extends JpaRepository<Todo, Long>, JpaSpecificationExecutor<Todo> {
    @Query("select todo from Todo todo where todo.user.login = ?#{principal.preferredUsername}")
    List<Todo> findByUserIsCurrentUser();
}
