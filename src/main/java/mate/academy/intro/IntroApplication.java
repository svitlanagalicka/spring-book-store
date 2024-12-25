package mate.academy.intro;

import java.math.BigDecimal;
import java.util.List;
import mate.academy.intro.model.Book;
import mate.academy.intro.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class IntroApplication {
    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(IntroApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                Book book = new Book();
                book.setTitle("Head First Java");
                book.setAuthor("Kathy Sierra, Bert Bates");
                book.setPrice(BigDecimal.valueOf(700));
                book.setIsbn("1234567890");
                book.setDescription("A great book to learn Java basics");
                bookService.save(book);
                List<Book> books = bookService.findAll();
                System.out.println(books);
            }
        };
    }
}
