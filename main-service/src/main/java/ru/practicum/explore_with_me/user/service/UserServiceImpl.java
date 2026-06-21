package ru.practicum.explore_with_me.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore_with_me.exception.ConflictException;
import ru.practicum.explore_with_me.exception.NotFoundException;
import ru.practicum.explore_with_me.user.dto.NewUserRequest;
import ru.practicum.explore_with_me.user.dto.UserDto;
import ru.practicum.explore_with_me.user.mapper.UserMapper;
import ru.practicum.explore_with_me.user.model.User;
import ru.practicum.explore_with_me.user.repository.UserRepository;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь с id = " + id + " не найден"));
    }

    @Override
    public List<UserDto> getListUserDtoByIds(List<Long> ids, Integer from, Integer size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        List<User> userList;
        if (ids == null || ids.isEmpty()) {
            userList = userRepository.findAll(pageable).getContent();
        } else {
            userList = userRepository.findAllByIds(ids, pageable);
        }
        return UserMapper.toUserDtoList(userList);
    }

    @Transactional
    @Override
    public UserDto addUser(NewUserRequest newUserRequest) {
        if (userRepository.existsByEmail(newUserRequest.getEmail())) {
            throw new ConflictException("Email: " + newUserRequest.getEmail() + " - не является уникальным");
        }
        User user = UserMapper.toUser(newUserRequest);
        User savedUser = userRepository.save(user);
        return UserMapper.toUserDto(savedUser);
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        User user = getUserById(userId);
        UserDto dto = UserMapper.toUserDto(user);
        userRepository.delete(user);
    }

}
