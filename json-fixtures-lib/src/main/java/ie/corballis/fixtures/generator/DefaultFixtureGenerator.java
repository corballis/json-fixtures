package ie.corballis.fixtures.generator;

import com.google.common.base.Defaults;
import com.google.common.primitives.Primitives;
import ie.corballis.fixtures.util.FieldReader;
import ie.corballis.fixtures.util.FieldSetter;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static ie.corballis.fixtures.settings.SettingsHolder.settings;

public class DefaultFixtureGenerator implements FixtureGenerator {
    @Override
    public Map<String, Object> generateMapFromBeanDirectly(Class clazz) throws Exception {
        checkNotNull(clazz, "The target class may not be null!");
        Object instance = createBeanInstance(clazz);
        return generateMap(instance);
    }

    public Object createBeanInstance(Class clazz) throws Exception {
        Object instance = clazz.newInstance();
        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                setField(field, instance);
            }
            clazz = clazz.getSuperclass();
        }
        return instance;
    }

    private void setField(Field field, Object instance) throws Exception {
        Class<?> type = field.getType();

        if (type.isPrimitive()) {
            return;
        }

        if (new FieldReader(instance, field).isNull()) {
            if (type.isArray()) {
                Object value = Array.newInstance(type.getComponentType(), 0);
                setFieldValue(field, instance, value);
            } else if (Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type)) {
                if (!type.isInterface()) {
                    Object value = type.newInstance();
                    setFieldValue(field, instance, value);
                }
            } else if (Primitives.isWrapperType(type)) {
                Class primitiveType = Primitives.unwrap(type);
                Object value = Defaults.defaultValue(primitiveType);
                setFieldValue(field, instance, value);
            } else if (type == String.class) {
                setFieldValue(field, instance, "");
            } else if (type == Date.class) {
                setFieldValue(field, instance, new Date(0));
            } // enum and object field: remains null!
        }
    }

    private void setFieldValue(Field field, Object instance, Object value) throws Exception {
        try {
            new FieldSetter(instance, field).set(value);
        } catch (Exception e) {
            throw new Exception("Problems setting field '" + field.getName() + "'", e);
        }
    }

    private Map<String, Object> generateMap(Object instance) {
        return settings().getObjectMapper().convertValue(instance, Map.class);
    }
}