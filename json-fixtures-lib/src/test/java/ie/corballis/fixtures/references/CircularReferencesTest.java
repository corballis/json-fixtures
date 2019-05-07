package ie.corballis.fixtures.references;

import ie.corballis.fixtures.annotation.Fixture;
import ie.corballis.fixtures.annotation.FixtureAnnotations;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

public class CircularReferencesTest {

    @Fixture
    private List<GraphVertex> graph;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void circularReferencesAreNotAllowed() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Circular dependency detected between references: " +
                                        "graph->0->#vertex1->neighbors->1->#vertex3->neighbors->0->#vertex6->neighbors->0->#vertex5->neighbors->0->#vertex3");
        FixtureAnnotations.initFixtures(this);
    }

}
