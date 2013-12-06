package ie.corballis.fixtures.util;

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

}
