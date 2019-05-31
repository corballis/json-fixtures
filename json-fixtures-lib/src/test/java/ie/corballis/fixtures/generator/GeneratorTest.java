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
import static ie.corballis.fixtures.settings.SettingsHolder.settings;
import static ie.corballis.fixtures.util.StringUtils.unifyLineEndings;
import static org.assertj.core.api.Assertions.assertThat;

public class GeneratorTest {

    private static final String fileNamePrefix = "sample1";
    private static final String fixtureName = "sampleFixture1";
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private String folder;
    private boolean append;

    @Before
    public void setUp() throws Exception {
        folder = Files.createTempDirectory("fixture-generator").toAbsolutePath().toString();
    }

    @Test
    public void fixtureFileWritingTest() throws Exception {
        append = true;

        Map<String, Object> objectAsMap =
            new DefaultFixtureGenerator().generateMapFromBeanDirectly(SampleClassCollections.class);
        new GeneratorFixtureWriter(settings().getObjectMapper()).write(folder,
                                                                       fileNamePrefix,
                                                                       fixtureName,
                                                                       objectAsMap);
        String expectedContents = Resources.toString(getResource("generated.fixtures.out"), Charsets.UTF_8);
        String actualContents = new String(Files.readAllBytes(Paths.get(folder, fileNamePrefix + ".fixtures.json")));
        assertThat(unifyLineEndings(actualContents)).isEqualTo(unifyLineEndings(expectedContents));
    }

}