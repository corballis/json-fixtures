package ie.corballis.fixtures.util;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Stack;

public interface JsonNodeVisitor {

    enum AppendMode {
        NONE
    }

    Object visitElement(JsonNode original, Object newObject, Stack<Object> path);
}
