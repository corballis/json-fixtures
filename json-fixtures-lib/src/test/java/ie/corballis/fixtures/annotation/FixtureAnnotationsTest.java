package ie.corballis.fixtures.annotation;

import ie.corballis.fixtures.core.MyBean;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

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

    @Test
    public void defaultFixtureNames() throws Exception {
        FixtureAnnotations.initFixtures(this);
        checkFixture1(fixture1);
        checkList(fixture6);
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
}
