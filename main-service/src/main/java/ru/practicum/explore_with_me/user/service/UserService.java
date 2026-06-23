package ru.practicum.explore_with_me.user.service;

import ru.practicum.explore_with_me.user.dto.NewUserRequest;
import ru.practicum.explore_with_me.user.dto.UserDto;
import ru.practicum.explore_with_me.user.model.User;

import java.util.List;

public interface UserService {
    User getUserById(Long id);

    List<UserDto> getListUserDtoByIds(List<Long> ids, Integer from, Integer size);

    UserDto addUser(NewUserRequest newUserRequest);

    void deleteUser(Long userId);
}
