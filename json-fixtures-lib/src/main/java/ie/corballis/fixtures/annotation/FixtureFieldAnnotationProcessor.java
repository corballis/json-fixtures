package ie.corballis.fixtures.annotation;

import com.fasterxml.jackson.databind.JavaType;
import ie.corballis.fixtures.core.BeanFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import static ie.corballis.fixtures.util.ReflectionUtils.getFieldType;

public class FixtureFieldAnnotationProcessor implements FieldAnnotationProcessor<Fixture> {

    @Override
    public Object process(Fixture annotation, Field field, BeanFactory factory) {
        String[] fixtures = annotation.value();

        if (fixtures.length == 0) {
            fixtures = new String[] {field.getName()};
        }

        Type fieldType = getFieldType(field);
        if (fieldType instanceof Class) {
            return factory.create(annotation.referencePrefix(), (Class) fieldType, fixtures);
        }
        return factory.create(annotation.referencePrefix(), (JavaType) fieldType, fixtures);
    }

}