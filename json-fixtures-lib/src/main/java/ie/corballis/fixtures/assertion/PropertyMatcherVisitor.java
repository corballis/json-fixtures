package ie.corballis.fixtures.assertion;

import ie.corballis.fixtures.util.JsonNodeVisitor;

import java.util.List;
import java.util.Stack;

import static java.util.stream.Collectors.toList;

public abstract class PropertyMatcherVisitor implements JsonNodeVisitor {

    protected final List<String> propertyChain;
    private boolean matched;

    public PropertyMatcherVisitor(List<String> propertyChain) {
        this.propertyChain = propertyChain;
    }

    protected boolean isMatchingPath(Stack<Object> path) {
        boolean matched = convertToMatcherProperties(path).equals(propertyChain);
        if (matched) {
            this.matched = true;
        }
        return matched;
    }

    protected List<Object> convertToMatcherProperties(Stack<Object> stack) {
        return stack.stream().filter(value -> value instanceof String).collect(toList());
    }

    public boolean isMatched() {
        return matched;
    }

}
