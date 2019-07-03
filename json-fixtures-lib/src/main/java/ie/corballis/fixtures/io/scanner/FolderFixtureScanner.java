package ie.corballis.fixtures.io.scanner;

import ie.corballis.fixtures.io.FileSystemResource;
import ie.corballis.fixtures.io.Resource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FolderFixtureScanner implements FixtureScanner {

    private static final PathMatcher FIXTURE_PATH_MATCHER = FileSystems.getDefault()
                                                                       .getPathMatcher("glob:**.fixtures.json");

    private final List<Resource> resources;

    public FolderFixtureScanner(Class testClass) {
        resources = collectFixturesInFolder(getFolderOfTestClass(testClass));
    }

    private String getFolderOfTestClass(Class testClass) {
        try {
            return Paths.get(testClass.getResource("").toURI()).toString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
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

    private boolean isFixtureFile(Path path) {
        return FIXTURE_PATH_MATCHER.matches(path);
    }

    public FolderFixtureScanner(String folder) {
        resources = collectFixturesInFolder(folder);
    }

    @Override
    public List<Resource> collectResources() {
        return resources;
    }

}