package ie.corballis.fixtures.util;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class ResourceUtils {

    public static URI toURI(URL url) throws URISyntaxException {
        return toURI(url.toString());
    }

    public static URI toURI(String location) throws URISyntaxException {
        return new URI(StringUtils.replaceAll(location, " ", "%20"));
    }

    public static String getResourceFilePath(Class c) {
        String path = System.getProperty("user.dir") + "/src/test/resources/fixtures/"
                + c.getPackage().getName().replace(".", "/") + "/";
        createIfNotExist(path);
        return path;
    }

    public static String getResourceFilePath(Class c, String relativePath) {
        String path = System.getProperty("user.dir") + "/src/test/resources/fixtures/"
                + c.getPackage().getName().replace(".", "/");
        createIfNotExist(path);
        return path + "/" +relativePath;
    }

    public static String getDefaultResourceFilePath() {
        String path = System.getProperty("user.dir") + "/src/test/resources/fixtures/";
        createIfNotExist(path);
        return path;
    }

    public static String getDefaultResourceFilePath(String relativePath) {
        String path = System.getProperty("user.dir") + "/src/test/resources/fixtures/" + relativePath;
        createIfNotExist(path);
        return path;
    }

    private static void createIfNotExist(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }
}
