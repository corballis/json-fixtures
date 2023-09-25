package ie.corballis.fixtures.io;

import com.google.common.collect.ImmutableList;
import ie.corballis.fixtures.util.ClassUtils;
import ie.corballis.fixtures.util.ResourceUtils;
import ie.corballis.fixtures.util.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static ie.corballis.fixtures.settings.SettingsHolder.settings;

public class ClassPathResource implements Resource {

    private final String path;

    private ClassLoader classLoader;

    private Class<?> clazz;

    public static List<Resource> collectClasspathResources(Pattern pattern) {
        Set<String> fixturePaths = settings().getReflections().getResources(pattern);
        return ImmutableList.copyOf(convertToResources(fixturePaths));
    }

    private static List<Resource> convertToResources(Collection<String> paths) {
        List<Resource> resources = newArrayList();

        for (String path : paths) {
            resources.add(new ClassPathResource(path));
        }

        return resources;
    }

    public ClassPathResource(String path) {
        this(path, (ClassLoader) null);
    }

    public ClassPathResource(String path, ClassLoader classLoader) {
        checkNotNull(path, "Path must not be null");
        String pathToUse = StringUtils.cleanPath(path);
        if (pathToUse.startsWith("/")) {
            pathToUse = pathToUse.substring(1);
        }
        this.path = pathToUse;
        this.classLoader = (classLoader != null ? classLoader : ClassUtils.getDefaultClassLoader());
    }

    public ClassPathResource(String path, Class<?> clazz) {
        checkNotNull(path, "Path must not be null");
        this.path = StringUtils.cleanPath(path);
        this.clazz = clazz;
    }

    protected ClassPathResource(String path, ClassLoader classLoader, Class<?> clazz) {
        this.path = StringUtils.cleanPath(path);
        this.classLoader = classLoader;
        this.clazz = clazz;
    }

    @Override
    public boolean exists() {
        URL url = getUrlFromPath();
        return (url != null);
    }

    @Override
    public URL getURL() throws IOException {
        URL url = getUrlFromPath();
        if (url == null) {
            throw new FileNotFoundException(path + " cannot be resolved to URL because it does not exist");
        }
        return url;
    }

    private URL getUrlFromPath() {
        URL url;
        if (this.clazz != null) {
            url = this.clazz.getResource(this.path);
        } else {
            url = this.classLoader.getResource(this.path);
        }
        return url;
    }

    @Override
    public URI getURI() throws IOException {
        URL url = getURL();
        try {
            return ResourceUtils.toURI(url);
        } catch (URISyntaxException ex) {
            throw new IOException("Invalid URI [" + url + "]", ex);
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        InputStream is;
        if (this.clazz != null) {
            is = this.clazz.getResourceAsStream(this.path);
        } else {
            is = this.classLoader.getResourceAsStream(this.path);
        }
        if (is == null) {
            throw new FileNotFoundException(path + " cannot be opened because it does not exist");
        }
        return is;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ClassPathResource");
        sb.append("[path='").append(path).append('\'');
        sb.append(']');
        return sb.toString();
    }

}