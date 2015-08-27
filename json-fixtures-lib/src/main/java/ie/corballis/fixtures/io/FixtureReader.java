package ie.corballis.fixtures.io;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Map;

public interface FixtureReader {

    Map<String, JsonNode> read(Resource resource) throws IOException;

}