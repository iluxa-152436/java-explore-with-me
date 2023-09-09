package ru.practicum.explorewithme.service.category;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.category.NewCategoryRequest;
import ru.practicum.explorewithme.entity.Category;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.service.Page;
import ru.practicum.explorewithme.service.category.CategoryService;
import ru.practicum.explorewithme.storage.CategoryStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryStorage storage;
    private final ModelMapper mapper;

    @Override
    public CategoryDto addNewCategory(NewCategoryRequest newCategoryRequest) {
        return mapper.map(storage.save(mapper.map(newCategoryRequest, Category.class)), CategoryDto.class);
    }

    @Override
    public void deleteCategory(long id) {
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

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategories(int from, int size) {
        return storage.findAll(Page.getPageable(from, size, Optional.empty())).stream()
                .map(cat -> mapper.map(cat, CategoryDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(long categoryId) {
        Optional<Category> category = storage.findById(categoryId);
        if (category.isPresent()) {
            return mapper.map(category.get(), CategoryDto.class);
        } else {
            throw new NotFoundException("Category with id=" + categoryId + " was not found");
        }
    }
}
