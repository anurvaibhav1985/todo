package com.todoapp.web.rest;

import com.todoapp.domain.TaskInstance;
import com.todoapp.domain.Todo;
import com.todoapp.domain.User;
import com.todoapp.domain.enumeration.TaskStatus;
import com.todoapp.repository.TaskInstanceRepository;
import com.todoapp.repository.TaskRepository;
import com.todoapp.repository.TodoRepository;
import com.todoapp.repository.UserRepository;
import com.todoapp.security.SecurityUtils;
import com.todoapp.service.TaskInstanceQueryService;
import com.todoapp.service.TaskQueryService;
import com.todoapp.service.TodoQueryService;
import com.todoapp.service.TodoService;
import com.todoapp.service.UserService;
import com.todoapp.service.criteria.TaskCriteria;
import com.todoapp.service.criteria.TaskInstanceCriteria;
import com.todoapp.service.criteria.TaskInstanceCriteria.TaskStatusFilter;
import com.todoapp.service.criteria.TodoCriteria;
import com.todoapp.service.dto.BulkAssignDTO;
import com.todoapp.service.dto.TaskDTO;
import com.todoapp.service.dto.TaskInstanceDTO;
import com.todoapp.service.dto.TodoDTO;
import com.todoapp.service.dto.UserDTO;
import com.todoapp.service.mapper.TaskInstanceMapper;
import com.todoapp.service.mapper.TaskMapper;
import com.todoapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.compress.utils.Sets;
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
import org.springframework.util.MultiValueMap;
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
 * REST controller for managing {@link com.todoapp.domain.Todo}.
 */
@RestController
@RequestMapping("/api")
public class TodoResource {

    private final Logger log = LoggerFactory.getLogger(TodoResource.class);

    private static final String ENTITY_NAME = "todo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TodoService todoService;

    private final TodoRepository todoRepository;

    private final TodoQueryService todoQueryService;

    @Autowired
    private TaskInstanceQueryService taskInstanceQueryService;

    @Autowired
    private TaskQueryService taskQueryService;

    @Autowired
    private UserService userService;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskInstanceMapper taskInstanceMapper;

    @Autowired
    private TaskInstanceRepository taskInstanceRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    public TodoResource(TodoService todoService, TodoRepository todoRepository, TodoQueryService todoQueryService) {
        this.todoService = todoService;
        this.todoRepository = todoRepository;
        this.todoQueryService = todoQueryService;
    }

    /**
     * {@code POST  /todos} : Create a new todo.
     *
     * @param todoDTO the todoDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new todoDTO, or with status {@code 400 (Bad Request)} if the todo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/todos")
    public ResponseEntity<TodoDTO> createTodo(@RequestBody TodoDTO todoDTO) throws URISyntaxException {
        log.debug("REST request to save Todo : {}", todoDTO);
        if (todoDTO.getId() != null) {
            throw new BadRequestAlertException("A new todo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TodoDTO result = todoService.save(todoDTO);
        return ResponseEntity
            .created(new URI("/api/todos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @PutMapping("/todos/assign")
    @Transactional
    public ResponseEntity<Boolean> assignTodo(@RequestBody BulkAssignDTO bulkAssignDTO) throws URISyntaxException {
        log.debug("REST request to bulk assign Todo : {}", bulkAssignDTO);

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
        criteria.setUserId(userFilter);
        TaskStatusFilter status = new TaskStatusFilter();
        status.setIn(Arrays.asList(TaskStatus.INPROGRESS, TaskStatus.NOTSTARTED));
        criteria.setStatus(status);
        List<TaskInstanceDTO> result = taskInstanceQueryService.findByCriteria(criteria);
        if (!CollectionUtils.isEmpty(result)) {
            // Raise an error saying we have users who have this todo and with tasks in prgress , please close or delet thme before assigning
            throw new BadRequestAlertException(
                "This TODO has a user/users given with tasks in progress , please close or delete them before assigning",
                ENTITY_NAME,
                "todoAlreadyAssigned"
            );
        }

        // Get all tasks with this todo id
        LongFilter f = new LongFilter();
        f.setEquals(bulkAssignDTO.getTodoId());
        TaskCriteria taskCriteria = new TaskCriteria();
        taskCriteria.setTodoId(f);
        List<TaskDTO> tasks = taskQueryService.findByCriteria(taskCriteria);

        // Get the TODO
        Todo todo = todoRepository.findById(bulkAssignDTO.getTodoId()).get();

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
     * {@code PUT  /todos/:id} : Updates an existing todo.
     *
     * @param id the id of the todoDTO to save.
     * @param todoDTO the todoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated todoDTO,
     * or with status {@code 400 (Bad Request)} if the todoDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the todoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/todos/{id}")
    public ResponseEntity<TodoDTO> updateTodo(@PathVariable(value = "id", required = false) final Long id, @RequestBody TodoDTO todoDTO)
        throws URISyntaxException {
        log.debug("REST request to update Todo : {}, {}", id, todoDTO);
        if (todoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, todoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!todoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TodoDTO result = todoService.save(todoDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, todoDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /todos/:id} : Partial updates given fields of an existing todo, field will ignore if it is null
     *
     * @param id the id of the todoDTO to save.
     * @param todoDTO the todoDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated todoDTO,
     * or with status {@code 400 (Bad Request)} if the todoDTO is not valid,
     * or with status {@code 404 (Not Found)} if the todoDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the todoDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/todos/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<TodoDTO> partialUpdateTodo(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TodoDTO todoDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update Todo partially : {}, {}", id, todoDTO);
        if (todoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, todoDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!todoRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TodoDTO> result = todoService.partialUpdate(todoDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, todoDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /todos} : get all the todos.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of todos in body.
     */
    @GetMapping("/todos")
    public ResponseEntity<List<TodoDTO>> getAllTodos(TodoCriteria criteria, Pageable pageable) {
        log.debug("REST request to get Todos by criteria: {}", criteria);
        Page<TodoDTO> page = todoQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /todos/count} : count all the todos.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/todos/count")
    public ResponseEntity<Long> countTodos(TodoCriteria criteria) {
        log.debug("REST request to count Todos by criteria: {}", criteria);
        return ResponseEntity.ok().body(todoQueryService.countByCriteria(criteria));
    }

