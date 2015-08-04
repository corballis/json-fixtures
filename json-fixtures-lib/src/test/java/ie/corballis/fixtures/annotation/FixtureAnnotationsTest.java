package ie.corballis.fixtures.annotation;

import ie.corballis.fixtures.core.MyBean;
import org.junit.Test;

import java.util.List;

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

    @Fixture("fixture6")
    private List<MyBean> beanList;

    @Fixture
    private MyBean fixture1; // should contain the same data as bean1

    @Fixture
    private List<MyBean> fixture6; // should contain the same data as beanList

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

        assertThat(beanList).hasSize(3);
        assertThat(beanList.get(0).getStringProperty()).isEqualTo("property1");
        assertThat(beanList.get(0).getIntProperty()).isEqualTo(1);
        assertThat(beanList.get(1).getStringProperty()).isEqualTo("property2");
        assertThat(beanList.get(1).getIntProperty()).isEqualTo(2);
        assertThat(beanList.get(2).getStringProperty()).isEqualTo("property3");
        assertThat(beanList.get(2).getIntProperty()).isEqualTo(3);
    }

    @Test
     public void defaultFixtureNames() throws Exception {
        FixtureAnnotations.initFixtures(this);

        assertThat(fixture1).isNotNull();
        assertThat(fixture1.getStringProperty()).isEqualTo("property");
        assertThat(fixture1.getIntProperty()).isEqualTo(1);

        assertThat(fixture6).isNotNull();
        assertThat(fixture6).hasSize(3);
        assertThat(fixture6.get(0).getStringProperty()).isEqualTo("property1");
        assertThat(fixture6.get(0).getIntProperty()).isEqualTo(1);
        assertThat(fixture6.get(1).getStringProperty()).isEqualTo("property2");
        assertThat(fixture6.get(1).getIntProperty()).isEqualTo(2);
        assertThat(fixture6.get(2).getStringProperty()).isEqualTo("property3");
        assertThat(fixture6.get(2).getIntProperty()).isEqualTo(3);
    }
}
