package ie.corballis.fixtures.io.scanner;

import java.io.File;
import java.util.Collections;
import java.util.List;

import ie.corballis.fixtures.io.FileSystemResource;
import ie.corballis.fixtures.io.Resource;
import ie.corballis.fixtures.util.ResourceUtils;

public class FileFixtureScanner implements FixtureScanner {

    private final Resource resource;

    public FileFixtureScanner(String fileName) {
        this.resource = collectFixturesInFolder(ResourceUtils.getDefaultResourceFilePath() + fileName);
    }

    public FileFixtureScanner(Class testClass, String fileName) {
        this.resource = collectFixturesInFolder(ResourceUtils.getResourceFilePath(testClass) + fileName);
    }

    private Resource collectFixturesInFolder(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) throw new IllegalStateException(fileName + " file does not exist.");
        return new FileSystemResource(file);
    }

    @Override
    public List<Resource> collectResources() {
        return Collections.singletonList(resource);
    }

    public Resource collectResource() {
        return resource;
    }
}
