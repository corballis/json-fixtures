package ie.corballis.fixtures.generator;

import java.util.Map;

public interface FileSystemWriter {
    boolean fileWithPathAlreadyExists(String path);

    void writeOut(String folder,
                  String fileNamePrefix,
                  String fixtureName,
                  Map<String, Object> objectAsMap,
                  boolean append) throws Exception;
}