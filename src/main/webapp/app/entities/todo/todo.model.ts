import { ITask } from 'app/entities/task/task.model';
import { ITaskInstance } from 'app/entities/task-instance/task-instance.model';
import { IUser } from 'app/entities/user/user.model';

export interface ITodo {
  id?: number;
  name?: string | null;
  desc?: string | null;
  active?: string | null;
  version?: number | null;
  tasks?: ITask[] | null;
  taskInstances?: ITaskInstance[] | null;
  user?: IUser | null;
}

export class Todo implements ITodo {
  constructor(
    public id?: number,
    public name?: string | null,
    public desc?: string | null,
    public active?: string | null,
    public version?: number | null,
    public tasks?: ITask[] | null,
    public taskInstances?: ITaskInstance[] | null,
    public user?: IUser | null
  ) {}
}

export function getTodoIdentifier(todo: ITodo): number | undefined {
  return todo.id;
}
