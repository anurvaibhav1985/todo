package com.todoapp.service.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.todoapp.domain.TaskInstance;

/**
 * A DTO for the {@link com.todoapp.domain.Todo} entity.
 */
public class TodoDTO implements Serializable {

    private Long id;

    private String name;

    private String desc;

    private String active;

    private Integer version;

    private UserDTO user;

    private Set<TaskInstance> taskInstances = new HashSet<>();

    public Set<TaskInstance> getTaskInstances() {
        return taskInstances;
    }

    public void setTaskInstances(Set<TaskInstance> taskInstances) {
        this.taskInstances = taskInstances;
    }

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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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
        if (!(o instanceof TodoDTO)) {
            return false;
        }

        TodoDTO todoDTO = (TodoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, todoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TodoDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", desc='" + getDesc() + "'" +
            ", active='" + getActive() + "'" +
            ", version=" + getVersion() +
            ", user=" + getUser() +
            "}";
    }
}
