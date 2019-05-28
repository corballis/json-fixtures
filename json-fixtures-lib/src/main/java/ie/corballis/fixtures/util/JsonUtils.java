package ie.corballis.fixtures.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Joiner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.newLinkedHashMap;

public class JsonUtils {

    public static String pathToReferenceChain(Stack<Object> path) {
        return Joiner.on("->").skipNulls().join(path);
    }

    public static Object getPrimitiveValue(JsonNode original) {
        try {
            if (original.isBoolean()) {
                return original.asBoolean();
            } else if (original.isNumber()) {
                return original.numberValue();
            } else if (original.isBinary()) {
                return original.binaryValue();
            } else if (original.isNull()) {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return original.textValue();
    }

    public static Object visitElements(JsonNode original, JsonNodeVisitor visitor) {
        return visitElements(original, newHashMap(), new Stack<>(), visitor);
    }

    public static Object visitElements(JsonNode original,
                                       Object newObject,
                                       Stack<Object> path,
                                       JsonNodeVisitor visitor) {
        if (original.isObject()) {
            Iterator<String> fieldNames = original.fieldNames();
            HashMap<Object, Object> nested = newLinkedHashMap();
            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                path.push(fieldName);

                JsonNode childNode = original.get(fieldName);
                Object value = visitElements(childNode, nested, path, visitor);
                if (value != JsonNodeVisitor.AppendMode.NONE) {
                    nested.put(fieldName, value);
                }
                path.pop();
            }

            return nested;
        } else if (original.isArray()) {
            List<Object> nestedList = newArrayList();
            for (int i = 0; i < original.size(); i++) {
                path.push(i);
                Object value = visitElements(original.get(i), nestedList, path, visitor);
                if (value != JsonNodeVisitor.AppendMode.NONE) {
                    nestedList.add(value);
                }
                path.pop();
            }

            return nestedList;
        } else {
            return visitor.visitElement(original, newObject, path);
        }
    }

}
