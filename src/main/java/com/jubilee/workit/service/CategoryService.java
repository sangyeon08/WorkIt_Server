package com.jubilee.workit.service;

import com.jubilee.workit.dto.CategoryDto;
import com.jubilee.workit.entity.Category;
import com.jubilee.workit.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAllByOrderByNameAsc().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private CategoryDto toDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        // jobCount는 필요할 때 추가 쿼리로 조회, 계산 가능. 야리~
        dto.setJobCount(0);
        return dto;
    }
}