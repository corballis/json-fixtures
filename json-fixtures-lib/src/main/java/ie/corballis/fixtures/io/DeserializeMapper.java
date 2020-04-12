package ie.corballis.fixtures.io;

import com.fasterxml.jackson.databind.JavaType;

public interface DeserializeMapper {

    <T> T convertValue(Object baseObjectMap, Class<T> type);

    <T> T convertValue(Object baseObjectMap, JavaType type);
}
