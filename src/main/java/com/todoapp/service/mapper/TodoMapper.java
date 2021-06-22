package com.todoapp.service.mapper;

import com.todoapp.domain.*;
import com.todoapp.service.dto.TodoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Todo} and its DTO {@link TodoDTO}.
 */
@Mapper(componentModel = "spring", uses = { UserMapper.class})
public interface TodoMapper extends EntityMapper<TodoDTO, Todo> {
    @Mapping(target = "user", source = "user", qualifiedByName = "id")
    TodoDTO toDto(Todo s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TodoDTO toDtoId(Todo todo);
}
