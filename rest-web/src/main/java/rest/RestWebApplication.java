package rest;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import rest.domain.Board;
import rest.domain.User;
import rest.domain.enums.BoardType;
import rest.repository.BoardRepository;
import rest.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

@SpringBootApplication
public class RestWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(RestWebApplication.class, args);
    }

    @Bean
    public CommandLineRunner runner(UserRepository userRepository, BoardRepository boardRepository) {
        return args -> {
            User user = new User();
            user.setName("asdf");
            user.setPassword("test");
            user.setEmail("asd@gmail.com");
            user.setCreatedDate(LocalDateTime.now());
            userRepository.save(user);

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
