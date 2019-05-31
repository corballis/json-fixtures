package ie.corballis.fixtures.io.write;

public class TestClassFileNamingStrategy implements FileNamingStrategy {

    public static final String FIXTURE_FILE_POSTFIX = ".fixtures.json";

    private static TestClassFileNamingStrategy instance;

    private TestClassFileNamingStrategy() {
    }

    public static synchronized TestClassFileNamingStrategy getInstance() {
        if (instance == null) {
            instance = new TestClassFileNamingStrategy();
        }
        return instance;
    }

    @Override
    public String getFileName(String folder, String fileNamePrefix, String fixtureName) {
        return fileNamePrefix + getFixtureFilePostfix();
    }

    protected String getFixtureFilePostfix() {
        return FIXTURE_FILE_POSTFIX;
    }
}
