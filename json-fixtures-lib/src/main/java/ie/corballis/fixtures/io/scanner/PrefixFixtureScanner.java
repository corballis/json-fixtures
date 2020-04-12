package ie.corballis.fixtures.io.scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;

import ie.corballis.fixtures.io.Resource;
import ie.corballis.fixtures.util.ResourceUtils;

class PrefixFixtureScanner extends FolderFixtureScanner {

    private static final Logger logger = LoggerFactory.getLogger(PrefixFixtureScanner.class);
    private final String prefix;

    public PrefixFixtureScanner(String pattern) {
        super(ResourceUtils.getDefaultResourceFilePath());
        prefix = pattern;
    }

    @Override
    protected boolean isFixtureFile(Path path) {
        String regex = String.format("%s.*\\.fixtures\\.json", prefix);
        return path.toFile().getName().matches(regex);
    }

    @Override
    public List<Resource> collectResources() {
        List<Resource> resources = super.collectResources();
        logger.debug("Detected fixture files: {}", resources);
        return resources;
    }
}
