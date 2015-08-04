package ie.corballis.fixtures.annotation;

import static org.fest.assertions.api.Assertions.assertThat;

public class SubTestClass extends SuperTestClass {
    @Override
    public void testFieldInheritance() {
        assertThat(superclassField).isNotNull();
        assertThat(superclassField.getStringProperty()).isEqualTo("property");
        assertThat(superclassField.getIntProperty()).isEqualTo(1);
    }
}