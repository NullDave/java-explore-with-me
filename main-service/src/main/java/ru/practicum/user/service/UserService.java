package ru.practicum.user.service;

import ru.practicum.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllByAdmin(List<Long> userIds, Integer from, Integer size);

    UserDto addByAdmin(UserDto userDto);

    void deleteByAdmin(Long userId);
}
