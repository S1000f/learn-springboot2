package my.learning;

import my.learning.domain.Board;
import my.learning.domain.User;
import my.learning.domain.enums.BoardType;
import my.learning.repository.BoardRepository;
import my.learning.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

@SpringBootApplication
public class Springboot2Web {
    public static void main(String[] args) {
        SpringApplication.run(Springboot2Web.class, args);
    }

    @Bean
    public CommandLineRunner runner(UserRepository userRepository, BoardRepository boardRepository) {
        return args -> {
            User user = userRepository.save(User.builder()
                    .name("havi")
                    .password("test")
                    .email("havi@gmail.com")
                    .createdDate(LocalDateTime.now())
                    .build()
            );

            IntStream.rangeClosed(1, 200).forEach(index ->
                    boardRepository.save(Board.builder()
                            .title("title" + index)
                            .subTitle("number" + index)
                            .content("content")
                            .boardType(BoardType.FREE)
                            .createdDate(LocalDateTime.now())
                            .updatedDate(LocalDateTime.now())
                            .user(user)
                            .build()
                    )
            );
        };
    }
}
