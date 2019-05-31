package ie.corballis.fixtures.io.write;

public interface FileNamingStrategy {

    String getFileName(String folder, String fileNamePrefix, String fixtureName);

}
