package mate.academy.intro.dto;

import java.math.BigDecimal;
import lombok.Data;
import mate.academy.intro.model.Book;

@Data
public class BookDto extends Book {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private BigDecimal price;
    private String description;
    private String coverImage;
}
