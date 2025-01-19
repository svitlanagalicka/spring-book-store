package mate.academy.intro.repository;

import lombok.RequiredArgsConstructor;
import mate.academy.intro.dto.BookSearchParametersDto;
import mate.academy.intro.model.Book;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationBuilder implements SpecificationBuilder<Book> {
    private static final String TITLE_COLUMN = "title";
    private static final String AUTHOR_COLUMN = "author";
    private static final String ISBN_COLUMN = "isbn";
    private final SpecificationProviderManager<Book> bookSpecificationProviderManager;

    @Override
    public Specification<Book> build(BookSearchParametersDto searchParameters) {
        Specification<Book> specification = Specification.where(null);
        if (searchParameters.author() != null && searchParameters.author().length > 0) {
            specification = specification
                    .and(bookSpecificationProviderManager.getSpecificationProvider(AUTHOR_COLUMN)
                    .getSpecification(searchParameters.author()));
        }
        if (searchParameters.title() != null && searchParameters.title().length > 0) {
            specification = specification
                    .and(bookSpecificationProviderManager.getSpecificationProvider(TITLE_COLUMN)
                    .getSpecification(searchParameters.title()));
        }
        if ((searchParameters.isbn() != null) && (searchParameters.isbn().length > 0)) {
            specification = specification
                    .and(bookSpecificationProviderManager.getSpecificationProvider(ISBN_COLUMN)
                    .getSpecification(searchParameters.isbn()));
        }
        return specification;
    }
}
