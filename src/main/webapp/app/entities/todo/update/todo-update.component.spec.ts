jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { TodoService } from '../service/todo.service';
import { ITodo, Todo } from '../todo.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { TodoUpdateComponent } from './todo-update.component';

describe('Component Tests', () => {
  describe('Todo Management Update Component', () => {
    let comp: TodoUpdateComponent;
    let fixture: ComponentFixture<TodoUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let todoService: TodoService;
    let userService: UserService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [TodoUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(TodoUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(TodoUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      todoService = TestBed.inject(TodoService);
      userService = TestBed.inject(UserService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call User query and add missing value', () => {
        const todo: ITodo = { id: 456 };
        const user: IUser = { id: '26e2f6d5-b59e-4d3e-ba9c-beeef3727102' };
        todo.user = user;

        const userCollection: IUser[] = [{ id: 'c3b49c65-8f65-4933-b0fe-0ed365274058' }];
        jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
        const additionalUsers = [user];
        const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
        jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ todo });
        comp.ngOnInit();

        expect(userService.query).toHaveBeenCalled();
        expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(userCollection, ...additionalUsers);
        expect(comp.usersSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const todo: ITodo = { id: 456 };
        const user: IUser = { id: '86dc321b-10c0-4fe1-a641-793b6a1d9baa' };
        todo.user = user;

        activatedRoute.data = of({ todo });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(todo));
        expect(comp.usersSharedCollection).toContain(user);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Todo>>();
        const todo = { id: 123 };
        jest.spyOn(todoService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ todo });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: todo }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(todoService.update).toHaveBeenCalledWith(todo);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Todo>>();
        const todo = new Todo();
        jest.spyOn(todoService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ todo });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: todo }));
        saveSubject.complete();

        // THEN
        expect(todoService.create).toHaveBeenCalledWith(todo);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Todo>>();
        const todo = { id: 123 };
        jest.spyOn(todoService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ todo });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(todoService.update).toHaveBeenCalledWith(todo);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
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
