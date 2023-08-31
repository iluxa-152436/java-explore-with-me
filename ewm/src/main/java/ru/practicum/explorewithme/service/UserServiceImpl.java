package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.entity.User;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.storage.UserStorage;
import ru.practicum.explorewithme.dto.NewUserRequest;
import ru.practicum.explorewithme.dto.UserDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage storage;
    private final ModelMapper mapper;

    @Override
    public UserDto addNewUser(NewUserRequest newUserRequest) {
        return mapper.map(storage.save(mapper.map(newUserRequest, User.class)), UserDto.class);
    }

    @Override
    public List<UserDto> getUsers(Optional<List<Long>> userIds, int from, int size) {
        if (userIds.isPresent()) {
            return storage.findByIdIn(userIds.get(), Page.getPageable(from, size)).stream()
                    .map(u -> mapper.map(u, UserDto.class))
                    .collect(Collectors.toList());
        } else {
            return storage.findAll(Page.getPageable(from, size)).stream()
                    .map(u-> mapper.map(u, UserDto.class))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public void deleteUser(long userId) {
        if (storage.existsById(userId)) {
            storage.deleteById(userId);
        } else throw new NotFoundException("User with id=" + userId + " was not found");
    }

    @Override
    public void verifyUserExistence(long userId) {
        if (!storage.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
    }
}
