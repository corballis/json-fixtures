package ie.corballis.fixtures.util;

import org.junit.Test;

import ie.corballis.fixtures.annotation.FixtureAnnotations;

import static org.assertj.core.api.Assertions.assertThat;

public class ClassUtilsTest {

    @Test
    public void shouldGetTestInfo() throws Exception {
        FixtureAnnotations.initFixtures(this);
        aMethodCall();
    }

    private void aMethodCall() {
        assertThat(ClassUtils.getTestMethodName()).isEqualTo("shouldGetTestInfo");
        assertThat(ClassUtils.getTestClassName()).isEqualTo(ClassUtilsTest.class.getName());
        assertThat(ClassUtils.getTestClass()).isEqualTo(ClassUtilsTest.class);
    }
}
