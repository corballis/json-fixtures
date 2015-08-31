package ie.corballis.fixtures.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import ie.corballis.fixtures.io.DefaultFixtureReader;
import ie.corballis.fixtures.io.FixtureReader;
import ie.corballis.fixtures.io.FixtureScanner;
import ie.corballis.fixtures.io.Resource;
import ie.corballis.fixtures.util.ClassUtils;
import ie.corballis.fixtures.util.FieldReader;
import ie.corballis.fixtures.util.FieldSetter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

public class BeanFactory {

    private ObjectMapper objectMapper;
    private FixtureScanner scanner;
    private FixtureReader reader;

    private Cache<String, JsonNode> fixtures = CacheBuilder.newBuilder().build();

    public BeanFactory() {
        this((FixtureScanner) null);
    }

    public BeanFactory(FixtureScanner scanner) {
        this(new ObjectMapper(), scanner);
    }

    public BeanFactory(ObjectMapper objectMapper) {
        this(objectMapper, null);
    }

    public BeanFactory(ObjectMapper objectMapper, FixtureScanner scanner) {
        this.objectMapper = objectMapper;
        this.scanner = scanner;
        this.reader = new DefaultFixtureReader(objectMapper);
    }

    public void init() throws IOException {
        if (scanner != null) {
            List<Resource> resources = scanner.collectResources();
            for (Resource resource : resources) {
                registerAll(reader.read(resource));
            }
        }
    }

    public void registerAll(Map<String, JsonNode> fixtures) {
        checkNotNull(fixtures, "Fixtures must not be null");
        for (Map.Entry<String, JsonNode> entry : fixtures.entrySet()) {
            registerFixture(entry.getKey(), entry.getValue());
        }
    }

    public void registerFixture(String name, JsonNode fixture) {
        checkNotNull(name, "Name must not be null");
        checkNotNull(fixture, "Fixture must not be null");
        fixtures.put(name, fixture);
    }

    public void unregisterAll(Collection<String> names) {
        for (String name : names) {
            fixtures.invalidate(name);
        }
    }

    public void unregisterFixture(String name) {
        fixtures.invalidate(name);
    }

