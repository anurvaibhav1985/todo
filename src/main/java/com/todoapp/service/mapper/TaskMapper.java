package com.todoapp.service.mapper;

import com.todoapp.domain.*;
import com.todoapp.service.dto.TaskDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Task} and its DTO {@link TaskDTO}.
 */
@Mapper(componentModel = "spring", uses = { TodoMapper.class })
public interface TaskMapper extends EntityMapper<TaskDTO, Task> {
    @Mapping(target = "todo", source = "todo", qualifiedByName = "id")
    TaskDTO toDto(Task s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TaskDTO toDtoId(Task task);
}
