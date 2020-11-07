package my.learning;

import my.learning.domain.Board;
import my.learning.domain.User;
import my.learning.domain.enums.BoardType;
import my.learning.repository.BoardRepository;
import my.learning.repository.UserRepository;
import my.learning.resolver.UserArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

@SpringBootApplication
public class Springboot2Web extends WebMvcConfigurerAdapter {
    public static void main(String[] args) {
        SpringApplication.run(Springboot2Web.class, args);
    }

    // 커스터마이징 된 리졸버
    private UserArgumentResolver userArgumentResolver;

    @Autowired
    public void setUserArgumentResolver(UserArgumentResolver userArgumentResolver) {
        this.userArgumentResolver = userArgumentResolver;
    }

    /**
     * 커스텀 HandlerMethodArgumentResolver 를 필터에 등록시켜야 한다
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(userArgumentResolver);
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
