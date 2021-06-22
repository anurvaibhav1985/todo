package com.todoapp.service.dto;

import com.todoapp.domain.enumeration.TaskStatus;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.todoapp.domain.TaskInstance} entity.
 */
public class TaskInstanceDTO implements Serializable {

    private Long id;

    private String name;

    private String type;

    private String desc;

    private TaskStatus status;

    private Instant plannedStartDate;

    private Instant plannedEndDate;

    private Instant actualStartDate;

    private Instant actualEndDate;

    private Double timeSpent;

    private String active;

    private Integer version;

    private TaskDTO task;

    private TodoDTO todo;

    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Instant getPlannedStartDate() {
        return plannedStartDate;
    }

    public void setPlannedStartDate(Instant plannedStartDate) {
        this.plannedStartDate = plannedStartDate;
    }

    public Instant getPlannedEndDate() {
        return plannedEndDate;
    }

    public void setPlannedEndDate(Instant plannedEndDate) {
        this.plannedEndDate = plannedEndDate;
    }

    public Instant getActualStartDate() {
        return actualStartDate;
    }

    public void setActualStartDate(Instant actualStartDate) {
        this.actualStartDate = actualStartDate;
    }

    public Instant getActualEndDate() {
        return actualEndDate;
    }

    public void setActualEndDate(Instant actualEndDate) {
        this.actualEndDate = actualEndDate;
    }

    public Double getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(Double timeSpent) {
        this.timeSpent = timeSpent;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public TaskDTO getTask() {
        return task;
    }

    public void setTask(TaskDTO task) {
        this.task = task;
    }

    public TodoDTO getTodo() {
        return todo;
    }

    public void setTodo(TodoDTO todo) {
        this.todo = todo;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaskInstanceDTO)) {
            return false;
        }

        TaskInstanceDTO taskInstanceDTO = (TaskInstanceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, taskInstanceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TaskInstanceDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", type='" + getType() + "'" +
            ", desc='" + getDesc() + "'" +
            ", status='" + getStatus() + "'" +
            ", plannedStartDate='" + getPlannedStartDate() + "'" +
            ", plannedEndDate='" + getPlannedEndDate() + "'" +
            ", actualStartDate='" + getActualStartDate() + "'" +
            ", actualEndDate='" + getActualEndDate() + "'" +
            ", timeSpent=" + getTimeSpent() +
            ", active='" + getActive() + "'" +
            ", version=" + getVersion() +
            ", task=" + getTask() +
            ", todo=" + getTodo() +
            ", user=" + getUser() +
            "}";
    }
}
