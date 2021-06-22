package com.todoapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Task.
 */
@Entity
@Table(name = "task")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Task implements Serializable {

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

    @Column(name = "planned_start_date")
    private Instant plannedStartDate;

    @Column(name = "planned_end_date")
    private Instant plannedEndDate;

    @Column(name = "active")
    private String active;

    //@Version
    @Column(name = "version")
    private Integer version;

    @OneToMany(mappedBy = "task")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "task", "todo", "user" }, allowSetters = true)
    private Set<TaskInstance> taskInstances = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "tasks", "taskInstances", "user" }, allowSetters = true)
    private Todo todo;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Task id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Task name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return this.type;
    }

    public Task type(String type) {
        this.type = type;
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return this.desc;
    }

    public Task desc(String desc) {
        this.desc = desc;
        return this;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Instant getPlannedStartDate() {
        return this.plannedStartDate;
    }

    public Task plannedStartDate(Instant plannedStartDate) {
        this.plannedStartDate = plannedStartDate;
        return this;
    }

    public void setPlannedStartDate(Instant plannedStartDate) {
        this.plannedStartDate = plannedStartDate;
    }

    public Instant getPlannedEndDate() {
        return this.plannedEndDate;
    }

    public Task plannedEndDate(Instant plannedEndDate) {
        this.plannedEndDate = plannedEndDate;
        return this;
    }

    public void setPlannedEndDate(Instant plannedEndDate) {
        this.plannedEndDate = plannedEndDate;
    }

    public String getActive() {
        return this.active;
    }

    public Task active(String active) {
        this.active = active;
        return this;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public Integer getVersion() {
        return this.version;
    }

    public Task version(Integer version) {
        this.version = version;
        return this;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Set<TaskInstance> getTaskInstances() {
        return this.taskInstances;
    }

    public Task taskInstances(Set<TaskInstance> taskInstances) {
        this.setTaskInstances(taskInstances);
        return this;
    }

    public Task addTaskInstance(TaskInstance taskInstance) {
        this.taskInstances.add(taskInstance);
        taskInstance.setTask(this);
        return this;
    }

    public Task removeTaskInstance(TaskInstance taskInstance) {
        this.taskInstances.remove(taskInstance);
        taskInstance.setTask(null);
        return this;
    }

    public void setTaskInstances(Set<TaskInstance> taskInstances) {
        if (this.taskInstances != null) {
            this.taskInstances.forEach(i -> i.setTask(null));
        }
        if (taskInstances != null) {
            taskInstances.forEach(i -> i.setTask(this));
        }
        this.taskInstances = taskInstances;
    }

    public Todo getTodo() {
        return this.todo;
    }

    public Task todo(Todo todo) {
        this.setTodo(todo);
        return this;
    }

    public void setTodo(Todo todo) {
        this.todo = todo;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Task)) {
            return false;
        }
        return id != null && id.equals(((Task) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Task{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", type='" + getType() + "'" +
            ", desc='" + getDesc() + "'" +
            ", plannedStartDate='" + getPlannedStartDate() + "'" +
            ", plannedEndDate='" + getPlannedEndDate() + "'" +
            ", active='" + getActive() + "'" +
            ", version=" + getVersion() +
            "}";
    }
}
