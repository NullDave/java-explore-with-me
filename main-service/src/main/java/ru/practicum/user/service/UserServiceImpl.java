package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.UserMapper;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllByAdmin(List<Long> userIds, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        if (userIds != null) {
            return userMapper.toUserDtoList(userRepository.findAllByIdIn(userIds, page));
        }
        return userMapper.toUserDtoList(userRepository.findAll(page).toList());
    }

    @Override
    public UserDto addByAdmin(UserDto userDto) {
        return userMapper.toUserDto(userRepository.save(userMapper.toUser(userDto)));
    }

    @Override
    public void deleteByAdmin(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Not found user by id:" + userId);
        }
        userRepository.deleteById(userId);
    }

}
