package ie.corballis.fixtures.io.write;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

public interface FixtureWriter {

    File write(String folder,
               String fileNamePrefix,
               String fixtureName,
               Object contents,
               FileNamingStrategy namingStrategy,
               Consumer<Map<String, Object>> contentTransformer) throws IOException;

}
