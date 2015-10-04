package net.samagames.core.api.player;

import java.util.HashMap;
import java.util.Map;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by Thog
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class Multiplier
{
    private int globalAmount;
    private final long endTime;
    private final Map<String, Integer> combinedData = new HashMap<>();
    private String message;

    public Multiplier(int globalAmount, long endTime, String message)
    {
        this(globalAmount, endTime);
        this.message = message;
    }

    public Multiplier(int globalAmount, long endTime)
    {
        this.globalAmount = globalAmount;
        this.endTime = endTime;
    }
    public int getGlobalAmount()
    {
        return (globalAmount >= 1) ? globalAmount : 1;
    }

    public boolean isValid()
    {
        return endTime < System.currentTimeMillis();
    }
    
    public String getMessage()
    {
        return message;
    }

    public Multiplier cross(Multiplier multiplier)
    {
        this.globalAmount *= multiplier.getGlobalAmount();
        this.combinedData.put(multiplier.getMessage(), multiplier.getGlobalAmount());
        return this;
    }

    public Map<String, Integer> getCombinedData()
    {
        return combinedData;
    }

    public Multiplier cross(int multiplier)
    {
        this.combinedData.put("", multiplier);
        this.globalAmount *= multiplier;
        return this;
    }
}
