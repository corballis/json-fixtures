package ie.corballis.fixtures.io.scanner;

import ie.corballis.fixtures.assertion.FixtureAssertSnapshotsTest;
import ie.corballis.fixtures.io.Resource;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

public class FixtureScannerTest {

    @Test
    public void scannersShouldFindResourcesProperly() {
        FolderFixtureScanner folderFixtureScanner = new FolderFixtureScanner(getClass());
        verifyResources(folderFixtureScanner.collectResources(),
                        "FixtureScannerTest-all.fixtures.json",
                        "FixtureScannerTest-results.fixtures.json",
                        "this-is-a-sample.fixtures.json",
                        "this-is-a-sample2.fixtures.json");

        TestFileNameFixtureScanner testFileNameFixtureScanner = new TestFileNameFixtureScanner(getClass());
        verifyResources(testFileNameFixtureScanner.collectResources(),
                        "FixtureScannerTest-all.fixtures.json",
                        "FixtureScannerTest-results.fixtures.json");

        CompositeFixtureScanner compositeFixtureScanner = new CompositeFixtureScanner(folderFixtureScanner,
                                                                                      testFileNameFixtureScanner,
                                                                                      new TestFileNameFixtureScanner(
                                                                                          FixtureAssertSnapshotsTest.class));
        verifyResources(compositeFixtureScanner.collectResources(),
                        "FixtureScannerTest-all.fixtures.json",
                        "FixtureScannerTest-results.fixtures.json",
                        "this-is-a-sample.fixtures.json",
                        "this-is-a-sample2.fixtures.json",
                        "FixtureAssertSnapshotsTest.fixtures.json");
    }

    private void verifyResources(List<Resource> input, String... fileNames) {
        List<String> expectedFiles = newArrayList(fileNames);
        List<String> paths = input.stream().map(resource -> {
            try {
                return FilenameUtils.getName(resource.getURI().getPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
        assertThat(input).hasSize(expectedFiles.size());
        assertThat(paths).containsAll(expectedFiles);
    }

}