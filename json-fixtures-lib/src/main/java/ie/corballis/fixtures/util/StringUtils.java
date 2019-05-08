package ie.corballis.fixtures.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    public static String replaceAll(String str, String originalToken, String replacementToken) {
        return str.replaceAll(Pattern.quote(originalToken), Matcher.quoteReplacement(replacementToken));
    }

    public static String cleanPath(String path) {
        return replaceAll(path, "\\", "/");
    }

    // changes the Windows CR LF line endings to Unix LF type in a string
    // so that the pretty strings are formatted uniformly, independently of the OS platform
    public static String unifyLineEndings(String s) {
        return s.replaceAll("\\r\\n", "\\\n");
    }
    
}