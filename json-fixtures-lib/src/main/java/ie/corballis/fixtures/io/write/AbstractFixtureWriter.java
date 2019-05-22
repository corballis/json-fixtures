package ie.corballis.fixtures.io.write;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

import static com.google.common.collect.Maps.newHashMap;

public abstract class AbstractFixtureWriter implements FixtureWriter {

    private ObjectMapper objectMapper;

    public AbstractFixtureWriter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public File write(String folder,
                      String fileNamePrefix,
                      String fixtureName,
                      Object contents,
                      FileNamingStrategy namingStrategy,
                      Consumer<Map<String, Object>> contentTransformer) throws IOException {
        String fileName = namingStrategy.getFileName(folder, fileNamePrefix, fixtureName);
        String fixtureFilePath = Joiner.on(File.separator).join(folder, fileName);
        try {
            File fixtureFile = new File(fixtureFilePath);
            Map fixtureFileContents = newHashMap();
            if (!fixtureFile.exists()) {
                fixtureFile.createNewFile();
            } else {
                fixtureFileContents = objectMapper.readValue(fixtureFile, Map.class);
            }
            fixtureFileContents.put(fixtureName, contents);

            if (contentTransformer != null) {
                contentTransformer.accept(fixtureFileContents);
            }

            String jsonContent = writeFormattedJson(fixtureFileContents);
            FileUtils.writeStringToFile(fixtureFile, jsonContent);

            return fixtureFile;

        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    protected String writeFormattedJson(Map fixtureFileContents) throws JsonProcessingException {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(fixtureFileContents);
    }
}
