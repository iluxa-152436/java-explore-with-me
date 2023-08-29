package ru.explorewithme.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.explorewithme.entity.Category;

public interface CategoryStorage extends JpaRepository<Category, Long> {
}
