package net.samagames.core.rest.request;

import java.util.HashMap;
import java.util.Map;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by Thog
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class Request
{
    private Map<String, Object> data;

    public Request addProperty(String key, Object value)
    {
        if(data == null)
            data = new HashMap<>();
        data.put(key, value);
        return this;
    }
}
