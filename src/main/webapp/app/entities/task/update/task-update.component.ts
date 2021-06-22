import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import * as dayjs from 'dayjs';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { ITask, Task } from '../task.model';
import { TaskService } from '../service/task.service';
import { ITodo } from 'app/entities/todo/todo.model';
import { TodoService } from 'app/entities/todo/service/todo.service';

@Component({
  selector: 'jhi-task-update',
  templateUrl: './task-update.component.html',
})
export class TaskUpdateComponent implements OnInit {
  isSaving = false;

  todosSharedCollection: ITodo[] = [];

  editForm = this.fb.group({
    id: [],
    name: [],
    type: [],
    desc: [],
    plannedStartDate: [],
    plannedEndDate: [],
    active: [],
    version: [],
    todo: [],
  });

  constructor(
    protected taskService: TaskService,
    protected todoService: TodoService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ task }) => {
      if (task.id === undefined) {
        const today = dayjs().startOf('day');
        task.plannedStartDate = today;
        task.plannedEndDate = today;
      }

      this.updateForm(task);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const task = this.createFromForm();
    if (task.id !== undefined) {
      this.subscribeToSaveResponse(this.taskService.update(task));
    } else {
      this.subscribeToSaveResponse(this.taskService.create(task));
    }
  }

  trackTodoById(index: number, item: ITodo): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITask>>): void {
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

  protected updateForm(task: ITask): void {
    this.editForm.patchValue({
      id: task.id,
      name: task.name,
      type: task.type,
      desc: task.desc,
      plannedStartDate: task.plannedStartDate ? task.plannedStartDate.format(DATE_TIME_FORMAT) : null,
      plannedEndDate: task.plannedEndDate ? task.plannedEndDate.format(DATE_TIME_FORMAT) : null,
      active: task.active,
      version: task.version,
      todo: task.todo,
    });

    this.todosSharedCollection = this.todoService.addTodoToCollectionIfMissing(this.todosSharedCollection, task.todo);
  }

  protected loadRelationshipsOptions(): void {
    this.todoService
      .query()
      .pipe(map((res: HttpResponse<ITodo[]>) => res.body ?? []))
      .pipe(map((todos: ITodo[]) => this.todoService.addTodoToCollectionIfMissing(todos, this.editForm.get('todo')!.value)))
      .subscribe((todos: ITodo[]) => (this.todosSharedCollection = todos));
  }

  protected createFromForm(): ITask {
    return {
      ...new Task(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      type: this.editForm.get(['type'])!.value,
      desc: this.editForm.get(['desc'])!.value,
      plannedStartDate: this.editForm.get(['plannedStartDate'])!.value
        ? dayjs(this.editForm.get(['plannedStartDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      plannedEndDate: this.editForm.get(['plannedEndDate'])!.value
        ? dayjs(this.editForm.get(['plannedEndDate'])!.value, DATE_TIME_FORMAT)
        : undefined,
      active: this.editForm.get(['active'])!.value,
      version: this.editForm.get(['version'])!.value,
      todo: this.editForm.get(['todo'])!.value,
    };
  }
}
