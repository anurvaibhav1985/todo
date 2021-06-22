import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { TaskStatus } from 'app/entities/enumerations/task-status.model';
import { ITaskInstance, TaskInstance } from '../task-instance.model';

import { TaskInstanceService } from './task-instance.service';

describe('Service Tests', () => {
  describe('TaskInstance Service', () => {
    let service: TaskInstanceService;
    let httpMock: HttpTestingController;
    let elemDefault: ITaskInstance;
    let expectedResult: ITaskInstance | ITaskInstance[] | boolean | null;
    let currentDate: dayjs.Dayjs;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(TaskInstanceService);
      httpMock = TestBed.inject(HttpTestingController);
      currentDate = dayjs();

      elemDefault = {
        id: 0,
        name: 'AAAAAAA',
        type: 'AAAAAAA',
        desc: 'AAAAAAA',
        status: TaskStatus.INPROGRESS,
        plannedStartDate: currentDate,
        plannedEndDate: currentDate,
        actualStartDate: currentDate,
        actualEndDate: currentDate,
        timeSpent: 0,
        active: 'AAAAAAA',
        version: 0,
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            plannedStartDate: currentDate.format(DATE_TIME_FORMAT),
            plannedEndDate: currentDate.format(DATE_TIME_FORMAT),
            actualStartDate: currentDate.format(DATE_TIME_FORMAT),
            actualEndDate: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a TaskInstance', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            plannedStartDate: currentDate.format(DATE_TIME_FORMAT),
            plannedEndDate: currentDate.format(DATE_TIME_FORMAT),
            actualStartDate: currentDate.format(DATE_TIME_FORMAT),
            actualEndDate: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            plannedStartDate: currentDate,
            plannedEndDate: currentDate,
            actualStartDate: currentDate,
            actualEndDate: currentDate,
          },
          returnedFromService
        );

        service.create(new TaskInstance()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a TaskInstance', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            name: 'BBBBBB',
            type: 'BBBBBB',
            desc: 'BBBBBB',
            status: 'BBBBBB',
            plannedStartDate: currentDate.format(DATE_TIME_FORMAT),
            plannedEndDate: currentDate.format(DATE_TIME_FORMAT),
            actualStartDate: currentDate.format(DATE_TIME_FORMAT),
            actualEndDate: currentDate.format(DATE_TIME_FORMAT),
            timeSpent: 1,
            active: 'BBBBBB',
            version: 1,
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            plannedStartDate: currentDate,
            plannedEndDate: currentDate,
            actualStartDate: currentDate,
            actualEndDate: currentDate,
          },
          returnedFromService
        );

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a TaskInstance', () => {
        const patchObject = Object.assign(
          {
            desc: 'BBBBBB',
            status: 'BBBBBB',
            plannedStartDate: currentDate.format(DATE_TIME_FORMAT),
            actualStartDate: currentDate.format(DATE_TIME_FORMAT),
            active: 'BBBBBB',
            version: 1,
          },
          new TaskInstance()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign(
          {
            plannedStartDate: currentDate,
            plannedEndDate: currentDate,
            actualStartDate: currentDate,
            actualEndDate: currentDate,
          },
          returnedFromService
        );

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of TaskInstance', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            name: 'BBBBBB',
            type: 'BBBBBB',
            desc: 'BBBBBB',
            status: 'BBBBBB',
            plannedStartDate: currentDate.format(DATE_TIME_FORMAT),
            plannedEndDate: currentDate.format(DATE_TIME_FORMAT),
            actualStartDate: currentDate.format(DATE_TIME_FORMAT),
            actualEndDate: currentDate.format(DATE_TIME_FORMAT),
            timeSpent: 1,
            active: 'BBBBBB',
            version: 1,
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            plannedStartDate: currentDate,
            plannedEndDate: currentDate,
            actualStartDate: currentDate,
            actualEndDate: currentDate,
          },
          returnedFromService
        );

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a TaskInstance', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addTaskInstanceToCollectionIfMissing', () => {
        it('should add a TaskInstance to an empty array', () => {
          const taskInstance: ITaskInstance = { id: 123 };
          expectedResult = service.addTaskInstanceToCollectionIfMissing([], taskInstance);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(taskInstance);
        });

        it('should not add a TaskInstance to an array that contains it', () => {
          const taskInstance: ITaskInstance = { id: 123 };
          const taskInstanceCollection: ITaskInstance[] = [
            {
              ...taskInstance,
            },
            { id: 456 },
          ];
          expectedResult = service.addTaskInstanceToCollectionIfMissing(taskInstanceCollection, taskInstance);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a TaskInstance to an array that doesn't contain it", () => {
          const taskInstance: ITaskInstance = { id: 123 };
          const taskInstanceCollection: ITaskInstance[] = [{ id: 456 }];
          expectedResult = service.addTaskInstanceToCollectionIfMissing(taskInstanceCollection, taskInstance);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(taskInstance);
        });

        it('should add only unique TaskInstance to an array', () => {
          const taskInstanceArray: ITaskInstance[] = [{ id: 123 }, { id: 456 }, { id: 89239 }];
          const taskInstanceCollection: ITaskInstance[] = [{ id: 123 }];
          expectedResult = service.addTaskInstanceToCollectionIfMissing(taskInstanceCollection, ...taskInstanceArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const taskInstance: ITaskInstance = { id: 123 };
          const taskInstance2: ITaskInstance = { id: 456 };
          expectedResult = service.addTaskInstanceToCollectionIfMissing([], taskInstance, taskInstance2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(taskInstance);
          expect(expectedResult).toContain(taskInstance2);
        });

        it('should accept null and undefined values', () => {
          const taskInstance: ITaskInstance = { id: 123 };
          expectedResult = service.addTaskInstanceToCollectionIfMissing([], null, taskInstance, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(taskInstance);
        });

        it('should return initial array if no TaskInstance is added', () => {
          const taskInstanceCollection: ITaskInstance[] = [{ id: 123 }];
          expectedResult = service.addTaskInstanceToCollectionIfMissing(taskInstanceCollection, undefined, null);
          expect(expectedResult).toEqual(taskInstanceCollection);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
