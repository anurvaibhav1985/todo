jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { ITaskInstance, TaskInstance } from '../task-instance.model';
import { TaskInstanceService } from '../service/task-instance.service';

import { TaskInstanceRoutingResolveService } from './task-instance-routing-resolve.service';

describe('Service Tests', () => {
  describe('TaskInstance routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: TaskInstanceRoutingResolveService;
    let service: TaskInstanceService;
    let resultTaskInstance: ITaskInstance | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(TaskInstanceRoutingResolveService);
      service = TestBed.inject(TaskInstanceService);
      resultTaskInstance = undefined;
    });

    describe('resolve', () => {
      it('should return ITaskInstance returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultTaskInstance = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultTaskInstance).toEqual({ id: 123 });
      });

      it('should return new ITaskInstance if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultTaskInstance = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultTaskInstance).toEqual(new TaskInstance());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as TaskInstance })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultTaskInstance = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultTaskInstance).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
