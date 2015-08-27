package ie.corballis.fixtures.util;

import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.SimpleType;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

public class ClassUtils {

    public static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back to system class loader...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = ClassUtils.class.getClassLoader();
        }
        return cl;
    }

    public static Type getFixtureType(Field field) {
        if (Collection.class.isAssignableFrom(field.getType()) && field.getGenericType() instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
            Type fieldArgType = parameterizedType.getActualTypeArguments()[0];
            SimpleType elementType = SimpleType.construct((Class<?>) fieldArgType);
            return CollectionType.construct(field.getType(), elementType);
        } else if (Map.class.isAssignableFrom(field.getType()) && field.getGenericType() instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
            Type fieldArgType1 = parameterizedType.getActualTypeArguments()[0];
            Type fieldArgType2 = parameterizedType.getActualTypeArguments()[1];
            SimpleType keyType = SimpleType.construct((Class<?>) fieldArgType1);
            SimpleType valueType = SimpleType.construct((Class<?>) fieldArgType2);
            return MapType.construct(field.getType(), keyType, valueType);
        } else {
            return field.getType();
        }
    }

}