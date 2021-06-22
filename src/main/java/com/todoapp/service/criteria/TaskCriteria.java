package com.todoapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.todoapp.domain.Task} entity. This class is used
 * in {@link com.todoapp.web.rest.TaskResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /tasks?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class TaskCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter type;

    private StringFilter desc;

    private InstantFilter plannedStartDate;

    private InstantFilter plannedEndDate;

    private StringFilter active;

    private IntegerFilter version;

    private LongFilter taskInstanceId;

    private LongFilter todoId;

    public TaskCriteria() {}

    public TaskCriteria(TaskCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.type = other.type == null ? null : other.type.copy();
        this.desc = other.desc == null ? null : other.desc.copy();
        this.plannedStartDate = other.plannedStartDate == null ? null : other.plannedStartDate.copy();
        this.plannedEndDate = other.plannedEndDate == null ? null : other.plannedEndDate.copy();
        this.active = other.active == null ? null : other.active.copy();
        this.version = other.version == null ? null : other.version.copy();
        this.taskInstanceId = other.taskInstanceId == null ? null : other.taskInstanceId.copy();
        this.todoId = other.todoId == null ? null : other.todoId.copy();
    }

    @Override
    public TaskCriteria copy() {
        return new TaskCriteria(this);
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

    public StringFilter getType() {
        return type;
    }

    public StringFilter type() {
        if (type == null) {
            type = new StringFilter();
        }
        return type;
    }

    public void setType(StringFilter type) {
        this.type = type;
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

    public InstantFilter getPlannedStartDate() {
        return plannedStartDate;
    }

    public InstantFilter plannedStartDate() {
        if (plannedStartDate == null) {
            plannedStartDate = new InstantFilter();
        }
        return plannedStartDate;
    }

    public void setPlannedStartDate(InstantFilter plannedStartDate) {
        this.plannedStartDate = plannedStartDate;
    }

    public InstantFilter getPlannedEndDate() {
        return plannedEndDate;
    }

    public InstantFilter plannedEndDate() {
        if (plannedEndDate == null) {
            plannedEndDate = new InstantFilter();
        }
        return plannedEndDate;
    }

    public void setPlannedEndDate(InstantFilter plannedEndDate) {
        this.plannedEndDate = plannedEndDate;
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

    public LongFilter getTodoId() {
        return todoId;
    }

    public LongFilter todoId() {
        if (todoId == null) {
            todoId = new LongFilter();
        }
        return todoId;
    }

    public void setTodoId(LongFilter todoId) {
        this.todoId = todoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TaskCriteria that = (TaskCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(type, that.type) &&
            Objects.equals(desc, that.desc) &&
            Objects.equals(plannedStartDate, that.plannedStartDate) &&
            Objects.equals(plannedEndDate, that.plannedEndDate) &&
            Objects.equals(active, that.active) &&
            Objects.equals(version, that.version) &&
            Objects.equals(taskInstanceId, that.taskInstanceId) &&
            Objects.equals(todoId, that.todoId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type, desc, plannedStartDate, plannedEndDate, active, version, taskInstanceId, todoId);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TaskCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (type != null ? "type=" + type + ", " : "") +
            (desc != null ? "desc=" + desc + ", " : "") +
            (plannedStartDate != null ? "plannedStartDate=" + plannedStartDate + ", " : "") +
            (plannedEndDate != null ? "plannedEndDate=" + plannedEndDate + ", " : "") +
            (active != null ? "active=" + active + ", " : "") +
            (version != null ? "version=" + version + ", " : "") +
            (taskInstanceId != null ? "taskInstanceId=" + taskInstanceId + ", " : "") +
            (todoId != null ? "todoId=" + todoId + ", " : "") +
            "}";
    }
}
