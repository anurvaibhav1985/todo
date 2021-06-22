package com.todoapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;

import com.todoapp.domain.enumeration.TaskStatus;

import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.InstantFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link com.todoapp.domain.TaskInstance} entity. This class is used
 * in {@link com.todoapp.web.rest.TaskInstanceResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /task-instances?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class TaskInstanceCriteria implements Serializable, Criteria {

    /**
     * Class for filtering TaskStatus
     */
    public static class TaskStatusFilter extends Filter<TaskStatus> {

        public TaskStatusFilter() {}

        public TaskStatusFilter(TaskStatusFilter filter) {
            super(filter);
        }

        @Override
        public TaskStatusFilter copy() {
            return new TaskStatusFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter type;

    private StringFilter desc;

    private TaskStatusFilter status;

    private InstantFilter plannedStartDate;

    private InstantFilter plannedEndDate;

    private InstantFilter actualStartDate;

    private InstantFilter actualEndDate;

    private DoubleFilter timeSpent;

    private StringFilter active;

    private IntegerFilter version;

    private LongFilter taskId;

    private LongFilter todoId;

    private StringFilter userId;

    public TaskInstanceCriteria() {}

    public TaskInstanceCriteria(TaskInstanceCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.type = other.type == null ? null : other.type.copy();
        this.desc = other.desc == null ? null : other.desc.copy();
        this.status = other.status == null ? null : other.status.copy();
        this.plannedStartDate = other.plannedStartDate == null ? null : other.plannedStartDate.copy();
        this.plannedEndDate = other.plannedEndDate == null ? null : other.plannedEndDate.copy();
        this.actualStartDate = other.actualStartDate == null ? null : other.actualStartDate.copy();
        this.actualEndDate = other.actualEndDate == null ? null : other.actualEndDate.copy();
        this.timeSpent = other.timeSpent == null ? null : other.timeSpent.copy();
        this.active = other.active == null ? null : other.active.copy();
        this.version = other.version == null ? null : other.version.copy();
        this.taskId = other.taskId == null ? null : other.taskId.copy();
        this.todoId = other.todoId == null ? null : other.todoId.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
    }

    @Override
    public TaskInstanceCriteria copy() {
        return new TaskInstanceCriteria(this);
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

    public TaskStatusFilter getStatus() {
        return status;
    }

    public TaskStatusFilter status() {
        if (status == null) {
            status = new TaskStatusFilter();
        }
        return status;
    }

    public void setStatus(TaskStatusFilter status) {
        this.status = status;
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

    public InstantFilter getActualStartDate() {
        return actualStartDate;
    }

    public InstantFilter actualStartDate() {
        if (actualStartDate == null) {
            actualStartDate = new InstantFilter();
        }
        return actualStartDate;
    }

    public void setActualStartDate(InstantFilter actualStartDate) {
        this.actualStartDate = actualStartDate;
    }

    public InstantFilter getActualEndDate() {
        return actualEndDate;
    }

    public InstantFilter actualEndDate() {
        if (actualEndDate == null) {
            actualEndDate = new InstantFilter();
        }
        return actualEndDate;
    }

    public void setActualEndDate(InstantFilter actualEndDate) {
        this.actualEndDate = actualEndDate;
    }

    public DoubleFilter getTimeSpent() {
        return timeSpent;
    }

    public DoubleFilter timeSpent() {
        if (timeSpent == null) {
            timeSpent = new DoubleFilter();
        }
        return timeSpent;
    }

    public void setTimeSpent(DoubleFilter timeSpent) {
        this.timeSpent = timeSpent;
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
        final TaskInstanceCriteria that = (TaskInstanceCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(type, that.type) &&
            Objects.equals(desc, that.desc) &&
            Objects.equals(status, that.status) &&
            Objects.equals(plannedStartDate, that.plannedStartDate) &&
            Objects.equals(plannedEndDate, that.plannedEndDate) &&
            Objects.equals(actualStartDate, that.actualStartDate) &&
            Objects.equals(actualEndDate, that.actualEndDate) &&
            Objects.equals(timeSpent, that.timeSpent) &&
            Objects.equals(active, that.active) &&
            Objects.equals(version, that.version) &&
            Objects.equals(taskId, that.taskId) &&
            Objects.equals(todoId, that.todoId) &&
            Objects.equals(userId, that.userId)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            name,
            type,
            desc,
            status,
            plannedStartDate,
            plannedEndDate,
            actualStartDate,
            actualEndDate,
            timeSpent,
            active,
            version,
            taskId,
            todoId,
            userId
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TaskInstanceCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (type != null ? "type=" + type + ", " : "") +
            (desc != null ? "desc=" + desc + ", " : "") +
            (status != null ? "status=" + status + ", " : "") +
            (plannedStartDate != null ? "plannedStartDate=" + plannedStartDate + ", " : "") +
            (plannedEndDate != null ? "plannedEndDate=" + plannedEndDate + ", " : "") +
            (actualStartDate != null ? "actualStartDate=" + actualStartDate + ", " : "") +
            (actualEndDate != null ? "actualEndDate=" + actualEndDate + ", " : "") +
            (timeSpent != null ? "timeSpent=" + timeSpent + ", " : "") +
            (active != null ? "active=" + active + ", " : "") +
            (version != null ? "version=" + version + ", " : "") +
            (taskId != null ? "taskId=" + taskId + ", " : "") +
            (todoId != null ? "todoId=" + todoId + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            "}";
    }
}
