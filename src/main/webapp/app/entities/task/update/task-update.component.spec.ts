jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { TaskService } from '../service/task.service';
import { ITask, Task } from '../task.model';
import { ITodo } from 'app/entities/todo/todo.model';
import { TodoService } from 'app/entities/todo/service/todo.service';

import { TaskUpdateComponent } from './task-update.component';

describe('Component Tests', () => {
  describe('Task Management Update Component', () => {
    let comp: TaskUpdateComponent;
    let fixture: ComponentFixture<TaskUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let taskService: TaskService;
    let todoService: TodoService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [TaskUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(TaskUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(TaskUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      taskService = TestBed.inject(TaskService);
      todoService = TestBed.inject(TodoService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Todo query and add missing value', () => {
        const task: ITask = { id: 456 };
        const todo: ITodo = { id: 25672 };
        task.todo = todo;

        const todoCollection: ITodo[] = [{ id: 53990 }];
        jest.spyOn(todoService, 'query').mockReturnValue(of(new HttpResponse({ body: todoCollection })));
        const additionalTodos = [todo];
        const expectedCollection: ITodo[] = [...additionalTodos, ...todoCollection];
        jest.spyOn(todoService, 'addTodoToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ task });
        comp.ngOnInit();

        expect(todoService.query).toHaveBeenCalled();
        expect(todoService.addTodoToCollectionIfMissing).toHaveBeenCalledWith(todoCollection, ...additionalTodos);
        expect(comp.todosSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const task: ITask = { id: 456 };
        const todo: ITodo = { id: 62745 };
        task.todo = todo;

        activatedRoute.data = of({ task });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(task));
        expect(comp.todosSharedCollection).toContain(todo);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Task>>();
        const task = { id: 123 };
        jest.spyOn(taskService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ task });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: task }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(taskService.update).toHaveBeenCalledWith(task);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Task>>();
        const task = new Task();
        jest.spyOn(taskService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ task });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: task }));
        saveSubject.complete();

        // THEN
        expect(taskService.create).toHaveBeenCalledWith(task);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Task>>();
        const task = { id: 123 };
        jest.spyOn(taskService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ task });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(taskService.update).toHaveBeenCalledWith(task);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackTodoById', () => {
        it('Should return tracked Todo primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackTodoById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
