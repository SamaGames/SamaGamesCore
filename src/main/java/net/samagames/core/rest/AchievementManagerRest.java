package net.samagames.core.rest;

import net.samagames.api.achievements.Achievement;
import net.samagames.api.achievements.AchievementCategory;
import net.samagames.api.achievements.IAchievementManager;
import net.samagames.api.achievements.IncrementationAchievement;
import net.samagames.core.ApiImplementation;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * Created by Thog
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
public class AchievementManagerRest implements IAchievementManager
{
    public AchievementManagerRest(ApiImplementation apiImplementation)
    {

    }

    @Override
    public void reloadList()
    {

    }

    @Override
    public void incrementAchievement(Player player, IncrementationAchievement incrementationAchievement)
    {

    }

    @Override
    public Achievement getAchievementByName(String s)
    {
        return null;
    }

    @Override
    public AchievementCategory getAchievementCategoryByName(String s)
    {
        return null;
    }

    @Override
    public ArrayList<Achievement> getAchievements()
    {
        return null;
    }

    @Override
    public ArrayList<AchievementCategory> getAchievementsCategories()
    {
        return null;
    }

    @Override
    public boolean isUnlocked(Player player, Achievement achievement)
    {
        return false;
    }

    @Override
    public boolean isUnlocked(Player player, String s)
    {
        return false;
    }
}
