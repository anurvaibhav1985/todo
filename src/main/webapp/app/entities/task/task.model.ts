import * as dayjs from 'dayjs';
import { ITaskInstance } from 'app/entities/task-instance/task-instance.model';
import { ITodo } from 'app/entities/todo/todo.model';

export interface ITask {
  id?: number;
  name?: string | null;
  type?: string | null;
  desc?: string | null;
  plannedStartDate?: dayjs.Dayjs | null;
  plannedEndDate?: dayjs.Dayjs | null;
  active?: string | null;
  version?: number | null;
  taskInstances?: ITaskInstance[] | null;
  todo?: ITodo | null;
}

export class Task implements ITask {
  constructor(
    public id?: number,
    public name?: string | null,
    public type?: string | null,
    public desc?: string | null,
    public plannedStartDate?: dayjs.Dayjs | null,
    public plannedEndDate?: dayjs.Dayjs | null,
    public active?: string | null,
    public version?: number | null,
    public taskInstances?: ITaskInstance[] | null,
    public todo?: ITodo | null
  ) {}
}

export function getTaskIdentifier(task: ITask): number | undefined {
  return task.id;
}
