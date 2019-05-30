package ie.corballis.fixtures.snapshot;

import ie.corballis.fixtures.core.BeanFactory;
import ie.corballis.fixtures.io.ClassPathFixtureScanner;
import ie.corballis.fixtures.io.ClassPathResource;
import ie.corballis.fixtures.io.Resource;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static ie.corballis.fixtures.settings.SettingsHolder.settings;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SnapshotGeneratorTest {

    @Mock
    private ClassPathFixtureScanner scanner;

    private SnapshotGenerator snapshotGenerator;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        BeanFactory beanFactory = new BeanFactory(settings().getObjectMapper(), scanner);
        beanFactory.init();

        snapshotGenerator = new SnapshotGenerator(beanFactory, scanner);
    }

    @Test
    public void shouldFailWhenInvalidAutoGeneratedClassDetected() {
        List<Resource> mockResources = createMockResources();
        mockResources.add(new ClassPathResource("invalid-fixture.json"));

        when(scanner.collectResources()).thenReturn(mockResources);

        expectedException.expectMessage("ie.corballis.fixtures.snapshot.NoSuchClass does not exist anymore in ");
        expectedException.expectMessage("invalid-fixture.json' file. " + "If the file has been renamed/removed, " +
                                        "please update the value of _AUTO_GENERATED_FOR_ property " +
                                        "and move the file to the correct location");

        snapshotGenerator.validateSnapshots();
    }

    @Test
    public void shouldNotFailWhenValidAutoGeneratedClassDetected() {
        List<Resource> mockResources = createMockResources();
        mockResources.add(new ClassPathResource("valid-auto-generated-fixture.json"));
        when(scanner.collectResources()).thenReturn(mockResources);
        snapshotGenerator.validateSnapshots();
    }

    @Test
    public void shouldNotCareWithNonAutoGeneratedFixtures() {
        List<Resource> mockResources = createMockResources();
        when(scanner.collectResources()).thenReturn(mockResources);
        snapshotGenerator.validateSnapshots();
    }

    private List<Resource> createMockResources() {
        List<Resource> resources = newArrayList();

        resources.add(new ClassPathResource("owners.fixtures.json"));
        resources.add(new ClassPathResource("references.fixtures.json"));

        return resources;
    }
}