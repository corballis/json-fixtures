package ie.corballis.fixtures.annotation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import ie.corballis.fixtures.core.MyBean;
import ie.corballis.fixtures.io.write.DefaultSnapshotWriter;
import ie.corballis.fixtures.io.write.FileNamingStrategy;
import ie.corballis.fixtures.io.write.StaticPathDefaultSnapshotWriter;
import ie.corballis.fixtures.settings.Settings;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static ie.corballis.fixtures.settings.SettingsHolder.settings;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

public class FixtureAnnotationsTest {

    @Fixture({"fixture1"})
    private MyBean bean1;

    @Fixture({"fixture1", "fixture2"})
    private MyBean bean2;

    @Fixture({"fixture1", "fixture3"})
    private MyBean bean3;

    @Fixture({"fixture1", "otherFixture"})
    private MyBean bean4;

    @Fixture("fixture6")
    private List<MyBean> beanList;

    @Fixture("fixture6")
    private Set<MyBean> beanSet;

    @Fixture("fixture6")
    private MyBean[] beanArray;

    @Fixture
    private MyBean fixture1; // should contain the same data as bean1

    @Fixture
    private List<MyBean> fixture6; // should contain the same data as beanList, beanSet and beanArray

    @Test
    public void shouldInitializeBeans() throws Exception {
        FixtureAnnotations.initFixtures(this);

        checkFixture1(bean1);

        assertThat(bean2).isNotNull();
        assertThat(bean2.getStringProperty()).isEqualTo("property2");
        assertThat(bean2.getIntProperty()).isEqualTo(1);

        assertThat(bean3).isNotNull();
        assertThat(bean3.getStringProperty()).isEqualTo("property");
        assertThat(bean3.getIntProperty()).isEqualTo(1);
        assertThat(bean3.getListProperty()).containsExactly("element1", "element2", "element3");

        assertThat(bean4).isNotNull();
        assertThat(bean4.getStringProperty()).isEqualTo("property");
        assertThat(bean4.getIntProperty()).isEqualTo(3);

        checkList(beanList);
        checkSet(beanSet);
        checkList(Arrays.asList(beanArray));
    }

    private void checkFixture1(MyBean bean) {
        assertThat(bean).isNotNull();
        assertThat(bean.getStringProperty()).isEqualTo("property");
        assertThat(bean.getIntProperty()).isEqualTo(1);
    }

    private void checkSet(Set<MyBean> set) {
        assertThat(set).isNotNull();
        assertThat(set).hasSize(3);
        assertTrue(set.contains(new MyBean("property1", 1)));
        assertTrue(set.contains(new MyBean("property2", 2)));
        assertTrue(set.contains(new MyBean("property3", 3)));
    }

    private void checkList(List<MyBean> list) {
        assertThat(list).isNotNull();
        assertThat(list).hasSize(3);
        assertThat(list.get(0).getStringProperty()).isEqualTo("property1");
        assertThat(list.get(0).getIntProperty()).isEqualTo(1);
        assertThat(list.get(1).getStringProperty()).isEqualTo("property2");
        assertThat(list.get(1).getIntProperty()).isEqualTo(2);
        assertThat(list.get(2).getStringProperty()).isEqualTo("property3");
        assertThat(list.get(2).getIntProperty()).isEqualTo(3);
    }

    @Test
    public void defaultFixtureNames() throws Exception {
        FixtureAnnotations.initFixtures(this);
        checkFixture1(fixture1);
        checkList(fixture6);
    }

    @Test
    public void shouldUseDifferentSettingsInEachThread() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);

        FileNamingStrategy fileNamingStrategy = (folder, fileNamePrefix, fixtureName) -> "abc.test";

        Settings.Builder settings = new Settings.Builder().setObjectMapper(objectMapper)
                                                          .setDefaultSnapshotWriter()
                                                          .setGeneratorFileNamingStrategy(fileNamingStrategy)
                                                          .setSnapshotFileNamingStrategy(fileNamingStrategy);

        FixtureAnnotations.initFixtures(this, settings);

        Thread t2 = new Thread() {
            @Override
            public void run() {
                FileNamingStrategy fileNamingStrategy = (folder, fileNamePrefix, fixtureName) -> "cba.test";
                Settings.Builder settings = new Settings.Builder().setDefaultObjectMapper()
                                                                  .setGeneratorFileNamingStrategy(fileNamingStrategy)
                                                                  .setSnapshotFolderPath("a/b/c");

                try {
                    FixtureAnnotations.initFixtures(this, settings);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                assertThat(settings().getObjectMapper()).isNotEqualTo(objectMapper);
                assertThat(settings().getGeneratorFileNamingStrategy()).isEqualTo(fileNamingStrategy);
                assertThat(settings().getSnapshotFileNamingStrategy()).isEqualTo(fileNamingStrategy);
                assertThat(settings().getSnapshotFixtureWriter()).isInstanceOf(StaticPathDefaultSnapshotWriter.class);
            }
        };
        t2.start();
        t2.join();

        assertThat(settings().getObjectMapper()).isEqualTo(objectMapper);
        assertThat(settings().getGeneratorFileNamingStrategy()).isEqualTo(fileNamingStrategy);
        assertThat(settings().getSnapshotFileNamingStrategy()).isEqualTo(fileNamingStrategy);
        assertThat(settings().getSnapshotFixtureWriter()).isInstanceOf(DefaultSnapshotWriter.class);
    }
}
