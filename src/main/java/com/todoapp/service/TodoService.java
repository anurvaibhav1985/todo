package com.todoapp.service;

import com.todoapp.domain.Todo;
import com.todoapp.repository.TodoRepository;
import com.todoapp.service.dto.TodoDTO;
import com.todoapp.service.mapper.TodoMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Todo}.
 */
@Service
@Transactional
public class TodoService {

    private final Logger log = LoggerFactory.getLogger(TodoService.class);

    private final TodoRepository todoRepository;

    private final TodoMapper todoMapper;

    public TodoService(TodoRepository todoRepository, TodoMapper todoMapper) {
        this.todoRepository = todoRepository;
        this.todoMapper = todoMapper;
    }

    /**
     * Save a todo.
     *
     * @param todoDTO the entity to save.
     * @return the persisted entity.
     */
    public TodoDTO save(TodoDTO todoDTO) {
        log.debug("Request to save Todo : {}", todoDTO);
        Todo todo = todoMapper.toEntity(todoDTO);
        todo = todoRepository.save(todo);
        return todoMapper.toDto(todo);
    }

    /**
     * Partially update a todo.
     *
     * @param todoDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TodoDTO> partialUpdate(TodoDTO todoDTO) {
        log.debug("Request to partially update Todo : {}", todoDTO);

        return todoRepository
            .findById(todoDTO.getId())
            .map(
                existingTodo -> {
                    todoMapper.partialUpdate(existingTodo, todoDTO);

                    return existingTodo;
                }
            )
            .map(todoRepository::save)
            .map(todoMapper::toDto);
    }

    /**
     * Get all the todos.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TodoDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Todos");
        return todoRepository.findAll(pageable).map(todoMapper::toDto);
    }

    /**
     * Get one todo by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TodoDTO> findOne(Long id) {
        log.debug("Request to get Todo : {}", id);
        return todoRepository.findById(id).map(todoMapper::toDto);
    }

    /**
     * Delete the todo by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Todo : {}", id);
        // Soft delete to be done only , find by id and set active is false , this should have business validation
        todoRepository.deleteById(id);
    }
}