    @GetMapping("/todos/dashboard")
    @Transactional
    public ResponseEntity<List<TodoDTO>> getDashboard() {
        Optional<String> currentUser = SecurityUtils.getCurrentUserLogin();
        User user = userRepository.findOneByLogin(currentUser.get()).get();
        //log.debug("REST request to count Todos by criteria: {}", criteria);
        TaskInstanceCriteria criteria = new TaskInstanceCriteria();
        StringFilter f = new StringFilter();
        f.setEquals(user.getId());
        criteria.setUserId(f);
        List<TaskInstanceDTO> dtoList = taskInstanceQueryService.findByCriteria(criteria);
        Map<Long, List<TaskInstanceDTO>> todoTaskMap = dtoList.stream().collect(Collectors.groupingBy(x -> x.getTodo().getId()));

        // get the todo list from this
        List<Long> todos = dtoList.stream().map(k -> k.getTodo().getId()).collect(Collectors.toList());
        TodoCriteria c = new TodoCriteria();
        LongFilter t = new LongFilter();
        t.setIn(todos);
        c.setId(t);
        List<TodoDTO> todoDtoList = todoQueryService.findByCriteria(c);
        for (TodoDTO todoDTO : todoDtoList) {
            todoDTO.setTaskInstances(new HashSet<TaskInstanceDTO>(todoTaskMap.get(todoDTO.getId())));
        }

        return ResponseEntity.ok().body(todoDtoList);
    }

    /**
     * {@code GET  /todos/:id} : get the "id" todo.
     *
     * @param id the id of the todoDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the todoDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/todos/{id}")
    public ResponseEntity<TodoDTO> getTodo(@PathVariable Long id) {
        log.debug("REST request to get Todo : {}", id);
        Optional<TodoDTO> todoDTO = todoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(todoDTO);
    }

    /**
     * {@code DELETE  /todos/:id} : delete the "id" todo.
     *
     * @param id the id of the todoDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/todos/{id}")
    @Transactional
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        log.debug("REST request to delete Todo : {}", id);

        // Find task instances of users and mark them as withdrawn and deactive
        TaskInstanceCriteria criteria = new TaskInstanceCriteria();
        LongFilter f = new LongFilter();
        f.setEquals(id);
        criteria.setTodoId(f);
        List<TaskInstanceDTO> dtoList = taskInstanceQueryService.findByCriteria(criteria);
        for (TaskInstanceDTO dto : dtoList) {
            dto.setActive("N");
            dto.setStatus(TaskStatus.WITHDRAWN);
        }
        taskInstanceRepository.saveAll(taskInstanceMapper.toEntity(dtoList));

        // Find all tasks for this Todo and soft delete tasks
        TaskCriteria taskCriteria = new TaskCriteria();
        f = new LongFilter();
        f.setEquals(id);
        criteria.setTodoId(f);
        List<TaskDTO> taskDtoList = taskQueryService.findByCriteria(taskCriteria);
        for (TaskDTO dto : taskDtoList) {
            dto.setActive("N");
        }
        taskRepository.saveAll(taskMapper.toEntity(taskDtoList));

        // SOft delete todo
        TodoDTO dto = todoService.findOne(id).get();
        dto.setActive("N");
        todoService.partialUpdate(dto);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
