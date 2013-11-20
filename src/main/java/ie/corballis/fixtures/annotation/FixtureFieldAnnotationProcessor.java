package ie.corballis.fixtures.annotation;

import com.fasterxml.jackson.core.JsonProcessingException;
import ie.corballis.fixtures.core.BeanFactory;

import java.lang.reflect.Field;

public class FixtureFieldAnnotationProcessor implements FieldAnnotationProcessor<Fixture> {

    @Override
    public Object process(
            Fixture annotation, Field field,
            BeanFactory factory) throws IllegalAccessException, JsonProcessingException, InstantiationException {

        String[] fixtures = annotation.value();
        return factory.create(field.getType(), fixtures);
    }

}
