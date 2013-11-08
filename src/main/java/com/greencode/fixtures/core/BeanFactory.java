package com.greencode.fixtures.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

public class BeanFactory {

    private ObjectMapper objectMapper;

    private Cache<String, JsonNode> fixtures = CacheBuilder.newBuilder().build();

    public BeanFactory() {
        this.objectMapper = new ObjectMapper();
    }

    public BeanFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
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

    public <T> T create(
            Class<T> clazz,
            String... fixturesNames) throws IllegalAccessException, InstantiationException, JsonProcessingException {

        if (fixturesNames.length == 0) {
            return clazz.newInstance();
        }

        List<JsonNode> fixtureList = collectFixtures(fixturesNames);
        JsonNode result = fixtureList.remove(0).deepCopy();
        for (JsonNode node : fixtureList) {
            merge(result, node);
        }

        return objectMapper.treeToValue(result, clazz);
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
