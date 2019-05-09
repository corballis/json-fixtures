package ie.corballis.fixtures.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import ie.corballis.fixtures.io.write.AbstractFixtureWriter;
import ie.corballis.fixtures.io.write.TestClassFileNamingStrategy;

import java.io.File;
import java.io.IOException;

public class GeneratorFixtureWriter extends AbstractFixtureWriter {

    public GeneratorFixtureWriter(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    public File write(String folder, String fileNamePrefix, String fixtureName, Object contents) throws IOException {
        return write(folder, fileNamePrefix, fixtureName, contents, TestClassFileNamingStrategy.getInstance(), null);
    }
}
