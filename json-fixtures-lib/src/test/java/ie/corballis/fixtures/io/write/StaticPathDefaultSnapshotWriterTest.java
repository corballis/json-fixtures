package ie.corballis.fixtures.io.write;

import com.fasterxml.jackson.databind.ObjectMapper;
import ie.corballis.fixtures.annotation.Fixture;
import ie.corballis.fixtures.annotation.FixtureAnnotations;
import ie.corballis.fixtures.assertion.FixtureAssert;
import ie.corballis.fixtures.core.MyBean;
import ie.corballis.fixtures.references.Person;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static ie.corballis.fixtures.settings.SettingsHolder.settings;
import static org.assertj.core.api.Assertions.assertThat;

public class StaticPathDefaultSnapshotWriterTest {

    @Fixture
    private Person person1r;

    @Fixture("fixture6")
    private List<MyBean> fixture6;

    private StaticPathDefaultSnapshotWriter snapshotWriter;

    private ObjectMapper objectMapper;

    @Before
    public void setUp() throws Exception {
        FixtureAnnotations.initFixtures(this);
        objectMapper = settings().getObjectMapper();
        snapshotWriter = new StaticPathDefaultSnapshotWriter(System.getProperty("java.io.tmpdir"), objectMapper);
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteQuietly(getFixtureFile());
    }

    @Test
    public void shouldCreateAndUpdateSnapshotFile() throws IOException {
        String myFixture = "myFixture";
        assertThat(getFixtureFile()).doesNotExist();
        snapshotWriter.write(getClass(), myFixture, person1r);
        verifySavedFile(myFixture, "person1r");

        snapshotWriter.write(getClass(), myFixture, fixture6);
        verifySavedFile(myFixture, "fixture6");
    }

    @Test
    public void shouldKeepOlderEntriesWhenUpdatingTheSnapshotFile() throws IOException {
        assertThat(getFixtureFile()).doesNotExist();
        snapshotWriter.write(getClass(), "myFixture", person1r);
        verifySavedFile("myFixture", "person1r");

        snapshotWriter.write(getClass(), "myFixture2", fixture6);
        verifySavedFile("myFixture2", "fixture6");
        verifySavedFile("myFixture", "person1r");
    }

    private void verifySavedFile(String fixtureName, String expectedFixture) throws IOException {
        File fixtureFile = getFixtureFile();
        String s = FileUtils.readFileToString(fixtureFile);
        Map fixtureMap = objectMapper.readValue(s, Map.class);
        assertThat(fixtureMap.get(DefaultSnapshotWriter.AUTO_GENERATED_FOR)).isEqualTo(getClass().getName());
        FixtureAssert.assertThat(fixtureMap.get(fixtureName)).matches(expectedFixture);
    }

    private File getFixtureFile() {
        String path =
            System.getProperty("java.io.tmpdir") + File.separator + getClass().getSimpleName() + ".fixtures.json";
        return new File(path);
    }
}