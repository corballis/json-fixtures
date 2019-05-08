package ie.corballis.fixtures.generator;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static com.google.common.io.Resources.getResource;
import static org.assertj.core.api.Assertions.assertThat;

public class GeneratorTest {

    private String folder;
    private static final String fileNamePrefix = "sample1";
    private static final String fixtureName = "sampleFixture1";
    private boolean append;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        folder = Files.createTempDirectory("fixture-generator").toAbsolutePath().toString();
    }

    @Test
    public void fixtureFileWritingTest() throws Exception {
        append = true;

        Map<String, Object> objectAsMap = new DefaultFixtureGenerator().generateMapFromBeanDirectly(
            SampleClassCollections.class);
        new DefaultFileSystemWriter().writeOut(folder, fileNamePrefix, fixtureName, objectAsMap, append);
        String expectedContents = Resources.toString(getResource("generated.fixtures.out"), Charsets.UTF_8);
        String actualContents = new String(Files.readAllBytes(Paths.get(folder, fileNamePrefix + ".fixtures.json")));
        assertThat(actualContents).isEqualTo(expectedContents);
    }

    @Test
    public void existingFileIsNotTouchedIfNotInAppendMode() throws Exception {
        fixtureFileWritingTest();

        append = false;
        expectedException.expect(Exception.class);
        expectedException.expectMessage("The fixture file already exists," +
                                        " but the user didn't allow appending the new fixture to its end" +
                                        " - so nothing has been executed!");

        Map<String, Object> objectAsMap = new DefaultFixtureGenerator().generateMapFromBeanDirectly(
            SampleClassCollections.class);
        new DefaultFileSystemWriter().writeOut(folder, fileNamePrefix, fixtureName, objectAsMap, append);

    }
    
    @Test
    public void existingFileIsNotTouchedIfItAlreadyHasAFixtureWithTheSameName() throws Exception {
        fixtureFileWritingTest();

        append = true;
        expectedException.expect(Exception.class);
        expectedException.expectMessage("There already exists a fixture with fixture name 'sampleFixture1' in this fixture file!");

        Map<String, Object> objectAsMap = new DefaultFixtureGenerator().generateMapFromBeanDirectly(
            SampleClassCollections.class);
        new DefaultFileSystemWriter().writeOut(folder, fileNamePrefix, fixtureName, objectAsMap, append);
    }  
    
    @Test
    public void appendMode() throws Exception {
        fixtureFileWritingTest();

        Map<String, Object> objectAsMap = new DefaultFixtureGenerator().generateMapFromBeanDirectly(
            SampleClassCollections.class);
        new DefaultFileSystemWriter().writeOut(folder, fileNamePrefix, "sampleFixture2", objectAsMap, append);

        String expectedContents = Resources.toString(getResource("generated2.fixtures.out"), Charsets.UTF_8);
        String actualContents = new String(Files.readAllBytes(Paths.get(folder, fileNamePrefix + ".fixtures.json")));
        assertThat(actualContents).isEqualTo(expectedContents);
    }

}