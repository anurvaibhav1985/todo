package com.todoapp.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaskInstanceMapperTest {

    private TaskInstanceMapper taskInstanceMapper;

    @BeforeEach
    public void setUp() {
        taskInstanceMapper = new TaskInstanceMapperImpl();
    }
}
