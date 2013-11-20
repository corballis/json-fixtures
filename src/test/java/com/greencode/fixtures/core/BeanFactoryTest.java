package com.greencode.fixtures.core;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.greencode.fixtures.io.ClassPathResource;
import com.greencode.fixtures.io.DefaultFixtureReader;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class BeanFactoryTest {

    private BeanFactory factory;

    @Before
    public void setUp() throws Exception {
        factory = new BeanFactory();
        ClassPathResource resource = new ClassPathResource("test2.fixtures.json");
        DefaultFixtureReader reader = new DefaultFixtureReader();
        factory.registerAll(reader.read(resource));
    }

    @Test
    public void createsBeanFromSingleFixture() throws Exception {
        MyBean bean = factory.create(MyBean.class, "fixture1");

        assertThat(bean.getStringProperty()).isEqualTo("property");
        assertThat(bean.getIntProperty()).isEqualTo(1);
    }

    @Test
    public void createsEmptyBean() throws Exception {
        MyBean bean = factory.create(MyBean.class);

        assertThat(bean.getStringProperty()).isNull();
        assertThat(bean.getIntProperty()).isEqualTo(0);
    }

    @Test
    public void laterFixturesOverwritePreviousValues() throws Exception {
        MyBean bean = factory.create(MyBean.class, "fixture1", "fixture2");

        assertThat(bean.getStringProperty()).isEqualTo("property2");
        assertThat(bean.getIntProperty()).isEqualTo(1);
    }

    @Test(expected = UnrecognizedPropertyException.class)
    public void doesNotAllowInvalidPropertiesByDefault() throws Exception {
        factory.create(MyBean.class, "fixture1", "fixture4");
    }

    @Test
    public void ignoresInvalidPropertiesIfAsked() throws Exception {
        factory.setAllowUnknownProperties(true);
        factory.create(MyBean.class, "fixture1", "fixture4");
    }

    @Test
    public void canReadLists() throws Exception {
        MyBean bean = factory.create(MyBean.class, "fixture1", "fixture3");

        assertThat(bean.getStringProperty()).isEqualTo("property");
        assertThat(bean.getIntProperty()).isEqualTo(1);
        assertThat(bean.getListProperty()).containsExactly("element1", "element2", "element3");
    }

    @Test
    public void canReadNestedObjects() throws Exception {
        MyBean bean = factory.create(MyBean.class, "fixture3", "fixture5");

        assertThat(bean.getStringProperty()).isNull();
        assertThat(bean.getIntProperty()).isEqualTo(0);
        assertThat(bean.getListProperty()).containsExactly("element1", "element2", "element3");
        assertThat(bean.getNested().getProp1()).isEqualTo("value");
    }

}
