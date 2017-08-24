package net.samagames.core.api.player;

import java.util.HashMap;
import java.util.Map;

/*
 * This file is part of SamaGamesCore.
 *
 * SamaGamesCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SamaGamesCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SamaGamesCore.  If not, see <http://www.gnu.org/licenses/>.
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
        return endTime > System.currentTimeMillis();
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
