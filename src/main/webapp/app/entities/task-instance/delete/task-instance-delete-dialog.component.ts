import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITaskInstance } from '../task-instance.model';
import { TaskInstanceService } from '../service/task-instance.service';

@Component({
  templateUrl: './task-instance-delete-dialog.component.html',
})
export class TaskInstanceDeleteDialogComponent {
  taskInstance?: ITaskInstance;

  constructor(protected taskInstanceService: TaskInstanceService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.taskInstanceService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
