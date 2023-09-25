package ie.corballis.fixtures.io.scanner;

public class TestFileNameFixtureScanner extends PrefixFixtureScanner {

    public TestFileNameFixtureScanner(Class testClass) {
        super(testClass.getSimpleName());
    }

}