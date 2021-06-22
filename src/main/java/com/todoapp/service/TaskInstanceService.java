package com.todoapp.service;

import com.todoapp.domain.TaskInstance;
import com.todoapp.repository.TaskInstanceRepository;
import com.todoapp.service.dto.TaskInstanceDTO;
import com.todoapp.service.mapper.TaskInstanceMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link TaskInstance}.
 */
@Service
@Transactional
public class TaskInstanceService {

    private final Logger log = LoggerFactory.getLogger(TaskInstanceService.class);

    private final TaskInstanceRepository taskInstanceRepository;

    private final TaskInstanceMapper taskInstanceMapper;

    public TaskInstanceService(TaskInstanceRepository taskInstanceRepository, TaskInstanceMapper taskInstanceMapper) {
        this.taskInstanceRepository = taskInstanceRepository;
        this.taskInstanceMapper = taskInstanceMapper;
    }

    /**
     * Save a taskInstance.
     *
     * @param taskInstanceDTO the entity to save.
     * @return the persisted entity.
     */
    public TaskInstanceDTO save(TaskInstanceDTO taskInstanceDTO) {
        log.debug("Request to save TaskInstance : {}", taskInstanceDTO);
        TaskInstance taskInstance = taskInstanceMapper.toEntity(taskInstanceDTO);
        taskInstance = taskInstanceRepository.save(taskInstance);
        return taskInstanceMapper.toDto(taskInstance);
    }

    /**
     * Partially update a taskInstance.
     *
     * @param taskInstanceDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TaskInstanceDTO> partialUpdate(TaskInstanceDTO taskInstanceDTO) {
        log.debug("Request to partially update TaskInstance : {}", taskInstanceDTO);

        return taskInstanceRepository
            .findById(taskInstanceDTO.getId())
            .map(
                existingTaskInstance -> {
                    taskInstanceMapper.partialUpdate(existingTaskInstance, taskInstanceDTO);

                    return existingTaskInstance;
                }
            )
            .map(taskInstanceRepository::save)
            .map(taskInstanceMapper::toDto);
    }

    /**
     * Get all the taskInstances.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<TaskInstanceDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TaskInstances");
        return taskInstanceRepository.findAll(pageable).map(taskInstanceMapper::toDto);
    }

    /**
     * Get one taskInstance by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TaskInstanceDTO> findOne(Long id) {
        log.debug("Request to get TaskInstance : {}", id);
        return taskInstanceRepository.findById(id).map(taskInstanceMapper::toDto);
    }

    /**
     * Delete the taskInstance by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete TaskInstance : {}", id);
        taskInstanceRepository.deleteById(id);
    }
}
