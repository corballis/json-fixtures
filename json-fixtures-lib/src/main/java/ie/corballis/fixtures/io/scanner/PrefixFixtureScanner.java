package ie.corballis.fixtures.io.scanner;

import ie.corballis.fixtures.io.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Pattern;

import static ie.corballis.fixtures.io.ClassPathResource.collectClasspathResources;

class PrefixFixtureScanner implements FixtureScanner {

    private static final Logger logger = LoggerFactory.getLogger(PrefixFixtureScanner.class);
    private final Pattern pattern;
    private List<Resource> resources;

    public PrefixFixtureScanner(String pattern) {
        this.pattern = Pattern.compile(String.format("%s.*\\.fixtures\\.json", pattern));
    }

    @Override
    public synchronized List<Resource> collectResources() {
        if (resources == null) {
            resources = collectClasspathResources(pattern);
            logger.debug("Detected fixture files: {}", resources);
        }
        return resources;
    }

}