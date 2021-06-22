package com.todoapp.web.rest;

import com.todoapp.domain.enumeration.TaskStatus;
import com.todoapp.repository.TaskInstanceRepository;
import com.todoapp.service.TaskInstanceQueryService;
import com.todoapp.service.TaskInstanceService;
import com.todoapp.service.criteria.TaskInstanceCriteria;
import com.todoapp.service.dto.TaskInstanceDTO;
import com.todoapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.todoapp.domain.TaskInstance}.
 */
@RestController
@RequestMapping("/api")
public class TaskInstanceResource {

    private final Logger log = LoggerFactory.getLogger(TaskInstanceResource.class);

    private static final String ENTITY_NAME = "taskInstance";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TaskInstanceService taskInstanceService;

    private final TaskInstanceRepository taskInstanceRepository;

    private final TaskInstanceQueryService taskInstanceQueryService;

    public TaskInstanceResource(
        TaskInstanceService taskInstanceService,
        TaskInstanceRepository taskInstanceRepository,
        TaskInstanceQueryService taskInstanceQueryService
    ) {
        this.taskInstanceService = taskInstanceService;
        this.taskInstanceRepository = taskInstanceRepository;
        this.taskInstanceQueryService = taskInstanceQueryService;
    }

    /**
     * {@code POST  /task-instances} : Create a new taskInstance.
     *
     * @param taskInstanceDTO the taskInstanceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new taskInstanceDTO, or with status {@code 400 (Bad Request)} if the taskInstance has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/task-instances")
    public ResponseEntity<TaskInstanceDTO> createTaskInstance(@RequestBody TaskInstanceDTO taskInstanceDTO) throws URISyntaxException {
        log.debug("REST request to save TaskInstance : {}", taskInstanceDTO);
        if (taskInstanceDTO.getId() != null) {
            throw new BadRequestAlertException("A new taskInstance cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TaskInstanceDTO result = taskInstanceService.save(taskInstanceDTO);
        return ResponseEntity
            .created(new URI("/api/task-instances/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /task-instances/:id} : Updates an existing taskInstance.
     *
     * @param id the id of the taskInstanceDTO to save.
     * @param taskInstanceDTO the taskInstanceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated taskInstanceDTO,
     * or with status {@code 400 (Bad Request)} if the taskInstanceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the taskInstanceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/task-instances/{id}")
    public ResponseEntity<TaskInstanceDTO> updateTaskInstance(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TaskInstanceDTO taskInstanceDTO
    ) throws URISyntaxException {
        log.debug("REST request to update TaskInstance : {}, {}", id, taskInstanceDTO);
        if (taskInstanceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taskInstanceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!taskInstanceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TaskInstanceDTO result = taskInstanceService.save(taskInstanceDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, taskInstanceDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /task-instances/:id} : Partial updates given fields of an existing taskInstance, field will ignore if it is null
     *
     * @param id the id of the taskInstanceDTO to save.
     * @param taskInstanceDTO the taskInstanceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated taskInstanceDTO,
     * or with status {@code 400 (Bad Request)} if the taskInstanceDTO is not valid,
     * or with status {@code 404 (Not Found)} if the taskInstanceDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the taskInstanceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/task-instances/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<TaskInstanceDTO> partialUpdateTaskInstance(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TaskInstanceDTO taskInstanceDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update TaskInstance partially : {}, {}", id, taskInstanceDTO);
        if (taskInstanceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taskInstanceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!taskInstanceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TaskInstanceDTO> result = taskInstanceService.partialUpdate(taskInstanceDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, taskInstanceDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /task-instances} : get all the taskInstances.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of taskInstances in body.
     */
    @GetMapping("/task-instances")
    public ResponseEntity<List<TaskInstanceDTO>> getAllTaskInstances(TaskInstanceCriteria criteria, Pageable pageable) {
        log.debug("REST request to get TaskInstances by criteria: {}", criteria);
        Page<TaskInstanceDTO> page = taskInstanceQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /task-instances/count} : count all the taskInstances.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/task-instances/count")
    public ResponseEntity<Long> countTaskInstances(TaskInstanceCriteria criteria) {
        log.debug("REST request to count TaskInstances by criteria: {}", criteria);
        return ResponseEntity.ok().body(taskInstanceQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /task-instances/:id} : get the "id" taskInstance.
     *
     * @param id the id of the taskInstanceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the taskInstanceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/task-instances/{id}")
    public ResponseEntity<TaskInstanceDTO> getTaskInstance(@PathVariable Long id) {
        log.debug("REST request to get TaskInstance : {}", id);
        Optional<TaskInstanceDTO> taskInstanceDTO = taskInstanceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(taskInstanceDTO);
    }

    /**
     * {@code DELETE  /task-instances/:id} : Soft deletes the "id" taskInstance.
     *
     * @param id the id of the taskInstanceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/task-instances/{id}")
    @Transactional
    public ResponseEntity<Void> deleteTaskInstance(@PathVariable Long id) {
        log.debug("REST request to delete TaskInstance : {}", id);
        TaskInstanceDTO dto = taskInstanceService.findOne(id).get();
        dto.setActive("N");
        dto.setStatus(TaskStatus.WITHDRAWN);
        taskInstanceService.partialUpdate(dto);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
