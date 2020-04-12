package ie.corballis.fixtures.io.scanner;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ie.corballis.fixtures.io.FileSystemResource;
import ie.corballis.fixtures.io.Resource;
import ie.corballis.fixtures.util.ResourceUtils;

public class FolderFixtureScanner implements FixtureScanner {

    private static final PathMatcher FIXTURE_PATH_MATCHER = FileSystems.getDefault()
            .getPathMatcher("glob:**.fixtures.json");

    private final String folder;

    public FolderFixtureScanner(Class testClass) {
        folder = getFolderOfTestClass(testClass);
    }

    private String getFolderOfTestClass(Class testClass) {
        return ResourceUtils.getResourceFilePath(testClass);
    }

    private List<Resource> collectFixturesInFolder(String folder) {
        List<Resource> resources;
        try (Stream<Path> paths = Files.walk(Paths.get(folder))) {
            resources = paths.filter(Files::isRegularFile)
                    .filter(this::isFixtureFile)
                    .map(path -> new FileSystemResource(path.toString()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return resources;
    }

    protected boolean isFixtureFile(Path path) {
        return FIXTURE_PATH_MATCHER.matches(path);
    }

    public FolderFixtureScanner(String folder) {
        this.folder = folder;
    }

    @Override
    public List<Resource> collectResources() {
        return collectFixturesInFolder(folder);
    }

}
