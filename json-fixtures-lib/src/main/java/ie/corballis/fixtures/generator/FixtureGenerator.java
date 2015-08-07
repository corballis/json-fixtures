package ie.corballis.fixtures.generator;

import java.util.Map;

public interface FixtureGenerator {
    String generateJsonStringFromBean(Class clazz) throws Exception;

    Map<String, Object> generateMapFromBeanDirectly(Class clazz) throws Exception;

    Map<String, Object> generateMapFromBeanThroughJsonString(Class clazz) throws Exception;
}