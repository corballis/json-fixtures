package ie.corballis.fixtures.annotation;

import ie.corballis.fixtures.core.BeanFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public interface FieldAnnotationProcessor<A extends Annotation> {

    Object process(A annotation, Field field, BeanFactory factory) throws IllegalAccessException, IOException, InstantiationException;

}
