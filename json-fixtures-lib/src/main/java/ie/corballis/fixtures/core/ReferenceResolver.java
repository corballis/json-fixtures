package ie.corballis.fixtures.core;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newLinkedHashMap;

public class ReferenceResolver {

    private final ObjectMapper objectMapper;
    private final BeanFactory beanFactory;

    public ReferenceResolver(ObjectMapper objectMapper, BeanFactory beanFactory) {
        this.objectMapper = objectMapper;
        this.beanFactory = beanFactory;
    }

    public <T> T resolve(JsonNode original, Class<T> type, String fixtureName, String referencePrefix) {
        Object baseObjectMap = resolveAndCreateBaseObjectMap(original, fixtureName, referencePrefix);
        return objectMapper.convertValue(baseObjectMap, type);
    }

    public <T> T resolve(JsonNode original, JavaType type, String fixtureName, String referencePrefix) {
        Object baseObjectMap = resolveAndCreateBaseObjectMap(original, fixtureName, referencePrefix);
        return objectMapper.convertValue(baseObjectMap, type);
    }

    private Object resolveAndCreateBaseObjectMap(JsonNode original, String fixtureName, String referencePrefix) {
        Object baseObjectMap = initBaseObject(original, referencePrefix);

        Stack<String> path = new Stack<>();
        path.push(fixtureName);

        buildBaseObject(original, path, baseObjectMap, referencePrefix);
        return baseObjectMap;
    }

    private Object initBaseObject(JsonNode original, String referencePrefix) {
        JsonNode referenceNode = getReferenceNode(original, referencePrefix);
        if (original.isObject()) {
            return newLinkedHashMap();
        } else if (original.isArray()) {
            return newArrayList();
        } else if (referenceNode != null) {
            return initBaseObject(referenceNode, referencePrefix);
        } else {
            throw new IllegalArgumentException(
                "The base object of fixtures must either be a bean, object, map or any collection type. " +
                "If you need to use primitives add variables into your test class instead. " + original +
                " is used as value which is not supported.");
        }
    }

    private JsonNode getReferenceNode(JsonNode node, String referencePrefix) {
        JsonNode referenceNode = null;
        if (node.isTextual()) {
            String value = node.textValue();
            if (value.startsWith(referencePrefix)) {
                String fixtureName = value.substring(referencePrefix.length());
                referenceNode = beanFactory.getFixtureAsJsonNode(fixtureName).orElse(null);
            }
        }
        return referenceNode;
    }

    private Object buildBaseObject(JsonNode original, Stack<String> path, Object baseObject, String referencePrefix) {
        if (original.isObject()) {
            Iterator<String> fieldNames = original.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                path.push(fieldName);

                JsonNode childNode = original.get(fieldName);

                populateNestedElementIfNeeded(baseObject, fieldName, childNode, path, referencePrefix);
                path.pop();
            }
        } else if (original.isArray()) {
            for (int i = 0; i < original.size(); i++) {
                path.push(String.valueOf(i));
                populateArrayItemIfNeeded(baseObject, original.get(i), path, referencePrefix);
                path.pop();
            }
        } else if (original.isTextual()) {
            String value = original.textValue();
            path.push(value);
            if (value.startsWith(referencePrefix)) {
                String[] referenceParts = value.split(referencePrefix);
                validateReference(path, referencePrefix, referenceParts);
                JsonNode referenceNode = beanFactory.getFixtureAsJsonNode(referenceParts[1]).orElse(null);
                buildBaseObject(referenceNode, path, baseObject, referencePrefix);
            } else {
                baseObject = getPrimitiveValue(original);
            }
            path.pop();
        } else {
            baseObject = getPrimitiveValue(original);
        }
        return baseObject;
    }

    private void populateNestedElementIfNeeded(Object baseObject,
                                               String fieldName,
                                               JsonNode childNode,
                                               Stack<String> path,
                                               String referencePrefix) {
        Map<String, Object> baseObjectMap = (Map<String, Object>) baseObject;
        Object nestedObject = baseObject;

        JsonNode referenceNode = getReferenceNode(childNode, referencePrefix);
        boolean isReferenceNode = referenceNode != null;
        if (isReferenceNode) {
            populateNestedElementIfNeeded(baseObject, fieldName, referenceNode, path, referencePrefix);
        } else if (childNode.isObject()) {
            nestedObject = newLinkedHashMap();
            baseObjectMap.put(fieldName, buildBaseObject(childNode, path, nestedObject, referencePrefix));
        } else if (childNode.isArray()) {
            nestedObject = newArrayList();
            baseObjectMap.put(fieldName, buildBaseObject(childNode, path, nestedObject, referencePrefix));
        } else {
            baseObjectMap.put(fieldName, buildBaseObject(childNode, path, nestedObject, referencePrefix));
        }
    }

    private Object getPrimitiveValue(JsonNode original) {
        try {
            if (original.isBoolean()) {
                return original.asBoolean();
            } else if (original.isNumber()) {
                return original.numberValue();
            } else if (original.isBinary()) {
                return original.binaryValue();
            } else if (original.isNull()) {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return original.textValue();
    }

    private void populateArrayItemIfNeeded(Object baseObject,
                                           JsonNode childNode,
                                           Stack<String> path,
                                           String referencePrefix) {
        List baseObjectList = (List) baseObject;
        Object nestedObject = baseObject;

        JsonNode referenceNode = getReferenceNode(childNode, referencePrefix);
        boolean isReferenceNode = referenceNode != null;
        if (isReferenceNode) {
            path.push(childNode.asText());
            validateReference(childNode, path, referencePrefix);
            populateArrayItemIfNeeded(baseObject, referenceNode, path, referencePrefix);
            path.pop();
        } else if (childNode.isObject()) {
            nestedObject = newLinkedHashMap();
            baseObjectList.add(buildBaseObject(childNode, path, nestedObject, referencePrefix));
        } else if (childNode.isArray()) {
            nestedObject = newArrayList();
            baseObjectList.add(buildBaseObject(childNode, path, nestedObject, referencePrefix));
        } else {
            baseObjectList.add(buildBaseObject(childNode, path, nestedObject, referencePrefix));
        }
    }

    private void validateReference(JsonNode node, Stack<String> path, String referencePrefix) {
        String[] referenceParts = node.textValue().split(referencePrefix);
        validateReference(path, referencePrefix, referenceParts);
    }

    private void validateReference(Stack<String> path, String referencePrefix, String[] referenceParts) {
        String helpText =
            "Every string value starting with " + referencePrefix + " is considered as fixture reference. " +
            "If you would like to refer to a fixture, please provide a valid fixture name after " + referencePrefix +
            ", otherwise if you have a non-referring string starting with " + referencePrefix +
            " then change the default prefix in @Fixture annotation";
        checkArgument(referenceParts.length == 2,
                      "Fixture reference value detected without fixture name in " + pathToReferenceChain(path) +
                      " property. " + helpText);
        String fixtureName = referenceParts[1];

        beanFactory.getFixtureAsJsonNode(fixtureName)
                   .orElseThrow(() -> new IllegalArgumentException(
                       "Fixture reference value detected without existing fixture in " + pathToReferenceChain(path) +
                       " property. " + helpText));

        boolean hasNoCircularDependency = path.indexOf(referencePrefix + fixtureName) == (path.size() - 1);
        checkArgument(hasNoCircularDependency,
                      "Circular dependency detected between references: " + pathToReferenceChain(path));
    }

    private String pathToReferenceChain(Stack<String> path) {
        return Joiner.on("->").skipNulls().join(path);
    }

}
