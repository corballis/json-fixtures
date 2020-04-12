package ie.corballis.fixtures.core;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import ie.corballis.fixtures.io.DeserializeMapper;

public class JacksonDeserializer implements DeserializeMapper {
    private ObjectMapper objectMapper;

    public JacksonDeserializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T convertValue(Object baseObjectMap, Class<T> type) {
        return objectMapper.convertValue(baseObjectMap, type);
    }

    public <T> T convertValue(Object baseObjectMap, JavaType type) {
        return objectMapper.convertValue(baseObjectMap, type);
    }
}
