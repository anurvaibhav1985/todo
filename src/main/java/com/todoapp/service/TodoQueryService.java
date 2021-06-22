package com.todoapp.service;

import com.todoapp.domain.*; // for static metamodels
import com.todoapp.domain.Todo;
import com.todoapp.repository.TodoRepository;
import com.todoapp.service.criteria.TodoCriteria;
import com.todoapp.service.dto.TodoDTO;
import com.todoapp.service.mapper.TodoMapper;
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
 * Service for executing complex queries for {@link Todo} entities in the database.
 * The main input is a {@link TodoCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link TodoDTO} or a {@link Page} of {@link TodoDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class TodoQueryService extends QueryService<Todo> {

    private final Logger log = LoggerFactory.getLogger(TodoQueryService.class);

    private final TodoRepository todoRepository;

    private final TodoMapper todoMapper;

    public TodoQueryService(TodoRepository todoRepository, TodoMapper todoMapper) {
        this.todoRepository = todoRepository;
        this.todoMapper = todoMapper;
    }

    /**
     * Return a {@link List} of {@link TodoDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TodoDTO> findByCriteria(TodoCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<Todo> specification = createSpecification(criteria);
        return todoMapper.toDto(todoRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link TodoDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TodoDTO> findByCriteria(TodoCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<Todo> specification = createSpecification(criteria);
        return todoRepository.findAll(specification, page).map(todoMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(TodoCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<Todo> specification = createSpecification(criteria);
        return todoRepository.count(specification);
    }

    /**
     * Function to convert {@link TodoCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<Todo> createSpecification(TodoCriteria criteria) {
        Specification<Todo> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), Todo_.id));
            }
            if (criteria.getName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getName(), Todo_.name));
            }
            if (criteria.getDesc() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDesc(), Todo_.desc));
            }
            if (criteria.getActive() != null) {
                specification = specification.and(buildStringSpecification(criteria.getActive(), Todo_.active));
            }
            if (criteria.getVersion() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getVersion(), Todo_.version));
            }
            if (criteria.getTaskId() != null) {
                specification =
                    specification.and(
                        buildSpecification(criteria.getTaskId(), root -> root.join(Todo_.tasks, JoinType.LEFT).get(Task_.id))
                    );
            }
            if (criteria.getTaskInstanceId() != null) {
                specification =
                    specification.and(
                        buildSpecification(
                            criteria.getTaskInstanceId(),
                            root -> root.join(Todo_.taskInstances, JoinType.LEFT).get(TaskInstance_.id)
                        )
                    );
            }
            if (criteria.getUserId() != null) {
                specification =
                    specification.and(buildSpecification(criteria.getUserId(), root -> root.join(Todo_.user, JoinType.LEFT).get(User_.id)));
            }
        }
        return specification;
    }
}
