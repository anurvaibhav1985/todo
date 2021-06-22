package com.todoapp.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.todoapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TaskInstanceDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TaskInstanceDTO.class);
        TaskInstanceDTO taskInstanceDTO1 = new TaskInstanceDTO();
        taskInstanceDTO1.setId(1L);
        TaskInstanceDTO taskInstanceDTO2 = new TaskInstanceDTO();
        assertThat(taskInstanceDTO1).isNotEqualTo(taskInstanceDTO2);
        taskInstanceDTO2.setId(taskInstanceDTO1.getId());
        assertThat(taskInstanceDTO1).isEqualTo(taskInstanceDTO2);
        taskInstanceDTO2.setId(2L);
        assertThat(taskInstanceDTO1).isNotEqualTo(taskInstanceDTO2);
        taskInstanceDTO1.setId(null);
        assertThat(taskInstanceDTO1).isNotEqualTo(taskInstanceDTO2);
    }
}
