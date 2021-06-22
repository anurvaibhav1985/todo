import * as dayjs from 'dayjs';
import { ITask } from 'app/entities/task/task.model';
import { ITodo } from 'app/entities/todo/todo.model';
import { IUser } from 'app/entities/user/user.model';
import { TaskStatus } from 'app/entities/enumerations/task-status.model';

export interface ITaskInstance {
  id?: number;
  name?: string | null;
  type?: string | null;
  desc?: string | null;
  status?: TaskStatus | null;
  plannedStartDate?: dayjs.Dayjs | null;
  plannedEndDate?: dayjs.Dayjs | null;
  actualStartDate?: dayjs.Dayjs | null;
  actualEndDate?: dayjs.Dayjs | null;
  timeSpent?: number | null;
  active?: string | null;
  version?: number | null;
  task?: ITask | null;
  todo?: ITodo | null;
  user?: IUser | null;
}

export class TaskInstance implements ITaskInstance {
  constructor(
    public id?: number,
    public name?: string | null,
    public type?: string | null,
    public desc?: string | null,
    public status?: TaskStatus | null,
    public plannedStartDate?: dayjs.Dayjs | null,
    public plannedEndDate?: dayjs.Dayjs | null,
    public actualStartDate?: dayjs.Dayjs | null,
    public actualEndDate?: dayjs.Dayjs | null,
    public timeSpent?: number | null,
    public active?: string | null,
    public version?: number | null,
    public task?: ITask | null,
    public todo?: ITodo | null,
    public user?: IUser | null
  ) {}
}

export function getTaskInstanceIdentifier(taskInstance: ITaskInstance): number | undefined {
  return taskInstance.id;
}
