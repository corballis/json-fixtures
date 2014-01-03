package ie.corballis.fixtures.annotation;

import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.SimpleType;
import ie.corballis.fixtures.core.BeanFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

public class FixtureFieldAnnotationProcessor implements FieldAnnotationProcessor<Fixture> {

    @Override
    public Object process(
            Fixture annotation, Field field,
            BeanFactory factory) throws IllegalAccessException, IOException, InstantiationException {

        Object bean;
        String[] fixtures = annotation.value();
        Class<?> fieldType = field.getType();

        if (Collection.class.isAssignableFrom(fieldType) && field.getGenericType() instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
            Type fieldArgType = parameterizedType.getActualTypeArguments()[0];
            SimpleType elementType = SimpleType.construct((Class<?>) fieldArgType);
            CollectionType collectionType = CollectionType.construct(fieldType, elementType);
            bean = factory.create(collectionType, fixtures);
        } else {
            bean = factory.create(fieldType, fixtures);
        }

        return bean;
    }

}
