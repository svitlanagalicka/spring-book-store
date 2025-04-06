package mate.academy.intro.service;

import jakarta.validation.Valid;
import java.awt.print.Pageable;
import java.util.List;
import mate.academy.intro.dto.CreateCategoryRequestDto;

public interface CategoryService {
    List<CreateCategoryRequestDto> findAll(Pageable pageable);

    CreateCategoryRequestDto getById(Long id);

    CreateCategoryRequestDto save(@Valid CreateCategoryRequestDto categoryDto);

    CreateCategoryRequestDto update(Long id, CreateCategoryRequestDto categoryDto);

    void deleteById(Long id);
}
