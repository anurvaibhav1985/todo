import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import * as dayjs from 'dayjs';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { ITaskInstance, TaskInstance } from '../task-instance.model';
import { TaskInstanceService } from '../service/task-instance.service';
import { ITask } from 'app/entities/task/task.model';
import { TaskService } from 'app/entities/task/service/task.service';
import { ITodo } from 'app/entities/todo/todo.model';
import { TodoService } from 'app/entities/todo/service/todo.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'jhi-task-instance-update',
  templateUrl: './task-instance-update.component.html',
})
export class TaskInstanceUpdateComponent implements OnInit {
  isSaving = false;

  tasksSharedCollection: ITask[] = [];
  todosSharedCollection: ITodo[] = [];
  usersSharedCollection: IUser[] = [];

  editForm = this.fb.group({
    id: [],
    name: [],
    type: [],
    desc: [],
    status: [],
    plannedStartDate: [],
    plannedEndDate: [],
    actualStartDate: [],
    actualEndDate: [],
    timeSpent: [],
    active: [],
    version: [],
    task: [],
    todo: [],
    user: [],
  });

  constructor(
    protected taskInstanceService: TaskInstanceService,
    protected taskService: TaskService,
    protected todoService: TodoService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ taskInstance }) => {
      if (taskInstance.id === undefined) {
        const today = dayjs().startOf('day');
        taskInstance.plannedStartDate = today;
        taskInstance.plannedEndDate = today;
        taskInstance.actualStartDate = today;
        taskInstance.actualEndDate = today;
      }

      this.updateForm(taskInstance);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const taskInstance = this.createFromForm();
    if (taskInstance.id !== undefined) {
      this.subscribeToSaveResponse(this.taskInstanceService.update(taskInstance));
    } else {
      this.subscribeToSaveResponse(this.taskInstanceService.create(taskInstance));
    }
  }

  trackTaskById(index: number, item: ITask): number {
    return item.id!;
  }

  trackTodoById(index: number, item: ITodo): number {
    return item.id!;
  }

  trackUserById(index: number, item: IUser): string {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITaskInstance>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(taskInstance: ITaskInstance): void {
    this.editForm.patchValue({
      id: taskInstance.id,
      name: taskInstance.name,
      type: taskInstance.type,
      desc: taskInstance.desc,
      status: taskInstance.status,
      plannedStartDate: taskInstance.plannedStartDate ? taskInstance.plannedStartDate.format(DATE_TIME_FORMAT) : null,
      plannedEndDate: taskInstance.plannedEndDate ? taskInstance.plannedEndDate.format(DATE_TIME_FORMAT) : null,
      actualStartDate: taskInstance.actualStartDate ? taskInstance.actualStartDate.format(DATE_TIME_FORMAT) : null,
      actualEndDate: taskInstance.actualEndDate ? taskInstance.actualEndDate.format(DATE_TIME_FORMAT) : null,
      timeSpent: taskInstance.timeSpent,
      active: taskInstance.active,
      version: taskInstance.version,
      task: taskInstance.task,
      todo: taskInstance.todo,
      user: taskInstance.user,
    });

    this.tasksSharedCollection = this.taskService.addTaskToCollectionIfMissing(this.tasksSharedCollection, taskInstance.task);
    this.todosSharedCollection = this.todoService.addTodoToCollectionIfMissing(this.todosSharedCollection, taskInstance.todo);
    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing(this.usersSharedCollection, taskInstance.user);
  }

  protected loadRelationshipsOptions(): void {
    this.taskService
      .query()
      .pipe(map((res: HttpResponse<ITask[]>) => res.body ?? []))
      .pipe(map((tasks: ITask[]) => this.taskService.addTaskToCollectionIfMissing(tasks, this.editForm.get('task')!.value)))
      .subscribe((tasks: ITask[]) => (this.tasksSharedCollection = tasks));

    this.todoService
      .query()
      .pipe(map((res: HttpResponse<ITodo[]>) => res.body ?? []))
      .pipe(map((todos: ITodo[]) => this.todoService.addTodoToCollectionIfMissing(todos, this.editForm.get('todo')!.value)))
      .subscribe((todos: ITodo[]) => (this.todosSharedCollection = todos));

    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing(users, this.editForm.get('user')!.value)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }

  protected createFromForm(): ITaskInstance {
    return {
      ...new TaskInstance(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      type: this.editForm.get(['type'])!.value,
      desc: this.editForm.get(['desc'])!.value,
      status: this.editForm.get(['status'])!.value,
      plannedStartDate: this.editForm.get(['plannedStartDate'])!.value
        ? dayjs(this.editForm.get(['plannedStartDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      plannedEndDate: this.editForm.get(['plannedEndDate'])!.value
        ? dayjs(this.editForm.get(['plannedEndDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      actualStartDate: this.editForm.get(['actualStartDate'])!.value
        ? dayjs(this.editForm.get(['actualStartDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      actualEndDate: this.editForm.get(['actualEndDate'])!.value
        ? dayjs(this.editForm.get(['actualEndDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      timeSpent: this.editForm.get(['timeSpent'])!.value,
      active: this.editForm.get(['active'])!.value,
      version: this.editForm.get(['version'])!.value,
      task: this.editForm.get(['task'])!.value,
      todo: this.editForm.get(['todo'])!.value,
      user: this.editForm.get(['user'])!.value,
    };
  }
}
