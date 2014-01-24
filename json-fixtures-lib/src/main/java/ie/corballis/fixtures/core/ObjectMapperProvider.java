package ie.corballis.fixtures.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ObjectMapperProvider {

    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public synchronized static void setObjectMapper(ObjectMapper objectMapper) {
        ObjectMapperProvider.objectMapper = objectMapper;
    }

    public synchronized static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
