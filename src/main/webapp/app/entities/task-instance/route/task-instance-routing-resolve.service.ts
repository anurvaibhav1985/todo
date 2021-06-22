import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITaskInstance, TaskInstance } from '../task-instance.model';
import { TaskInstanceService } from '../service/task-instance.service';

@Injectable({ providedIn: 'root' })
export class TaskInstanceRoutingResolveService implements Resolve<ITaskInstance> {
  constructor(protected service: TaskInstanceService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITaskInstance> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((taskInstance: HttpResponse<TaskInstance>) => {
          if (taskInstance.body) {
            return of(taskInstance.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new TaskInstance());
  }
}