    public void setAllowUnknownProperties(boolean allowUnknownProperties) {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, !allowUnknownProperties);
    }

    public String createAsString(String... fixtureNames) throws JsonProcessingException {
        return createAsString(false, newArrayList(fixtureNames));
    }

    public String createAsString(boolean pretty, String... fixtureNames) throws JsonProcessingException {
        return createAsString(pretty, newArrayList(fixtureNames));
    }

    public String createAsString(boolean pretty, List<String> fixtureNames) throws JsonProcessingException {
        if (fixtureNames.size() == 0) {
            return "{}";
        }
        JsonNode result = mergeFixtures(fixtureNames.toArray(new String[fixtureNames.size()]));
        return pretty ? objectMapper.writer().withDefaultPrettyPrinter().writeValueAsString(result) : result.toString();
    }

    public <T> T create(Type type, String... fixtureNames) throws IOException {
        checkArgument(fixtureNames.length > 0, "At least one fixture needs to be specified.");
        ReferenceContext context = new ReferenceContext();
        JsonNode cleanBaseNode = getCleanJson(fixtureNames, type, context);
        T baseObject = convertJsonNodeToObject(cleanBaseNode, type);
        String fixtureName = getAndRegisterFixtureNameForBaseObjectIfNotMerged(fixtureNames, baseObject, context);
        // fixtureName is null if and only if cleanBaseNode is merged from multiple fixtures
        resolveReferencesToObjects(context);
        createListRepresentationsOfSets(context);
        baseObject = setContentsOfReferencedFields(baseObject, fixtureName, context);
        return baseObject;
    }

    private JsonNode getCleanJson(String[] fixtureNames, Type type, ReferenceContext context) throws IOException {
        if (fixtureNames.length == 1) {
            return getFixtureAsCleanJsonNode(fixtureNames[0], type, context);
        } else {
            JsonNode baseNode = mergeFixtures(fixtureNames);
            return cleanExistingJsonNode(baseNode, type, context);
        }
    }

    private JsonNode getFixtureAsCleanJsonNode(String fixtureName, Type type, ReferenceContext context) throws
                                                                                                        IOException {
        JsonNode fixtureAsJsonNode = getFixtureAsJsonNode(fixtureName);
        return cleanJsonNode(fixtureAsJsonNode, type, new Path(fixtureName), context);
    }

    private JsonNode getFixtureAsJsonNode(String fixtureName) {
        JsonNode fixtureAsJsonNode = fixtures.getIfPresent(fixtureName);
        checkNotNull(fixtureAsJsonNode, "'" + fixtureName + "' is not a valid fixture name!");
        return fixtureAsJsonNode.deepCopy();
    }

    private JsonNode mergeFixtures(String[] fixtureNames) {
        List<JsonNode> fixtureList = collectFixtures(fixtureNames);
        JsonNode result = fixtureList.remove(0).deepCopy();
        for (JsonNode node : fixtureList) {
            merge(result, node);
        }
        return result;
    }

    private List<JsonNode> collectFixtures(String[] fixtureNames) {
        List<JsonNode> fixtureList = newArrayList();
        for (String fixtureName : fixtureNames) {
            JsonNode fixtureAsJsonNode = getFixtureAsJsonNode(fixtureName);
            fixtureList.add(fixtureAsJsonNode);
        }
        return fixtureList;
    }

    private static JsonNode merge(JsonNode targetNode, JsonNode sourceNode) {
        Iterator<String> fieldNames = sourceNode.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            JsonNode jsonNode = targetNode.get(fieldName);
            if (jsonNode != null && jsonNode.isObject()) {
                merge(jsonNode, sourceNode.get(fieldName));
            } else {
                if (targetNode instanceof ObjectNode) {
                    JsonNode value = sourceNode.get(fieldName);
                    ((ObjectNode) targetNode).put(fieldName, value);
                }
            }

        }
        return targetNode;
    }

    private JsonNode cleanExistingJsonNode(JsonNode node, Type type, ReferenceContext context) throws IOException {
        return cleanJsonNode(node, type, new Path(null), context);
    }

    // changes the reference strings that occur in a node, to NullNodes
    private JsonNode cleanJsonNode(JsonNode original, Type type, Path path, ReferenceContext context) throws
                                                                                                      IOException {
        if (original.isObject()) {
            Iterator<String> fieldNames = original.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                path.push(fieldName);
                try {
                    Type fieldType = findNextType(type, fieldName);
                    JsonNode newNode = cleanJsonNode(original.get(fieldName), fieldType, path, context);
                    path.pop();
                    ((ObjectNode) original).replace(fieldName, newNode);
                } catch (NoSuchFieldException e) {
                    if (objectMapper.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)) {
                        throw new IOException(e.getMessage());
                    }
                }
            }
        } else if (original.isArray()) {
            Type elementType = getElementTypeOfCollectionOrArray(type, original, path, context);
            for (int i = 0; i < original.size(); i++) {
                path.push(i);
                ((ArrayNode) original).set(i, cleanJsonNode(original.get(i), elementType, path, context));
                path.pop();
            }
        } else if (original.isTextual()) {
            if (isValidReference(original.textValue(), type, path, context)) {
                original = NullNode.getInstance();
            }
        }
        return original;
    }

    private Type findNextType(Type type, String fieldName) throws NoSuchFieldException {
        if (type instanceof MapType) {
            return ((MapType) type).containedType(1).getRawClass();
        } else {
            Field field = findField((Class) type, fieldName);
            return ClassUtils.getFixtureType(field);
        }
    }

    private Field findField(Class clazz, String fieldName) throws NoSuchFieldException {
        do {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        } while (clazz != null);
        throw new NoSuchFieldException("Unknown property '" + fieldName + "'!");
    }

    private Type getElementTypeOfCollectionOrArray(Type type,
                                                   JsonNode listJsonNode,
                                                   Path path,
                                                   ReferenceContext context) {
        if (type instanceof CollectionType) {
            if (Set.class.isAssignableFrom(((CollectionType) type).getRawClass())) {
                context.listJsonNodesOfSets.put(path.copy(), listJsonNode);
                context.elementTypesOfSets.put(path.copy(), ((CollectionType) type).getContentType());
            }
            return ((CollectionType) type).getContentType().getRawClass();
        } else { // if type is an array
            return ((Class) type).getComponentType();
        }
    }

    @SuppressWarnings("unchecked")
    private boolean isValidReference(String text, Type type, Path path, ReferenceContext context) {
        if (text.startsWith("#")) {
            String reference = text.substring(1);
            JsonNode exists = fixtures.getIfPresent(reference);
            if (exists != null) {
                context.referencesAdjacent.put(reference, path.copy());
                context.fieldTypes.put(reference, type);
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private <T> T convertJsonNodeToObject(JsonNode node, Type type) throws IOException {
        if (type instanceof Class<?>) {
            return objectMapper.treeToValue(node, (Class<T>) type);
        } else if (type instanceof JavaType) {
            return objectMapper.readValue(node.toString(), (JavaType) type);
        } else {
            throw new IOException("Invalid type");
        }
    }

    private String getAndRegisterFixtureNameForBaseObjectIfNotMerged(String[] fixtureNames,
                                                                     Object baseObject,
                                                                     ReferenceContext context) {
        if (fixtureNames.length == 1) {
            String fixtureName = fixtureNames[0];
            context.resolves.put(fixtureName, baseObject);
            return fixtureName;
        }
        return null;
    }

    private void resolveReferencesToObjects(ReferenceContext context) throws IOException {
        Multimap<String, Path> queue = HashMultimap.create();
        while (!context.referencesAdjacent.isEmpty()) {
            queue.clear();
            queue.putAll(context.referencesAdjacent);
            context.referencesAdjacent.clear();
            context.referencesVisited.putAll(queue);
            for (String reference : queue.keySet()) {
                if (!context.resolves.containsKey(reference)) {
                    Type type = context.fieldTypes.get(reference);
                    JsonNode referencedNode = getFixtureAsCleanJsonNode(reference, type, context);
                    Object referencedObject = convertJsonNodeToObject(referencedNode, type);
                    context.resolves.put(reference, referencedObject);
                }
            }
        }
    }

    private void createListRepresentationsOfSets(ReferenceContext context) throws IOException {
        for (Path path : context.elementTypesOfSets.keySet()) {
            JavaType elementType = context.elementTypesOfSets.get(path);
            JsonNode listNode = context.listJsonNodesOfSets.get(path);
            CollectionType listType = CollectionType.construct(List.class, elementType);
            List<Object> list = convertJsonNodeToObject(listNode, listType);
            context.listRepresentationsOfSets.put(path.copy(), list);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T setContentsOfReferencedFields(T baseObject, String baseObjectName, ReferenceContext context) throws
                                                                                                               IOException {
        for (Map.Entry<String, Path> entry : context.referencesVisited.entries()) {
            String reference = entry.getKey();
            Path path = entry.getValue();
            Object value = context.resolves.get(reference);
            if (path.size() == 1) {
                boolean isBaseObject;
                if (path.firstElement() == null) {
                    isBaseObject = (baseObjectName == null);
                } else {
                    isBaseObject = path.firstElement().equals(baseObjectName);
                }
                if (isBaseObject) {
                    baseObject = (T) value;
                } else {
                    context.resolves.put((String) path.firstElement(), value);
                }
            } else {
                setFieldValue(baseObject, path, value, context);
            }
        }
        return baseObject;
    }

    @SuppressWarnings("unchecked")
    private void setFieldValue(Object baseObject, Path path, Object value, ReferenceContext context) throws
                                                                                                     IOException {
        checkArgument(path.size() > 1, "Wrong parameter: the size of the path must be at least 2!");
        Object targetObject = null;
        Object field = null;

        int firstFieldIndex = 1;
        int lastFieldIndex = path.size() - 1;
        for (int i = firstFieldIndex; i <= lastFieldIndex; i++) {

            if (i > 1) {
                if (field instanceof Field) {
                    targetObject = new FieldReader(targetObject, (Field) field).read();
                } else if (field instanceof String) {
                    targetObject = ((Map) targetObject).get(field);
                } else if (targetObject instanceof List) {
                    targetObject = ((List) targetObject).get((Integer) field);
                } else { // if targetObject instanceof Set
                    List<Object> list = context.listRepresentationsOfSets.get(path);
                    targetObject = list.get((Integer) field);
                }
            } else {
                Object targetObjectName = path.firstElement();
                if (targetObjectName == null) {
                    targetObject = baseObject;
                } else {
                    targetObject = context.resolves.get(targetObjectName);
                }
            }

            Object nextContextElement = path.get(i);
            if (targetObject instanceof Map || nextContextElement instanceof Integer) {
                field = nextContextElement;
            } else {
                try {
                    field = targetObject.getClass().getDeclaredField((String) nextContextElement);
                } catch (NoSuchFieldException e) {
                    throw new IOException(e.getMessage());
                }
            }

        }

        checkNotNull(targetObject, "The target object is null!");
        checkNotNull(field, "The field of the target object to be set is null!");

        if (field instanceof Field) {
            new FieldSetter(targetObject, (Field) field).set(value);
        } else if (field instanceof String) {
            ((Map) targetObject).put(field, value);
        } else if (targetObject instanceof List) {
            ((List) targetObject).set((Integer) field, value);
        } else { // if targetObject instanceof Set
            setElementOfSet((Set) targetObject, path, (Integer) field, value, context);
        }
    }

    private void setElementOfSet(Set<Object> set, Path path, int index, Object value, ReferenceContext context) {
        List<Object> list = context.listRepresentationsOfSets.get(path.withoutLastElement());
        list.set(index, value);
        set.clear();
        set.addAll(list);
    }

    private static class ReferenceContext {
        private Map<String, Object> resolves = newHashMap();
        private Map<String, Type> fieldTypes = newHashMap();
        private Map<Path, JsonNode> listJsonNodesOfSets = newHashMap();
        private Map<Path, List<Object>> listRepresentationsOfSets = newHashMap();
        private Map<Path, JavaType> elementTypesOfSets = newHashMap();
        private Multimap<String, Path> referencesVisited = HashMultimap.create();
        private Multimap<String, Path> referencesAdjacent = HashMultimap.create();
    }
}