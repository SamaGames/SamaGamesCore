package net.samagames.core.rest.response;

import java.lang.reflect.Field;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by Thog
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class Response
{
    @Override
    public String toString()
    {
        String tmp = getClass().getSimpleName() + " [";
        for (Field field : getClass().getDeclaredFields())
        {
            try
            {
                field.setAccessible(true);
                tmp += field.getName() + ": " + field.get(this) + ", ";
            } catch (IllegalAccessException e)
            {
                continue;
            }
        }
        tmp += "]";
        return tmp;
    }
}
