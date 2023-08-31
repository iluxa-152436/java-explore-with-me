package ru.practicum.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.service.CategoryService;
import ru.practicum.explorewithme.dto.CategoryDto;
import ru.practicum.explorewithme.dto.NewCategoryRequest;
import ru.practicum.explorewithme.dto.NewUserRequest;
import ru.practicum.explorewithme.dto.UserDto;
import ru.practicum.explorewithme.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final CategoryService categoryService;

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto postUser(@RequestBody @Valid NewUserRequest newUserRequest) {
        return userService.addNewUser(newUserRequest);
    }

    @GetMapping("/users")
    public List<UserDto> getUsers(@RequestParam Optional<List<Long>> ids,
                                  @RequestParam(defaultValue = "0") int from,
                                  @RequestParam(defaultValue = "10") int size) {
        return userService.getUsers(ids, from, size);
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
    }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto postCategory(@RequestBody @Valid NewCategoryRequest newCategoryRequest) {
        return categoryService.addNewCategory(newCategoryRequest);
    }

    @DeleteMapping("/categories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable long id) {
        categoryService.deleteCategory(id);
    }

    @PatchMapping("/categories/{id}")
    public CategoryDto patchCategory(@RequestBody @Valid NewCategoryRequest newCategoryRequest,
                                     @PathVariable long id) {
        return categoryService.updateCategory(id, newCategoryRequest);
    }
}
