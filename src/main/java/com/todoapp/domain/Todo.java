package com.todoapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Todo.
 */
@Entity
@Table(name = "todo")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Todo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "jhi_desc")
    private String desc;

    @Column(name = "active")
    private String active;

    //@Version
    @Column(name = "version")
    private Integer version;

    @OneToMany(mappedBy = "todo")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "taskInstances", "todo" }, allowSetters = true)
    private Set<Task> tasks = new HashSet<>();

    @OneToMany(mappedBy = "todo", fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "task", "todo", "user" }, allowSetters = true)
    private Set<TaskInstance> taskInstances = new HashSet<>();

    @ManyToOne
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Todo id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Todo name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return this.desc;
    }

    public Todo desc(String desc) {
        this.desc = desc;
        return this;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getActive() {
        return this.active;
    }

    public Todo active(String active) {
        this.active = active;
        return this;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public Integer getVersion() {
        return this.version;
    }

    public Todo version(Integer version) {
        this.version = version;
        return this;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Set<Task> getTasks() {
        return this.tasks;
    }

    public Todo tasks(Set<Task> tasks) {
        this.setTasks(tasks);
        return this;
    }

    public Todo addTask(Task task) {
        this.tasks.add(task);
        task.setTodo(this);
        return this;
    }

    public Todo removeTask(Task task) {
        this.tasks.remove(task);
        task.setTodo(null);
        return this;
    }

    public void setTasks(Set<Task> tasks) {
        if (this.tasks != null) {
            this.tasks.forEach(i -> i.setTodo(null));
        }
        if (tasks != null) {
            tasks.forEach(i -> i.setTodo(this));
        }
        this.tasks = tasks;
    }

    public Set<TaskInstance> getTaskInstances() {
        return this.taskInstances;
    }

    public Todo taskInstances(Set<TaskInstance> taskInstances) {
        this.setTaskInstances(taskInstances);
        return this;
    }

    public Todo addTaskInstance(TaskInstance taskInstance) {
        this.taskInstances.add(taskInstance);
        taskInstance.setTodo(this);
        return this;
    }

    public Todo removeTaskInstance(TaskInstance taskInstance) {
        this.taskInstances.remove(taskInstance);
        taskInstance.setTodo(null);
        return this;
    }

    public void setTaskInstances(Set<TaskInstance> taskInstances) {
        if (this.taskInstances != null) {
            this.taskInstances.forEach(i -> i.setTodo(null));
        }
        if (taskInstances != null) {
            taskInstances.forEach(i -> i.setTodo(this));
        }
        this.taskInstances = taskInstances;
    }

    public User getUser() {
        return this.user;
    }

    public Todo user(User user) {
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
        if (!(o instanceof Todo)) {
            return false;
        }
        return id != null && id.equals(((Todo) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Todo{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", desc='" + getDesc() + "'" +
            ", active='" + getActive() + "'" +
            ", version=" + getVersion() +
            "}";
    }
}
