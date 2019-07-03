package ie.corballis.fixtures.io.scanner;

import ie.corballis.fixtures.io.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Pattern;

import static ie.corballis.fixtures.io.ClassPathResource.collectClasspathResources;

class PrefixFixtureScanner implements FixtureScanner {

    private static final Logger logger = LoggerFactory.getLogger(PrefixFixtureScanner.class);
    private final List<Resource> resources;

    public PrefixFixtureScanner(String pattern) {
        String regex = String.format("%s.*\\.fixtures\\.json", pattern);
        resources = collectClasspathResources(Pattern.compile(regex));
        logger.debug("Detected fixture files: {}", resources);
    }

    @Override
    public List<Resource> collectResources() {
        return resources;
    }

}