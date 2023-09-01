package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.dto.CategoryDto;
import ru.practicum.explorewithme.dto.NewCategoryRequest;
import ru.practicum.explorewithme.entity.Category;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.storage.CategoryStorage;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryStorage storage;
    private final ModelMapper mapper;

    @Override
    public CategoryDto addNewCategory(NewCategoryRequest newCategoryRequest) {
        return mapper.map(storage.save(mapper.map(newCategoryRequest, Category.class)), CategoryDto.class);
    }

    @Override
    public void deleteCategory(long id) {
        //TODO перед удалением проверить,что категория не использовалась ранее
        if (storage.existsById(id)) {
            storage.deleteById(id);
        } else {
            throw new NotFoundException("Category with id=" + id + " was not found");
        }
    }

    @Override
    public CategoryDto updateCategory(long id, NewCategoryRequest newCategoryRequest) {
        if (storage.existsById(id)) {
            Category oldCategory = storage.findById(id).get();
            oldCategory.setName(newCategoryRequest.getName());
            return mapper.map(storage.save(oldCategory), CategoryDto.class);
        } else {
            throw new NotFoundException("Category with id=" + id + " was not found");
        }
    }
}