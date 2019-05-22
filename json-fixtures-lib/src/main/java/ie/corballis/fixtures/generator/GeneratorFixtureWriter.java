package ie.corballis.fixtures.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import ie.corballis.fixtures.io.write.AbstractFixtureWriter;

import java.io.File;
import java.io.IOException;

import static ie.corballis.fixtures.settings.SettingsHolder.settings;

public class GeneratorFixtureWriter extends AbstractFixtureWriter {

    public GeneratorFixtureWriter(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    public File write(String folder, String fileNamePrefix, String fixtureName, Object contents) throws IOException {
        return write(folder, fileNamePrefix, fixtureName, contents, settings().getGeneratorFileNamingStrategy(), null);
    }
}
