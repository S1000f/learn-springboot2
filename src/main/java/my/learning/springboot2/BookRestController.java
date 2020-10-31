package my.learning.springboot2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookRestController {

    @Autowired
    private BookRestService bookRestService;

    @GetMapping(value = "/rest/test", produces = "application/json")
    public Book getRestBooks() {
        return bookRestService.getRestBook();
    }
}
