package ie.corballis.fixtures.assertion;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Joiner;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;

import java.util.List;
import java.util.Stack;

import static com.google.common.base.Preconditions.checkNotNull;
import static ie.corballis.fixtures.util.JsonUtils.getPrimitiveValue;
import static ie.corballis.fixtures.util.JsonUtils.pathToReferenceChain;

public class PropertyMatcherAssertingVisitor extends PropertyMatcherVisitor {

    private final PropertyMatchers matchers;

    public PropertyMatcherAssertingVisitor(List<String> propertyChain, PropertyMatchers matchers) {
        super(propertyChain);
        this.matchers = matchers;
    }

    @Override
    public Object visitElement(JsonNode original, Object newObject, Stack<Object> path) {
        Object actualValue = getPrimitiveValue(original);
        if (isMatchingPath(path)) {
            String property = Joiner.on(".").join(propertyChain);
            Matcher matcher = matchers.getMatcher(property);
            checkNotNull(matcher,
                         "Could not find matcher to " + property +
                         ". Please check that you PropertyMatchers are configured properly.");
            MatcherAssert.assertThat(
                "Property '" + pathToReferenceChain(path) + "' did not match with the PropertyMatcher you provided. ",
                actualValue,
                matcher);
        }
        return actualValue;
    }

}
