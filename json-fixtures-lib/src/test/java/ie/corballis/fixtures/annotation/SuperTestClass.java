package ie.corballis.fixtures.annotation;

import ie.corballis.fixtures.core.MyBean;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class SuperTestClass {
    @Fixture("fixture1")
    protected MyBean superclassField;

    @Before
    public void setUp() throws Exception {
        FixtureAnnotations.initFixtures(this);
    }

    @Test
    public void testFieldInheritance(){
        assertThat(superclassField).isNotNull();
        assertThat(superclassField.getStringProperty()).isEqualTo("property");
        assertThat(superclassField.getIntProperty()).isEqualTo(1);
    }
}