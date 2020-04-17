package ie.corballis.fixtures.core;

import com.fasterxml.jackson.databind.JsonNode;
import ie.corballis.fixtures.util.JsonNodeVisitor;
import ie.corballis.fixtures.util.VisitedValue;

import java.util.Stack;

import static com.google.common.base.Preconditions.checkArgument;
import static ie.corballis.fixtures.util.JsonUtils.*;
import static ie.corballis.fixtures.util.VisitedValue.valueOf;

public class ReferenceResolverNodeVisitor implements JsonNodeVisitor {

    private final String referencePrefix;
    private final ReferenceResolver referenceResolver;
    private final BeanFactory beanFactory;

    public ReferenceResolverNodeVisitor(String referencePrefix,
                                        ReferenceResolver referenceResolver,
                                        BeanFactory beanFactory) {
        this.referencePrefix = referencePrefix;
        this.referenceResolver = referenceResolver;
        this.beanFactory = beanFactory;
    }

    @Override
    public VisitedValue visitElement(JsonNode original, Object newObject, Stack<Object> path) {
        Object append;
        if (original.isTextual()) {
            String value = original.textValue();
            path.push(value);
            if (value.startsWith(referencePrefix)) {
                validateReference(original, path, referencePrefix);
                append = visitElements(referenceResolver.getReferenceNode(original, referencePrefix),
                                       newObject,
                                       path,
                                       this);
            } else {
                append = getPrimitiveValue(original);
            }
            path.pop();
        } else {
            append = getPrimitiveValue(original);
        }

        return valueOf(append);
    }

    private void validateReference(JsonNode node, Stack<Object> path, String referencePrefix) {
        String[] referenceParts = node.textValue().split(referencePrefix);
        validateReference(path, referencePrefix, referenceParts);
    }

    private void validateReference(Stack<Object> path, String referencePrefix, String[] referenceParts) {
        String helpText = "Every string value starting with " +
                          referencePrefix +
                          " is considered as fixture reference. " +
                          "If you would like to refer to a fixture, please provide a valid fixture name after " +
                          referencePrefix +
                          ", otherwise if you have a non-referring string starting with " +
                          referencePrefix +
                          " then change the default prefix in @Fixture annotation";
        checkArgument(referenceParts.length == 2,
                      "Fixture reference value detected without fixture name in " +
                      pathToReferenceChain(path) +
                      " property. " +
                      helpText);
        String fixtureName = referenceParts[1];

        beanFactory.getFixtureAsJsonNode(fixtureName)
                   .orElseThrow(() -> new IllegalArgumentException(
                       "Fixture reference value detected without existing fixture in " +
                       pathToReferenceChain(path) +
                       " property. " +
                       helpText));

        boolean hasNoCircularDependency = path.indexOf(referencePrefix + fixtureName) == (path.size() - 1);
        checkArgument(hasNoCircularDependency,
                      "Circular dependency detected between references: " + pathToReferenceChain(path));
    }

}
