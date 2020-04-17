package ie.corballis.fixtures.assertion;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Joiner;
import ie.corballis.fixtures.assertion.matcher.NestedObjectMatcher;
import ie.corballis.fixtures.util.VisitedValue;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;

import java.util.List;
import java.util.Stack;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static ie.corballis.fixtures.util.JsonUtils.getPrimitiveValue;
import static ie.corballis.fixtures.util.JsonUtils.pathToReferenceChain;
import static ie.corballis.fixtures.util.VisitedValue.skipValue;
import static ie.corballis.fixtures.util.VisitedValue.valueOf;

public class PropertyMatcherAssertingVisitor extends PropertyMatcherVisitor {

    private final PropertyMatchers matchers;
    private final Matcher matcher;

    public PropertyMatcherAssertingVisitor(List<String> propertyChain, PropertyMatchers matchers) {
        super(propertyChain);
        this.matchers = matchers;
        String property = Joiner.on(".").join(propertyChain);
        this.matcher = matchers.getMatcher(property);
        checkNotNull(matcher,
                     "Could not find matcher to " +
                     property +
                     ". Please check that you PropertyMatchers are configured properly.");
    }

    @Override
    public VisitedValue visitElement(JsonNode original, Object newObject, Stack<Object> path) {
        Object actualValue = getPrimitiveValue(original);
        executeMatcher(path, actualValue);
        return valueOf(actualValue);
    }

    private void executeMatcher(Stack<Object> path, Object actualValue) {
        if (isMatchingPath(path)) {
            MatcherAssert.assertThat("Property '" +
                                     pathToReferenceChain(path) +
                                     "' did not match with the PropertyMatcher you provided. ", actualValue, matcher);
        }
    }

    @Override
    public VisitedValue visitObject(JsonNode original, Stack<Object> path) {
        VisitedValue value = null;
        if (isMatchingPath(path)) {
            checkState(matcher instanceof NestedObjectMatcher,
                       "Property '" +
                       pathToReferenceChain(path) +
                       "' is an object. In order to use matchers with an object, " +
                       "you must wrap that in a nested matcher. e.g:" +
                       " nested(equalTo(...), ClassOfNestedObject.class)");

            executeMatcher(path, original);
            value = skipValue();
        }
        return value;
    }

}
