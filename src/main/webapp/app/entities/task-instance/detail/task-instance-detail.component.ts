import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITaskInstance } from '../task-instance.model';

@Component({
  selector: 'jhi-task-instance-detail',
  templateUrl: './task-instance-detail.component.html',
})
export class TaskInstanceDetailComponent implements OnInit {
  taskInstance: ITaskInstance | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ taskInstance }) => {
      this.taskInstance = taskInstance;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
