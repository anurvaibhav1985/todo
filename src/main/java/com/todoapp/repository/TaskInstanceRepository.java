package com.todoapp.repository;

import com.todoapp.domain.TaskInstance;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the TaskInstance entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TaskInstanceRepository extends JpaRepository<TaskInstance, Long>, JpaSpecificationExecutor<TaskInstance> {
    @Query("select taskInstance from TaskInstance taskInstance where taskInstance.user.login = ?#{principal.preferredUsername}")
    List<TaskInstance> findByUserIsCurrentUser();
}
