import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITask, getTaskIdentifier } from '../task.model';

export type EntityResponseType = HttpResponse<ITask>;
export type EntityArrayResponseType = HttpResponse<ITask[]>;

@Injectable({ providedIn: 'root' })
export class TaskService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/tasks');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(task: ITask): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(task);
    return this.http
      .post<ITask>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(task: ITask): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(task);
    return this.http
      .put<ITask>(`${this.resourceUrl}/${getTaskIdentifier(task) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(task: ITask): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(task);
    return this.http
      .patch<ITask>(`${this.resourceUrl}/${getTaskIdentifier(task) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ITask>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ITask[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addTaskToCollectionIfMissing(taskCollection: ITask[], ...tasksToCheck: (ITask | null | undefined)[]): ITask[] {
    const tasks: ITask[] = tasksToCheck.filter(isPresent);
    if (tasks.length > 0) {
      const taskCollectionIdentifiers = taskCollection.map(taskItem => getTaskIdentifier(taskItem)!);
      const tasksToAdd = tasks.filter(taskItem => {
        const taskIdentifier = getTaskIdentifier(taskItem);
        if (taskIdentifier == null || taskCollectionIdentifiers.includes(taskIdentifier)) {
          return false;
        }
        taskCollectionIdentifiers.push(taskIdentifier);
        return true;
      });
      return [...tasksToAdd, ...taskCollection];
    }
    return taskCollection;
  }

  protected convertDateFromClient(task: ITask): ITask {
    return Object.assign({}, task, {
      plannedStartDate: task.plannedStartDate?.isValid() ? task.plannedStartDate.toJSON() : undefined,
      plannedEndDate: task.plannedEndDate?.isValid() ? task.plannedEndDate.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.plannedStartDate = res.body.plannedStartDate ? dayjs(res.body.plannedStartDate) : undefined;
      res.body.plannedEndDate = res.body.plannedEndDate ? dayjs(res.body.plannedEndDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((task: ITask) => {
        task.plannedStartDate = task.plannedStartDate ? dayjs(task.plannedStartDate) : undefined;
        task.plannedEndDate = task.plannedEndDate ? dayjs(task.plannedEndDate) : undefined;
      });
    }
    return res;
  }
}
