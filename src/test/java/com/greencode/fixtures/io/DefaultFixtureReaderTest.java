package com.greencode.fixtures.io;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;

import java.util.TreeMap;

import static org.fest.assertions.api.Assertions.assertThat;

public class DefaultFixtureReaderTest {

    @Test
    public void testRead() throws Exception {
        ClassPathResource resource = new ClassPathResource("fixtures1.json");

        DefaultFixtureReader reader = new DefaultFixtureReader();

        TreeMap<String, JsonNode> fixtures = new TreeMap<String, JsonNode>(reader.read(resource));

        assertThat(fixtures.toString()).isEqualTo(
                "{fixture1={\"name\":\"test1\",\"stringProperty\":\"property\",\"intProperty\":1}, fixture2={\"name\":\"test2\",\"stringProperty\":\"property2\",\"intProperty\":2}}");
    }
}
