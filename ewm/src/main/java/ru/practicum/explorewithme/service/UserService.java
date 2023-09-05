package ru.practicum.explorewithme.service;

import ru.practicum.explorewithme.dto.NewUserRequest;
import ru.practicum.explorewithme.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDto addNewUser(NewUserRequest newUserRequest);

    List<UserDto> getUsers(Optional<List<Long>> userIds, int from, int size);

    void deleteUser(long userId);

    void verifyUserExistence(long userId);
}
