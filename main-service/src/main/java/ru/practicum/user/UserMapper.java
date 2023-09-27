package ru.practicum.user;

import org.mapstruct.Mapper;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserDto userDto);

    UserDto toUserDto(User user);

    List<UserDto> toUserDtoList(List<User> users);

    UserShortDto toUserShortDto(User user);
}
