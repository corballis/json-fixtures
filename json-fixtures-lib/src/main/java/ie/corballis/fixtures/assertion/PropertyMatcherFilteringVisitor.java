package ie.corballis.fixtures.assertion;

import com.fasterxml.jackson.databind.JsonNode;
import ie.corballis.fixtures.util.VisitedValue;

import java.util.List;
import java.util.Stack;

import static ie.corballis.fixtures.util.JsonUtils.getPrimitiveValue;
import static ie.corballis.fixtures.util.VisitedValue.skipValue;
import static ie.corballis.fixtures.util.VisitedValue.valueOf;

public class PropertyMatcherFilteringVisitor extends PropertyMatcherVisitor {

    public PropertyMatcherFilteringVisitor(List<String> propertyChain) {
        super(propertyChain);
    }

    @Override
    public VisitedValue visitElement(JsonNode original, Object newObject, Stack<Object> path) {
        return isMatchingPath(path) ? skipValue() : valueOf(getPrimitiveValue(original));
    }

    @Override
    public VisitedValue visitObject(JsonNode original, Stack<Object> path) {
        return isMatchingPath(path) ? skipValue() : null;
    }

}
