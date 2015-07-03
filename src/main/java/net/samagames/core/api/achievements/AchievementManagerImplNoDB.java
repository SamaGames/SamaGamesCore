package net.samagames.core.api.achievements;

import net.samagames.api.achievements.Achievement;
import net.samagames.api.achievements.AchievementCategory;
import net.samagames.api.achievements.AchievementManager;
import net.samagames.api.achievements.IncrementationAchievement;
import net.samagames.core.ApiImplementation;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class AchievementManagerImplNoDB implements AchievementManager
{
    private final ApiImplementation api;

    private ArrayList<Achievement> achievements;
    private ArrayList<AchievementCategory> achievementCategories;

    public AchievementManagerImplNoDB(ApiImplementation api)
    {
        this.api = api;

        this.achievements = new ArrayList<>();
        this.achievementCategories = new ArrayList<>();
    }

    @Override
    public void reloadList() {}

    @Override
    public void incrementAchievement(Player player, IncrementationAchievement achievement)
    {
        achievement.increment(player);
    }

    @Override
    public Achievement getAchievementByName(String name)
    {
        return null;
    }

    @Override
    public AchievementCategory getAchievementCategoryByName(String name)
    {
        return null;
    }

    @Override
    public ArrayList<Achievement> getAchievements()
    {
        return this.achievements;
    }

    @Override
    public ArrayList<AchievementCategory> getAchievementsCategories()
    {
        return this.achievementCategories;
    }

    public boolean isUnlocked(Player player, Achievement achievement)
    {
        return true;
    }

    @Override
    public boolean isUnlocked(Player player, String achievement)
    {
        if(this.getAchievementByName(achievement) == null)
            return false;

        return this.isUnlocked(player, this.getAchievementByName(achievement));
    }
}
