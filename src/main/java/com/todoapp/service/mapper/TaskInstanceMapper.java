package com.todoapp.service.mapper;

import com.todoapp.domain.*;
import com.todoapp.service.dto.TaskInstanceDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TaskInstance} and its DTO {@link TaskInstanceDTO}.
 */
@Mapper(componentModel = "spring", uses = { TaskMapper.class, TodoMapper.class, UserMapper.class })
public interface TaskInstanceMapper extends EntityMapper<TaskInstanceDTO, TaskInstance> {
    @Mapping(target = "task", source = "task", qualifiedByName = "id")
    @Mapping(target = "todo", source = "todo", qualifiedByName = "id")
    @Mapping(target = "user", source = "user", qualifiedByName = "id")
    TaskInstanceDTO toDto(TaskInstance s);
}
