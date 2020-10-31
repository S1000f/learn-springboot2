package my.learning.springboot2;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest(
        properties = {"property.value=testing"},
        classes = {Springboot2Application.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class Springboot2ApplicationTests {

    @Value("${property.value}")
    private String propertyValue;

    @Test
    void contextLoads() {
    }

    @Test
    public void test() {
        assertThat(propertyValue, is("testing"));
    }

}
