package ie.corballis.fixtures.annotation;

import ie.corballis.fixtures.core.BeanFactory;
import ie.corballis.fixtures.core.ObjectMapperProvider;
import ie.corballis.fixtures.io.ClassPathFixtureScanner;
import ie.corballis.fixtures.util.FieldSetter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;

public class FixtureAnnotations {

    private static final Map<Class<? extends Annotation>, FieldAnnotationProcessor<?>> annotationProcessorMap = newHashMap();

    static {
        annotationProcessorMap.put(Fixture.class, new FixtureFieldAnnotationProcessor());
    }

    public static void initFixtures(Object targetInstance) throws Exception {
        checkNotNull(targetInstance, "Target instance must not be null");

        BeanFactory beanFactory = new BeanFactory(ObjectMapperProvider.getObjectMapper(), new ClassPathFixtureScanner());
        beanFactory.init();

        processAnnotations(targetInstance, beanFactory);
    }

    private static void processAnnotations(Object targetInstance, BeanFactory beanFactory) throws Exception {
        Field[] fields = targetInstance.getClass().getDeclaredFields();
        for (Field field : fields) {
            for (Annotation annotation : field.getAnnotations()) {
                Object bean = generateFixture(annotation, field, beanFactory);
                if (bean != null) {
                    try {
                        new FieldSetter(targetInstance, field).set(bean);
                    } catch (Exception e) {
                        throw new Exception("Problems setting field " + field.getName() + " annotated with "
                                                    + annotation, e);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static Object generateFixture(
            Annotation annotation, Field field,
            BeanFactory beanFactory) throws IllegalAccessException, InstantiationException, IOException {

        Object result = null;

        if (annotationProcessorMap.containsKey(annotation.annotationType())) {
            FieldAnnotationProcessor processor = annotationProcessorMap.get(annotation.annotationType());
            result = processor.process(annotation, field, beanFactory);
        }

        return result;
    }

}
