package ie.corballis.fixtures.references;

import com.fasterxml.jackson.core.JsonProcessingException;
import ie.corballis.fixtures.annotation.Fixture;
import ie.corballis.fixtures.annotation.FixtureAnnotations;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertArrayEquals;

public class CircularContainingTest {
    @Fixture
    private Owner owner1;
    @Fixture
    private Owner owner2;
    @Fixture
    private Owner owner3;

    @Fixture
    private List<GraphVertex> graph;

    @Before
    public void init() throws Exception {
        FixtureAnnotations.initFixtures(this);
    }

    @Test
    public void simpleCircularContaining() {
        System.out.println(owner1);
        System.out.println(owner2);
        System.out.println(owner3);
        Owner manualOwner1 = constructOwner1Manually();
        Owner manualOwner2 = constructOwner2Manually();
        Owner manualOwner3 = constructOwner3Manually();
        assertThat(owner1).isEqualTo(manualOwner1);
        assertThat(owner2).isEqualTo(manualOwner2);
        assertThat(owner3).isEqualTo(manualOwner3);
    }

    public Owner constructOwner1Manually() {
        Owner owner = new Owner("Owner1");
        Thing thing1 = new Thing("Thing1", owner);
        Thing thing2 = new Thing("Thing2", owner);
        owner.add(thing1);
        owner.add(thing2);
        return owner;
    }

    public Owner constructOwner2Manually() {
        Owner owner = new Owner("Owner2");
        Thing thing3 = new Thing("Thing3", owner);
        owner.add(thing3);
        return owner;
    }

    public Owner constructOwner3Manually() {
        return new Owner("Owner3");
    }

    @Test
    public void complexCircularContaining() throws JsonProcessingException {
        printGraph(graph);
        assertArrayEquals(graph.toArray(), constructGraphManually().toArray());
    }

    public void printGraph(List<GraphVertex> graph) {
        for (GraphVertex vertex : graph) {
            System.out.println(vertex);
        }
    }

    public List<GraphVertex> constructGraphManually() {
        GraphVertex vertex1 = new GraphVertex(1);
        GraphVertex vertex2 = new GraphVertex(2);
        GraphVertex vertex3 = new GraphVertex(3);
        GraphVertex vertex4 = new GraphVertex(4);
        GraphVertex vertex5 = new GraphVertex(5);
        GraphVertex vertex6 = new GraphVertex(6);
        GraphVertex vertex7 = new GraphVertex(7);
        GraphVertex vertex8 = new GraphVertex(8);
        GraphVertex vertex9 = new GraphVertex(9);
        GraphVertex vertex10 = new GraphVertex(10);

        vertex1.addNeighbor(vertex2);
        vertex1.addNeighbor(vertex3);
        vertex3.addNeighbor(vertex6);
        vertex4.addNeighbor(vertex3);
        vertex4.addNeighbor(vertex8);
        vertex5.addNeighbor(vertex3);
        vertex5.addNeighbor(vertex6);
        vertex6.addNeighbor(vertex5);
        vertex7.addNeighbor(vertex7);
        vertex7.addNeighbor(vertex9);
        vertex8.addNeighbor(vertex6);
        vertex8.addNeighbor(vertex7);
        vertex9.addNeighbor(vertex8);

        List<GraphVertex> graph = newArrayList();
        graph.add(vertex1);
        graph.add(vertex2);
        graph.add(vertex3);
        graph.add(vertex4);
        graph.add(vertex5);
        graph.add(vertex6);
        graph.add(vertex7);
        graph.add(vertex8);
        graph.add(vertex9);
        graph.add(vertex10);

        return graph;
    }
}