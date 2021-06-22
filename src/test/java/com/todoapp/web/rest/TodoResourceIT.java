package com.todoapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
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
import com.todoapp.domain.User;
import com.todoapp.repository.TaskInstanceRepository;
import com.todoapp.repository.TodoRepository;
import com.todoapp.service.dto.BulkAssignDTO;
import com.todoapp.service.dto.TodoDTO;
import com.todoapp.service.mapper.TodoMapper;
import java.util.Arrays;
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
 * Integration tests for the {@link TodoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TodoResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESC = "AAAAAAAAAA";
    private static final String UPDATED_DESC = "BBBBBBBBBB";

    private static final String DEFAULT_ACTIVE = "AAAAAAAAAA";
    private static final String UPDATED_ACTIVE = "BBBBBBBBBB";

    private static final Integer DEFAULT_VERSION = 1;
    private static final Integer UPDATED_VERSION = 2;
    private static final Integer SMALLER_VERSION = 1 - 1;

    private static final String ENTITY_API_URL = "/api/todos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private TaskInstanceRepository taskInstanceRepository;

    @Autowired
    private TodoMapper todoMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTodoMockMvc;

    private Todo todo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Todo createEntity(EntityManager em) {
        Todo todo = new Todo().name(DEFAULT_NAME).desc(DEFAULT_DESC).active(DEFAULT_ACTIVE).version(DEFAULT_VERSION);
        return todo;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Todo createUpdatedEntity(EntityManager em) {
        Todo todo = new Todo().name(UPDATED_NAME).desc(UPDATED_DESC).active(UPDATED_ACTIVE).version(UPDATED_VERSION);
        return todo;
    }

    @BeforeEach
    public void initTest() {
        todo = createEntity(em);
    }

    @Test
    @Transactional
    void createTodo() throws Exception {
        int databaseSizeBeforeCreate = todoRepository.findAll().size();
        // Create the Todo
        TodoDTO todoDTO = todoMapper.toDto(todo);
        restTodoMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(todoDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Todo in the database
        List<Todo> todoList = todoRepository.findAll();
        assertThat(todoList).hasSize(databaseSizeBeforeCreate + 1);
        Todo testTodo = todoList.get(todoList.size() - 1);
        assertThat(testTodo.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTodo.getDesc()).isEqualTo(DEFAULT_DESC);
        assertThat(testTodo.getActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testTodo.getVersion()).isEqualTo(DEFAULT_VERSION);
    }

    @Test
    @Transactional
    void createTodoWithExistingId() throws Exception {
        // Create the Todo with an existing ID
        todo.setId(1L);
        TodoDTO todoDTO = todoMapper.toDto(todo);

        int databaseSizeBeforeCreate = todoRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTodoMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(todoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Todo in the database
        List<Todo> todoList = todoRepository.findAll();
        assertThat(todoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllTodos() throws Exception {
        // Initialize the database
        todoRepository.saveAndFlush(todo);

        // Get all the todoList
        restTodoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(todo.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)));
    }

    @Test
    @Transactional
    void getTodo() throws Exception {
        // Initialize the database
        todoRepository.saveAndFlush(todo);

        // Get the todo
        restTodoMockMvc
            .perform(get(ENTITY_API_URL_ID, todo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(todo.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.desc").value(DEFAULT_DESC))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE))
            .andExpect(jsonPath("$.version").value(DEFAULT_VERSION));
    }

    @Test
    @Transactional
    void testAssignTodoToUsers() throws Exception {
        // Initialize the database
        // todoRepository.saveAndFlush(todo);

        BulkAssignDTO t = new BulkAssignDTO();
        t.setTodoId(1L);
        List<String> emails = Arrays.asList("user2", "user3", "user4");
        t.setEmailAddresses(emails);

        // Get the todo
        restTodoMockMvc
            .perform(
                put(ENTITY_API_URL + "/assign")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(t))
            )
            .andReturn();

        int taskInstanceSizeAfterAssign = taskInstanceRepository.findAll().size();

        // 2 users * 6 tasks = 12 task instances
        assertThat(taskInstanceSizeAfterAssign).isEqualTo(18);

        // Try to assign again , the API should return an error
        // Get the todo
        restTodoMockMvc
            .perform(
                put(ENTITY_API_URL + "/assign")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(t))
            )
            .andExpect(jsonPath("$.errorKey").value("todoAlreadyAssigned"));

        // Get the task instances after asisgnment
        restTodoMockMvc
        .perform(get(ENTITY_API_URL_ID, 1L)).andDo(
            k-> {
                System.out.println(k.getResponse().getContentAsString());
            }
        );

    }

    @Test
    @Transactional
    void getTodosByIdFiltering() throws Exception {
        // Initialize the database
        todoRepository.saveAndFlush(todo);

        Long id = todo.getId();

        defaultTodoShouldBeFound("id.equals=" + id);
        defaultTodoShouldNotBeFound("id.notEquals=" + id);

        defaultTodoShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTodoShouldNotBeFound("id.greaterThan=" + id);

        defaultTodoShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTodoShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTodosByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        todoRepository.saveAndFlush(todo);

        // Get all the todoList where name equals to DEFAULT_NAME
        defaultTodoShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the todoList where name equals to UPDATED_NAME
        defaultTodoShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTodosByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        todoRepository.saveAndFlush(todo);

        // Get all the todoList where name not equals to DEFAULT_NAME
        defaultTodoShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the todoList where name not equals to UPDATED_NAME
        defaultTodoShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTodosByNameIsInShouldWork() throws Exception {
        // Initialize the database
        todoRepository.saveAndFlush(todo);

        // Get all the todoList where name in DEFAULT_NAME or UPDATED_NAME
        defaultTodoShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the todoList where name equals to UPDATED_NAME
        defaultTodoShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTodosByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        todoRepository.saveAndFlush(todo);

        // Get all the todoList where name is not null
        defaultTodoShouldBeFound("name.specified=true");

        // Get all the todoList where name is null
        defaultTodoShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllTodosByNameContainsSomething() throws Exception {
        // Initialize the database
        todoRepository.saveAndFlush(todo);

        // Get all the todoList where name contains DEFAULT_NAME
        defaultTodoShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the todoList where name contains UPDATED_NAME
        defaultTodoShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTodosByNameNotContainsSomething() throws Exception {
        // Initialize the database
        todoRepository.saveAndFlush(todo);

        // Get all the todoList where name does not contain DEFAULT_NAME
        defaultTodoShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the todoList where name does not contain UPDATED_NAME
        defaultTodoShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTodosByDescIsEqualToSomething() throws Exception {
        // Initialize the database
        todoRepository.saveAndFlush(todo);

        // Get all the todoList where desc equals to DEFAULT_DESC
        defaultTodoShouldBeFound("desc.equals=" + DEFAULT_DESC);

        // Get all the todoList where desc equals to UPDATED_DESC
        defaultTodoShouldNotBeFound("desc.equals=" + UPDATED_DESC);
    }

    @Test
    @Transactional
    void getAllTodosByDescIsNotEqualToSomething() throws Exception {
        // Initialize the database
        todoRepository.saveAndFlush(todo);

        // Get all the todoList where desc not equals to DEFAULT_DESC
        defaultTodoShouldNotBeFound("desc.notEquals=" + DEFAULT_DESC);

        // Get all the todoList where desc not equals to UPDATED_DESC
        defaultTodoShouldBeFound("desc.notEquals=" + UPDATED_DESC);
    }

    @Test
    @Transactional
    void getAllTodosByDescIsInShouldWork() throws Exception {
        // Initialize the database
        todoRepository.saveAndFlush(todo);

        // Get all the todoList where desc in DEFAULT_DESC or UPDATED_DESC
        defaultTodoShouldBeFound("desc.in=" + DEFAULT_DESC + "," + UPDATED_DESC);

        // Get all the todoList where desc equals to UPDATED_DESC
        defaultTodoShouldNotBeFound("desc.in=" + UPDATED_DESC);
    }

    @Test
    @Transactional
    void getAllTodosByDescIsNullOrNotNull() throws Exception {
        // Initialize the database
        todoRepository.saveAndFlush(todo);

        // Get all the todoList where desc is not null
        defaultTodoShouldBeFound("desc.specified=true");

        // Get all the todoList where desc is null
        defaultTodoShouldNotBeFound("desc.specified=false");
    }

    @Test
    @Transactional
    void getAllTodosByDescContainsSomething() throws Exception {
        // Initialize the database
        todoRepository.saveAndFlush(todo);

        // Get all the todoList where desc contains DEFAULT_DESC
        defaultTodoShouldBeFound("desc.contains=" + DEFAULT_DESC);

        // Get all the todoList where desc contains UPDATED_DESC
        defaultTodoShouldNotBeFound("desc.contains=" + UPDATED_DESC);
    }

    @Test
    @Transactional
    void getAllTodosByDescNotContainsSomething() throws Exception {
        // Initialize the database
        todoRepository.saveAndFlush(todo);

        // Get all the todoList where desc does not contain DEFAULT_DESC
        defaultTodoShouldNotBeFound("desc.doesNotContain=" + DEFAULT_DESC);

        // Get all the todoList where desc does not contain UPDATED_DESC
        defaultTodoShouldBeFound("desc.doesNotContain=" + UPDATED_DESC);
    }

    @Test
    @Transactional
    void getAllTodosByActiveIsEqualToSomething() throws Exception {
        // Initialize the database
        todoRepository.saveAndFlush(todo);

        // Get all the todoList where active equals to DEFAULT_ACTIVE
        defaultTodoShouldBeFound("active.equals=" + DEFAULT_ACTIVE);

        // Get all the todoList where active equals to UPDATED_ACTIVE
        defaultTodoShouldNotBeFound("active.equals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllTodosByActiveIsNotEqualToSomething() throws Exception {
        // Initialize the database
        todoRepository.saveAndFlush(todo);

        // Get all the todoList where active not equals to DEFAULT_ACTIVE
        defaultTodoShouldNotBeFound("active.notEquals=" + DEFAULT_ACTIVE);

        // Get all the todoList where active not equals to UPDATED_ACTIVE
        defaultTodoShouldBeFound("active.notEquals=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllTodosByActiveIsInShouldWork() throws Exception {
        // Initialize the database
        todoRepository.saveAndFlush(todo);

        // Get all the todoList where active in DEFAULT_ACTIVE or UPDATED_ACTIVE
        defaultTodoShouldBeFound("active.in=" + DEFAULT_ACTIVE + "," + UPDATED_ACTIVE);

        // Get all the todoList where active equals to UPDATED_ACTIVE
        defaultTodoShouldNotBeFound("active.in=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllTodosByActiveIsNullOrNotNull() throws Exception {
        // Initialize the database
        todoRepository.saveAndFlush(todo);

        // Get all the todoList where active is not null
        defaultTodoShouldBeFound("active.specified=true");

        // Get all the todoList where active is null
        defaultTodoShouldNotBeFound("active.specified=false");
    }

    @Test
    @Transactional
    void getAllTodosByActiveContainsSomething() throws Exception {
        // Initialize the database
        todoRepository.saveAndFlush(todo);

        // Get all the todoList where active contains DEFAULT_ACTIVE
        defaultTodoShouldBeFound("active.contains=" + DEFAULT_ACTIVE);

        // Get all the todoList where active contains UPDATED_ACTIVE
        defaultTodoShouldNotBeFound("active.contains=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllTodosByActiveNotContainsSomething() throws Exception {
        // Initialize the database
        todoRepository.saveAndFlush(todo);

        // Get all the todoList where active does not contain DEFAULT_ACTIVE
        defaultTodoShouldNotBeFound("active.doesNotContain=" + DEFAULT_ACTIVE);

        // Get all the todoList where active does not contain UPDATED_ACTIVE
        defaultTodoShouldBeFound("active.doesNotContain=" + UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void getAllTodosByVersionIsEqualToSomething() throws Exception {
        // Initialize the database
        todoRepository.saveAndFlush(todo);

        // Get all the todoList where version equals to DEFAULT_VERSION
        defaultTodoShouldBeFound("version.equals=" + DEFAULT_VERSION);

        // Get all the todoList where version equals to UPDATED_VERSION
        defaultTodoShouldNotBeFound("version.equals=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllTodosByVersionIsNotEqualToSomething() throws Exception {
        // Initialize the database
        todoRepository.saveAndFlush(todo);

        // Get all the todoList where version not equals to DEFAULT_VERSION
        defaultTodoShouldNotBeFound("version.notEquals=" + DEFAULT_VERSION);

        // Get all the todoList where version not equals to UPDATED_VERSION
        defaultTodoShouldBeFound("version.notEquals=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllTodosByVersionIsInShouldWork() throws Exception {
        // Initialize the database
        todoRepository.saveAndFlush(todo);

        // Get all the todoList where version in DEFAULT_VERSION or UPDATED_VERSION
        defaultTodoShouldBeFound("version.in=" + DEFAULT_VERSION + "," + UPDATED_VERSION);

        // Get all the todoList where version equals to UPDATED_VERSION
        defaultTodoShouldNotBeFound("version.in=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllTodosByVersionIsNullOrNotNull() throws Exception {
        // Initialize the database
        todoRepository.saveAndFlush(todo);

        // Get all the todoList where version is not null
        defaultTodoShouldBeFound("version.specified=true");

        // Get all the todoList where version is null
        defaultTodoShouldNotBeFound("version.specified=false");
    }

    @Test
    @Transactional
    void getAllTodosByVersionIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        todoRepository.saveAndFlush(todo);

        // Get all the todoList where version is greater than or equal to DEFAULT_VERSION
        defaultTodoShouldBeFound("version.greaterThanOrEqual=" + DEFAULT_VERSION);

        // Get all the todoList where version is greater than or equal to UPDATED_VERSION
        defaultTodoShouldNotBeFound("version.greaterThanOrEqual=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllTodosByVersionIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        todoRepository.saveAndFlush(todo);

        // Get all the todoList where version is less than or equal to DEFAULT_VERSION
        defaultTodoShouldBeFound("version.lessThanOrEqual=" + DEFAULT_VERSION);

        // Get all the todoList where version is less than or equal to SMALLER_VERSION
        defaultTodoShouldNotBeFound("version.lessThanOrEqual=" + SMALLER_VERSION);
    }

    @Test
    @Transactional
    void getAllTodosByVersionIsLessThanSomething() throws Exception {
        // Initialize the database
        todoRepository.saveAndFlush(todo);

        // Get all the todoList where version is less than DEFAULT_VERSION
        defaultTodoShouldNotBeFound("version.lessThan=" + DEFAULT_VERSION);

        // Get all the todoList where version is less than UPDATED_VERSION
        defaultTodoShouldBeFound("version.lessThan=" + UPDATED_VERSION);
    }

    @Test
    @Transactional
    void getAllTodosByVersionIsGreaterThanSomething() throws Exception {
        // Initialize the database
        todoRepository.saveAndFlush(todo);

        // Get all the todoList where version is greater than DEFAULT_VERSION
        defaultTodoShouldNotBeFound("version.greaterThan=" + DEFAULT_VERSION);

        // Get all the todoList where version is greater than SMALLER_VERSION
        defaultTodoShouldBeFound("version.greaterThan=" + SMALLER_VERSION);
    }

    @Test
    @Transactional
    void getAllTodosByTaskIsEqualToSomething() throws Exception {
        // Initialize the database
        todoRepository.saveAndFlush(todo);
        Task task = TaskResourceIT.createEntity(em);
        em.persist(task);
        em.flush();
        todo.addTask(task);
        todoRepository.saveAndFlush(todo);
        Long taskId = task.getId();

        // Get all the todoList where task equals to taskId
        defaultTodoShouldBeFound("taskId.equals=" + taskId);

        // Get all the todoList where task equals to (taskId + 1)
        defaultTodoShouldNotBeFound("taskId.equals=" + (taskId + 1));
    }

    @Test
    @Transactional
    void getAllTodosByTaskInstanceIsEqualToSomething() throws Exception {
        // Initialize the database
        todoRepository.saveAndFlush(todo);
        TaskInstance taskInstance = TaskInstanceResourceIT.createEntity(em);
        em.persist(taskInstance);
        em.flush();
        todo.addTaskInstance(taskInstance);
        todoRepository.saveAndFlush(todo);
        Long taskInstanceId = taskInstance.getId();

        // Get all the todoList where taskInstance equals to taskInstanceId
        defaultTodoShouldBeFound("taskInstanceId.equals=" + taskInstanceId);

        // Get all the todoList where taskInstance equals to (taskInstanceId + 1)
        defaultTodoShouldNotBeFound("taskInstanceId.equals=" + (taskInstanceId + 1));
    }

    @Test
    @Transactional
    void getAllTodosByUserIsEqualToSomething() throws Exception {
        // Initialize the database
        todoRepository.saveAndFlush(todo);
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        todo.setUser(user);
        todoRepository.saveAndFlush(todo);
        String userId = user.getId();

        // Get all the todoList where user equals to userId
        defaultTodoShouldBeFound("userId.equals=" + userId);

        // Get all the todoList where user equals to "invalid-id"
        defaultTodoShouldNotBeFound("userId.equals=" + "invalid-id");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTodoShouldBeFound(String filter) throws Exception {
        restTodoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(todo.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].version").value(hasItem(DEFAULT_VERSION)));

        // Check, that the count call also returns 1
        restTodoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTodoShouldNotBeFound(String filter) throws Exception {
        restTodoMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTodoMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTodo() throws Exception {
        // Get the todo
        restTodoMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTodo() throws Exception {
        // Initialize the database
        todoRepository.saveAndFlush(todo);

        int databaseSizeBeforeUpdate = todoRepository.findAll().size();

        // Update the todo
        Todo updatedTodo = todoRepository.findById(todo.getId()).get();
        // Disconnect from session so that the updates on updatedTodo are not directly saved in db
        em.detach(updatedTodo);
        updatedTodo.name(UPDATED_NAME).desc(UPDATED_DESC).active(UPDATED_ACTIVE).version(UPDATED_VERSION);
        TodoDTO todoDTO = todoMapper.toDto(updatedTodo);

        restTodoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, todoDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(todoDTO))
            )
            .andExpect(status().isOk());

        // Validate the Todo in the database
        List<Todo> todoList = todoRepository.findAll();
        assertThat(todoList).hasSize(databaseSizeBeforeUpdate);
        Todo testTodo = todoList.get(todoList.size() - 1);
        assertThat(testTodo.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTodo.getDesc()).isEqualTo(UPDATED_DESC);
        assertThat(testTodo.getActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testTodo.getVersion()).isEqualTo(UPDATED_VERSION);
    }

    @Test
    @Transactional
    void putNonExistingTodo() throws Exception {
        int databaseSizeBeforeUpdate = todoRepository.findAll().size();
        todo.setId(count.incrementAndGet());

        // Create the Todo
        TodoDTO todoDTO = todoMapper.toDto(todo);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTodoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, todoDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(todoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Todo in the database
        List<Todo> todoList = todoRepository.findAll();
        assertThat(todoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTodo() throws Exception {
        int databaseSizeBeforeUpdate = todoRepository.findAll().size();
        todo.setId(count.incrementAndGet());

        // Create the Todo
        TodoDTO todoDTO = todoMapper.toDto(todo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTodoMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(todoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Todo in the database
        List<Todo> todoList = todoRepository.findAll();
        assertThat(todoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTodo() throws Exception {
        int databaseSizeBeforeUpdate = todoRepository.findAll().size();
        todo.setId(count.incrementAndGet());

        // Create the Todo
        TodoDTO todoDTO = todoMapper.toDto(todo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTodoMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(todoDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Todo in the database
        List<Todo> todoList = todoRepository.findAll();
        assertThat(todoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTodoWithPatch() throws Exception {
        // Initialize the database
        todoRepository.saveAndFlush(todo);

        int databaseSizeBeforeUpdate = todoRepository.findAll().size();

        // Update the todo using partial update
        Todo partialUpdatedTodo = new Todo();
        partialUpdatedTodo.setId(todo.getId());

        partialUpdatedTodo.name(UPDATED_NAME).desc(UPDATED_DESC);

        restTodoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTodo.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTodo))
            )
            .andExpect(status().isOk());

        // Validate the Todo in the database
        List<Todo> todoList = todoRepository.findAll();
        assertThat(todoList).hasSize(databaseSizeBeforeUpdate);
        Todo testTodo = todoList.get(todoList.size() - 1);
        assertThat(testTodo.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTodo.getDesc()).isEqualTo(UPDATED_DESC);
        assertThat(testTodo.getActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testTodo.getVersion()).isEqualTo(DEFAULT_VERSION);
    }

    @Test
    @Transactional
    void fullUpdateTodoWithPatch() throws Exception {
        // Initialize the database
        todoRepository.saveAndFlush(todo);

        int databaseSizeBeforeUpdate = todoRepository.findAll().size();

        // Update the todo using partial update
        Todo partialUpdatedTodo = new Todo();
        partialUpdatedTodo.setId(todo.getId());

        partialUpdatedTodo.name(UPDATED_NAME).desc(UPDATED_DESC).active(UPDATED_ACTIVE).version(UPDATED_VERSION);

        restTodoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTodo.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTodo))
            )
            .andExpect(status().isOk());

        // Validate the Todo in the database
        List<Todo> todoList = todoRepository.findAll();
        assertThat(todoList).hasSize(databaseSizeBeforeUpdate);
        Todo testTodo = todoList.get(todoList.size() - 1);
        assertThat(testTodo.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTodo.getDesc()).isEqualTo(UPDATED_DESC);
        assertThat(testTodo.getActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testTodo.getVersion()).isEqualTo(UPDATED_VERSION);
    }

    @Test
    @Transactional
    void patchNonExistingTodo() throws Exception {
        int databaseSizeBeforeUpdate = todoRepository.findAll().size();
        todo.setId(count.incrementAndGet());

        // Create the Todo
        TodoDTO todoDTO = todoMapper.toDto(todo);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTodoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, todoDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(todoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Todo in the database
        List<Todo> todoList = todoRepository.findAll();
        assertThat(todoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTodo() throws Exception {
        int databaseSizeBeforeUpdate = todoRepository.findAll().size();
        todo.setId(count.incrementAndGet());

        // Create the Todo
        TodoDTO todoDTO = todoMapper.toDto(todo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTodoMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(todoDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Todo in the database
        List<Todo> todoList = todoRepository.findAll();
        assertThat(todoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTodo() throws Exception {
        int databaseSizeBeforeUpdate = todoRepository.findAll().size();
        todo.setId(count.incrementAndGet());

        // Create the Todo
        TodoDTO todoDTO = todoMapper.toDto(todo);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTodoMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(todoDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Todo in the database
        List<Todo> todoList = todoRepository.findAll();
        assertThat(todoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTodo() throws Exception {
        // Initialize the database
        todoRepository.saveAndFlush(todo);

        int databaseSizeBeforeDelete = todoRepository.findAll().size();

        // Delete the todo
        restTodoMockMvc
            .perform(delete(ENTITY_API_URL_ID, todo.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Todo> todoList = todoRepository.findAll();
        assertThat(todoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
