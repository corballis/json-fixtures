package ie.corballis.fixtures.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ClassUtilsJUnit4Test {

    @Test
    public void shouldGetTestInfo() {
        aMethodCall();
    }

    private void aMethodCall() {
        assertThat(ClassUtils.getTestMethodName()).isEqualTo("shouldGetTestInfo");
        assertThat(ClassUtils.getTestClassName()).isEqualTo(ClassUtilsJUnit4Test.class.getName());
        assertThat(ClassUtils.getTestClass()).isEqualTo(ClassUtilsJUnit4Test.class);
    }
}