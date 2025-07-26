package net.bitbylogic.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@Getter
public enum StringProcessor {

    DEFAULT(new Class[]{String.class}, (type, value) -> value),
    INT(new Class[]{Integer.class, int.class}, (type, value) -> Integer.parseInt(value)),
    LONG(new Class[]{Long.class, long.class}, (type, value) -> Long.parseLong(value)),
    DOUBLE(new Class[]{Double.class, double.class}, (type, value) -> Double.parseDouble(value)),
    FLOAT(new Class[]{Float.class, float.class}, (type, value) -> Float.parseFloat(value)),
    SHORT(new Class[]{Short.class, short.class}, (type, value) -> Short.parseShort(value)),
    BYTE(new Class[]{Byte.class, byte.class}, (type, value) -> Byte.parseByte(value)),
    BOOLEAN(new Class[]{Boolean.class, boolean.class}, (type, value) -> {
        if (NumberUtil.isNumber(value)) {
            return value.equalsIgnoreCase("1");
        }
        return Boolean.parseBoolean(value);
    }),
    CHAR(new Class[]{Character.class, char.class}, (type, value) -> value.isEmpty() ? 'A' : value.charAt(0)),
    UUID(new Class[]{UUID.class}, (type, value) -> java.util.UUID.fromString(value)),
    ENUM(new Class[]{Enum.class}, (type, value) -> {
        if (type instanceof Class<?> enumClass && Enum.class.isAssignableFrom(enumClass)) {
            @SuppressWarnings("unchecked")
            Class<Enum> casted = (Class<Enum>) enumClass;
            return EnumUtil.getValue(casted, value, null);
        }

        return null;
    });

    private final Class<?>[] dataTypes;
    private final StringProcessorFunction<?> processor;

    public static Object findAndProcess(Type type, String value) {
        if (value == null || value.isEmpty()) {
            if (type instanceof ParameterizedType pt) {
                Type raw = pt.getRawType();
                if (raw instanceof Class<?> rawClass && List.class.isAssignableFrom(rawClass)) {
                    return List.of();
                }
                if (raw instanceof Class<?> mapClass && Map.class.isAssignableFrom(mapClass)) {
                    return new HashMap<>();
                }
            }

            if (type instanceof Class<?> clazz) {
                if (List.class.isAssignableFrom(clazz)) {
                    return List.of();
                }
                if (Map.class.isAssignableFrom(clazz)) {
                    return new HashMap<>();
                }
            }

            return "";
        }

        if (type instanceof ParameterizedType pt) {
            Type raw = pt.getRawType();
            Type[] args = pt.getActualTypeArguments();

            if (raw instanceof Class<?> rawClass && List.class.isAssignableFrom(rawClass)) {
                Type elementType = args[0];
                return ListUtil.stringToList(value, ":", string -> (Object) findAndProcess(elementType, string));
            }

            if (raw instanceof Class<?> mapClass && Map.class.isAssignableFrom(mapClass)) {
                Type keyType = args[0];
                Type valueType = args[1];
                return HashMapUtil.mapFromString(new HashMapUtil.ObjectWrapper<>() {
                    @Override
                    public Object wrapKey(String key) {
                        return findAndProcess(keyType, key);
                    }

                    @Override
                    public Object wrapValue(String val) {
                        return findAndProcess(valueType, val);
                    }
                }, value);
            }
        }

        if (type instanceof Class<?> clazz) {
            for (StringProcessor processor : values()) {
                for (Class<?> supported : processor.getDataTypes()) {
                    if (supported.isAssignableFrom(clazz)) {
                        return processor.getProcessor().process(clazz, value);
                    }
                }
            }
        }

        return value;
    }

    public static String toStringFromObject(Type type, Object value) {
        if (value == null) {
            return "";
        }

        if (type instanceof ParameterizedType pt) {
            Type raw = pt.getRawType();
            Type[] args = pt.getActualTypeArguments();

            if (raw instanceof Class<?> rawClass && List.class.isAssignableFrom(rawClass)) {
                Type elementType = args[0];
                return ListUtil.listToString((List<?>) value, ":", v -> toStringFromObject(elementType, v));
            }

            if (raw instanceof Class<?> mapClass && Map.class.isAssignableFrom(mapClass)) {
                Type keyType = args[0];
                Type valueType = args[1];

                @SuppressWarnings("unchecked")
                HashMap<Object, Object> castedMap = new HashMap<>((Map<Object, Object>) value);

                return HashMapUtil.mapToString(castedMap, new HashMapUtil.ObjectParser<>() {
                    @Override
                    public String wrapKey(Object key) {
                        return toStringFromObject(keyType, key);
                    }

                    @Override
                    public String wrapValue(Object val) {
                        return toStringFromObject(valueType, val);
                    }
                });
            }
        }

        if (type instanceof Class<?> clazz) {
            if (clazz.isEnum()) {
                return ((Enum<?>) value).name();
            }
            return value.toString();
        }

        return value.toString();
    }

    public static Object findAndProcess(Class<?> clazz, String value) {
        return findAndProcess((Type) clazz, value);
    }

    public static String toStringFromObject(Class<?> clazz, Object value) {
        return toStringFromObject((Type) clazz, value);
    }

    private interface StringProcessorFunction<V> {
        V process(Type targetType, String value);
    }

}