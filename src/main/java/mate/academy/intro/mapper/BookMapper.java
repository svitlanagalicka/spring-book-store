package mate.academy.intro.mapper;

import mate.academy.intro.dto.BookDto;
import mate.academy.intro.dto.CreateBookRequestDto;
import mate.academy.intro.model.Book;

public interface BookMapper {

    BookDto bookToBookDto(Book book);

    Book toModel(CreateBookRequestDto requestDto);
}
