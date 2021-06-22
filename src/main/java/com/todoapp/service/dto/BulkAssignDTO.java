package com.todoapp.service.dto;

import java.io.Serializable;
import java.util.List;

public class BulkAssignDTO implements Serializable {

    private Long todoId;
    private List<Long> tasks;
    private List<String> emailAddresses;

    public Long getTodoId() {
        return todoId;
    }

    public void setTodoId(Long todoId) {
        this.todoId = todoId;
    }

    public List<Long> getTasks() {
        return tasks;
    }

    public void setTasks(List<Long> tasks) {
        this.tasks = tasks;
    }

    public List<String> getEmailAddresses() {
        return emailAddresses;
    }

    public void setEmailAddresses(List<String> emailAddresses) {
        this.emailAddresses = emailAddresses;
    }
}
