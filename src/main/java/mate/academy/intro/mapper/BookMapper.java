package mate.academy.intro.mapper;

import java.util.List;
import java.util.Set;
import mate.academy.intro.config.MapperConfig;
import mate.academy.intro.dto.BookDto;
import mate.academy.intro.dto.BookDtoWithoutCategoryIds;
import mate.academy.intro.dto.CreateBookRequestDto;
import mate.academy.intro.model.Book;
import mate.academy.intro.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface BookMapper {

    @Mapping(target = "categoryIds", source = "categories", qualifiedByName = "categoriesToIds")
    BookDto bookToBookDto(Book book);

    Book toModel(CreateBookRequestDto requestDto);

    Book updateBook(@MappingTarget Book book, CreateBookRequestDto bookDto);

    BookDtoWithoutCategoryIds toDtoWithoutCategories(Book book);

    @Named("bookFromId")
    default Book bookFromId(Long id) {
        Book book = new Book();
        book.setId(id);
        return book;
    }

    @Named("categoriesToIds")
    default List<Long> categoriesToIds(Set<Category> categories) {
        if (categories == null) {
            return null;
        }
        return categories.stream()
                .map(Category::getId)
                .sorted()
                .toList();
    }
}
