package ie.corballis.fixtures.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import static com.google.common.base.Preconditions.checkNotNull;

public class FileSystemResource implements Resource {

    private final File file;

    public FileSystemResource(File file) {
        checkNotNull(file, "File must not be null");
        this.file = file;
    }

    public FileSystemResource(String path) {
        checkNotNull(path, "Path must not be null");
        this.file = new File(path);
    }

    @Override
    public boolean exists() {
        return this.file.exists();
    }

    @Override
    public URL getURL() throws IOException {
        return this.file.toURI().toURL();
    }

    @Override
    public URI getURI() throws IOException {
        return this.file.toURI();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(this.file);
    }
}