package ie.corballis.fixtures.core;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ObjectMapperProvider {
    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    public synchronized static void setObjectMapper(ObjectMapper objectMapper) {
        ObjectMapperProvider.objectMapper = objectMapper;
    }

    public synchronized static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}