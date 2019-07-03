package ie.corballis.fixtures.io.scanner;

import com.google.common.collect.ImmutableList;
import ie.corballis.fixtures.io.Resource;

import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

public class CompositeFixtureScanner implements FixtureScanner {

    private final List<FixtureScanner> scanners;

    public CompositeFixtureScanner(FixtureScanner... scanners) {
        this(ImmutableList.copyOf(scanners));
    }

    public CompositeFixtureScanner(List<FixtureScanner> scanners) {
        this.scanners = scanners;
    }

    @Override
    public List<Resource> collectResources() {
        Set<URI> uris = new HashSet<>();
        return scanners.stream()
                       .flatMap(scanner -> scanner.collectResources().stream())
                       .filter(resource -> uniqueResource(resource, uris))
                       .collect(toList());
    }

    private boolean uniqueResource(Resource resource, Set<URI> uris) {
        try {
            return uris.add(resource.getURI());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
