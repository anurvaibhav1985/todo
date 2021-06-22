import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TaskInstanceComponent } from './list/task-instance.component';
import { TaskInstanceDetailComponent } from './detail/task-instance-detail.component';
import { TaskInstanceUpdateComponent } from './update/task-instance-update.component';
import { TaskInstanceDeleteDialogComponent } from './delete/task-instance-delete-dialog.component';
import { TaskInstanceRoutingModule } from './route/task-instance-routing.module';

@NgModule({
  imports: [SharedModule, TaskInstanceRoutingModule],
  declarations: [TaskInstanceComponent, TaskInstanceDetailComponent, TaskInstanceUpdateComponent, TaskInstanceDeleteDialogComponent],
  entryComponents: [TaskInstanceDeleteDialogComponent],
})
export class TaskInstanceModule {}
