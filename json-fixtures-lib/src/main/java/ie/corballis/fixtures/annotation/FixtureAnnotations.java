package ie.corballis.fixtures.annotation;

import ie.corballis.fixtures.core.BeanFactory;
import ie.corballis.fixtures.settings.Settings;
import ie.corballis.fixtures.util.FieldSetter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;
import static ie.corballis.fixtures.core.InvocationContextHolder.initTestExecutorThread;
import static ie.corballis.fixtures.settings.Settings.defaultSettings;
import static ie.corballis.fixtures.settings.SettingsHolder.settings;
import static ie.corballis.fixtures.settings.SettingsHolder.updateSettings;
import static java.lang.Thread.currentThread;

public class FixtureAnnotations {

    private static final Map<Class<? extends Annotation>, FieldAnnotationProcessor<?>> annotationProcessorMap =
        newHashMap();

    static {
        annotationProcessorMap.put(Fixture.class, new FixtureFieldAnnotationProcessor());
    }

    public static void initFixtures(Object targetInstance) throws Exception {
        initFixtures(targetInstance, null);
    }

    public static void initFixtures(Object targetInstance, Settings.Builder settings) throws Exception {
        checkNotNull(targetInstance, "Target instance must not be null");
        updateSettings(settings == null ? defaultSettings() : settings.build());
        initTestExecutorThread(currentThread());

        settings().getSnapshotGenerator().validateSnapshots();

        processAnnotations(targetInstance, settings().getBeanFactory());
    }

    private static void processAnnotations(Object targetInstance, BeanFactory beanFactory) throws Exception {
        Class clazz = targetInstance.getClass();
        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                for (Annotation annotation : field.getAnnotations()) {
                    Object bean = generateFixture(annotation, field, beanFactory);
                    if (bean != null) {
                        try {
                            new FieldSetter(targetInstance, field).set(bean);
                        } catch (Exception e) {
                            throw new Exception(
                                "Problems setting field " + field.getName() + " annotated with " + annotation, e);
                        }
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    @SuppressWarnings("unchecked")
    private static Object generateFixture(Annotation annotation, Field field, BeanFactory beanFactory) throws
                                                                                                       IllegalAccessException,
                                                                                                       InstantiationException,
                                                                                                       IOException {

        Object result = null;

        if (annotationProcessorMap.containsKey(annotation.annotationType())) {
            FieldAnnotationProcessor processor = annotationProcessorMap.get(annotation.annotationType());
            result = processor.process(annotation, field, beanFactory);
        }

        return result;
    }

}