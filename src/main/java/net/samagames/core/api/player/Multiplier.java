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

    public final Map<String, Integer> data = new HashMap<>();
    public int globalAmount = 1;

    public int getGlobalAmount()
    {
        return (globalAmount >= 1) ? globalAmount : 1;
    }

}
