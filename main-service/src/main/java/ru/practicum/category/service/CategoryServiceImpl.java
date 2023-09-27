package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.CategoryMapper;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.UseException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getByPublic(Long categoryId) {
        return categoryMapper.toCategoryDto(categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Not find category by id:" + categoryId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllByPublic(Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        return categoryMapper.toCategoryDtoList(categoryRepository.findAll(page).toList());
    }

    @Override
    public CategoryDto addByAdmin(NewCategoryDto category) {
        return categoryMapper.toCategoryDto(categoryRepository.save(categoryMapper.toCategory(category)));
    }

    @Override
    public CategoryDto updateByAdmin(CategoryDto categoryDto, Long categoryId) {
        existsById(categoryId);
        categoryDto.setId(categoryId);
        return categoryMapper.toCategoryDto(categoryRepository.save(categoryMapper.toCategory(categoryDto)));
    }

    @Override
    public void deleteByAdmin(Long categoryId) {
        existsById(categoryId);
        if (!eventRepository.findByCategoryId(categoryId).isEmpty()) {
            throw new UseException("This is category uses in events, category by id:" + categoryId);
        }
        categoryRepository.deleteById(categoryId);
    }

    private void existsById(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new NotFoundException("Not find category by id:" + categoryId);
        }
    }
}
