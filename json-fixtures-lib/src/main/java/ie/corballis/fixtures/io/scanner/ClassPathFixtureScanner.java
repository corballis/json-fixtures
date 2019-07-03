package ie.corballis.fixtures.io.scanner;

import ie.corballis.fixtures.io.ClassPathResource;
import ie.corballis.fixtures.io.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Pattern;

public class ClassPathFixtureScanner implements FixtureScanner {

    private static final Logger logger = LoggerFactory.getLogger(ClassPathFixtureScanner.class);

    private static final String FIXTURE_REG_EXP = ".*\\.fixtures\\.json";
    private static final Pattern FIXTURE_PATTERN = Pattern.compile(FIXTURE_REG_EXP);
    private static final List<Resource> resources;

    static {
        resources = ClassPathResource.collectClasspathResources(FIXTURE_PATTERN);
        logger.debug("Detected fixture files: {}", resources);
    }

    @Override
    public List<Resource> collectResources() {
        return resources;
    }

}