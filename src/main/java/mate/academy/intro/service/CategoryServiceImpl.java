package mate.academy.intro.service;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.intro.dto.BookDtoWithoutCategoryIds;
import mate.academy.intro.dto.CategoryDto;
import mate.academy.intro.dto.CreateCategoryRequestDto;
import mate.academy.intro.exception.EntityNotFoundException;
import mate.academy.intro.mapper.BookMapper;
import mate.academy.intro.mapper.CategoryMapper;
import mate.academy.intro.model.Category;
import mate.academy.intro.repository.BookRepository;
import mate.academy.intro.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final CategoryMapper categoryMapper;
    private final BookMapper bookMapper;

    @Override
    public Page<CategoryDto> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(categoryMapper::toCategoryDto);
    }

    @Override
    public CategoryDto getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found with id " + id));
        return categoryMapper.toCategoryDto(category);
    }

    @Override
    public CategoryDto save(@Valid CreateCategoryRequestDto categoryDto) {
        Category category = categoryMapper.toEntity(categoryDto);
        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public CategoryDto update(Long id, CreateCategoryRequestDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cant find category by id " + id));
        categoryMapper.update(category, categoryDto);
        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public void deleteById(Long id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Can not find category with id: " + id);
        }
    }

    @Override
    public List<BookDtoWithoutCategoryIds> getBookCategoryById(Long categoryId) {
        return bookRepository.findAllByCategoriesId(categoryId).stream()
                .map(bookMapper::toDtoWithoutCategories)
                .toList();
    }
}
