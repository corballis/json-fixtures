package ie.corballis.fixtures.generator;

import com.fasterxml.jackson.core.JsonProcessingException;
import ie.corballis.fixtures.core.ObjectMapperProvider;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DefaultFileSystemWriter implements FileSystemWriter {
    private static final String FILE_NAME_ENDING = ".fixtures.json";

    @Override
    public boolean fileWithPathAlreadyExists(String path) {
        return new File(path).exists();
    }

    @Override
    public void writeOut(String folder,
                         String fileNamePrefix,
                         String fixtureName,
                         Map<String, Object> objectAsMap,
                         boolean append) throws Exception {
        String path = folder + '/' + fileNamePrefix + FILE_NAME_ENDING;
        File file = new File(path);
        if (fileWithPathAlreadyExists(path)) {
            if (append) {
                Map<String, Object> map = readMapFromFile(file);
                if (map.put(fixtureName, objectAsMap) != null) {
                    throw new Exception("There already exists a fixture with fixture name '" + fixtureName +
                                        "' in this fixture file!");
                }
                String jsonOutputString = mapToString(map);
                FileUtils.writeStringToFile(file, jsonOutputString);
            } else {
                throw new Exception("The fixture file already exists," +
                                    "but the user didn't allow appending the new fixture to its end" +
                                    "- so nothing has been executed!");
            }
        } else {
            Map<String, Object> map = new HashMap<String, Object>(); // wrapper map
            map.put(fixtureName, objectAsMap);
            String jsonOutputString = mapToString(map);
            FileUtils.writeStringToFile(file, jsonOutputString);
        }
    }

    private String mapToString(Map<String, Object> map) throws JsonProcessingException {
        return ObjectMapperProvider.getObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(map);
    }

    private Map<String, Object> readMapFromFile(File file) throws IOException {
        return ObjectMapperProvider.getObjectMapper().readValue(file, Map.class);
    }
}