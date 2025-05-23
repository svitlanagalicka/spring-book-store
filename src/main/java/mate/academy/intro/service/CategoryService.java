package mate.academy.intro.service;

import java.util.List;
import mate.academy.intro.dto.BookDtoWithoutCategoryIds;
import mate.academy.intro.dto.CategoryDto;
import mate.academy.intro.dto.CreateCategoryRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    Page<CategoryDto> findAll(Pageable pageable);

    CategoryDto getById(Long id);

    CategoryDto save(CreateCategoryRequestDto categoryDto);

    CategoryDto update(Long id, CreateCategoryRequestDto categoryDto);

    void deleteById(Long id);

    List<BookDtoWithoutCategoryIds> getBookCategoryById(Long categoryId);
}
