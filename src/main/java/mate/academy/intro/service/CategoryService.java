package mate.academy.intro.service;

import java.awt.print.Pageable;
import java.util.List;
import mate.academy.intro.dto.CategoryDto;

public interface CategoryService {
    List<CategoryDto> findAll(Pageable pageable);

    CategoryDto getById(Long id);

    CategoryDto save(CategoryDto categoryDto);

    CategoryDto update(Long id, CategoryDto categoryDto);

    void deleteById(Long id);
}
