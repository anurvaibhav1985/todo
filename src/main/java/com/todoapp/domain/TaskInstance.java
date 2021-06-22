package com.todoapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.todoapp.domain.enumeration.TaskStatus;
import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TaskInstance.
 */
@Entity
@Table(name = "task_instance")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TaskInstance implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "jhi_desc")
    private String desc;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TaskStatus status;

    @Column(name = "planned_start_date")
    private Instant plannedStartDate;

    @Column(name = "planned_end_date")
    private Instant plannedEndDate;

    @Column(name = "actual_start_date")
    private Instant actualStartDate;

    @Column(name = "actual_end_date")
    private Instant actualEndDate;

    @Column(name = "time_spent")
    private Double timeSpent;

    @Column(name = "active")
    private String active;

    //@Version
    @Column(name = "version")
    private Integer version;

    @ManyToOne
    @JsonIgnoreProperties(value = { "taskInstances", "todo" }, allowSetters = true)
    private Task task;

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = { "tasks", "taskInstances", "user" }, allowSetters = true)
    private Todo todo;

    @ManyToOne
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TaskInstance id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public TaskInstance name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public TaskInstance type(String type) {
        this.type = type;
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return this.desc;
    }

    public TaskInstance desc(String desc) {
        this.desc = desc;
        return this;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public TaskStatus getStatus() {
        return this.status;
    }

    public TaskInstance status(TaskStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Instant getPlannedStartDate() {
        return this.plannedStartDate;
    }

    public TaskInstance plannedStartDate(Instant plannedStartDate) {
        this.plannedStartDate = plannedStartDate;
        return this;
    }

    public void setPlannedStartDate(Instant plannedStartDate) {
        this.plannedStartDate = plannedStartDate;
    }

    public Instant getPlannedEndDate() {
        return this.plannedEndDate;
    }

    public TaskInstance plannedEndDate(Instant plannedEndDate) {
        this.plannedEndDate = plannedEndDate;
        return this;
    }

    public void setPlannedEndDate(Instant plannedEndDate) {
        this.plannedEndDate = plannedEndDate;
    }

    public Instant getActualStartDate() {
        return this.actualStartDate;
    }

    public TaskInstance actualStartDate(Instant actualStartDate) {
        this.actualStartDate = actualStartDate;
        return this;
    }

    public void setActualStartDate(Instant actualStartDate) {
        this.actualStartDate = actualStartDate;
    }

    public Instant getActualEndDate() {
        return this.actualEndDate;
    }

    public TaskInstance actualEndDate(Instant actualEndDate) {
        this.actualEndDate = actualEndDate;
        return this;
    }

    public void setActualEndDate(Instant actualEndDate) {
        this.actualEndDate = actualEndDate;
    }

    public Double getTimeSpent() {
        return this.timeSpent;
    }

    public TaskInstance timeSpent(Double timeSpent) {
        this.timeSpent = timeSpent;
        return this;
    }

    public void setTimeSpent(Double timeSpent) {
        this.timeSpent = timeSpent;
    }

    public String getActive() {
        return this.active;
    }

    public TaskInstance active(String active) {
        this.active = active;
        return this;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public Integer getVersion() {
        return this.version;
    }

    public TaskInstance version(Integer version) {
        this.version = version;
        return this;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Task getTask() {
        return this.task;
    }

    public TaskInstance task(Task task) {
        this.setTask(task);
        return this;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Todo getTodo() {
        return this.todo;
    }

    public TaskInstance todo(Todo todo) {
        this.setTodo(todo);
        return this;
    }

    public void setTodo(Todo todo) {
        this.todo = todo;
    }

    public User getUser() {
        return this.user;
    }

    public TaskInstance user(User user) {
        this.setUser(user);
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TaskInstance)) {
            return false;
        }
        return id != null && id.equals(((TaskInstance) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TaskInstance{" +
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
            "}";
    }
}
