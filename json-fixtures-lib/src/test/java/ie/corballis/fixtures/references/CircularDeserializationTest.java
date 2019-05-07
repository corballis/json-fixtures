package ie.corballis.fixtures.references;

import ie.corballis.fixtures.annotation.Fixture;
import ie.corballis.fixtures.annotation.FixtureAnnotations;
import ie.corballis.fixtures.assertion.FixtureAssert;
import org.junit.Test;

public class CircularDeserializationTest {

    @Fixture
    private Owner owner1;
    @Fixture
    private Owner owner3;
    @Fixture
    private Owner owner4;

    @Test
    public void circularObjectDeserializationIsAllowedWhenCircleIsNotInReferences() throws Exception {
        FixtureAnnotations.initFixtures(this);

        Owner manualOwner1 = new Owner("Owner1");
        Owner manualOwner2 = new Owner("Owner2");
        Owner manualOwner3 = new Owner("Owner3");
        Owner manualOwner4 = new Owner("Owner4");

        Thing thing1 = new Thing(1, "Thing1", manualOwner2);
        Thing thing2 = new Thing(2, "Thing2", null);

        manualOwner1.add(thing1);
        manualOwner1.add(thing1);

        manualOwner2.add(thing1);

        manualOwner4.add(thing1);
        manualOwner4.add(thing2);

        FixtureAssert.assertThat(owner1).isEqualTo(manualOwner1);
        FixtureAssert.assertThat(owner3).isEqualTo(manualOwner3);
        FixtureAssert.assertThat(owner4).isEqualTo(manualOwner4);
    }

}