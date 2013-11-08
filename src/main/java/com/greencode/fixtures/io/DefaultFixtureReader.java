package com.greencode.fixtures.io;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public class DefaultFixtureReader implements FixtureReader {

    private ObjectMapper objectMapper;

    public DefaultFixtureReader() {
        this.objectMapper = new ObjectMapper();
    }

    public DefaultFixtureReader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Map<String, JsonNode> read(Resource resource) throws IOException {
        Map<String, JsonNode> nodes = newHashMap();

        JsonNode node = objectMapper.readTree(resource.getInputStream());

        Iterator<String> fieldIterator = node.fieldNames();
        while (fieldIterator.hasNext()) {
            String fieldName = fieldIterator.next();
            nodes.put(fieldName, node.get(fieldName));
        }

        return nodes;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
