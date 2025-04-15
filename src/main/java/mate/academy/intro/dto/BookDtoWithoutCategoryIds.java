package mate.academy.intro.dto;

import java.math.BigDecimal;

public record BookDtoWithoutCategoryIds(
        Long id,
        String title,
        String author,
        String description,
        BigDecimal price,
        String isbn,
        String coverImage) {
}
