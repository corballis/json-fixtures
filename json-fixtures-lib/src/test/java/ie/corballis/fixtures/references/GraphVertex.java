package ie.corballis.fixtures.references;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class GraphVertex {
    private int id;
    private List<GraphVertex> neighbors = newArrayList();

    public int getId() {
        return id;
    }

    public GraphVertex(int id) {
        this.id = id;
    }

    public GraphVertex() {
    }

    public void addNeighbor(GraphVertex neighbor) {
        neighbors.add(neighbor);
    }

    @Override
    public String toString() {
        return "GraphVertex:" +
               "\n\tid = " + id +
               ",\n\tneighbors = " + constructNeighborsString();
    }

    private String constructNeighborsString() {
        StringBuilder sb = new StringBuilder();
        if (neighbors.size() > 0) {
            for (GraphVertex vertex : neighbors) {
                if (vertex == null) {
                    sb.append("null");
                } else {
                    sb.append(vertex.getId());
                }
                sb.append(", ");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
        } else {
            sb.append("no neighbors");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GraphVertex that = (GraphVertex) o;
        if (id != that.id) {
            return false;
        }
        return !(neighbors != null ? !constructNeighborsString().equals(that.constructNeighborsString()) :
                 that.neighbors != null);
    }
}