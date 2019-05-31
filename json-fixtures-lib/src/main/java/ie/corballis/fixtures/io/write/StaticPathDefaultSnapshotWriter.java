package ie.corballis.fixtures.io.write;

import com.fasterxml.jackson.databind.ObjectMapper;

public class StaticPathDefaultSnapshotWriter extends DefaultSnapshotWriter {

    private final String fixtureFolder;

    public StaticPathDefaultSnapshotWriter(String fixtureFolder, ObjectMapper objectMapper) {
        super(objectMapper);
        this.fixtureFolder = fixtureFolder;
    }

    @Override
    protected String getFixtureFileFolder(Class testClass) {
        return fixtureFolder;
    }
}
