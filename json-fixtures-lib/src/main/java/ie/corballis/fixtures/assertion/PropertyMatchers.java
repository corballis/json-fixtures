package ie.corballis.fixtures.assertion;

import com.google.common.collect.ImmutableMap;
import org.hamcrest.Matcher;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toMap;

public class PropertyMatchers {

    private final Map<String, Matcher> matchers;

    public PropertyMatchers(Map<String, Matcher> matchers) {
        this.matchers = ImmutableMap.copyOf(matchers);
    }

    private PropertyMatchers(Set<Map.Entry<String, Matcher>> matcherEntries) {
        matchers = ImmutableMap.copyOf(matcherEntries.stream()
                                                     .collect(toMap(Map.Entry::getKey,
                                                                    Map.Entry::getValue,
                                                                    (a, b) -> b)));
    }

    public static PropertyMatchers overriddenMatchers(String property, Matcher matcher) {
        return new PropertyMatchers(newHashSet(entryOf(property, matcher)));
    }

    private static Map.Entry<String, Matcher> entryOf(String property, Matcher matcher) {
        return new AbstractMap.SimpleImmutableEntry<>(property, matcher);
    }

    public static PropertyMatchers overriddenMatchers(String p1, Matcher m1, String p2, Matcher m2) {
        return new PropertyMatchers(newHashSet(entryOf(p1, m1), entryOf(p2, m2)));
    }

    public static PropertyMatchers overriddenMatchers(String p1,
                                                      Matcher m1,
                                                      String p2,
                                                      Matcher m2,
                                                      String p3,
                                                      Matcher m3) {
        return new PropertyMatchers(newHashSet(entryOf(p1, m1), entryOf(p2, m2), entryOf(p3, m3)));
    }

    public static PropertyMatchers overriddenMatchers(String p1,
                                                      Matcher m1,
                                                      String p2,
                                                      Matcher m2,
                                                      String p3,
                                                      Matcher m3,
                                                      String p4,
                                                      Matcher m4) {
        return new PropertyMatchers(newHashSet(entryOf(p1, m1), entryOf(p2, m2), entryOf(p3, m3), entryOf(p4, m4)));
    }

    public static PropertyMatchers overriddenMatchers(String p1,
                                                      Matcher m1,
                                                      String p2,
                                                      Matcher m2,
                                                      String p3,
                                                      Matcher m3,
                                                      String p4,
                                                      Matcher m4,
                                                      String p5,
                                                      Matcher m5) {
        return new PropertyMatchers(newHashSet(entryOf(p1, m1),
                                               entryOf(p2, m2),
                                               entryOf(p3, m3),
                                               entryOf(p4, m4),
                                               entryOf(p5, m5)));
    }

    public static PropertyMatchers overriddenMatchers(String p1,
                                                      Matcher m1,
                                                      String p2,
                                                      Matcher m2,
                                                      String p3,
                                                      Matcher m3,
                                                      String p4,
                                                      Matcher m4,
                                                      String p5,
                                                      Matcher m5,
                                                      Object... additionalMatchers) {
        boolean oddValuesAreStrings = IntStream.range(0, additionalMatchers.length)
                                               .allMatch(index -> oddValuesAreStrings(additionalMatchers, index));
        boolean evenValuesAreMatchers = IntStream.range(0, additionalMatchers.length)
                                                 .allMatch(index -> evenValuesAreMatchers(additionalMatchers, index));
        checkArgument(oddValuesAreStrings && evenValuesAreMatchers,
                      "Matchers are not defined correctly, " +
                      "you must set the matchers after the property definition." +
                      " e.g.: \"id\", Matchers.any(), \"createdAt\", Matchers.any()");

        Set<Map.Entry<String, Matcher>> entries = newHashSet(entryOf(p1, m1),
                                                             entryOf(p2, m2),
                                                             entryOf(p3, m3),
                                                             entryOf(p4, m4),
                                                             entryOf(p5, m5));
        for (int i = 0; i < additionalMatchers.length; i = i + 2) {
            String property = String.valueOf(additionalMatchers[i]);
            Matcher matcher = (Matcher) additionalMatchers[i + 1];
            entries.add(entryOf(property, matcher));
        }
        return new PropertyMatchers(entries);
    }

    private static boolean oddValuesAreStrings(Object[] matchers, int index) {
        if (!((index + 1) % 2 == 0)) {
            return matchers[index] instanceof String;
        }
        return true;
    }

    private static boolean evenValuesAreMatchers(Object[] matchers, int index) {
        if (((index + 1) % 2) == 0) {
            return matchers[index] instanceof Matcher;
        }
        return true;
    }

    protected static PropertyMatchers empty() {
        return new PropertyMatchers(newHashMap());
    }

    public boolean isEmpty() {
        return matchers.isEmpty();
    }

    public Set<String> getProperties() {
        return matchers.keySet();
    }

    public Matcher getMatcher(String property) {
        return matchers.get(property);
    }

}
