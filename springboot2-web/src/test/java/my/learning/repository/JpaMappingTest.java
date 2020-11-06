package my.learning.repository;

import my.learning.domain.Board;
import my.learning.domain.User;
import my.learning.domain.enums.BoardType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class JpaMappingTest {

    private final String boardTestTitle = "test";
    private final String email = "test@gmail.com";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BoardRepository boardRepository;

    @BeforeEach
    public void setUp() {
        User user = userRepository.save(User.builder()
                .name("havi")
                .password("test")
                .email(email)
                .createdDate(LocalDateTime.now())
                .build()
        );
        boardRepository.save(Board.builder()
                .title(boardTestTitle)
                .subTitle("subTitle")
                .content("content")
                .boardType(BoardType.FREE)
                .createdDate(LocalDateTime.now())
                .updatedDate(LocalDateTime.now())
                .user(user)
                .build()
        );
    }

    @Test
    public void oneToOneTest() {
        User user = userRepository.findByEmail(email);
        assertEquals("havi", user.getName());
        assertEquals("test", user.getPassword());
        assertEquals("test@gmail.com", user.getEmail());

        Board board = boardRepository.findByUser(user);
        assertEquals(boardTestTitle, board.getTitle());
        assertEquals("subTitle", board.getSubTitle());
        assertEquals("content", board.getContent());
        assertEquals(BoardType.FREE, board.getBoardType());


    }
}
