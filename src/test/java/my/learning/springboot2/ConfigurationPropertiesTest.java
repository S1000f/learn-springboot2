package my.learning.springboot2;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest
public class ConfigurationPropertiesTest {

    @Value("${property.test.name}")
    private String propertyTestName;
    @Value("${propertyTest}")
    private String propertyTest;
    @Value("${noKey:default value}")
    private String defaultValue;
    @Value("${propertyTestList}")
    private String[] propertyTestArray;
    @Value("#{'${propertyTestList}'.toUpperCase()}")
    private List<String> propertyTestList;

    @Autowired
    private FruitProperties fruitProperties;

    @Test
    public void testValue() {
        assertThat(propertyTestName, is("property depth test"));
        assertThat(propertyTest, is("test"));
        assertThat(defaultValue, is("default value"));

        assertThat(propertyTestArray[0], is("a"));
        assertThat(propertyTestList.get(1), is("B"));
    }
}
