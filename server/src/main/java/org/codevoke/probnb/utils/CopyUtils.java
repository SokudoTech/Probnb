package org.codevoke.probnb.utils;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;


@Slf4j
public class CopyUtils {
    public static void copyNonNullProperties(Object source, Object target) {
        Class<?> sourceClass = source.getClass();
        Class<?> targetClass = target.getClass();

        try {
            Field[] fields = sourceClass.getDeclaredFields();

            for (Field sourceField : fields) {
                try {
                    Field targetField = targetClass.getDeclaredField(sourceField.getName());

                    sourceField.setAccessible(true);
                    targetField.setAccessible(true);

                    Object value = sourceField.get(source);

                    if (value != null) {
                        targetField.set(target, value);
                    }
                } catch (NoSuchFieldException ignored) {
                } catch (IllegalAccessException | IllegalArgumentException e) {
                    log.error(e.getMessage());
                }
            }
        } catch (SecurityException e) {
            log.error(e.getMessage());
        }
    }
}
