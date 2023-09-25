package ie.corballis.fixtures.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Joiner;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import ie.corballis.fixtures.io.DefaultFixtureReader;
import ie.corballis.fixtures.io.FixtureReader;
import ie.corballis.fixtures.io.Resource;
import ie.corballis.fixtures.io.scanner.FixtureScanner;

import java.io.IOException;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static ie.corballis.fixtures.settings.SettingsHolder.settings;

public class BeanFactory {

    public static final String DEFAULT_REFERENCE_PREFIX = "#";

    private ObjectMapper objectMapper;
    private FixtureScanner scanner;
    private FixtureReader reader;
    private ReferenceResolver referenceResolver;
    private volatile boolean initialized = false;

    private Cache<String, JsonNode> fixtures = CacheBuilder.newBuilder().build();

    public BeanFactory() {
        this(settings().getObjectMapper(), settings().getFixtureScanner());
    }

    public BeanFactory(ObjectMapper objectMapper, FixtureScanner scanner) {
        this.objectMapper = objectMapper;
        this.scanner = scanner;
        this.reader = new DefaultFixtureReader(objectMapper);
        this.referenceResolver = new ReferenceResolver(objectMapper, this);
    }

    public BeanFactory(ObjectMapper objectMapper) {
        this(objectMapper, null);
    }

    public synchronized void init() {
        if (!initialized) {
            initialized = true;
            try {
                if (scanner != null) {
                    List<Resource> resources = scanner.collectResources();
                    for (Resource resource : resources) {
                        registerAll(reader.read(resource));
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void registerAll(Map<String, JsonNode> fixtures) {
        init();
        checkNotNull(fixtures, "Fixtures must not be null");
        for (Map.Entry<String, JsonNode> entry : fixtures.entrySet()) {
            registerFixture(entry.getKey(), entry.getValue());
        }
    }

    public void registerFixture(String name, JsonNode fixture) {
        init();
        checkNotNull(name, "Name must not be null");
        checkNotNull(fixture, "Fixture must not be null");
        fixtures.put(name, fixture);
    }

    public void unregisterAll(Collection<String> names) {
        init();
        for (String name : names) {
            fixtures.invalidate(name);
        }
    }

    public void unregisterFixture(String name) {
        init();
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
        return createAsString(pretty, result);
    }

    public String createAsString(boolean pretty, JsonNode jsonNode) throws JsonProcessingException {
        return pretty ?
               objectMapper.writer().withDefaultPrettyPrinter().writeValueAsString(jsonNode) :
               jsonNode.toString();
    }

    public <T> T create(Class<T> type, String... fixtureNames) {
        return create(DEFAULT_REFERENCE_PREFIX, type, fixtureNames);
    }

    public <T> T create(JavaType type, String... fixtureNames) {
        return create(DEFAULT_REFERENCE_PREFIX, type, fixtureNames);
    }

    public <T> T create(String referencePrefix, JavaType type, String... fixtureNames) {
        checkArgument(fixtureNames.length > 0, "At least one fixture needs to be specified.");
        JsonNode jsonNode = mergeFixtures(fixtureNames);
        return referenceResolver.resolve(jsonNode, type, getFixtureName(fixtureNames), referencePrefix);
    }

    public <T> T create(String referencePrefix, Class<T> type, String... fixtureNames) {
        checkArgument(fixtureNames.length > 0, "At least one fixture needs to be specified.");
        JsonNode jsonNode = mergeFixtures(fixtureNames);
        return referenceResolver.resolve(jsonNode, type, getFixtureName(fixtureNames), referencePrefix);
    }

    private String getFixtureName(String[] fixtureNames) {
        return fixtureNames.length == 1 ? fixtureNames[0] : "[MERGED:" + Joiner.on(",").join(fixtureNames) + "]";
    }

    private JsonNode getFixtureAsJsonNodeOrFail(String fixtureName) {
        return getFixtureAsJsonNode(fixtureName).orElseThrow(() -> new NullPointerException(
            "'" + fixtureName + "' is not a valid fixture name!"));
    }

    public Optional<JsonNode> getFixtureAsJsonNode(String fixtureName) {
        init();
        JsonNode fixtureAsJsonNode = fixtures.getIfPresent(fixtureName);
        if (fixtureAsJsonNode == null) {
            return Optional.empty();
        }

        return Optional.of(fixtureAsJsonNode.deepCopy());
    }

    private JsonNode mergeFixtures(String[] fixtureNames) {
        init();
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
            JsonNode fixtureAsJsonNode = getFixtureAsJsonNodeOrFail(fixtureName);
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
}