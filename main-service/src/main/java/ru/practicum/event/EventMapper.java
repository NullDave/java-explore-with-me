package ru.practicum.event;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.category.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.location.LocationMapper;
import ru.practicum.user.UserMapper;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {UserMapper.class, CategoryMapper.class, LocationMapper.class},
        imports = {Category.class})
public interface EventMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", expression = "java(Category.builder().id(newEventDto.getCategory()).build())")
    Event toEvent(NewEventDto newEventDto);

    EventFullDto toEventFullDto(Event event);

    List<EventFullDto> toEventFullDtoList(List<Event> events);

    List<EventShortDto> toEventShortDtoList(List<Event> events);


}
