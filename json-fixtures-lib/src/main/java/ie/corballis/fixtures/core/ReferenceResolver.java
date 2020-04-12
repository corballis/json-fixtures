package ie.corballis.fixtures.core;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Stack;

import ie.corballis.fixtures.io.DeserializeMapper;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static ie.corballis.fixtures.util.JsonUtils.visitElements;

public class ReferenceResolver {

    private final DeserializeMapper deserializeMapper;
    private final BeanFactory beanFactory;

    public ReferenceResolver(DeserializeMapper deserializeMapper, BeanFactory beanFactory) {
        this.deserializeMapper = deserializeMapper;
        this.beanFactory = beanFactory;
    }

    public <T> T resolve(JsonNode original, Class<T> type, String fixtureName, String referencePrefix) {
        Object baseObjectMap = resolveAndCreateBaseObjectMap(original, fixtureName, referencePrefix);
        return deserializeMapper.convertValue(baseObjectMap, type);
    }

    public <T> T resolve(JsonNode original, JavaType type, String fixtureName, String referencePrefix) {
        Object baseObjectMap = resolveAndCreateBaseObjectMap(original, fixtureName, referencePrefix);
        return deserializeMapper.convertValue(baseObjectMap, type);
    }

    private Object resolveAndCreateBaseObjectMap(JsonNode original, String fixtureName, String referencePrefix) {
        Object baseObjectMap = initBaseObject(original, referencePrefix);

        Stack<Object> path = new Stack<>();
        path.push(fixtureName);

        baseObjectMap = visitElements(original,
                                      baseObjectMap,
                                      path,
                                      new ReferenceResolverNodeVisitor(referencePrefix, this, beanFactory));
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

    protected JsonNode getReferenceNode(JsonNode node, String referencePrefix) {
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

}
