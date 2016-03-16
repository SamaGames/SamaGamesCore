package net.samagames.core.utils;

import java.lang.reflect.Field;

/**
 * Created by Silvanosky on 15/03/2016.
 */
public class ReflectionUtils {

    /**
     *
     * @param a Element to be copied
     * @param b Element to copy (receive)
     */
    public static void copySameFields(Object a, Object b)
    {
        Field[] declaredFields = a.getClass().getDeclaredFields();

        for (Field field : declaredFields)
        {
            try {
                Field declaredField = b.getClass().getDeclaredField(field.getName());
                declaredField.set(b, field.get(a));
            } catch (NoSuchFieldException e) {
                continue;
            } catch (IllegalAccessException e) {
                continue;
            }
        }
    }
}
