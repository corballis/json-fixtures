package ie.corballis.fixtures.generator;

import java.util.Map;

public interface FixtureGenerator {
    Map<String, Object> generateMapFromBeanDirectly(Class clazz) throws Exception;
}