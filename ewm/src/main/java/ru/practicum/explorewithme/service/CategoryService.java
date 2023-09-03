package ru.practicum.explorewithme.service;

import ru.practicum.explorewithme.dto.CategoryDto;
import ru.practicum.explorewithme.dto.NewCategoryRequest;

import java.util.List;

public interface CategoryService {
    CategoryDto addNewCategory(NewCategoryRequest newCategoryRequest);
    void deleteCategory(long id);
    CategoryDto updateCategory(long id, NewCategoryRequest newCategoryRequest);

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategoryById(long categoryId);
}
