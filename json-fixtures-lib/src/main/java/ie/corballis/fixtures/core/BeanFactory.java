package ie.corballis.fixtures.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import ie.corballis.fixtures.io.DefaultFixtureReader;
import ie.corballis.fixtures.io.FixtureReader;
import ie.corballis.fixtures.io.FixtureScanner;
import ie.corballis.fixtures.io.Resource;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

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

    public void registerFixture(String name, JsonNode fixture) {
        checkNotNull(name, "Name must not be null");
        checkNotNull(fixture, "Fixture must not be null");
        fixtures.put(name, fixture);
    }

    public void registerAll(Map<String, JsonNode> fixtures) {
        checkNotNull(fixtures, "Fixtures must not be null");
        for (Map.Entry<String, JsonNode> entry : fixtures.entrySet()) {
            registerFixture(entry.getKey(), entry.getValue());
        }
    }

    public void unregisterFixture(String name) {
        fixtures.invalidate(name);
    }

    public void unregisterAll(Collection<String> names) {
        for (String name : names) {
            fixtures.invalidate(name);
        }
    }

    public <T> T create(
            Class<T> clazz,
            String... fixtureNames) throws IllegalAccessException, InstantiationException, JsonProcessingException {

        checkArgument(fixtureNames.length > 0, "At least one fixture needs to be specified.");
        JsonNode result = mergeFixtures(fixtureNames);
        return objectMapper.treeToValue(result, clazz);
    }

    public <T> T create(JavaType type, String... fixtureNames) throws IOException {
        checkArgument(fixtureNames.length > 0, "At least one fixture needs to be specified.");
        String mergedString = createAsString(fixtureNames);
        return objectMapper.readValue(mergedString, type);
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

    private JsonNode mergeFixtures(String[] fixturesNames) {
        List<JsonNode> fixtureList = collectFixtures(fixturesNames);
        JsonNode result = fixtureList.remove(0).deepCopy();
        for (JsonNode node : fixtureList) {
            merge(result, node);
        }
        return result;
    }

    private List<JsonNode> collectFixtures(String[] fixturesNames) {
        List<JsonNode> fixtureList = newArrayList();

        for (String fixturesName : fixturesNames) {
            JsonNode fixture = fixtures.getIfPresent(fixturesName);
            checkNotNull(fixture, fixturesName + " is not a valid fixture");
            fixtureList.add(fixture);
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

    public void setAllowUnknownProperties(boolean allowUnknownProperties) {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, !allowUnknownProperties);
    }
}