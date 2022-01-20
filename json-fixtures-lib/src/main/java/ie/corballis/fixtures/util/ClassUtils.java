package ie.corballis.fixtures.util;

import java.lang.reflect.Method;
import java.util.Arrays;

import static ie.corballis.fixtures.core.InvocationContextHolder.getTestExecutorThread;

public class ClassUtils {

    private static final String JUNIT4_TEST = "org.junit.Test";
    private static final String JUNIT5_TEST = "org.junit.jupiter.api.Test";
    private static final String[] TEST_ANNOTATIONS = {JUNIT4_TEST, JUNIT5_TEST};

    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back to system class loader...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = ClassUtils.class.getClassLoader();
        }
        return cl;
    }

    public static String getTestMethodName() {
        StackTraceElement[] stackTrace = getTestExecutorThread().getStackTrace();
        return Arrays.stream(stackTrace)
                     .filter(ClassUtils::isTestMethod)
                     .map(StackTraceElement::getMethodName)
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException(
                         "Could not find any method annotated with @Test. This assertion only supports JUnit testcases."));
    }

    private static boolean isTestMethod(StackTraceElement stackTraceElement) {
        try {
            Class<?> stackTraceClass = Class.forName(stackTraceElement.getClassName());
            Method declaredMethod = stackTraceClass.getDeclaredMethod(stackTraceElement.getMethodName());
            return Arrays.stream(declaredMethod.getAnnotations())
                         .anyMatch(annotation -> Arrays.stream(TEST_ANNOTATIONS)
                                                       .anyMatch(testAnnotation -> testAnnotation.equals(annotation.annotationType()
                                                                                                                   .getCanonicalName())));
        } catch (Exception e) {
            return false;
        }
    }

    public static Class getTestClass() {
        return getClass(getTestClassName());
    }

    public static String getTestClassName() {
        StackTraceElement[] stackTrace = getTestExecutorThread().getStackTrace();
        return Arrays.stream(stackTrace)
                     .filter(ClassUtils::isTestMethod)
                     .map(StackTraceElement::getClassName)
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException("Could not find test class. " +
                                                                     "This could happen because there was no method annotated with @Test. " +
                                                                     "This assertion only supports JUnit testcases."));
    }

    public static Class getClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}