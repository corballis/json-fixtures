package ie.corballis.fixtures.annotation;

import ie.corballis.fixtures.core.BeanFactory;
import ie.corballis.fixtures.util.ClassUtils;

import java.io.IOException;
import java.lang.reflect.Field;

public class FixtureFieldAnnotationProcessor implements FieldAnnotationProcessor<Fixture> {

    @Override
    public Object process(Fixture annotation, Field field, BeanFactory factory) throws IllegalAccessException,
                                                                                       IOException,
                                                                                       InstantiationException {
        String[] fixtures = annotation.value();

        // if no fixture name is specified as annotation parameter, the default name is the name of the field
        if (fixtures.length == 0) {
            fixtures = new String[] {field.getName()};
        }

        return factory.create(ClassUtils.getFixtureType(field), fixtures);
    }

}