package net.samagames.core.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

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
                field.setAccessible(true);
                Field declaredField = b.getClass().getDeclaredField(field.getName());
                declaredField.setAccessible(true);
                declaredField.set(b, field.get(a));
            } catch (NoSuchFieldException | IllegalAccessException  e) {
                continue;
            }
        }
    }

    public static void deserialiseFromRedis(Jedis jedis, String key, Object output)
    {
        Field[] declaredFields = output.getClass().getDeclaredFields();
        for (Field field : declaredFields)
        {
            field.setAccessible(true);
            try {
                //TODO check if good cast
                field.set(output, jedis.hget(key, field.getName()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static void serializeInRedis(Jedis jedis, String key, Object input)
    {
        Pipeline pipeline = jedis.pipelined();
        Field[] declaredFields = input.getClass().getDeclaredFields();
        for (Field field : declaredFields)
        {
            field.setAccessible(true);
            try {
                pipeline.hset(key, field.getName(), field.get(input).toString());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        pipeline.exec();
        pipeline.discard();
    }
}
