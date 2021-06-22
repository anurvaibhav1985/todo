jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { TaskInstanceService } from '../service/task-instance.service';
import { ITaskInstance, TaskInstance } from '../task-instance.model';
import { ITask } from 'app/entities/task/task.model';
import { TaskService } from 'app/entities/task/service/task.service';
import { ITodo } from 'app/entities/todo/todo.model';
import { TodoService } from 'app/entities/todo/service/todo.service';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { TaskInstanceUpdateComponent } from './task-instance-update.component';

describe('Component Tests', () => {
  describe('TaskInstance Management Update Component', () => {
    let comp: TaskInstanceUpdateComponent;
    let fixture: ComponentFixture<TaskInstanceUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let taskInstanceService: TaskInstanceService;
    let taskService: TaskService;
    let todoService: TodoService;
    let userService: UserService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [TaskInstanceUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(TaskInstanceUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(TaskInstanceUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      taskInstanceService = TestBed.inject(TaskInstanceService);
      taskService = TestBed.inject(TaskService);
      todoService = TestBed.inject(TodoService);
      userService = TestBed.inject(UserService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Task query and add missing value', () => {
        const taskInstance: ITaskInstance = { id: 456 };
        const task: ITask = { id: 68069 };
        taskInstance.task = task;

        const taskCollection: ITask[] = [{ id: 96816 }];
        jest.spyOn(taskService, 'query').mockReturnValue(of(new HttpResponse({ body: taskCollection })));
        const additionalTasks = [task];
        const expectedCollection: ITask[] = [...additionalTasks, ...taskCollection];
        jest.spyOn(taskService, 'addTaskToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ taskInstance });
        comp.ngOnInit();

        expect(taskService.query).toHaveBeenCalled();
        expect(taskService.addTaskToCollectionIfMissing).toHaveBeenCalledWith(taskCollection, ...additionalTasks);
        expect(comp.tasksSharedCollection).toEqual(expectedCollection);
      });

      it('Should call Todo query and add missing value', () => {
        const taskInstance: ITaskInstance = { id: 456 };
        const todo: ITodo = { id: 49206 };
        taskInstance.todo = todo;

        const todoCollection: ITodo[] = [{ id: 48795 }];
        jest.spyOn(todoService, 'query').mockReturnValue(of(new HttpResponse({ body: todoCollection })));
        const additionalTodos = [todo];
        const expectedCollection: ITodo[] = [...additionalTodos, ...todoCollection];
        jest.spyOn(todoService, 'addTodoToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ taskInstance });
        comp.ngOnInit();

        expect(todoService.query).toHaveBeenCalled();
        expect(todoService.addTodoToCollectionIfMissing).toHaveBeenCalledWith(todoCollection, ...additionalTodos);
        expect(comp.todosSharedCollection).toEqual(expectedCollection);
      });

      it('Should call User query and add missing value', () => {
        const taskInstance: ITaskInstance = { id: 456 };
        const user: IUser = { id: '3c8b659d-5ebc-49a2-a5f6-a5cd7a1e5039' };
        taskInstance.user = user;

        const userCollection: IUser[] = [{ id: 'a443610b-6de8-4b9d-9fc8-34c5257ae8b6' }];
        jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
        const additionalUsers = [user];
        const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
        jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ taskInstance });
        comp.ngOnInit();

        expect(userService.query).toHaveBeenCalled();
        expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(userCollection, ...additionalUsers);
        expect(comp.usersSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const taskInstance: ITaskInstance = { id: 456 };
        const task: ITask = { id: 91786 };
        taskInstance.task = task;
        const todo: ITodo = { id: 25599 };
        taskInstance.todo = todo;
        const user: IUser = { id: '3c4e36f3-6520-47ae-bae3-1b1917c1bf85' };
        taskInstance.user = user;

        activatedRoute.data = of({ taskInstance });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(taskInstance));
        expect(comp.tasksSharedCollection).toContain(task);
        expect(comp.todosSharedCollection).toContain(todo);
        expect(comp.usersSharedCollection).toContain(user);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<TaskInstance>>();
        const taskInstance = { id: 123 };
        jest.spyOn(taskInstanceService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ taskInstance });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: taskInstance }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(taskInstanceService.update).toHaveBeenCalledWith(taskInstance);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<TaskInstance>>();
        const taskInstance = new TaskInstance();
        jest.spyOn(taskInstanceService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ taskInstance });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: taskInstance }));
        saveSubject.complete();

        // THEN
        expect(taskInstanceService.create).toHaveBeenCalledWith(taskInstance);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<TaskInstance>>();
        const taskInstance = { id: 123 };
        jest.spyOn(taskInstanceService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ taskInstance });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(taskInstanceService.update).toHaveBeenCalledWith(taskInstance);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackTaskById', () => {
        it('Should return tracked Task primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackTaskById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackTodoById', () => {
        it('Should return tracked Todo primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackTodoById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });

      describe('trackUserById', () => {
        it('Should return tracked User primary key', () => {
          const entity = { id: 'ABC' };
          const trackResult = comp.trackUserById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
