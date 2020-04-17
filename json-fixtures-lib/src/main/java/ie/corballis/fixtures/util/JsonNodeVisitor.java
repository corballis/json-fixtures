package ie.corballis.fixtures.util;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Stack;

public interface JsonNodeVisitor {

    VisitedValue visitElement(JsonNode original, Object newObject, Stack<Object> path);

    default VisitedValue visitList(JsonNode original, Stack<Object> path) {
        return null;
    }

    default VisitedValue visitObject(JsonNode original, Stack<Object> path) {
        return null;
    }

}
