package ie.corballis.fixtures.settings;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import ie.corballis.fixtures.core.BeanFactory;
import ie.corballis.fixtures.core.JacksonDeserializer;
import ie.corballis.fixtures.io.DeserializeMapper;
import ie.corballis.fixtures.io.scanner.CompositeFixtureScanner;
import ie.corballis.fixtures.io.scanner.FixtureScanner;
import ie.corballis.fixtures.io.scanner.FolderFixtureScanner;
import ie.corballis.fixtures.io.scanner.ResourceRootFixtureScanner;
import ie.corballis.fixtures.io.scanner.TestFileNameFixtureScanner;
import ie.corballis.fixtures.io.write.DefaultSnapshotWriter;
import ie.corballis.fixtures.io.write.FileNamingStrategy;
import ie.corballis.fixtures.io.write.FixtureWriter;
import ie.corballis.fixtures.io.write.JsonFixturesPrettyPrinter;
import ie.corballis.fixtures.io.write.SnapshotFixtureWriter;
import ie.corballis.fixtures.io.write.StaticPathDefaultSnapshotWriter;
import ie.corballis.fixtures.io.write.TestClassFileNamingStrategy;
import ie.corballis.fixtures.snapshot.SnapshotGenerator;

import static com.google.common.base.Preconditions.checkNotNull;

public class Settings {

    private final ObjectMapper objectMapper;
    private final FileNamingStrategy snapshotFileNamingStrategy;
    private final FileNamingStrategy generatorFileNamingStrategy;
    private final SnapshotFixtureWriter snapshotFixtureWriter;
    private final FixtureScanner fixtureScanner;
    private final BeanFactory beanFactory;
    private final SnapshotGenerator snapshotGenerator;
    private final DeserializeMapper deserializeMapper;

    public Settings(Settings.Builder builder) {
        this.objectMapper = builder.objectMapper;
        this.snapshotFileNamingStrategy = builder.snapshotFileNamingStrategy;
        this.generatorFileNamingStrategy = builder.generatorFileNamingStrategy;
        this.snapshotFixtureWriter = builder.snapshotFixtureWriter;
        this.fixtureScanner = builder.fixtureScanner;
        this.deserializeMapper = builder.deserializeMapper;
        this.beanFactory = new BeanFactory(this.objectMapper, this.fixtureScanner, this.deserializeMapper);
        this.beanFactory.init();
        this.snapshotGenerator = new SnapshotGenerator(beanFactory, this.fixtureScanner);
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

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public SnapshotGenerator getSnapshotGenerator() {
        return snapshotGenerator;
    }

    public static class Builder {
        private DeserializeMapper deserializeMapper;
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
            setDefaultDeserializeMapper();
            setSnapshotFileNamingStrategy(fileNamingStrategy);
            setGeneratorFileNamingStrategy(fileNamingStrategy);
        }

        public Builder setDeserializeMapper(DeserializeMapper deserializeMapper) {
            this.deserializeMapper = deserializeMapper;
            return this;
        }

        public Builder setDefaultObjectMapper() {
            this.objectMapper = defaultObjectMapper();
            return this;
        }

        public static ObjectMapper defaultObjectMapper() {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.setDefaultPrettyPrinter(new JsonFixturesPrettyPrinter());
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
            return objectMapper;
        }

        public Builder setDefaultSnapshotWriter() {
            checkNotNull(objectMapper, "You must set ObjectMapper first to instantiate DefaultSnapshotWriter");
            return setSnapshotFixtureWriter(new DefaultSnapshotWriter(objectMapper));
        }

        public Builder setDefaultFixtureScanner() {
            return setFixtureScanner(new ResourceRootFixtureScanner());
        }

        public Builder setFixtureScanner(FixtureScanner fixtureScanner) {
            this.fixtureScanner = fixtureScanner;
            return this;
        }

        public ObjectMapper getObjectMapper() {
            return objectMapper;
        }

        public Builder setObjectMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
            return this;
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

        public Builder setSnapshotFolderPath(String snapshotFolderPath) {
            this.snapshotFixtureWriter = new StaticPathDefaultSnapshotWriter(snapshotFolderPath, objectMapper);
            return this;
        }

        public Builder setDefaultDeserializeMapper() {
            this.deserializeMapper = new JacksonDeserializer(objectMapper);
            return this;
        }

        public Builder useTestFileNameFixtureScanner(Class testClass) {
            return setFixtureScanner(new TestFileNameFixtureScanner(testClass));
        }

        public Builder useTestFolderFixture(Class testClass) {
            return setFixtureScanner(new FolderFixtureScanner(testClass));
        }

        public Builder useFolderFixtureScanner(Class testClass) {
            return setFixtureScanner(new FolderFixtureScanner(testClass));
        }

        public Builder useFolderFixtureScanner(String path) {
            return setFixtureScanner(new FolderFixtureScanner(path));
        }

        public Builder useCompositeFixtureScanner(FixtureScanner... scanners) {
            return setFixtureScanner(new CompositeFixtureScanner(scanners));
        }

        public Settings build() {
            return new Settings(this);
        }

    }

}
