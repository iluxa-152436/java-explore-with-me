package ru.explorewithme.service;

import ru.explorewithme.dto.CategoryDto;
import ru.explorewithme.dto.NewCategoryRequest;

public interface CategoryService {
    CategoryDto addNewCategory(NewCategoryRequest newCategoryRequest);
    void deleteCategory(long id);
    CategoryDto updateCategory(long id, NewCategoryRequest newCategoryRequest);
}
