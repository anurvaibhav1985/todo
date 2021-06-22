package com.todoapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.todoapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TaskInstanceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TaskInstance.class);
        TaskInstance taskInstance1 = new TaskInstance();
        taskInstance1.setId(1L);
        TaskInstance taskInstance2 = new TaskInstance();
        taskInstance2.setId(taskInstance1.getId());
        assertThat(taskInstance1).isEqualTo(taskInstance2);
        taskInstance2.setId(2L);
        assertThat(taskInstance1).isNotEqualTo(taskInstance2);
        taskInstance1.setId(null);
        assertThat(taskInstance1).isNotEqualTo(taskInstance2);
    }
}
