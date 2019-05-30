package ie.corballis.fixtures.settings;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import ie.corballis.fixtures.io.ClassPathFixtureScanner;
import ie.corballis.fixtures.io.FixtureScanner;
import ie.corballis.fixtures.io.write.*;

import static com.google.common.base.Preconditions.checkNotNull;

public class Settings {

    private final ObjectMapper objectMapper;
    private final FileNamingStrategy snapshotFileNamingStrategy;
    private final FileNamingStrategy generatorFileNamingStrategy;
    private final SnapshotFixtureWriter snapshotFixtureWriter;
    private final FixtureScanner fixtureScanner;

    public Settings(Settings.Builder builder) {
        this.objectMapper = builder.objectMapper;
        this.snapshotFileNamingStrategy = builder.snapshotFileNamingStrategy;
        this.generatorFileNamingStrategy = builder.generatorFileNamingStrategy;
        this.snapshotFixtureWriter = builder.snapshotFixtureWriter;
        this.fixtureScanner = builder.fixtureScanner;
    }

    public static Settings defaultSettings() {
        return new Builder().build();
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public FileNamingStrategy getSnapshotFileNamingStrategy() {
        return snapshotFileNamingStrategy;
    }

    public SnapshotFixtureWriter getSnapshotFixtureWriter() {
        return snapshotFixtureWriter;
    }

    public FileNamingStrategy getGeneratorFileNamingStrategy() {
        return generatorFileNamingStrategy;
    }

    public FixtureScanner getFixtureScanner() {
        return fixtureScanner;
    }

    public static class Builder {

        private FixtureScanner fixtureScanner;
        private FileNamingStrategy snapshotFileNamingStrategy;
        private FileNamingStrategy generatorFileNamingStrategy;
        private SnapshotFixtureWriter snapshotFixtureWriter;
        private ObjectMapper objectMapper;

        public Builder() {
            FileNamingStrategy fileNamingStrategy = TestClassFileNamingStrategy.getInstance();
            setDefaultObjectMapper();
            setDefaultSnapshotWriter();
            setDefaultFixtureScanner();
            setSnapshotFileNamingStrategy(fileNamingStrategy);
            setGeneratorFileNamingStrategy(fileNamingStrategy);
        }

        public ObjectMapper getObjectMapper() {
            return objectMapper;
        }

        public Builder setObjectMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
            return this;
        }

        public Builder setDefaultObjectMapper() {
            this.objectMapper = defaultObjectMapper();
            return this;
        }

        private static ObjectMapper defaultObjectMapper() {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.setDefaultPrettyPrinter(new JsonFixturesPrettyPrinter());
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            return objectMapper;
        }

        public FileNamingStrategy getSnapshotFileNamingStrategy() {
            return snapshotFileNamingStrategy;
        }

        public Builder setSnapshotFileNamingStrategy(FileNamingStrategy snapshotFileNamingStrategy) {
            this.snapshotFileNamingStrategy = snapshotFileNamingStrategy;
            return this;
        }

        public FileNamingStrategy getGeneratorFileNamingStrategy() {
            return generatorFileNamingStrategy;
        }

        public Builder setGeneratorFileNamingStrategy(FileNamingStrategy snapshotFileNamingStrategy) {
            this.generatorFileNamingStrategy = snapshotFileNamingStrategy;
            return this;
        }

        public FixtureWriter getSnapshotFixtureWriter() {
            return snapshotFixtureWriter;
        }

        public Builder setSnapshotFixtureWriter(SnapshotFixtureWriter snapshotFixtureWriter) {
            this.snapshotFixtureWriter = snapshotFixtureWriter;
            return this;
        }

        public Builder setDefaultSnapshotWriter() {
            checkNotNull(objectMapper, "You must set ObjectMapper first to instantiate DefaultSnapshotWriter");
            return setSnapshotFixtureWriter(new DefaultSnapshotWriter(objectMapper));
        }

        public Builder setSnapshotFolderPath(String snapshotFolderPath) {
            this.snapshotFixtureWriter = new StaticPathDefaultSnapshotWriter(snapshotFolderPath, objectMapper);
            return this;
        }

        public Builder setFixtureScanner(FixtureScanner fixtureScanner) {
            this.fixtureScanner = fixtureScanner;
            return this;
        }

        public Builder setDefaultFixtureScanner() {
            return setFixtureScanner(new ClassPathFixtureScanner());
        }

        public Settings build() {
            return new Settings(this);
        }

    }

}
