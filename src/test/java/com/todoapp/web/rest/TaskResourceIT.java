package com.todoapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.todoapp.IntegrationTest;
import com.todoapp.domain.Task;
import com.todoapp.domain.TaskInstance;
import com.todoapp.domain.Todo;
import com.todoapp.repository.TaskInstanceRepository;
import com.todoapp.repository.TaskRepository;
import com.todoapp.service.TaskQueryService;
import com.todoapp.service.criteria.TaskCriteria;
import com.todoapp.service.dto.BulkAssignDTO;
import com.todoapp.service.dto.TaskDTO;
import com.todoapp.service.mapper.TaskMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.filter.LongFilter;

/**
 * Integration tests for the {@link TaskResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TaskResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_DESC = "AAAAAAAAAA";
    private static final String UPDATED_DESC = "BBBBBBBBBB";

    private static final Instant DEFAULT_PLANNED_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PLANNED_START_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_PLANNED_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PLANNED_END_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_ACTIVE = "AAAAAAAAAA";
    private static final String UPDATED_ACTIVE = "BBBBBBBBBB";

    private static final Integer DEFAULT_VERSION = 1;
    private static final Integer UPDATED_VERSION = 2;
    private static final Integer SMALLER_VERSION = 1 - 1;

    private static final String ENTITY_API_URL = "/api/tasks";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskInstanceRepository taskInstanceRepository;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTaskMockMvc;

    @Autowired
    private TaskQueryService taskQueryService;

    private Task task;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Task createEntity(EntityManager em) {
        Task task = new Task()
            .name(DEFAULT_NAME)
            .type(DEFAULT_TYPE)
            .desc(DEFAULT_DESC)
            .plannedStartDate(DEFAULT_PLANNED_START_DATE)
            .plannedEndDate(DEFAULT_PLANNED_END_DATE)
            .active(DEFAULT_ACTIVE)
            .version(DEFAULT_VERSION);
        return task;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Task createUpdatedEntity(EntityManager em) {
        Task task = new Task()
            .name(UPDATED_NAME)
            .type(UPDATED_TYPE)
            .desc(UPDATED_DESC)
            .plannedStartDate(UPDATED_PLANNED_START_DATE)
            .plannedEndDate(UPDATED_PLANNED_END_DATE)
            .active(UPDATED_ACTIVE)
            .version(UPDATED_VERSION);
        return task;
    }

    @BeforeEach
    public void initTest() {
        task = createEntity(em);
    }

    @Test
    @Transactional
    void createTask() throws Exception {
        int databaseSizeBeforeCreate = taskRepository.findAll().size();
        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);
        restTaskMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(taskDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeCreate + 1);
        Task testTask = taskList.get(taskList.size() - 1);
        assertThat(testTask.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTask.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testTask.getDesc()).isEqualTo(DEFAULT_DESC);
        assertThat(testTask.getPlannedStartDate()).isEqualTo(DEFAULT_PLANNED_START_DATE);
        assertThat(testTask.getPlannedEndDate()).isEqualTo(DEFAULT_PLANNED_END_DATE);
        assertThat(testTask.getActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testTask.getVersion()).isEqualTo(DEFAULT_VERSION);
    }

    @Test
    @Transactional
    void testAssignTasksToUsers() throws Exception {
        // Initialize the database
        // todoRepository.saveAndFlush(todo);

        // Get all tasks with this todo id
        LongFilter f = new LongFilter();
        f.setEquals(1L);
        TaskCriteria taskCriteria = new TaskCriteria();
        taskCriteria.setTodoId(f);
        List<TaskDTO> tasks = taskQueryService.findByCriteria(taskCriteria);
        tasks = tasks.subList(0, 2);

        BulkAssignDTO t = new BulkAssignDTO();
        t.setTodoId(1L);
        t.setTasks(tasks.stream().mapToLong(k -> k.getId()).boxed().collect(Collectors.toList()));
        List<String> emails = Arrays.asList("user2", "user3", "user4");
        t.setEmailAddresses(emails);

        // Get the todo
        restTaskMockMvc
            .perform(
                put(ENTITY_API_URL + "/assign")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(t))
            )
            .andReturn();

        int taskInstanceSizeAfterAssign = taskInstanceRepository.findAll().size();

        // 3 users *  2 tasks = 6 task instances
        assertThat(taskInstanceSizeAfterAssign).isEqualTo(6);

        // Try to assign again , the API should return an error
        // Get the todo
        restTaskMockMvc
            .perform(
                put(ENTITY_API_URL + "/assign")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(t))
            )
            .andExpect(jsonPath("$.errorKey").value("tasksAlreadyAssigned"));
    }

    @Test
    @Transactional
    void createTaskWithExistingId() throws Exception {
        // Create the Task with an existing ID
        task.setId(1L);
        TaskDTO taskDTO = taskMapper.toDto(task);

        int databaseSizeBeforeCreate = taskRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTaskMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(taskDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTasks() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList
        restTaskMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(task.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC)))
            .andExpect(jsonPath("$.[*].plannedStartDate").value(hasItem(DEFAULT_PLANNED_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].plannedEndDate").value(hasItem(DEFAULT_PLANNED_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)));
    }

    @Test
    @Transactional
    void getTask() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get the task
        restTaskMockMvc
            .perform(get(ENTITY_API_URL_ID, task.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(task.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.desc").value(DEFAULT_DESC))
            .andExpect(jsonPath("$.plannedStartDate").value(DEFAULT_PLANNED_START_DATE.toString()))
            .andExpect(jsonPath("$.plannedEndDate").value(DEFAULT_PLANNED_END_DATE.toString()))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION));
    }

    @Test
    @Transactional
    void testTaskWithInstancesAfterAssign() throws Exception {
        // Initialize the database
        // taskRepository.saveAndFlush(task);

        // Get all tasks with this todo id
        LongFilter f = new LongFilter();
        f.setEquals(1L);
        TaskCriteria taskCriteria = new TaskCriteria();
        taskCriteria.setTodoId(f);
        List<TaskDTO> tasks = taskQueryService.findByCriteria(taskCriteria);
        tasks = tasks.subList(0, 2);

        BulkAssignDTO t = new BulkAssignDTO();
        t.setTodoId(1L);
        t.setTasks(tasks.stream().mapToLong(k -> k.getId()).boxed().collect(Collectors.toList()));
        List<String> emails = Arrays.asList("user2", "user3", "user4");
        t.setEmailAddresses(emails);

        // Get the todo
        restTaskMockMvc
            .perform(
                put(ENTITY_API_URL + "/assign")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(t))
            )
            .andReturn();

        // Get the task
        restTaskMockMvc
            .perform(get(ENTITY_API_URL_ID, 1))
            .andDo(
                k -> {
                    System.out.println(k.getResponse().getContentAsString());
                }
            );
    }

    @Test
    @Transactional
    void getTasksByIdFiltering() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        Long id = task.getId();

        defaultTaskShouldBeFound("id.equals=" + id);
        defaultTaskShouldNotBeFound("id.notEquals=" + id);

        defaultTaskShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTaskShouldNotBeFound("id.greaterThan=" + id);

        defaultTaskShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTaskShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTasksByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where name equals to DEFAULT_NAME
        defaultTaskShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the taskList where name equals to UPDATED_NAME
        defaultTaskShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTasksByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where name not equals to DEFAULT_NAME
        defaultTaskShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the taskList where name not equals to UPDATED_NAME
        defaultTaskShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTasksByNameIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where name in DEFAULT_NAME or UPDATED_NAME
        defaultTaskShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the taskList where name equals to UPDATED_NAME
        defaultTaskShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTasksByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where name is not null
        defaultTaskShouldBeFound("name.specified=true");

        // Get all the taskList where name is null
        defaultTaskShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByNameContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where name contains DEFAULT_NAME
        defaultTaskShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the taskList where name contains UPDATED_NAME
        defaultTaskShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTasksByNameNotContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where name does not contain DEFAULT_NAME
        defaultTaskShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the taskList where name does not contain UPDATED_NAME
        defaultTaskShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTasksByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where type equals to DEFAULT_TYPE
        defaultTaskShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the taskList where type equals to UPDATED_TYPE
        defaultTaskShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllTasksByTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where type not equals to DEFAULT_TYPE
        defaultTaskShouldNotBeFound("type.notEquals=" + DEFAULT_TYPE);

        // Get all the taskList where type not equals to UPDATED_TYPE
        defaultTaskShouldBeFound("type.notEquals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllTasksByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultTaskShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the taskList where type equals to UPDATED_TYPE
        defaultTaskShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllTasksByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where type is not null
        defaultTaskShouldBeFound("type.specified=true");

        // Get all the taskList where type is null
        defaultTaskShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByTypeContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where type contains DEFAULT_TYPE
        defaultTaskShouldBeFound("type.contains=" + DEFAULT_TYPE);

        // Get all the taskList where type contains UPDATED_TYPE
        defaultTaskShouldNotBeFound("type.contains=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllTasksByTypeNotContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where type does not contain DEFAULT_TYPE
        defaultTaskShouldNotBeFound("type.doesNotContain=" + DEFAULT_TYPE);

        // Get all the taskList where type does not contain UPDATED_TYPE
        defaultTaskShouldBeFound("type.doesNotContain=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllTasksByDescIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where desc equals to DEFAULT_DESC
        defaultTaskShouldBeFound("desc.equals=" + DEFAULT_DESC);

        // Get all the taskList where desc equals to UPDATED_DESC
        defaultTaskShouldNotBeFound("desc.equals=" + UPDATED_DESC);
    }

    @Test
    @Transactional
    void getAllTasksByDescIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where desc not equals to DEFAULT_DESC
        defaultTaskShouldNotBeFound("desc.notEquals=" + DEFAULT_DESC);

        // Get all the taskList where desc not equals to UPDATED_DESC
        defaultTaskShouldBeFound("desc.notEquals=" + UPDATED_DESC);
    }

    @Test
    @Transactional
    void getAllTasksByDescIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where desc in DEFAULT_DESC or UPDATED_DESC
        defaultTaskShouldBeFound("desc.in=" + DEFAULT_DESC + "," + UPDATED_DESC);

        // Get all the taskList where desc equals to UPDATED_DESC
        defaultTaskShouldNotBeFound("desc.in=" + UPDATED_DESC);
    }

    @Test
    @Transactional
    void getAllTasksByDescIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where desc is not null
        defaultTaskShouldBeFound("desc.specified=true");

        // Get all the taskList where desc is null
        defaultTaskShouldNotBeFound("desc.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByDescContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where desc contains DEFAULT_DESC
        defaultTaskShouldBeFound("desc.contains=" + DEFAULT_DESC);

        // Get all the taskList where desc contains UPDATED_DESC
        defaultTaskShouldNotBeFound("desc.contains=" + UPDATED_DESC);
    }

    @Test
    @Transactional
    void getAllTasksByDescNotContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where desc does not contain DEFAULT_DESC
        defaultTaskShouldNotBeFound("desc.doesNotContain=" + DEFAULT_DESC);

        // Get all the taskList where desc does not contain UPDATED_DESC
        defaultTaskShouldBeFound("desc.doesNotContain=" + UPDATED_DESC);
    }

    @Test
    @Transactional
    void getAllTasksByPlannedStartDateIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where plannedStartDate equals to DEFAULT_PLANNED_START_DATE
        defaultTaskShouldBeFound("plannedStartDate.equals=" + DEFAULT_PLANNED_START_DATE);

        // Get all the taskList where plannedStartDate equals to UPDATED_PLANNED_START_DATE
        defaultTaskShouldNotBeFound("plannedStartDate.equals=" + UPDATED_PLANNED_START_DATE);
    }

    @Test
    @Transactional
    void getAllTasksByPlannedStartDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where plannedStartDate not equals to DEFAULT_PLANNED_START_DATE
        defaultTaskShouldNotBeFound("plannedStartDate.notEquals=" + DEFAULT_PLANNED_START_DATE);

        // Get all the taskList where plannedStartDate not equals to UPDATED_PLANNED_START_DATE
        defaultTaskShouldBeFound("plannedStartDate.notEquals=" + UPDATED_PLANNED_START_DATE);
    }

    @Test
    @Transactional
    void getAllTasksByPlannedStartDateIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where plannedStartDate in DEFAULT_PLANNED_START_DATE or UPDATED_PLANNED_START_DATE
        defaultTaskShouldBeFound("plannedStartDate.in=" + DEFAULT_PLANNED_START_DATE + "," + UPDATED_PLANNED_START_DATE);

        // Get all the taskList where plannedStartDate equals to UPDATED_PLANNED_START_DATE
        defaultTaskShouldNotBeFound("plannedStartDate.in=" + UPDATED_PLANNED_START_DATE);
    }

    @Test
    @Transactional
    void getAllTasksByPlannedStartDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where plannedStartDate is not null
        defaultTaskShouldBeFound("plannedStartDate.specified=true");

        // Get all the taskList where plannedStartDate is null
        defaultTaskShouldNotBeFound("plannedStartDate.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByPlannedEndDateIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where plannedEndDate equals to DEFAULT_PLANNED_END_DATE
        defaultTaskShouldBeFound("plannedEndDate.equals=" + DEFAULT_PLANNED_END_DATE);

        // Get all the taskList where plannedEndDate equals to UPDATED_PLANNED_END_DATE
        defaultTaskShouldNotBeFound("plannedEndDate.equals=" + UPDATED_PLANNED_END_DATE);
    }

    @Test
    @Transactional
    void getAllTasksByPlannedEndDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where plannedEndDate not equals to DEFAULT_PLANNED_END_DATE
        defaultTaskShouldNotBeFound("plannedEndDate.notEquals=" + DEFAULT_PLANNED_END_DATE);

        // Get all the taskList where plannedEndDate not equals to UPDATED_PLANNED_END_DATE
        defaultTaskShouldBeFound("plannedEndDate.notEquals=" + UPDATED_PLANNED_END_DATE);
    }

    @Test
    @Transactional
    void getAllTasksByPlannedEndDateIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where plannedEndDate in DEFAULT_PLANNED_END_DATE or UPDATED_PLANNED_END_DATE
        defaultTaskShouldBeFound("plannedEndDate.in=" + DEFAULT_PLANNED_END_DATE + "," + UPDATED_PLANNED_END_DATE);

        // Get all the taskList where plannedEndDate equals to UPDATED_PLANNED_END_DATE
        defaultTaskShouldNotBeFound("plannedEndDate.in=" + UPDATED_PLANNED_END_DATE);
    }

    @Test
    @Transactional
    void getAllTasksByPlannedEndDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where plannedEndDate is not null
        defaultTaskShouldBeFound("plannedEndDate.specified=true");

        // Get all the taskList where plannedEndDate is null
        defaultTaskShouldNotBeFound("plannedEndDate.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where active equals to DEFAULT_ACTIVE
        defaultTaskShouldBeFound("active.equals=" + DEFAULT_ACTIVE);

        // Get all the taskList where active equals to UPDATED_ACTIVE
        defaultTaskShouldNotBeFound("active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllTasksByActiveIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where active not equals to DEFAULT_ACTIVE
        defaultTaskShouldNotBeFound("active.notEquals=" + DEFAULT_ACTIVE);

        // Get all the taskList where active not equals to UPDATED_ACTIVE
        defaultTaskShouldBeFound("active.notEquals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllTasksByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where active in DEFAULT_ACTIVE or UPDATED_ACTIVE
        defaultTaskShouldBeFound("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE);

        // Get all the taskList where active equals to UPDATED_ACTIVE
        defaultTaskShouldNotBeFound("active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllTasksByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where active is not null
        defaultTaskShouldBeFound("active.specified=true");

        // Get all the taskList where active is null
        defaultTaskShouldNotBeFound("active.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByActiveContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where active contains DEFAULT_ACTIVE
        defaultTaskShouldBeFound("active.contains=" + DEFAULT_ACTIVE);

        // Get all the taskList where active contains UPDATED_ACTIVE
        defaultTaskShouldNotBeFound("active.contains=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllTasksByActiveNotContainsSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where active does not contain DEFAULT_ACTIVE
        defaultTaskShouldNotBeFound("active.doesNotContain=" + DEFAULT_ACTIVE);

        // Get all the taskList where active does not contain UPDATED_ACTIVE
        defaultTaskShouldBeFound("active.doesNotContain=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllTasksByVersionIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where version equals to DEFAULT_VERSION
        defaultTaskShouldBeFound("version.equals=" + DEFAULT_VERSION);

        // Get all the taskList where version equals to UPDATED_VERSION
        defaultTaskShouldNotBeFound("version.equals=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllTasksByVersionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where version not equals to DEFAULT_VERSION
        defaultTaskShouldNotBeFound("version.notEquals=" + DEFAULT_VERSION);

        // Get all the taskList where version not equals to UPDATED_VERSION
        defaultTaskShouldBeFound("version.notEquals=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllTasksByVersionIsInShouldWork() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where version in DEFAULT_VERSION or UPDATED_VERSION
        defaultTaskShouldBeFound("version.in=" + DEFAULT_VERSION + "," + UPDATED_VERSION);

        // Get all the taskList where version equals to UPDATED_VERSION
        defaultTaskShouldNotBeFound("version.in=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllTasksByVersionIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where version is not null
        defaultTaskShouldBeFound("version.specified=true");

        // Get all the taskList where version is null
        defaultTaskShouldNotBeFound("version.specified=false");
    }

    @Test
    @Transactional
    void getAllTasksByVersionIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where version is greater than or equal to DEFAULT_VERSION
        defaultTaskShouldBeFound("version.greaterThanOrEqual=" + DEFAULT_VERSION);

        // Get all the taskList where version is greater than or equal to UPDATED_VERSION
        defaultTaskShouldNotBeFound("version.greaterThanOrEqual=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllTasksByVersionIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where version is less than or equal to DEFAULT_VERSION
        defaultTaskShouldBeFound("version.lessThanOrEqual=" + DEFAULT_VERSION);

        // Get all the taskList where version is less than or equal to SMALLER_VERSION
        defaultTaskShouldNotBeFound("version.lessThanOrEqual=" + SMALLER_VERSION);
    }

    @Test
    @Transactional
    void getAllTasksByVersionIsLessThanSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where version is less than DEFAULT_VERSION
        defaultTaskShouldNotBeFound("version.lessThan=" + DEFAULT_VERSION);

        // Get all the taskList where version is less than UPDATED_VERSION
        defaultTaskShouldBeFound("version.lessThan=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllTasksByVersionIsGreaterThanSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList where version is greater than DEFAULT_VERSION
        defaultTaskShouldNotBeFound("version.greaterThan=" + DEFAULT_VERSION);

        // Get all the taskList where version is greater than SMALLER_VERSION
        defaultTaskShouldBeFound("version.greaterThan=" + SMALLER_VERSION);
    }

    @Test
    @Transactional
    void getAllTasksByTaskInstanceIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);
        TaskInstance taskInstance = TaskInstanceResourceIT.createEntity(em);
        em.persist(taskInstance);
        em.flush();
        task.addTaskInstance(taskInstance);
        taskRepository.saveAndFlush(task);
        Long taskInstanceId = taskInstance.getId();

        // Get all the taskList where taskInstance equals to taskInstanceId
        defaultTaskShouldBeFound("taskInstanceId.equals=" + taskInstanceId);

        // Get all the taskList where taskInstance equals to (taskInstanceId + 1)
        defaultTaskShouldNotBeFound("taskInstanceId.equals=" + (taskInstanceId + 1));
    }

    @Test
    @Transactional
    void getAllTasksByTodoIsEqualToSomething() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);
        Todo todo = TodoResourceIT.createEntity(em);
        em.persist(todo);
        em.flush();
        task.setTodo(todo);
        taskRepository.saveAndFlush(task);
        Long todoId = todo.getId();

        // Get all the taskList where todo equals to todoId
        defaultTaskShouldBeFound("todoId.equals=" + todoId);

        // Get all the taskList where todo equals to (todoId + 1)
        defaultTaskShouldNotBeFound("todoId.equals=" + (todoId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTaskShouldBeFound(String filter) throws Exception {
        restTaskMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(task.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC)))
            .andExpect(jsonPath("$.[*].plannedStartDate").value(hasItem(DEFAULT_PLANNED_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].plannedEndDate").value(hasItem(DEFAULT_PLANNED_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)));

        // Check, that the count call also returns 1
        restTaskMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTaskShouldNotBeFound(String filter) throws Exception {
        restTaskMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTaskMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTask() throws Exception {
        // Get the task
        restTaskMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTask() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        int databaseSizeBeforeUpdate = taskRepository.findAll().size();

        // Update the task
        Task updatedTask = taskRepository.findById(task.getId()).get();
        // Disconnect from session so that the updates on updatedTask are not directly saved in db
        em.detach(updatedTask);
        updatedTask
            .name(UPDATED_NAME)
            .type(UPDATED_TYPE)
            .desc(UPDATED_DESC)
            .plannedStartDate(UPDATED_PLANNED_START_DATE)
            .plannedEndDate(UPDATED_PLANNED_END_DATE)
            .active(UPDATED_ACTIVE)
            .version(UPDATED_VERSION);
        TaskDTO taskDTO = taskMapper.toDto(updatedTask);

        restTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, taskDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(taskDTO))
            )
            .andExpect(status().isOk());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
        Task testTask = taskList.get(taskList.size() - 1);
        assertThat(testTask.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTask.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testTask.getDesc()).isEqualTo(UPDATED_DESC);
        assertThat(testTask.getPlannedStartDate()).isEqualTo(UPDATED_PLANNED_START_DATE);
        assertThat(testTask.getPlannedEndDate()).isEqualTo(UPDATED_PLANNED_END_DATE);
        assertThat(testTask.getActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testTask.getVersion()).isEqualTo(UPDATED_VERSION);
    }

    @Test
    @Transactional
    void putNonExistingTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().size();
        task.setId(count.incrementAndGet());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, taskDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(taskDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().size();
        task.setId(count.incrementAndGet());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(taskDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().size();
        task.setId(count.incrementAndGet());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(taskDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTaskWithPatch() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        int databaseSizeBeforeUpdate = taskRepository.findAll().size();

        // Update the task using partial update
        Task partialUpdatedTask = new Task();
        partialUpdatedTask.setId(task.getId());

        partialUpdatedTask.type(UPDATED_TYPE).plannedStartDate(UPDATED_PLANNED_START_DATE);

        restTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTask.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTask))
            )
            .andExpect(status().isOk());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
        Task testTask = taskList.get(taskList.size() - 1);
        assertThat(testTask.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTask.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testTask.getDesc()).isEqualTo(DEFAULT_DESC);
        assertThat(testTask.getPlannedStartDate()).isEqualTo(UPDATED_PLANNED_START_DATE);
        assertThat(testTask.getPlannedEndDate()).isEqualTo(DEFAULT_PLANNED_END_DATE);
        assertThat(testTask.getActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testTask.getVersion()).isEqualTo(DEFAULT_VERSION);
    }

    @Test
    @Transactional
    void fullUpdateTaskWithPatch() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        int databaseSizeBeforeUpdate = taskRepository.findAll().size();

        // Update the task using partial update
        Task partialUpdatedTask = new Task();
        partialUpdatedTask.setId(task.getId());

        partialUpdatedTask
            .name(UPDATED_NAME)
            .type(UPDATED_TYPE)
            .desc(UPDATED_DESC)
            .plannedStartDate(UPDATED_PLANNED_START_DATE)
            .plannedEndDate(UPDATED_PLANNED_END_DATE)
            .active(UPDATED_ACTIVE)
            .version(UPDATED_VERSION);

        restTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTask.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTask))
            )
            .andExpect(status().isOk());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
        Task testTask = taskList.get(taskList.size() - 1);
        assertThat(testTask.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTask.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testTask.getDesc()).isEqualTo(UPDATED_DESC);
        assertThat(testTask.getPlannedStartDate()).isEqualTo(UPDATED_PLANNED_START_DATE);
        assertThat(testTask.getPlannedEndDate()).isEqualTo(UPDATED_PLANNED_END_DATE);
        assertThat(testTask.getActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testTask.getVersion()).isEqualTo(UPDATED_VERSION);
    }

    @Test
    @Transactional
    void patchNonExistingTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().size();
        task.setId(count.incrementAndGet());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, taskDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(taskDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().size();
        task.setId(count.incrementAndGet());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(taskDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().size();
        task.setId(count.incrementAndGet());

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(taskDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTask() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        int databaseSizeBeforeDelete = taskRepository.findAll().size();

        // Delete the task
        restTaskMockMvc
            .perform(delete(ENTITY_API_URL_ID, task.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
