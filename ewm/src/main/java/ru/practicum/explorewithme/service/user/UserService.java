package ru.practicum.explorewithme.service.user;

import ru.practicum.explorewithme.dto.user.NewUserRequest;
import ru.practicum.explorewithme.dto.user.UserDto;
import ru.practicum.explorewithme.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDto addNewUser(NewUserRequest newUserRequest);

    List<UserDto> getUsers(Optional<List<Long>> userIds, int from, int size);

    void deleteUser(long userId);

    void verifyUserExistence(long userId);

    User getUser(long userId);
}
