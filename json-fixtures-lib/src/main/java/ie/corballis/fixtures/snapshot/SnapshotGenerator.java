package ie.corballis.fixtures.snapshot;

import com.fasterxml.jackson.databind.JsonNode;
import ie.corballis.fixtures.core.BeanFactory;
import ie.corballis.fixtures.core.InvocationContextHolder;
import ie.corballis.fixtures.io.FixtureScanner;
import ie.corballis.fixtures.io.Resource;
import ie.corballis.fixtures.io.write.DefaultSnapshotWriter;

import java.io.IOException;

import static ie.corballis.fixtures.settings.SettingsHolder.settings;
import static ie.corballis.fixtures.util.ClassUtils.getTestClass;
import static ie.corballis.fixtures.util.ClassUtils.getTestMethodName;

public class SnapshotGenerator {

    private final BeanFactory beanFactory;
    private final FixtureScanner scanner;

    public SnapshotGenerator(BeanFactory beanFactory) {
        this(beanFactory, settings().getFixtureScanner());
    }

    public SnapshotGenerator(BeanFactory beanFactory, FixtureScanner scanner) {
        this.beanFactory = beanFactory;
        this.scanner = scanner;
    }

    public boolean createOrUpdateFixture(Object actual, boolean regenerateFixture) throws IOException {
        boolean createdOrUpdated = false;

        initTest();
        if (canGenerateNewSnapshot() || regenerateFixture) {
            Class testClass = getTestClass();
            settings().getSnapshotFixtureWriter()
                      .write(testClass, InvocationContextHolder.currentSnapshotName(), actual);
            createdOrUpdated = true;
        }

        return createdOrUpdated;
    }

    private void initTest() {
        String testMethodName = getTestMethodName();
        InvocationContextHolder.updateContext(testMethodName);
    }

    private boolean canGenerateNewSnapshot() {
        JsonNode snapshotFixture = getSnapshotFixtureNode();
        return snapshotFixture == null;
    }

    public JsonNode getSnapshotFixtureNode() {
        return beanFactory.getFixtureAsJsonNode(InvocationContextHolder.currentSnapshotName()).orElse(null);
    }

    public String getCurrentSnapshotFixtureName() {
        return InvocationContextHolder.currentSnapshotName();
    }

    public void validateSnapshots() {
        scanner.collectResources().stream().map(this::convertToJsonNode).forEach(this::verifyAutoGeneratedClasses);
    }

    private AutoGeneratedResource convertToJsonNode(Resource resource) {
        try {
            return new AutoGeneratedResource(settings().getObjectMapper().readTree(resource.getInputStream()),
                                             resource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void verifyAutoGeneratedClasses(AutoGeneratedResource autoGeneratedResource) {
        JsonNode snapshotClass = autoGeneratedResource.node.get(DefaultSnapshotWriter.AUTO_GENERATED_FOR);
        if (snapshotClass != null) {
            String fullClassName = snapshotClass.textValue();
            try {
                Class.forName(fullClassName);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(
                    fullClassName + " does not exist anymore in '" + autoGeneratedResource.getPath() +
                    "' file. If the file has been renamed/removed, please update the value of " +
                    DefaultSnapshotWriter.AUTO_GENERATED_FOR + " property and move the file to the correct location.");
            }
        }
    }

    private static class AutoGeneratedResource {

        private final JsonNode node;
        private final Resource resource;

        AutoGeneratedResource(JsonNode node, Resource resource) {
            this.node = node;
            this.resource = resource;
        }

        public String getPath() {
            try {
                return resource.getURI().getPath();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
