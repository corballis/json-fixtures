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

    private Map<String, Object> resolves = newHashMap();
    private Map<String, Type> fieldTypes = newHashMap();
    private Map<Context, JsonNode> listJsonNodesOfSets = newHashMap();
    private Map<Context, JavaType> elementTypesOfSets = newHashMap();
    private Map<Context, List<Object>> listRepresentationsOfSets = newHashMap();
    private Multimap<String, Context> referencesPermanent = HashMultimap.create();
    private Multimap<String, Context> referencesTemporaryFront = HashMultimap.create();

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

    // for outer use
    public void setAllowUnknownProperties(boolean allowUnknownProperties) {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, !allowUnknownProperties);
    }

    // for outer use
    public String createAsString(String... fixtureNames) throws JsonProcessingException {
        return createAsString(false, newArrayList(fixtureNames));
    }

    // for outer use
    public String createAsString(boolean pretty, String... fixtureNames) throws JsonProcessingException {
        return createAsString(pretty, newArrayList(fixtureNames));
    }

    // for outer use
    public String createAsString(boolean pretty, List<String> fixtureNames) throws JsonProcessingException {
        if (fixtureNames.size() == 0) {
            return "{}";
        }
        JsonNode result = mergeFixtures(fixtureNames.toArray(new String[fixtureNames.size()]));
        return pretty ? objectMapper.writer().withDefaultPrettyPrinter().writeValueAsString(result) : result.toString();
    }

    // access point
    public <T> T create(Type type, String... fixtureNames) throws IOException {
        checkArgument(fixtureNames.length > 0, "At least one fixture needs to be specified.");
        JsonNode cleanBaseNode = getCleanJson(fixtureNames, type);
        T baseObject = convertJsonNodeToObject(cleanBaseNode, type);
        String fixtureName = null;
        if (fixtureNames.length == 1) {
            fixtureName = fixtureNames[0];
            resolves.put(fixtureName, baseObject);
        }
        resolveReferencesToObjects();
        createListRepresentationsOfSets();
        baseObject = setContentsOfReferencedFields(baseObject, fixtureName);
        referencesPermanent.clear();
        return baseObject;
    }

    private void resolveReferencesToObjects() throws IOException {
        Multimap<String, Context> referencesTemporaryActual = HashMultimap.create();
        while (!referencesTemporaryFront.isEmpty()) {
            referencesTemporaryActual.clear();
            referencesTemporaryActual.putAll(referencesTemporaryFront);
            referencesTemporaryFront.clear();
            referencesPermanent.putAll(referencesTemporaryActual);
            for (String reference : referencesTemporaryActual.keySet()) {
                if (!resolves.containsKey(reference)) {
                    Type type = fieldTypes.get(reference);
                    JsonNode referencedNode = getFixtureAsCleanJsonNode(reference, type);
                    Object referencedObject = convertJsonNodeToObject(referencedNode, type);
                    resolves.put(reference, referencedObject);
                }
            }
        }
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

    private void createListRepresentationsOfSets() throws IOException {
        for (Context context : elementTypesOfSets.keySet()) {
            JavaType elementType = elementTypesOfSets.get(context);
            JsonNode listNode = listJsonNodesOfSets.get(context);
            CollectionType listType = CollectionType.construct(List.class, elementType);
            List<Object> list = convertJsonNodeToObject(listNode, listType);
            listRepresentationsOfSets.put(context.copy(), list);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T setContentsOfReferencedFields(T baseObject, String baseObjectName) throws IOException {
        for (Map.Entry<String, Context> entry : referencesPermanent.entries()) {
            String reference = entry.getKey();
            Context context = entry.getValue();
            Object value = resolves.get(reference);
            if (context.size() == 1) {
                boolean isBaseObject;
                if (context.firstElement() == null) {
                    isBaseObject = !(baseObjectName == null);
                } else {
                    isBaseObject = context.firstElement().equals(baseObjectName);
                }
                if (isBaseObject) {
                    baseObject = (T) value;
                } else {
                    resolves.replace((String) context.firstElement(), value);
                }
            } else {
                setFieldValue(baseObject, context, value);
            }
        }
        return baseObject;
    }

    @SuppressWarnings("unchecked")
    private void setFieldValue(Object baseObject, Context context, Object value) throws IOException {
        checkArgument(context.size() > 1);
        Object targetObject = null;
        Object field = null;

        for (int i = 1; i < context.size(); i++) {

            if (i > 1) {
                if (field instanceof Field) {
                    targetObject = new FieldReader(targetObject, (Field) field).read();
                } else if (field instanceof String) {
                    targetObject = ((Map) targetObject).get(field);
                } else if (targetObject instanceof List) {
                    targetObject = ((List) targetObject).get((Integer) field);
                } else { // if targetObject instanceof Set
                    List<Object> list = getListRepresentationForSet(context);
                    targetObject = list.get((Integer) field);
                }
            } else {
                Object targetObjectName = context.firstElement();
                if (targetObjectName == null) {
                    targetObject = baseObject;
                } else {
                    targetObject = resolves.get(targetObjectName);
                }
            }

            Object nextContextElement = context.get(i);
            String fieldName = String.valueOf(nextContextElement);
            if (targetObject instanceof Map) {
                field = fieldName;
            } else if (nextContextElement instanceof Integer) {
                field = nextContextElement;
            } else {
                try {
                    field = targetObject.getClass().getDeclaredField(fieldName);
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
            ((Map) targetObject).replace(field, value);
        } else if (targetObject instanceof List) {
            ((List) targetObject).set((Integer) field, value);
        } else { // if targetObject instanceof Set
            setElementOfSet((Set) targetObject, context, (Integer) field, value);
        }
    }

    private void setElementOfSet(Set<Object> set, Context context, int index, Object value) {
        List<Object> list = getListRepresentationForSet(context.withoutLastElement());
        list.set(index, value);
        set.clear();
        set.addAll(list);
    }

    private List<Object> getListRepresentationForSet(Context context) {
        return listRepresentationsOfSets.get(context);
    }

    private JsonNode getCleanJson(String[] fixtureNames, Type type) throws IOException {
        if (fixtureNames.length == 1) {
            return getFixtureAsCleanJsonNode(fixtureNames[0], type);
        } else {
            JsonNode baseNode = mergeFixtures(fixtureNames);
            return cleanExistingJsonNode(baseNode, type);
        }
    }

    private JsonNode getFixtureAsCleanJsonNode(String fixtureName, Type type) throws IOException {
        JsonNode fixtureAsJsonNode = getFixtureAsJsonNode(fixtureName);
        return cleanJsonNode(fixtureAsJsonNode, type, new Context(fixtureName));
    }

    private JsonNode getFixtureAsJsonNode(String fixtureName) {
        JsonNode fixtureAsJsonNode = fixtures.getIfPresent(fixtureName);
        checkNotNull(fixtureAsJsonNode, fixtureName + " is not a valid fixture name!");
        return fixtureAsJsonNode;
    }

    private JsonNode cleanExistingJsonNode(JsonNode node, Type type) throws IOException {
        return cleanJsonNode(node, type, new Context(null));
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

    // changes the reference strings that occur in the node, to NullNodes
    private JsonNode cleanJsonNode(JsonNode original, Type type, Context context) throws IOException {
        if (original.isObject()) {
            Iterator<String> fieldNames = original.fieldNames();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                context.push(fieldName);
                try {
                    Type type2 = findNextType(type, fieldName);
                    JsonNode newNode = cleanJsonNode(original.get(fieldName), type2, context);
                    context.pop();
                    ((ObjectNode) original).replace(fieldName, newNode);
                } catch (NoSuchFieldException e) {
                    if (objectMapper.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)) {
                        throw new IOException(e.getMessage());
                    }
                }
            }
        } else if (original.isArray()) {
            Type type2 = getComponentTypeOfCollectionOrArray(type, original, context);
            for (int i = 0; i < original.size(); i++) {
                context.push(i);
                ((ArrayNode) original).set(i, cleanJsonNode(original.get(i), type2, context));
                context.pop();
            }
        } else if (original.isTextual()) {
            if (isValidReference(original.textValue(), type, context)) {
                original = NullNode.getInstance();
            }
        }
        return original;
    }

    private Type findNextType(Type type, String fieldName) throws NoSuchFieldException {
        if (type instanceof MapType) { // if type is a map
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

    private Type getComponentTypeOfCollectionOrArray(Type type, JsonNode listJsonNode, Context context) {
        if (type instanceof CollectionType) {
            if (Set.class.isAssignableFrom(((CollectionType) type).getRawClass())) {
                listJsonNodesOfSets.put(context.copy(), listJsonNode);
                elementTypesOfSets.put(context.copy(), ((CollectionType) type).getContentType());
            }
            return ((CollectionType) type).getContentType().getRawClass();
        } else { // if type is an array
            return ((Class) type).getComponentType();
        }
    }

    @SuppressWarnings("unchecked")
    private boolean isValidReference(String text, Type type, Context context) {
        if (text.startsWith("#")) {
            String reference = text.substring(1);
            JsonNode exists = fixtures.getIfPresent(reference);
            if (exists != null) {
                referencesTemporaryFront.put(reference, context.copy());
                fieldTypes.put(reference, type);
                return true;
            }
        }
        return false;
    }
}