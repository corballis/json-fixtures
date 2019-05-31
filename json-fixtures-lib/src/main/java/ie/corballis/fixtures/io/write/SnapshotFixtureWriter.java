package ie.corballis.fixtures.io.write;

import java.io.File;
import java.io.IOException;

public interface SnapshotFixtureWriter extends FixtureWriter {

    File write(Class testClass, String fixtureName, Object contents) throws IOException;

}
