import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TaskInstanceComponent } from '../list/task-instance.component';
import { TaskInstanceDetailComponent } from '../detail/task-instance-detail.component';
import { TaskInstanceUpdateComponent } from '../update/task-instance-update.component';
import { TaskInstanceRoutingResolveService } from './task-instance-routing-resolve.service';

const taskInstanceRoute: Routes = [
  {
    path: '',
    component: TaskInstanceComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TaskInstanceDetailComponent,
    resolve: {
      taskInstance: TaskInstanceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TaskInstanceUpdateComponent,
    resolve: {
      taskInstance: TaskInstanceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TaskInstanceUpdateComponent,
    resolve: {
      taskInstance: TaskInstanceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(taskInstanceRoute)],
  exports: [RouterModule],
})
export class TaskInstanceRoutingModule {}
