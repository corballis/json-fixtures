package ie.corballis.fixtures.io.scanner;

import ie.corballis.fixtures.io.ClassPathResource;
import ie.corballis.fixtures.io.Resource;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Pattern;

public class ClassPathFixtureScanner implements FixtureScanner {

    private static final Logger logger = LoggerFactory.getLogger(ClassPathFixtureScanner.class);

    private static final String FIXTURE_REG_EXP = ".*\\.fixtures\\.json";
    private static final Pattern FIXTURE_PATTERN = Pattern.compile(FIXTURE_REG_EXP);
    private final List<Resource> resources;

    public ClassPathFixtureScanner() {
        this(null);
    }

    public ClassPathFixtureScanner(Reflections reflections) {
        resources = ClassPathResource.collectClasspathResources(FIXTURE_PATTERN, reflections);
        logger.debug("Detected fixture files: {}", resources);
    }

    @Override
    public List<Resource> collectResources() {
        return resources;
    }

}