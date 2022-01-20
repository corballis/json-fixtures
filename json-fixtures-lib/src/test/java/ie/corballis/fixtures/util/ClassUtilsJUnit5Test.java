package ie.corballis.fixtures.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ClassUtilsJUnit5Test {

    @Test
    public void shouldGetTestInfo() {
        aMethodCall();
    }

    private void aMethodCall() {
        assertThat(ClassUtils.getTestMethodName()).isEqualTo("shouldGetTestInfo");
        assertThat(ClassUtils.getTestClassName()).isEqualTo(ClassUtilsJUnit5Test.class.getName());
        assertThat(ClassUtils.getTestClass()).isEqualTo(ClassUtilsJUnit5Test.class);
    }

}