package mate.academy.intro.mapper;

import java.math.BigDecimal;
import mate.academy.intro.dto.BookDto;
import mate.academy.intro.dto.CreateBookRequestDto;
import mate.academy.intro.model.Book;
import org.springframework.stereotype.Component;

@Component
public class BookMapperImpl implements BookMapper {
    @Override
    public BookDto bookToBookDto(Book book) {
        BookDto bookDto = new BookDto();
        bookDto.setId(book.getId());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setTitle(book.getTitle());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setPrice(book.getPrice());
        bookDto.setDescription(book.getDescription());
        bookDto.setCoverImage(book.getCoverImage());
        return bookDto;
    }

    @Override
    public Book toModel(CreateBookRequestDto requestDto) {
        Book book = new Book();
        book.setAuthor(requestDto.getAuthor());
        book.setTitle(requestDto.getTitle());
        book.setPrice(BigDecimal.valueOf(requestDto.getPrice()));
        book.setIsbn(requestDto.getIsbn());
        book.setDescription(requestDto.getDescription());
        book.setCoverImage(requestDto.getCoverImage());
        return book;
    }
}
