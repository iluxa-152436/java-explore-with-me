package ru.practicum.explorewithme.service.category;

import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.category.NewCategoryRequest;

import java.util.List;

public interface CategoryService {
    CategoryDto addNewCategory(NewCategoryRequest newCategoryRequest);

    void deleteCategory(long id);

    CategoryDto updateCategory(long id, NewCategoryRequest newCategoryRequest);

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategoryById(long categoryId);
}
