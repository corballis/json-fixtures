package ie.corballis.fixtures.references;

import ie.corballis.fixtures.annotation.Fixture;
import ie.corballis.fixtures.annotation.FixtureAnnotations;
import ie.corballis.fixtures.assertion.FixtureAssert;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

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

        Thing thing1 = getThing1(owner1);
        Owner thing1Owner = thing1.getOwner();
        Thing thing1ViaOwner = getThing1(thing1Owner);
        assertThat(thing1).isSameAs(thing1ViaOwner);
    }

    private Thing getThing1(Owner owner) {
        return owner.getThings().stream().filter(thing -> thing.getName().equals("Thing1")).findFirst().get();
    }

}