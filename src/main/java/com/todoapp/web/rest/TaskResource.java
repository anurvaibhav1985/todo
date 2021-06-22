package com.todoapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.todoapp.domain.TaskInstance;
import com.todoapp.domain.Todo;
import com.todoapp.domain.enumeration.TaskStatus;
import com.todoapp.repository.TaskInstanceRepository;
import com.todoapp.repository.TaskRepository;
import com.todoapp.repository.TodoRepository;
import com.todoapp.service.TaskInstanceQueryService;
import com.todoapp.service.TaskQueryService;
import com.todoapp.service.TaskService;
import com.todoapp.service.UserService;
import com.todoapp.service.criteria.TaskCriteria;
import com.todoapp.service.criteria.TaskInstanceCriteria;
import com.todoapp.service.criteria.TaskInstanceCriteria.TaskStatusFilter;
import com.todoapp.service.dto.BulkAssignDTO;
import com.todoapp.service.dto.TaskDTO;
import com.todoapp.service.dto.TaskInstanceDTO;
import com.todoapp.service.dto.UserDTO;
import com.todoapp.service.mapper.TaskInstanceMapper;
import com.todoapp.service.mapper.TaskMapper;
import com.todoapp.web.rest.errors.BadRequestAlertException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.todoapp.domain.Task}.
 */
@RestController
@RequestMapping("/api")
public class TaskResource {

    private final Logger log = LoggerFactory.getLogger(TaskResource.class);

    private static final String ENTITY_NAME = "task";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TaskService taskService;

    private final TaskRepository taskRepository;

    private final TaskQueryService taskQueryService;

    @Autowired
    private TaskInstanceQueryService taskInstanceQueryService;

    @Autowired
    private UserService userService;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskInstanceMapper taskInstanceMapper;

    @Autowired
    private TaskInstanceRepository taskInstanceRepository;

    public TaskResource(TaskService taskService, TaskRepository taskRepository, TaskQueryService taskQueryService) {
        this.taskService = taskService;
        this.taskRepository = taskRepository;
        this.taskQueryService = taskQueryService;
    }

