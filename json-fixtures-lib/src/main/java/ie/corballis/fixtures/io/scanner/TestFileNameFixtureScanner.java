package ie.corballis.fixtures.io.scanner;

import org.reflections.Reflections;

public class TestFileNameFixtureScanner extends PrefixFixtureScanner {

    public TestFileNameFixtureScanner(Class testClass) {
        this(testClass, null);
    }

    public TestFileNameFixtureScanner(Class testClass, Reflections reflections) {
        super(testClass.getSimpleName(), reflections);
    }

}