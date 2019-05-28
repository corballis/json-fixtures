package ie.corballis.fixtures.assertion;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Stack;

import static ie.corballis.fixtures.util.JsonUtils.getPrimitiveValue;

public class PropertyMatcherFilteringVisitor extends PropertyMatcherVisitor {

    public PropertyMatcherFilteringVisitor(List<String> propertyChain) {
        super(propertyChain);
    }

    @Override
    public Object visitElement(JsonNode original, Object newObject, Stack<Object> path) {
        if (!isMatchingPath(path)) {
            return getPrimitiveValue(original);
        }
        return AppendMode.NONE;
    }

}
