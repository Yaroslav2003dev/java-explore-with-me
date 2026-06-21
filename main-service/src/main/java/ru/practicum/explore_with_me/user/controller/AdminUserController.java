package ru.practicum.explore_with_me.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explore_with_me.user.dto.NewUserRequest;
import ru.practicum.explore_with_me.user.dto.UserDto;
import ru.practicum.explore_with_me.user.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/admin/users")
public class AdminUserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDto>> getUserDtoList(@RequestParam(value = "ids", required = false) List<Long> ids, @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from, @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        return new ResponseEntity<>(userService.getListUserDtoByIds(ids, from, size), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserDto> addUser(@Valid @RequestBody NewUserRequest newUserRequest) {
        return new ResponseEntity<>(userService.addUser(newUserRequest), HttpStatus.CREATED);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

}