    /**
     * {@code POST  /tasks} : Create a new task.
     *
     * @param taskDTO the taskDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new taskDTO, or with status {@code 400 (Bad Request)} if the task has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/tasks")
    public ResponseEntity<TaskDTO> createTask(@RequestBody TaskDTO taskDTO) throws URISyntaxException {
        log.debug("REST request to save Task : {}", taskDTO);
        if (taskDTO.getId() != null) {
            throw new BadRequestAlertException("A new task cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TaskDTO result = taskService.save(taskDTO);
        return ResponseEntity
            .created(new URI("/api/tasks/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @PutMapping("/tasks/assign")
    @Transactional
    public ResponseEntity<Boolean> assignTasks(@RequestBody BulkAssignDTO bulkAssignDTO) throws URISyntaxException {
        // Get all users with these emails - ideally from cache
        Page<UserDTO> usersPage = userService.getAllPublicUsers(Pageable.unpaged());
        Map<String, String> userMap = usersPage
            .get()
            .filter(k -> bulkAssignDTO.getEmailAddresses().contains(k.getLogin()))
            .collect(Collectors.toMap(UserDTO::getLogin, UserDTO::getId));

        // Validation
        TaskInstanceCriteria criteria = new TaskInstanceCriteria();
        StringFilter userFilter = new StringFilter();
        userFilter.setIn(new ArrayList<String>(userMap.values()));
        TaskStatusFilter status = new TaskStatusFilter();
        status.setIn(Arrays.asList(TaskStatus.INPROGRESS, TaskStatus.NOTSTARTED));
        criteria.setUserId(userFilter);
        criteria.setStatus(status);
        List<TaskInstanceDTO> result = taskInstanceQueryService.findByCriteria(criteria);
        if (!CollectionUtils.isEmpty(result)) {
            // Raise an error saying we have users who have this todo and with tasks in prgress , please close or delet thme before assigning
            throw new BadRequestAlertException(
                "Given Tasks have already been assigned to one or more user/users , please close/delete existing tasks for the given user list before assigning again ",
                ENTITY_NAME,
                "tasksAlreadyAssigned"
            );
        }

        // Get all tasks with given array
        LongFilter f = new LongFilter();
        f.setIn(bulkAssignDTO.getTasks());
        TaskCriteria taskCriteria = new TaskCriteria();
        taskCriteria.setId(f);
        List<TaskDTO> tasks = taskQueryService.findByCriteria(taskCriteria);

        // Get the TODO
        Todo todo = todoRepository.findById(tasks.get(0).getTodo().getId()).get();

        // Should first create task instances for all user specified
        List<TaskInstance> taskInstances = new ArrayList<TaskInstance>();
        List<String> users = bulkAssignDTO.getEmailAddresses();
        for (String user : users) {
            for (TaskDTO task : tasks) {
                TaskInstance n = new TaskInstance();
                n.setActive(task.getActive());
                n.plannedStartDate(task.getPlannedStartDate());
                n.setPlannedEndDate(task.getPlannedEndDate());
                n.setDesc(task.getDesc());
                n.setName(task.getName());
                n.setStatus(TaskStatus.NOTSTARTED);
                n.setTodo(todo);
                com.todoapp.domain.User u = new com.todoapp.domain.User();
                u.setId(userMap.get(user));
                n.setUser(u);
                n.setTask(taskMapper.toEntity(task));
                taskInstances.add(n);
            }

            // Adding instances for one user
            taskInstanceRepository.saveAll(taskInstances);
        }

        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, bulkAssignDTO.getTodoId().toString()))
            .body(Boolean.TRUE);
    }

    /**
     * {@code PUT  /tasks/:id} : Updates an existing task.
     *
     * @param id the id of the taskDTO to save.
     * @param taskDTO the taskDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated taskDTO,
     * or with status {@code 400 (Bad Request)} if the taskDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the taskDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable(value = "id", required = false) final Long id, @RequestBody TaskDTO taskDTO)
        throws URISyntaxException {
        log.debug("REST request to update Task : {}, {}", id, taskDTO);
        if (taskDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taskDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!taskRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TaskDTO result = taskService.save(taskDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, taskDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /tasks/:id} : Partial updates given fields of an existing task, field will ignore if it is null
     *
     * @param id the id of the taskDTO to save.
     * @param taskDTO the taskDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated taskDTO,
     * or with status {@code 400 (Bad Request)} if the taskDTO is not valid,
     * or with status {@code 404 (Not Found)} if the taskDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the taskDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/tasks/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<TaskDTO> partialUpdateTask(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TaskDTO taskDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Task partially : {}, {}", id, taskDTO);
        if (taskDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taskDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!taskRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TaskDTO> result = taskService.partialUpdate(taskDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, taskDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /tasks} : get all the tasks.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tasks in body.
     */
    @GetMapping("/tasks")
    public ResponseEntity<List<TaskDTO>> getAllTasks(TaskCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Tasks by criteria: {}", criteria);
        Page<TaskDTO> page = taskQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /tasks/count} : count all the tasks.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/tasks/count")
    public ResponseEntity<Long> countTasks(TaskCriteria criteria) {
        log.debug("REST request to count Tasks by criteria: {}", criteria);
        return ResponseEntity.ok().body(taskQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /tasks/:id} : get the "id" task.
     *
     * @param id the id of the taskDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the taskDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskDTO> getTask(@PathVariable Long id) {
        log.debug("REST request to get Task : {}", id);
        Optional<TaskDTO> taskDTO = taskService.findOne(id);
        return ResponseUtil.wrapOrNotFound(taskDTO);
    }

    /**
     * {@code DELETE  /tasks/:id} : delete the "id" task.
     *
     * @param id the id of the taskDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/tasks/{id}")
    @Transactional
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        log.debug("REST request to delete Task : {}", id);

        // Find task instances of users and mark them as withdrawn
        TaskInstanceCriteria criteria = new TaskInstanceCriteria();
        LongFilter f = new LongFilter();
        f.setEquals(id);
        criteria.setTaskId(f);
        List<TaskInstanceDTO> dtoList = taskInstanceQueryService.findByCriteria(criteria);
        for (TaskInstanceDTO dto : dtoList) {
            dto.setActive("N");
            dto.setStatus(TaskStatus.WITHDRAWN);
        }
        taskInstanceRepository.saveAll(taskInstanceMapper.toEntity(dtoList));

        // Now soft delete the task
        //taskService.delete(id);
        TaskDTO dto = taskService.findOne(id).get();
        dto.setActive("N");
        taskService.partialUpdate(dto);

        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
