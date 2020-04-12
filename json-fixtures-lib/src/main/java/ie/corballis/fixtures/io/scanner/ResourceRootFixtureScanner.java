package ie.corballis.fixtures.io.scanner;

import ie.corballis.fixtures.util.ResourceUtils;

public class ResourceRootFixtureScanner extends FolderFixtureScanner {
    public ResourceRootFixtureScanner() {
        super(ResourceUtils.getDefaultResourceFilePath());
    }
}
