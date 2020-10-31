package my.learning.springboot2;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(BookRestService.class)
public class BookRestControllerTest {

    @Autowired
    private BookRestService bookRestService;
    @Autowired
    private MockRestServiceServer serviceServer;

    @Test
    public void restTest() {
        this.serviceServer.expect(requestTo("/rest/test"))
                .andRespond(withSuccess(
                        new ClassPathResource("/test.json", getClass()), MediaType.APPLICATION_JSON)
                );

        Book book = this.bookRestService.getRestBook();
        assertThat(book.getTitle()).isEqualTo("test");

    }
}
