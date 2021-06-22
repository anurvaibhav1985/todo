import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITaskInstance, getTaskInstanceIdentifier } from '../task-instance.model';

export type EntityResponseType = HttpResponse<ITaskInstance>;
export type EntityArrayResponseType = HttpResponse<ITaskInstance[]>;

@Injectable({ providedIn: 'root' })
export class TaskInstanceService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/task-instances');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(taskInstance: ITaskInstance): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(taskInstance);
    return this.http
      .post<ITaskInstance>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(taskInstance: ITaskInstance): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(taskInstance);
    return this.http
      .put<ITaskInstance>(`${this.resourceUrl}/${getTaskInstanceIdentifier(taskInstance) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(taskInstance: ITaskInstance): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(taskInstance);
    return this.http
      .patch<ITaskInstance>(`${this.resourceUrl}/${getTaskInstanceIdentifier(taskInstance) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ITaskInstance>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ITaskInstance[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addTaskInstanceToCollectionIfMissing(
    taskInstanceCollection: ITaskInstance[],
    ...taskInstancesToCheck: (ITaskInstance | null | undefined)[]
  ): ITaskInstance[] {
    const taskInstances: ITaskInstance[] = taskInstancesToCheck.filter(isPresent);
    if (taskInstances.length > 0) {
      const taskInstanceCollectionIdentifiers = taskInstanceCollection.map(
        taskInstanceItem => getTaskInstanceIdentifier(taskInstanceItem)!
      );
      const taskInstancesToAdd = taskInstances.filter(taskInstanceItem => {
        const taskInstanceIdentifier = getTaskInstanceIdentifier(taskInstanceItem);
        if (taskInstanceIdentifier == null || taskInstanceCollectionIdentifiers.includes(taskInstanceIdentifier)) {
          return false;
        }
        taskInstanceCollectionIdentifiers.push(taskInstanceIdentifier);
        return true;
      });
      return [...taskInstancesToAdd, ...taskInstanceCollection];
    }
    return taskInstanceCollection;
  }

  protected convertDateFromClient(taskInstance: ITaskInstance): ITaskInstance {
    return Object.assign({}, taskInstance, {
      plannedStartDate: taskInstance.plannedStartDate?.isValid() ? taskInstance.plannedStartDate.toJSON() : undefined,
      plannedEndDate: taskInstance.plannedEndDate?.isValid() ? taskInstance.plannedEndDate.toJSON() : undefined,
      actualStartDate: taskInstance.actualStartDate?.isValid() ? taskInstance.actualStartDate.toJSON() : undefined,
      actualEndDate: taskInstance.actualEndDate?.isValid() ? taskInstance.actualEndDate.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.plannedStartDate = res.body.plannedStartDate ? dayjs(res.body.plannedStartDate) : undefined;
      res.body.plannedEndDate = res.body.plannedEndDate ? dayjs(res.body.plannedEndDate) : undefined;
      res.body.actualStartDate = res.body.actualStartDate ? dayjs(res.body.actualStartDate) : undefined;
      res.body.actualEndDate = res.body.actualEndDate ? dayjs(res.body.actualEndDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((taskInstance: ITaskInstance) => {
        taskInstance.plannedStartDate = taskInstance.plannedStartDate ? dayjs(taskInstance.plannedStartDate) : undefined;
        taskInstance.plannedEndDate = taskInstance.plannedEndDate ? dayjs(taskInstance.plannedEndDate) : undefined;
        taskInstance.actualStartDate = taskInstance.actualStartDate ? dayjs(taskInstance.actualStartDate) : undefined;
        taskInstance.actualEndDate = taskInstance.actualEndDate ? dayjs(taskInstance.actualEndDate) : undefined;
      });
    }
    return res;
  }
}
