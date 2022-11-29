package ru.practicum.ewm.mappers;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.NewCategoryDto;
import ru.practicum.ewm.entities.Category;

@Component
public class CategoryMapper {
    public CategoryDto toDto(Category c) {
        return new CategoryDto(c.getId(), c.getName());
    }

    public Category fromDto(NewCategoryDto c) {
        return new Category(c.getName());
    }
}
