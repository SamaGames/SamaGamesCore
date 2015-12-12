package net.samagames.core.api.achievements;

import net.samagames.api.achievements.Achievement;
import net.samagames.api.achievements.AchievementCategory;
import net.samagames.api.achievements.IAchievementManager;
import net.samagames.api.achievements.IncrementationAchievement;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class AchievementManager implements IAchievementManager
{
    @Override
    public void reloadList()
    {

    }

    @Override
    public void incrementAchievement(Player player, IncrementationAchievement achievement)
    {

    }

    @Override
    public Achievement getAchievementByID(String id)
    {
        return null;
    }

    @Override
    public AchievementCategory getAchievementCategoryByID(String id)
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
    public boolean isUnlocked(Player player, String achievement)
    {
        return false;
    }
}
