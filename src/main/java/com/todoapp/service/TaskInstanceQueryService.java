package com.todoapp.service;

import com.todoapp.domain.*; // for static metamodels
import com.todoapp.domain.TaskInstance;
import com.todoapp.repository.TaskInstanceRepository;
import com.todoapp.service.criteria.TaskInstanceCriteria;
import com.todoapp.service.dto.TaskInstanceDTO;
import com.todoapp.service.mapper.TaskInstanceMapper;
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
 * Service for executing complex queries for {@link TaskInstance} entities in the database.
 * The main input is a {@link TaskInstanceCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TaskInstanceDTO} or a {@link Page} of {@link TaskInstanceDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TaskInstanceQueryService extends QueryService<TaskInstance> {

    private final Logger log = LoggerFactory.getLogger(TaskInstanceQueryService.class);

    private final TaskInstanceRepository taskInstanceRepository;

    private final TaskInstanceMapper taskInstanceMapper;

    public TaskInstanceQueryService(TaskInstanceRepository taskInstanceRepository, TaskInstanceMapper taskInstanceMapper) {
        this.taskInstanceRepository = taskInstanceRepository;
        this.taskInstanceMapper = taskInstanceMapper;
    }

    /**
     * Return a {@link List} of {@link TaskInstanceDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TaskInstanceDTO> findByCriteria(TaskInstanceCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<TaskInstance> specification = createSpecification(criteria);
        return taskInstanceMapper.toDto(taskInstanceRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link TaskInstanceDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TaskInstanceDTO> findByCriteria(TaskInstanceCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<TaskInstance> specification = createSpecification(criteria);
        return taskInstanceRepository.findAll(specification, page).map(taskInstanceMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TaskInstanceCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<TaskInstance> specification = createSpecification(criteria);
        return taskInstanceRepository.count(specification);
    }

    /**
     * Function to convert {@link TaskInstanceCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<TaskInstance> createSpecification(TaskInstanceCriteria criteria) {
        Specification<TaskInstance> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), TaskInstance_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), TaskInstance_.name));
            }
            if (criteria.getType() != null) {
                specification = specification.and(buildStringSpecification(criteria.getType(), TaskInstance_.type));
            }
            if (criteria.getDesc() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDesc(), TaskInstance_.desc));
            }
            if (criteria.getStatus() != null) {
                specification = specification.and(buildSpecification(criteria.getStatus(), TaskInstance_.status));
            }
            if (criteria.getPlannedStartDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPlannedStartDate(), TaskInstance_.plannedStartDate));
            }
            if (criteria.getPlannedEndDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPlannedEndDate(), TaskInstance_.plannedEndDate));
            }
            if (criteria.getActualStartDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getActualStartDate(), TaskInstance_.actualStartDate));
            }
            if (criteria.getActualEndDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getActualEndDate(), TaskInstance_.actualEndDate));
            }
            if (criteria.getTimeSpent() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getTimeSpent(), TaskInstance_.timeSpent));
            }
            if (criteria.getActive() != null) {
                specification = specification.and(buildStringSpecification(criteria.getActive(), TaskInstance_.active));
            }
            if (criteria.getVersion() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getVersion(), TaskInstance_.version));
            }
            if (criteria.getTaskId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getTaskId(), root -> root.join(TaskInstance_.task, JoinType.LEFT).get(Task_.id))
                    );
            }
            if (criteria.getTodoId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getTodoId(), root -> root.join(TaskInstance_.todo, JoinType.LEFT).get(Todo_.id))
                    );
            }
            if (criteria.getUserId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getUserId(), root -> root.join(TaskInstance_.user, JoinType.LEFT).get(User_.id))
                    );
            }
        }
        return specification;
    }
}
