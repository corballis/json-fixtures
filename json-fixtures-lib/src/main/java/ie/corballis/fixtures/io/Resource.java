package ie.corballis.fixtures.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

public interface Resource {

    boolean exists();

    URL getURL() throws IOException;

    URI getURI() throws IOException;

    InputStream getInputStream() throws IOException;

}