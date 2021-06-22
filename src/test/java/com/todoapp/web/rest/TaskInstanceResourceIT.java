package com.todoapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.todoapp.IntegrationTest;
import com.todoapp.domain.Task;
import com.todoapp.domain.TaskInstance;
import com.todoapp.domain.Todo;
import com.todoapp.domain.User;
import com.todoapp.domain.enumeration.TaskStatus;
import com.todoapp.repository.TaskInstanceRepository;
import com.todoapp.service.criteria.TaskInstanceCriteria;
import com.todoapp.service.dto.TaskInstanceDTO;
import com.todoapp.service.mapper.TaskInstanceMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TaskInstanceResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TaskInstanceResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_DESC = "AAAAAAAAAA";
    private static final String UPDATED_DESC = "BBBBBBBBBB";

    private static final TaskStatus DEFAULT_STATUS = TaskStatus.INPROGRESS;
    private static final TaskStatus UPDATED_STATUS = TaskStatus.NOTSTARTED;

    private static final Instant DEFAULT_PLANNED_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PLANNED_START_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_PLANNED_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PLANNED_END_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_ACTUAL_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ACTUAL_START_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_ACTUAL_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ACTUAL_END_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Double DEFAULT_TIME_SPENT = 1D;
    private static final Double UPDATED_TIME_SPENT = 2D;
    private static final Double SMALLER_TIME_SPENT = 1D - 1D;

    private static final String DEFAULT_ACTIVE = "AAAAAAAAAA";
    private static final String UPDATED_ACTIVE = "BBBBBBBBBB";

    private static final Integer DEFAULT_VERSION = 1;
    private static final Integer UPDATED_VERSION = 2;
    private static final Integer SMALLER_VERSION = 1 - 1;

    private static final String ENTITY_API_URL = "/api/task-instances";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TaskInstanceRepository taskInstanceRepository;

    @Autowired
    private TaskInstanceMapper taskInstanceMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTaskInstanceMockMvc;

    private TaskInstance taskInstance;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TaskInstance createEntity(EntityManager em) {
        TaskInstance taskInstance = new TaskInstance()
            .name(DEFAULT_NAME)
            .type(DEFAULT_TYPE)
            .desc(DEFAULT_DESC)
            .status(DEFAULT_STATUS)
            .plannedStartDate(DEFAULT_PLANNED_START_DATE)
            .plannedEndDate(DEFAULT_PLANNED_END_DATE)
            .actualStartDate(DEFAULT_ACTUAL_START_DATE)
            .actualEndDate(DEFAULT_ACTUAL_END_DATE)
            .timeSpent(DEFAULT_TIME_SPENT)
            .active(DEFAULT_ACTIVE)
            .version(DEFAULT_VERSION);
        return taskInstance;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TaskInstance createUpdatedEntity(EntityManager em) {
        TaskInstance taskInstance = new TaskInstance()
            .name(UPDATED_NAME)
            .type(UPDATED_TYPE)
            .desc(UPDATED_DESC)
            .status(UPDATED_STATUS)
            .plannedStartDate(UPDATED_PLANNED_START_DATE)
            .plannedEndDate(UPDATED_PLANNED_END_DATE)
            .actualStartDate(UPDATED_ACTUAL_START_DATE)
            .actualEndDate(UPDATED_ACTUAL_END_DATE)
            .timeSpent(UPDATED_TIME_SPENT)
            .active(UPDATED_ACTIVE)
            .version(UPDATED_VERSION);
        return taskInstance;
    }

    @BeforeEach
    public void initTest() {
        taskInstance = createEntity(em);
    }

    @Test
    @Transactional
    void createTaskInstance() throws Exception {
        int databaseSizeBeforeCreate = taskInstanceRepository.findAll().size();
        // Create the TaskInstance
        TaskInstanceDTO taskInstanceDTO = taskInstanceMapper.toDto(taskInstance);
        restTaskInstanceMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(taskInstanceDTO))
            )
            .andExpect(status().isCreated());

        // Validate the TaskInstance in the database
        List<TaskInstance> taskInstanceList = taskInstanceRepository.findAll();
        assertThat(taskInstanceList).hasSize(databaseSizeBeforeCreate + 1);
        TaskInstance testTaskInstance = taskInstanceList.get(taskInstanceList.size() - 1);
        assertThat(testTaskInstance.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTaskInstance.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testTaskInstance.getDesc()).isEqualTo(DEFAULT_DESC);
        assertThat(testTaskInstance.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testTaskInstance.getPlannedStartDate()).isEqualTo(DEFAULT_PLANNED_START_DATE);
        assertThat(testTaskInstance.getPlannedEndDate()).isEqualTo(DEFAULT_PLANNED_END_DATE);
        assertThat(testTaskInstance.getActualStartDate()).isEqualTo(DEFAULT_ACTUAL_START_DATE);
        assertThat(testTaskInstance.getActualEndDate()).isEqualTo(DEFAULT_ACTUAL_END_DATE);
        assertThat(testTaskInstance.getTimeSpent()).isEqualTo(DEFAULT_TIME_SPENT);
        assertThat(testTaskInstance.getActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testTaskInstance.getVersion()).isEqualTo(DEFAULT_VERSION);
    }

    @Test
    @Transactional
    void createTaskInstanceWithExistingId() throws Exception {
        // Create the TaskInstance with an existing ID
        taskInstance.setId(1L);
        TaskInstanceDTO taskInstanceDTO = taskInstanceMapper.toDto(taskInstance);

        int databaseSizeBeforeCreate = taskInstanceRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTaskInstanceMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(taskInstanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskInstance in the database
        List<TaskInstance> taskInstanceList = taskInstanceRepository.findAll();
        assertThat(taskInstanceList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTaskInstances() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList
        restTaskInstanceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(taskInstance.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].plannedStartDate").value(hasItem(DEFAULT_PLANNED_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].plannedEndDate").value(hasItem(DEFAULT_PLANNED_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].actualStartDate").value(hasItem(DEFAULT_ACTUAL_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].actualEndDate").value(hasItem(DEFAULT_ACTUAL_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].timeSpent").value(hasItem(DEFAULT_TIME_SPENT.doubleValue())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)));
    }

    @Test
    @Transactional
    void getTaskInstance() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get the taskInstance
        restTaskInstanceMockMvc
            .perform(get(ENTITY_API_URL_ID, taskInstance.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(taskInstance.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
            .andExpect(jsonPath("$.desc").value(DEFAULT_DESC))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.plannedStartDate").value(DEFAULT_PLANNED_START_DATE.toString()))
            .andExpect(jsonPath("$.plannedEndDate").value(DEFAULT_PLANNED_END_DATE.toString()))
            .andExpect(jsonPath("$.actualStartDate").value(DEFAULT_ACTUAL_START_DATE.toString()))
            .andExpect(jsonPath("$.actualEndDate").value(DEFAULT_ACTUAL_END_DATE.toString()))
            .andExpect(jsonPath("$.timeSpent").value(DEFAULT_TIME_SPENT.doubleValue()))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION));
    }

    @Test
    @Transactional
    void getTaskInstancesByIdFiltering() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        Long id = taskInstance.getId();

        defaultTaskInstanceShouldBeFound("id.equals=" + id);
        defaultTaskInstanceShouldNotBeFound("id.notEquals=" + id);

        defaultTaskInstanceShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTaskInstanceShouldNotBeFound("id.greaterThan=" + id);

        defaultTaskInstanceShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTaskInstanceShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where name equals to DEFAULT_NAME
        defaultTaskInstanceShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the taskInstanceList where name equals to UPDATED_NAME
        defaultTaskInstanceShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where name not equals to DEFAULT_NAME
        defaultTaskInstanceShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the taskInstanceList where name not equals to UPDATED_NAME
        defaultTaskInstanceShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByNameIsInShouldWork() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where name in DEFAULT_NAME or UPDATED_NAME
        defaultTaskInstanceShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the taskInstanceList where name equals to UPDATED_NAME
        defaultTaskInstanceShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where name is not null
        defaultTaskInstanceShouldBeFound("name.specified=true");

        // Get all the taskInstanceList where name is null
        defaultTaskInstanceShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllTaskInstancesByNameContainsSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where name contains DEFAULT_NAME
        defaultTaskInstanceShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the taskInstanceList where name contains UPDATED_NAME
        defaultTaskInstanceShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByNameNotContainsSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where name does not contain DEFAULT_NAME
        defaultTaskInstanceShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the taskInstanceList where name does not contain UPDATED_NAME
        defaultTaskInstanceShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where type equals to DEFAULT_TYPE
        defaultTaskInstanceShouldBeFound("type.equals=" + DEFAULT_TYPE);

        // Get all the taskInstanceList where type equals to UPDATED_TYPE
        defaultTaskInstanceShouldNotBeFound("type.equals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByTypeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where type not equals to DEFAULT_TYPE
        defaultTaskInstanceShouldNotBeFound("type.notEquals=" + DEFAULT_TYPE);

        // Get all the taskInstanceList where type not equals to UPDATED_TYPE
        defaultTaskInstanceShouldBeFound("type.notEquals=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByTypeIsInShouldWork() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where type in DEFAULT_TYPE or UPDATED_TYPE
        defaultTaskInstanceShouldBeFound("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE);

        // Get all the taskInstanceList where type equals to UPDATED_TYPE
        defaultTaskInstanceShouldNotBeFound("type.in=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where type is not null
        defaultTaskInstanceShouldBeFound("type.specified=true");

        // Get all the taskInstanceList where type is null
        defaultTaskInstanceShouldNotBeFound("type.specified=false");
    }

    @Test
    @Transactional
    void getAllTaskInstancesByTypeContainsSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where type contains DEFAULT_TYPE
        defaultTaskInstanceShouldBeFound("type.contains=" + DEFAULT_TYPE);

        // Get all the taskInstanceList where type contains UPDATED_TYPE
        defaultTaskInstanceShouldNotBeFound("type.contains=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByTypeNotContainsSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where type does not contain DEFAULT_TYPE
        defaultTaskInstanceShouldNotBeFound("type.doesNotContain=" + DEFAULT_TYPE);

        // Get all the taskInstanceList where type does not contain UPDATED_TYPE
        defaultTaskInstanceShouldBeFound("type.doesNotContain=" + UPDATED_TYPE);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByDescIsEqualToSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where desc equals to DEFAULT_DESC
        defaultTaskInstanceShouldBeFound("desc.equals=" + DEFAULT_DESC);

        // Get all the taskInstanceList where desc equals to UPDATED_DESC
        defaultTaskInstanceShouldNotBeFound("desc.equals=" + UPDATED_DESC);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByDescIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where desc not equals to DEFAULT_DESC
        defaultTaskInstanceShouldNotBeFound("desc.notEquals=" + DEFAULT_DESC);

        // Get all the taskInstanceList where desc not equals to UPDATED_DESC
        defaultTaskInstanceShouldBeFound("desc.notEquals=" + UPDATED_DESC);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByDescIsInShouldWork() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where desc in DEFAULT_DESC or UPDATED_DESC
        defaultTaskInstanceShouldBeFound("desc.in=" + DEFAULT_DESC + "," + UPDATED_DESC);

        // Get all the taskInstanceList where desc equals to UPDATED_DESC
        defaultTaskInstanceShouldNotBeFound("desc.in=" + UPDATED_DESC);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByDescIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where desc is not null
        defaultTaskInstanceShouldBeFound("desc.specified=true");

        // Get all the taskInstanceList where desc is null
        defaultTaskInstanceShouldNotBeFound("desc.specified=false");
    }

    @Test
    @Transactional
    void getAllTaskInstancesByDescContainsSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where desc contains DEFAULT_DESC
        defaultTaskInstanceShouldBeFound("desc.contains=" + DEFAULT_DESC);

        // Get all the taskInstanceList where desc contains UPDATED_DESC
        defaultTaskInstanceShouldNotBeFound("desc.contains=" + UPDATED_DESC);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByDescNotContainsSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where desc does not contain DEFAULT_DESC
        defaultTaskInstanceShouldNotBeFound("desc.doesNotContain=" + DEFAULT_DESC);

        // Get all the taskInstanceList where desc does not contain UPDATED_DESC
        defaultTaskInstanceShouldBeFound("desc.doesNotContain=" + UPDATED_DESC);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where status equals to DEFAULT_STATUS
        defaultTaskInstanceShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the taskInstanceList where status equals to UPDATED_STATUS
        defaultTaskInstanceShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByStatusIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where status not equals to DEFAULT_STATUS
        defaultTaskInstanceShouldNotBeFound("status.notEquals=" + DEFAULT_STATUS);

        // Get all the taskInstanceList where status not equals to UPDATED_STATUS
        defaultTaskInstanceShouldBeFound("status.notEquals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultTaskInstanceShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the taskInstanceList where status equals to UPDATED_STATUS
        defaultTaskInstanceShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where status is not null
        defaultTaskInstanceShouldBeFound("status.specified=true");

        // Get all the taskInstanceList where status is null
        defaultTaskInstanceShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllTaskInstancesByPlannedStartDateIsEqualToSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where plannedStartDate equals to DEFAULT_PLANNED_START_DATE
        defaultTaskInstanceShouldBeFound("plannedStartDate.equals=" + DEFAULT_PLANNED_START_DATE);

        // Get all the taskInstanceList where plannedStartDate equals to UPDATED_PLANNED_START_DATE
        defaultTaskInstanceShouldNotBeFound("plannedStartDate.equals=" + UPDATED_PLANNED_START_DATE);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByPlannedStartDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where plannedStartDate not equals to DEFAULT_PLANNED_START_DATE
        defaultTaskInstanceShouldNotBeFound("plannedStartDate.notEquals=" + DEFAULT_PLANNED_START_DATE);

        // Get all the taskInstanceList where plannedStartDate not equals to UPDATED_PLANNED_START_DATE
        defaultTaskInstanceShouldBeFound("plannedStartDate.notEquals=" + UPDATED_PLANNED_START_DATE);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByPlannedStartDateIsInShouldWork() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where plannedStartDate in DEFAULT_PLANNED_START_DATE or UPDATED_PLANNED_START_DATE
        defaultTaskInstanceShouldBeFound("plannedStartDate.in=" + DEFAULT_PLANNED_START_DATE + "," + UPDATED_PLANNED_START_DATE);

        // Get all the taskInstanceList where plannedStartDate equals to UPDATED_PLANNED_START_DATE
        defaultTaskInstanceShouldNotBeFound("plannedStartDate.in=" + UPDATED_PLANNED_START_DATE);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByPlannedStartDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where plannedStartDate is not null
        defaultTaskInstanceShouldBeFound("plannedStartDate.specified=true");

        // Get all the taskInstanceList where plannedStartDate is null
        defaultTaskInstanceShouldNotBeFound("plannedStartDate.specified=false");
    }

    @Test
    @Transactional
    void getAllTaskInstancesByPlannedEndDateIsEqualToSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where plannedEndDate equals to DEFAULT_PLANNED_END_DATE
        defaultTaskInstanceShouldBeFound("plannedEndDate.equals=" + DEFAULT_PLANNED_END_DATE);

        // Get all the taskInstanceList where plannedEndDate equals to UPDATED_PLANNED_END_DATE
        defaultTaskInstanceShouldNotBeFound("plannedEndDate.equals=" + UPDATED_PLANNED_END_DATE);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByPlannedEndDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where plannedEndDate not equals to DEFAULT_PLANNED_END_DATE
        defaultTaskInstanceShouldNotBeFound("plannedEndDate.notEquals=" + DEFAULT_PLANNED_END_DATE);

        // Get all the taskInstanceList where plannedEndDate not equals to UPDATED_PLANNED_END_DATE
        defaultTaskInstanceShouldBeFound("plannedEndDate.notEquals=" + UPDATED_PLANNED_END_DATE);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByPlannedEndDateIsInShouldWork() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where plannedEndDate in DEFAULT_PLANNED_END_DATE or UPDATED_PLANNED_END_DATE
        defaultTaskInstanceShouldBeFound("plannedEndDate.in=" + DEFAULT_PLANNED_END_DATE + "," + UPDATED_PLANNED_END_DATE);

        // Get all the taskInstanceList where plannedEndDate equals to UPDATED_PLANNED_END_DATE
        defaultTaskInstanceShouldNotBeFound("plannedEndDate.in=" + UPDATED_PLANNED_END_DATE);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByPlannedEndDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where plannedEndDate is not null
        defaultTaskInstanceShouldBeFound("plannedEndDate.specified=true");

        // Get all the taskInstanceList where plannedEndDate is null
        defaultTaskInstanceShouldNotBeFound("plannedEndDate.specified=false");
    }

    @Test
    @Transactional
    void getAllTaskInstancesByActualStartDateIsEqualToSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where actualStartDate equals to DEFAULT_ACTUAL_START_DATE
        defaultTaskInstanceShouldBeFound("actualStartDate.equals=" + DEFAULT_ACTUAL_START_DATE);

        // Get all the taskInstanceList where actualStartDate equals to UPDATED_ACTUAL_START_DATE
        defaultTaskInstanceShouldNotBeFound("actualStartDate.equals=" + UPDATED_ACTUAL_START_DATE);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByActualStartDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where actualStartDate not equals to DEFAULT_ACTUAL_START_DATE
        defaultTaskInstanceShouldNotBeFound("actualStartDate.notEquals=" + DEFAULT_ACTUAL_START_DATE);

        // Get all the taskInstanceList where actualStartDate not equals to UPDATED_ACTUAL_START_DATE
        defaultTaskInstanceShouldBeFound("actualStartDate.notEquals=" + UPDATED_ACTUAL_START_DATE);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByActualStartDateIsInShouldWork() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where actualStartDate in DEFAULT_ACTUAL_START_DATE or UPDATED_ACTUAL_START_DATE
        defaultTaskInstanceShouldBeFound("actualStartDate.in=" + DEFAULT_ACTUAL_START_DATE + "," + UPDATED_ACTUAL_START_DATE);

        // Get all the taskInstanceList where actualStartDate equals to UPDATED_ACTUAL_START_DATE
        defaultTaskInstanceShouldNotBeFound("actualStartDate.in=" + UPDATED_ACTUAL_START_DATE);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByActualStartDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where actualStartDate is not null
        defaultTaskInstanceShouldBeFound("actualStartDate.specified=true");

        // Get all the taskInstanceList where actualStartDate is null
        defaultTaskInstanceShouldNotBeFound("actualStartDate.specified=false");
    }

    @Test
    @Transactional
    void getAllTaskInstancesByActualEndDateIsEqualToSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where actualEndDate equals to DEFAULT_ACTUAL_END_DATE
        defaultTaskInstanceShouldBeFound("actualEndDate.equals=" + DEFAULT_ACTUAL_END_DATE);

        // Get all the taskInstanceList where actualEndDate equals to UPDATED_ACTUAL_END_DATE
        defaultTaskInstanceShouldNotBeFound("actualEndDate.equals=" + UPDATED_ACTUAL_END_DATE);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByActualEndDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where actualEndDate not equals to DEFAULT_ACTUAL_END_DATE
        defaultTaskInstanceShouldNotBeFound("actualEndDate.notEquals=" + DEFAULT_ACTUAL_END_DATE);

        // Get all the taskInstanceList where actualEndDate not equals to UPDATED_ACTUAL_END_DATE
        defaultTaskInstanceShouldBeFound("actualEndDate.notEquals=" + UPDATED_ACTUAL_END_DATE);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByActualEndDateIsInShouldWork() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where actualEndDate in DEFAULT_ACTUAL_END_DATE or UPDATED_ACTUAL_END_DATE
        defaultTaskInstanceShouldBeFound("actualEndDate.in=" + DEFAULT_ACTUAL_END_DATE + "," + UPDATED_ACTUAL_END_DATE);

        // Get all the taskInstanceList where actualEndDate equals to UPDATED_ACTUAL_END_DATE
        defaultTaskInstanceShouldNotBeFound("actualEndDate.in=" + UPDATED_ACTUAL_END_DATE);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByActualEndDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where actualEndDate is not null
        defaultTaskInstanceShouldBeFound("actualEndDate.specified=true");

        // Get all the taskInstanceList where actualEndDate is null
        defaultTaskInstanceShouldNotBeFound("actualEndDate.specified=false");
    }

    @Test
    @Transactional
    void getAllTaskInstancesByTimeSpentIsEqualToSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where timeSpent equals to DEFAULT_TIME_SPENT
        defaultTaskInstanceShouldBeFound("timeSpent.equals=" + DEFAULT_TIME_SPENT);

        // Get all the taskInstanceList where timeSpent equals to UPDATED_TIME_SPENT
        defaultTaskInstanceShouldNotBeFound("timeSpent.equals=" + UPDATED_TIME_SPENT);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByTimeSpentIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where timeSpent not equals to DEFAULT_TIME_SPENT
        defaultTaskInstanceShouldNotBeFound("timeSpent.notEquals=" + DEFAULT_TIME_SPENT);

        // Get all the taskInstanceList where timeSpent not equals to UPDATED_TIME_SPENT
        defaultTaskInstanceShouldBeFound("timeSpent.notEquals=" + UPDATED_TIME_SPENT);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByTimeSpentIsInShouldWork() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where timeSpent in DEFAULT_TIME_SPENT or UPDATED_TIME_SPENT
        defaultTaskInstanceShouldBeFound("timeSpent.in=" + DEFAULT_TIME_SPENT + "," + UPDATED_TIME_SPENT);

        // Get all the taskInstanceList where timeSpent equals to UPDATED_TIME_SPENT
        defaultTaskInstanceShouldNotBeFound("timeSpent.in=" + UPDATED_TIME_SPENT);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByTimeSpentIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where timeSpent is not null
        defaultTaskInstanceShouldBeFound("timeSpent.specified=true");

        // Get all the taskInstanceList where timeSpent is null
        defaultTaskInstanceShouldNotBeFound("timeSpent.specified=false");
    }

    @Test
    @Transactional
    void getAllTaskInstancesByTimeSpentIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where timeSpent is greater than or equal to DEFAULT_TIME_SPENT
        defaultTaskInstanceShouldBeFound("timeSpent.greaterThanOrEqual=" + DEFAULT_TIME_SPENT);

        // Get all the taskInstanceList where timeSpent is greater than or equal to UPDATED_TIME_SPENT
        defaultTaskInstanceShouldNotBeFound("timeSpent.greaterThanOrEqual=" + UPDATED_TIME_SPENT);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByTimeSpentIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where timeSpent is less than or equal to DEFAULT_TIME_SPENT
        defaultTaskInstanceShouldBeFound("timeSpent.lessThanOrEqual=" + DEFAULT_TIME_SPENT);

        // Get all the taskInstanceList where timeSpent is less than or equal to SMALLER_TIME_SPENT
        defaultTaskInstanceShouldNotBeFound("timeSpent.lessThanOrEqual=" + SMALLER_TIME_SPENT);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByTimeSpentIsLessThanSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where timeSpent is less than DEFAULT_TIME_SPENT
        defaultTaskInstanceShouldNotBeFound("timeSpent.lessThan=" + DEFAULT_TIME_SPENT);

        // Get all the taskInstanceList where timeSpent is less than UPDATED_TIME_SPENT
        defaultTaskInstanceShouldBeFound("timeSpent.lessThan=" + UPDATED_TIME_SPENT);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByTimeSpentIsGreaterThanSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where timeSpent is greater than DEFAULT_TIME_SPENT
        defaultTaskInstanceShouldNotBeFound("timeSpent.greaterThan=" + DEFAULT_TIME_SPENT);

        // Get all the taskInstanceList where timeSpent is greater than SMALLER_TIME_SPENT
        defaultTaskInstanceShouldBeFound("timeSpent.greaterThan=" + SMALLER_TIME_SPENT);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where active equals to DEFAULT_ACTIVE
        defaultTaskInstanceShouldBeFound("active.equals=" + DEFAULT_ACTIVE);

        // Get all the taskInstanceList where active equals to UPDATED_ACTIVE
        defaultTaskInstanceShouldNotBeFound("active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByActiveIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where active not equals to DEFAULT_ACTIVE
        defaultTaskInstanceShouldNotBeFound("active.notEquals=" + DEFAULT_ACTIVE);

        // Get all the taskInstanceList where active not equals to UPDATED_ACTIVE
        defaultTaskInstanceShouldBeFound("active.notEquals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where active in DEFAULT_ACTIVE or UPDATED_ACTIVE
        defaultTaskInstanceShouldBeFound("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE);

        // Get all the taskInstanceList where active equals to UPDATED_ACTIVE
        defaultTaskInstanceShouldNotBeFound("active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where active is not null
        defaultTaskInstanceShouldBeFound("active.specified=true");

        // Get all the taskInstanceList where active is null
        defaultTaskInstanceShouldNotBeFound("active.specified=false");
    }

    @Test
    @Transactional
    void getAllTaskInstancesByActiveContainsSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where active contains DEFAULT_ACTIVE
        defaultTaskInstanceShouldBeFound("active.contains=" + DEFAULT_ACTIVE);

        // Get all the taskInstanceList where active contains UPDATED_ACTIVE
        defaultTaskInstanceShouldNotBeFound("active.contains=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByActiveNotContainsSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where active does not contain DEFAULT_ACTIVE
        defaultTaskInstanceShouldNotBeFound("active.doesNotContain=" + DEFAULT_ACTIVE);

        // Get all the taskInstanceList where active does not contain UPDATED_ACTIVE
        defaultTaskInstanceShouldBeFound("active.doesNotContain=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByVersionIsEqualToSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where version equals to DEFAULT_VERSION
        defaultTaskInstanceShouldBeFound("version.equals=" + DEFAULT_VERSION);

        // Get all the taskInstanceList where version equals to UPDATED_VERSION
        defaultTaskInstanceShouldNotBeFound("version.equals=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByVersionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where version not equals to DEFAULT_VERSION
        defaultTaskInstanceShouldNotBeFound("version.notEquals=" + DEFAULT_VERSION);

        // Get all the taskInstanceList where version not equals to UPDATED_VERSION
        defaultTaskInstanceShouldBeFound("version.notEquals=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByVersionIsInShouldWork() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where version in DEFAULT_VERSION or UPDATED_VERSION
        defaultTaskInstanceShouldBeFound("version.in=" + DEFAULT_VERSION + "," + UPDATED_VERSION);

        // Get all the taskInstanceList where version equals to UPDATED_VERSION
        defaultTaskInstanceShouldNotBeFound("version.in=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByVersionIsNullOrNotNull() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where version is not null
        defaultTaskInstanceShouldBeFound("version.specified=true");

        // Get all the taskInstanceList where version is null
        defaultTaskInstanceShouldNotBeFound("version.specified=false");
    }

    @Test
    @Transactional
    void getAllTaskInstancesByVersionIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where version is greater than or equal to DEFAULT_VERSION
        defaultTaskInstanceShouldBeFound("version.greaterThanOrEqual=" + DEFAULT_VERSION);

        // Get all the taskInstanceList where version is greater than or equal to UPDATED_VERSION
        defaultTaskInstanceShouldNotBeFound("version.greaterThanOrEqual=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByVersionIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where version is less than or equal to DEFAULT_VERSION
        defaultTaskInstanceShouldBeFound("version.lessThanOrEqual=" + DEFAULT_VERSION);

        // Get all the taskInstanceList where version is less than or equal to SMALLER_VERSION
        defaultTaskInstanceShouldNotBeFound("version.lessThanOrEqual=" + SMALLER_VERSION);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByVersionIsLessThanSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where version is less than DEFAULT_VERSION
        defaultTaskInstanceShouldNotBeFound("version.lessThan=" + DEFAULT_VERSION);

        // Get all the taskInstanceList where version is less than UPDATED_VERSION
        defaultTaskInstanceShouldBeFound("version.lessThan=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByVersionIsGreaterThanSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        // Get all the taskInstanceList where version is greater than DEFAULT_VERSION
        defaultTaskInstanceShouldNotBeFound("version.greaterThan=" + DEFAULT_VERSION);

        // Get all the taskInstanceList where version is greater than SMALLER_VERSION
        defaultTaskInstanceShouldBeFound("version.greaterThan=" + SMALLER_VERSION);
    }

    @Test
    @Transactional
    void getAllTaskInstancesByTaskIsEqualToSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);
        Task task = TaskResourceIT.createEntity(em);
        em.persist(task);
        em.flush();
        taskInstance.setTask(task);
        taskInstanceRepository.saveAndFlush(taskInstance);
        Long taskId = task.getId();

        // Get all the taskInstanceList where task equals to taskId
        defaultTaskInstanceShouldBeFound("taskId.equals=" + taskId);

        // Get all the taskInstanceList where task equals to (taskId + 1)
        defaultTaskInstanceShouldNotBeFound("taskId.equals=" + (taskId + 1));
    }

    @Test
    @Transactional
    void getAllTaskInstancesByTodoIsEqualToSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);
        Todo todo = TodoResourceIT.createEntity(em);
        em.persist(todo);
        em.flush();
        taskInstance.setTodo(todo);
        taskInstanceRepository.saveAndFlush(taskInstance);
        Long todoId = todo.getId();

        // Get all the taskInstanceList where todo equals to todoId
        defaultTaskInstanceShouldBeFound("todoId.equals=" + todoId);

        // Get all the taskInstanceList where todo equals to (todoId + 1)
        defaultTaskInstanceShouldNotBeFound("todoId.equals=" + (todoId + 1));
    }

    @Test
    @Transactional
    void getAllTaskInstancesByUserIsEqualToSomething() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        taskInstance.setUser(user);
        taskInstanceRepository.saveAndFlush(taskInstance);
        String userId = user.getId();

        // Get all the taskInstanceList where user equals to userId
        defaultTaskInstanceShouldBeFound("userId.equals=" + userId);

        // Get all the taskInstanceList where user equals to "invalid-id"
        defaultTaskInstanceShouldNotBeFound("userId.equals=" + "invalid-id");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTaskInstanceShouldBeFound(String filter) throws Exception {
        restTaskInstanceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(taskInstance.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].plannedStartDate").value(hasItem(DEFAULT_PLANNED_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].plannedEndDate").value(hasItem(DEFAULT_PLANNED_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].actualStartDate").value(hasItem(DEFAULT_ACTUAL_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].actualEndDate").value(hasItem(DEFAULT_ACTUAL_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].timeSpent").value(hasItem(DEFAULT_TIME_SPENT.doubleValue())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)));

        // Check, that the count call also returns 1
        restTaskInstanceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTaskInstanceShouldNotBeFound(String filter) throws Exception {
        restTaskInstanceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTaskInstanceMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTaskInstance() throws Exception {
        // Get the taskInstance
        restTaskInstanceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTaskInstance() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        int databaseSizeBeforeUpdate = taskInstanceRepository.findAll().size();

        // Update the taskInstance
        TaskInstance updatedTaskInstance = taskInstanceRepository.findById(taskInstance.getId()).get();
        // Disconnect from session so that the updates on updatedTaskInstance are not directly saved in db
        em.detach(updatedTaskInstance);
        updatedTaskInstance
            .name(UPDATED_NAME)
            .type(UPDATED_TYPE)
            .desc(UPDATED_DESC)
            .status(UPDATED_STATUS)
            .plannedStartDate(UPDATED_PLANNED_START_DATE)
            .plannedEndDate(UPDATED_PLANNED_END_DATE)
            .actualStartDate(UPDATED_ACTUAL_START_DATE)
            .actualEndDate(UPDATED_ACTUAL_END_DATE)
            .timeSpent(UPDATED_TIME_SPENT)
            .active(UPDATED_ACTIVE)
            .version(UPDATED_VERSION);
        TaskInstanceDTO taskInstanceDTO = taskInstanceMapper.toDto(updatedTaskInstance);

        restTaskInstanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, taskInstanceDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(taskInstanceDTO))
            )
            .andExpect(status().isOk());

        // Validate the TaskInstance in the database
        List<TaskInstance> taskInstanceList = taskInstanceRepository.findAll();
        assertThat(taskInstanceList).hasSize(databaseSizeBeforeUpdate);
        TaskInstance testTaskInstance = taskInstanceList.get(taskInstanceList.size() - 1);
        assertThat(testTaskInstance.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTaskInstance.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testTaskInstance.getDesc()).isEqualTo(UPDATED_DESC);
        assertThat(testTaskInstance.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testTaskInstance.getPlannedStartDate()).isEqualTo(UPDATED_PLANNED_START_DATE);
        assertThat(testTaskInstance.getPlannedEndDate()).isEqualTo(UPDATED_PLANNED_END_DATE);
        assertThat(testTaskInstance.getActualStartDate()).isEqualTo(UPDATED_ACTUAL_START_DATE);
        assertThat(testTaskInstance.getActualEndDate()).isEqualTo(UPDATED_ACTUAL_END_DATE);
        assertThat(testTaskInstance.getTimeSpent()).isEqualTo(UPDATED_TIME_SPENT);
        assertThat(testTaskInstance.getActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testTaskInstance.getVersion()).isEqualTo(UPDATED_VERSION);
    }

    @Test
    @Transactional
    void putNonExistingTaskInstance() throws Exception {
        int databaseSizeBeforeUpdate = taskInstanceRepository.findAll().size();
        taskInstance.setId(count.incrementAndGet());

        // Create the TaskInstance
        TaskInstanceDTO taskInstanceDTO = taskInstanceMapper.toDto(taskInstance);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaskInstanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, taskInstanceDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(taskInstanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskInstance in the database
        List<TaskInstance> taskInstanceList = taskInstanceRepository.findAll();
        assertThat(taskInstanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTaskInstance() throws Exception {
        int databaseSizeBeforeUpdate = taskInstanceRepository.findAll().size();
        taskInstance.setId(count.incrementAndGet());

        // Create the TaskInstance
        TaskInstanceDTO taskInstanceDTO = taskInstanceMapper.toDto(taskInstance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskInstanceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(taskInstanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskInstance in the database
        List<TaskInstance> taskInstanceList = taskInstanceRepository.findAll();
        assertThat(taskInstanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTaskInstance() throws Exception {
        int databaseSizeBeforeUpdate = taskInstanceRepository.findAll().size();
        taskInstance.setId(count.incrementAndGet());

        // Create the TaskInstance
        TaskInstanceDTO taskInstanceDTO = taskInstanceMapper.toDto(taskInstance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskInstanceMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(taskInstanceDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TaskInstance in the database
        List<TaskInstance> taskInstanceList = taskInstanceRepository.findAll();
        assertThat(taskInstanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTaskInstanceWithPatch() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        int databaseSizeBeforeUpdate = taskInstanceRepository.findAll().size();

        // Update the taskInstance using partial update
        TaskInstance partialUpdatedTaskInstance = new TaskInstance();
        partialUpdatedTaskInstance.setId(taskInstance.getId());

        partialUpdatedTaskInstance
            .name(UPDATED_NAME)
            .type(UPDATED_TYPE)
            .desc(UPDATED_DESC)
            .plannedStartDate(UPDATED_PLANNED_START_DATE)
            .actualStartDate(UPDATED_ACTUAL_START_DATE)
            .actualEndDate(UPDATED_ACTUAL_END_DATE)
            .timeSpent(UPDATED_TIME_SPENT);

        restTaskInstanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTaskInstance.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTaskInstance))
            )
            .andExpect(status().isOk());

        // Validate the TaskInstance in the database
        List<TaskInstance> taskInstanceList = taskInstanceRepository.findAll();
        assertThat(taskInstanceList).hasSize(databaseSizeBeforeUpdate);
        TaskInstance testTaskInstance = taskInstanceList.get(taskInstanceList.size() - 1);
        assertThat(testTaskInstance.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTaskInstance.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testTaskInstance.getDesc()).isEqualTo(UPDATED_DESC);
        assertThat(testTaskInstance.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testTaskInstance.getPlannedStartDate()).isEqualTo(UPDATED_PLANNED_START_DATE);
        assertThat(testTaskInstance.getPlannedEndDate()).isEqualTo(DEFAULT_PLANNED_END_DATE);
        assertThat(testTaskInstance.getActualStartDate()).isEqualTo(UPDATED_ACTUAL_START_DATE);
        assertThat(testTaskInstance.getActualEndDate()).isEqualTo(UPDATED_ACTUAL_END_DATE);
        assertThat(testTaskInstance.getTimeSpent()).isEqualTo(UPDATED_TIME_SPENT);
        assertThat(testTaskInstance.getActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testTaskInstance.getVersion()).isEqualTo(DEFAULT_VERSION);
    }

    @Test
    @Transactional
    void fullUpdateTaskInstanceWithPatch() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        int databaseSizeBeforeUpdate = taskInstanceRepository.findAll().size();

        // Update the taskInstance using partial update
        TaskInstance partialUpdatedTaskInstance = new TaskInstance();
        partialUpdatedTaskInstance.setId(taskInstance.getId());

        partialUpdatedTaskInstance
            .name(UPDATED_NAME)
            .type(UPDATED_TYPE)
            .desc(UPDATED_DESC)
            .status(UPDATED_STATUS)
            .plannedStartDate(UPDATED_PLANNED_START_DATE)
            .plannedEndDate(UPDATED_PLANNED_END_DATE)
            .actualStartDate(UPDATED_ACTUAL_START_DATE)
            .actualEndDate(UPDATED_ACTUAL_END_DATE)
            .timeSpent(UPDATED_TIME_SPENT)
            .active(UPDATED_ACTIVE)
            .version(UPDATED_VERSION);

        restTaskInstanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTaskInstance.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTaskInstance))
            )
            .andExpect(status().isOk());

        // Validate the TaskInstance in the database
        List<TaskInstance> taskInstanceList = taskInstanceRepository.findAll();
        assertThat(taskInstanceList).hasSize(databaseSizeBeforeUpdate);
        TaskInstance testTaskInstance = taskInstanceList.get(taskInstanceList.size() - 1);
        assertThat(testTaskInstance.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTaskInstance.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testTaskInstance.getDesc()).isEqualTo(UPDATED_DESC);
        assertThat(testTaskInstance.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testTaskInstance.getPlannedStartDate()).isEqualTo(UPDATED_PLANNED_START_DATE);
        assertThat(testTaskInstance.getPlannedEndDate()).isEqualTo(UPDATED_PLANNED_END_DATE);
        assertThat(testTaskInstance.getActualStartDate()).isEqualTo(UPDATED_ACTUAL_START_DATE);
        assertThat(testTaskInstance.getActualEndDate()).isEqualTo(UPDATED_ACTUAL_END_DATE);
        assertThat(testTaskInstance.getTimeSpent()).isEqualTo(UPDATED_TIME_SPENT);
        assertThat(testTaskInstance.getActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testTaskInstance.getVersion()).isEqualTo(UPDATED_VERSION);
    }

    @Test
    @Transactional
    void patchNonExistingTaskInstance() throws Exception {
        int databaseSizeBeforeUpdate = taskInstanceRepository.findAll().size();
        taskInstance.setId(count.incrementAndGet());

        // Create the TaskInstance
        TaskInstanceDTO taskInstanceDTO = taskInstanceMapper.toDto(taskInstance);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaskInstanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, taskInstanceDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(taskInstanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskInstance in the database
        List<TaskInstance> taskInstanceList = taskInstanceRepository.findAll();
        assertThat(taskInstanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTaskInstance() throws Exception {
        int databaseSizeBeforeUpdate = taskInstanceRepository.findAll().size();
        taskInstance.setId(count.incrementAndGet());

        // Create the TaskInstance
        TaskInstanceDTO taskInstanceDTO = taskInstanceMapper.toDto(taskInstance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskInstanceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(taskInstanceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TaskInstance in the database
        List<TaskInstance> taskInstanceList = taskInstanceRepository.findAll();
        assertThat(taskInstanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTaskInstance() throws Exception {
        int databaseSizeBeforeUpdate = taskInstanceRepository.findAll().size();
        taskInstance.setId(count.incrementAndGet());

        // Create the TaskInstance
        TaskInstanceDTO taskInstanceDTO = taskInstanceMapper.toDto(taskInstance);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTaskInstanceMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(taskInstanceDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TaskInstance in the database
        List<TaskInstance> taskInstanceList = taskInstanceRepository.findAll();
        assertThat(taskInstanceList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTaskInstance() throws Exception {
        // Initialize the database
        taskInstanceRepository.saveAndFlush(taskInstance);

        int databaseSizeBeforeDelete = taskInstanceRepository.findAll().size();

        // Delete the taskInstance
        restTaskInstanceMockMvc
            .perform(delete(ENTITY_API_URL_ID, taskInstance.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TaskInstance> taskInstanceList = taskInstanceRepository.findAll();
        assertThat(taskInstanceList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
