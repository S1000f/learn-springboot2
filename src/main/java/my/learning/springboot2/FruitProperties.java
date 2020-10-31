package my.learning.springboot2;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@ConfigurationProperties(prefix = "fruit")
@Component
public class FruitProperties {
    private List<Fruit> list;
}
