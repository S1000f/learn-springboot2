package my.learning.springboot2;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

@DataJpaTest
public class BookJpaTest {

    @Autowired
    private static TestEntityManager testEntityManager;
    @Autowired
    private BookRepository bookRepository;
    private static Long id1;
    private static Long id2;
    private static Long id3;

    @BeforeAll
    static void setUp() {
        String title = "Spring Data JPA test";
        Book book = Book.builder()
                .title(title + "1")
                .publishedAt(LocalDateTime.now())
                .build();
        Book book2 = Book.builder()
                .title(title + "2")
                .publishedAt(LocalDateTime.now())
                .build();
        Book book3 = Book.builder()
                .title(title + "3")
                .publishedAt(LocalDateTime.now())
                .build();

        id1 = testEntityManager.persistAndGetId(book, Long.TYPE);
        id2 = testEntityManager.persistAndGetId(book, Long.TYPE);
        id3 = testEntityManager.persistAndGetId(book, Long.TYPE);
    }

    @Test
    public void getTest() {
        Book book1 = bookRepository.getOne(id1);
    }

}
