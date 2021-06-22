package com.todoapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.todoapp.domain.Todo} entity. This class is used
 * in {@link com.todoapp.web.rest.TodoResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /todos?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class TodoCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter desc;

    private StringFilter active;

    private IntegerFilter version;

    private LongFilter taskId;

    private LongFilter taskInstanceId;

    private StringFilter userId;

    public TodoCriteria() {}

    public TodoCriteria(TodoCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.desc = other.desc == null ? null : other.desc.copy();
        this.active = other.active == null ? null : other.active.copy();
        this.version = other.version == null ? null : other.version.copy();
        this.taskId = other.taskId == null ? null : other.taskId.copy();
        this.taskInstanceId = other.taskInstanceId == null ? null : other.taskInstanceId.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
    }

    @Override
    public TodoCriteria copy() {
        return new TodoCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getDesc() {
        return desc;
    }

    public StringFilter desc() {
        if (desc == null) {
            desc = new StringFilter();
        }
        return desc;
    }

    public void setDesc(StringFilter desc) {
        this.desc = desc;
    }

    public StringFilter getActive() {
        return active;
    }

    public StringFilter active() {
        if (active == null) {
            active = new StringFilter();
        }
        return active;
    }

    public void setActive(StringFilter active) {
        this.active = active;
    }

    public IntegerFilter getVersion() {
        return version;
    }

    public IntegerFilter version() {
        if (version == null) {
            version = new IntegerFilter();
        }
        return version;
    }

    public void setVersion(IntegerFilter version) {
        this.version = version;
    }

    public LongFilter getTaskId() {
        return taskId;
    }

    public LongFilter taskId() {
        if (taskId == null) {
            taskId = new LongFilter();
        }
        return taskId;
    }

    public void setTaskId(LongFilter taskId) {
        this.taskId = taskId;
    }

    public LongFilter getTaskInstanceId() {
        return taskInstanceId;
    }

    public LongFilter taskInstanceId() {
        if (taskInstanceId == null) {
            taskInstanceId = new LongFilter();
        }
        return taskInstanceId;
    }

    public void setTaskInstanceId(LongFilter taskInstanceId) {
        this.taskInstanceId = taskInstanceId;
    }

    public StringFilter getUserId() {
        return userId;
    }

    public StringFilter userId() {
        if (userId == null) {
            userId = new StringFilter();
        }
        return userId;
    }

    public void setUserId(StringFilter userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TodoCriteria that = (TodoCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(desc, that.desc) &&
            Objects.equals(active, that.active) &&
            Objects.equals(version, that.version) &&
            Objects.equals(taskId, that.taskId) &&
            Objects.equals(taskInstanceId, that.taskInstanceId) &&
            Objects.equals(userId, that.userId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, desc, active, version, taskId, taskInstanceId, userId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TodoCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (desc != null ? "desc=" + desc + ", " : "") +
            (active != null ? "active=" + active + ", " : "") +
            (version != null ? "version=" + version + ", " : "") +
            (taskId != null ? "taskId=" + taskId + ", " : "") +
            (taskInstanceId != null ? "taskInstanceId=" + taskInstanceId + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            "}";
    }
}
