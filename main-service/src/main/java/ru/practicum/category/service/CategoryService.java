package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto getByPublic(Long categoryId);

    List<CategoryDto> getAllByPublic(Integer from, Integer size);

    CategoryDto addByAdmin(NewCategoryDto newCategoryDto);

    CategoryDto updateByAdmin(CategoryDto categoryDto, Long categoryId);

    void deleteByAdmin(Long categoryId);

}
