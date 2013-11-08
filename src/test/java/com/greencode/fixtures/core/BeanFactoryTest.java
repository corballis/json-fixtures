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
        ClassPathResource resource = new ClassPathResource("fixtures1.json");
        DefaultFixtureReader reader = new DefaultFixtureReader();
        factory.registerAll(reader.read(resource));
    }

    @Test
    public void createsBeanFromSingleFixture() throws Exception {
        MyBean bean = factory.create(MyBean.class, "fixture1");

        assertThat(bean.getName()).isEqualTo("test1");
        assertThat(bean.getStringProperty()).isEqualTo("property");
        assertThat(bean.getIntProperty()).isEqualTo(1);
    }

    @Test
    public void laterFixturesOverwritePreviousValues() throws Exception {
        MyBean bean = factory.create(MyBean.class, "fixture1", "fixture2", "fixture3");

        assertThat(bean.getName()).isEqualTo("test3");
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

    private static class MyBean {
        private String name;
        private String stringProperty;
        private int intProperty;

        private String getName() {
            return name;
        }

        private void setName(String name) {
            this.name = name;
        }

        private String getStringProperty() {
            return stringProperty;
        }

        private void setStringProperty(String stringProperty) {
            this.stringProperty = stringProperty;
        }

        private int getIntProperty() {
            return intProperty;
        }

        private void setIntProperty(int intProperty) {
            this.intProperty = intProperty;
        }
    }

}
