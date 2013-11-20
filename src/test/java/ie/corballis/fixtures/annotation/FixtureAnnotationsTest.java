package ie.corballis.fixtures.annotation;

import ie.corballis.fixtures.core.MyBean;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class FixtureAnnotationsTest {

    @Fixture({"fixture1"})
    private MyBean bean1;

    @Fixture({"fixture1", "fixture2"})
    private MyBean bean2;

    @Fixture({"fixture1", "fixture3"})
    private MyBean bean3;

    @Fixture({"fixture1", "otherFixture"})
    private MyBean bean4;

    @Test
    public void shouldInitializeBeans() throws Exception {
        FixtureAnnotations.initFixtures(this);

        assertThat(bean1.getStringProperty()).isEqualTo("property");
        assertThat(bean1.getIntProperty()).isEqualTo(1);

        assertThat(bean2.getStringProperty()).isEqualTo("property2");
        assertThat(bean2.getIntProperty()).isEqualTo(1);

        assertThat(bean3.getStringProperty()).isEqualTo("property");
        assertThat(bean3.getIntProperty()).isEqualTo(1);
        assertThat(bean3.getListProperty()).containsExactly("element1", "element2", "element3");

        assertThat(bean4.getStringProperty()).isEqualTo("property");
        assertThat(bean4.getIntProperty()).isEqualTo(3);
    }
}
