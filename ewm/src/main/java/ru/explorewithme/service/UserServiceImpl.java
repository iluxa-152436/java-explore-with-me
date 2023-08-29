package ru.explorewithme.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.explorewithme.dto.NewUserRequest;
import ru.explorewithme.dto.UserDto;
import ru.explorewithme.entity.User;
import ru.explorewithme.exception.NotFoundException;
import ru.explorewithme.storage.UserStorage;

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
            return storage.findByIdIn(userIds.get(), getPageable(from, size)).stream()
                    .map(u -> mapper.map(u, UserDto.class))
                    .collect(Collectors.toList());
        } else {
            return storage.findAll(getPageable(from, size)).stream()
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

    private static PageRequest getPageable(int from, int size) {
        if (from < 0 || size < 1) {
            throw new IllegalArgumentException("From and size must be valid");
        }
        return PageRequest.of(from / size, size);
    }
}
