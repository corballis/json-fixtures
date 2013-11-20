package com.corballis.fixtures.io;

import com.google.common.collect.ImmutableList;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static com.google.common.collect.Lists.newArrayList;

public class ClassPathFixtureScanner implements FixtureScanner {

    private static final Logger logger = LoggerFactory.getLogger(ClassPathFixtureScanner.class);

    private static final String FIXTURE_REG_EXP = ".*\\.fixtures\\.json";
    private static final Pattern FIXTURE_PATTERN = Pattern.compile(FIXTURE_REG_EXP);
    private static final List<Resource> resources;

    static {
        Reflections reflections = new Reflections(ClasspathHelper.forJavaClassPath(), new ResourcesScanner());
        Set<String> fixturePaths = reflections.getResources(FIXTURE_PATTERN);
        resources = ImmutableList.copyOf(convertToResources(fixturePaths));
        logger.debug("Detected fixture files: {}", resources);
    }

    @Override
    public List<Resource> collectResources() {
        return resources;
    }

    private static List<Resource> convertToResources(Collection<String> paths) {
        List<Resource> resources = newArrayList();

        for (String path : paths) {
            resources.add(new ClassPathResource(path));
        }

        return resources;
    }

}
