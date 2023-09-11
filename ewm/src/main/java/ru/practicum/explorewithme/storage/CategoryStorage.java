package ru.practicum.explorewithme.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.entity.Category;

public interface CategoryStorage extends JpaRepository<Category, Long> {
}
