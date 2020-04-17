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
import static ie.corballis.fixtures.util.VisitedValue.valueOf;

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
        return visitElementsInternal(original, newHashMap(), new Stack<>(), visitor).getValue();
    }

    public static Object visitElements(JsonNode original,
                                       Object newObject,
                                       Stack<Object> path,
                                       JsonNodeVisitor visitor) {
        return visitElementsInternal(original, newObject, path, visitor).getValue();
    }

    private static VisitedValue visitElementsInternal(JsonNode original,
                                                      Object newObject,
                                                      Stack<Object> path,
                                                      JsonNodeVisitor visitor) {
        if (original.isObject()) {
            Iterator<String> fieldNames = original.fieldNames();
            HashMap<Object, Object> nested = newLinkedHashMap();

            VisitedValue object = visitor.visitObject(original, path);
            if (object != null) {
                return object;
            }

            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                path.push(fieldName);

                JsonNode childNode = original.get(fieldName);
                VisitedValue value = visitElementsInternal(childNode, nested, path, visitor);
                if (value.isAppendToResult()) {
                    nested.put(fieldName, value.getValue());
                }
                path.pop();
            }

            return valueOf(nested);
        } else if (original.isArray()) {
            List<Object> nestedList = newArrayList();

            VisitedValue object = visitor.visitList(original, path);
            if (object != null) {
                return object;
            }

            for (int i = 0; i < original.size(); i++) {
                path.push(i);
                VisitedValue value = visitElementsInternal(original.get(i), nestedList, path, visitor);
                if (value.isAppendToResult()) {
                    nestedList.add(value.getValue());
                }
                path.pop();
            }

            return valueOf(nestedList);
        } else {
            return visitor.visitElement(original, newObject, path);
        }
    }

}
