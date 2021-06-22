import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'todo',
        data: { pageTitle: 'todoApp.todo.home.title' },
        loadChildren: () => import('./todo/todo.module').then(m => m.TodoModule),
      },
      {
        path: 'task',
        data: { pageTitle: 'todoApp.task.home.title' },
        loadChildren: () => import('./task/task.module').then(m => m.TaskModule),
      },
      {
        path: 'task-instance',
        data: { pageTitle: 'todoApp.taskInstance.home.title' },
        loadChildren: () => import('./task-instance/task-instance.module').then(m => m.TaskInstanceModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
