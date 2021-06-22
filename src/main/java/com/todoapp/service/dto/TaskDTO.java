package com.todoapp.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.todoapp.domain.TaskInstance;

/**
 * A DTO for the {@link com.todoapp.domain.Task} entity.
 */
public class TaskDTO implements Serializable {

    private Long id;

    private String name;

    private String type;

    private String desc;

    private Instant plannedStartDate;

    private Instant plannedEndDate;

    private String active;

    private Integer version;

    private TodoDTO todo;

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

    public TodoDTO getTodo() {
        return todo;
    }

    public void setTodo(TodoDTO todo) {
        this.todo = todo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaskDTO)) {
            return false;
        }

        TaskDTO taskDTO = (TaskDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, taskDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TaskDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", type='" + getType() + "'" +
            ", desc='" + getDesc() + "'" +
            ", plannedStartDate='" + getPlannedStartDate() + "'" +
            ", plannedEndDate='" + getPlannedEndDate() + "'" +
            ", active='" + getActive() + "'" +
            ", version=" + getVersion() +
            ", todo=" + getTodo() +
            "}";
    }
}
