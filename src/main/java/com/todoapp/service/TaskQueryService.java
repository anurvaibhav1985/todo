package com.todoapp.service;

import com.todoapp.domain.*; // for static metamodels
import com.todoapp.domain.Task;
import com.todoapp.repository.TaskRepository;
import com.todoapp.service.criteria.TaskCriteria;
import com.todoapp.service.dto.TaskDTO;
import com.todoapp.service.mapper.TaskMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link Task} entities in the database.
 * The main input is a {@link TaskCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TaskDTO} or a {@link Page} of {@link TaskDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TaskQueryService extends QueryService<Task> {

    private final Logger log = LoggerFactory.getLogger(TaskQueryService.class);

    private final TaskRepository taskRepository;

    private final TaskMapper taskMapper;

    public TaskQueryService(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    /**
     * Return a {@link List} of {@link TaskDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TaskDTO> findByCriteria(TaskCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Task> specification = createSpecification(criteria);
        return taskMapper.toDto(taskRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link TaskDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TaskDTO> findByCriteria(TaskCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Task> specification = createSpecification(criteria);
        return taskRepository.findAll(specification, page).map(taskMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TaskCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Task> specification = createSpecification(criteria);
        return taskRepository.count(specification);
    }

    /**
     * Function to convert {@link TaskCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Task> createSpecification(TaskCriteria criteria) {
        Specification<Task> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Task_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Task_.name));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildStringSpecification(criteria.getType(), Task_.type));
            }
            if (criteria.getDesc() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDesc(), Task_.desc));
            }
            if (criteria.getPlannedStartDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPlannedStartDate(), Task_.plannedStartDate));
            }
            if (criteria.getPlannedEndDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPlannedEndDate(), Task_.plannedEndDate));
            }
            if (criteria.getActive() != null) {
                specification = specification.and(buildStringSpecification(criteria.getActive(), Task_.active));
            }
            if (criteria.getVersion() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getVersion(), Task_.version));
            }
            if (criteria.getTaskInstanceId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getTaskInstanceId(),
                            root -> root.join(Task_.taskInstances, JoinType.LEFT).get(TaskInstance_.id)
                        )
                    );
            }
            if (criteria.getTodoId() != null) {
                specification =
                    specification.and(buildSpecification(criteria.getTodoId(), root -> root.join(Task_.todo, JoinType.LEFT).get(Todo_.id)));
            }
        }
        return specification;
    }
}
