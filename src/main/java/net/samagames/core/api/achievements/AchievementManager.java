package net.samagames.core.api.achievements;

import com.google.common.base.Preconditions;
import com.sun.javafx.UnmodifiableArrayList;
import net.samagames.api.achievements.*;
import net.samagames.core.ApiImplementation;
import net.samagames.core.api.player.PlayerData;
import net.samagames.persistanceapi.beans.achievements.AchievementBean;
import net.samagames.persistanceapi.beans.achievements.AchievementCategoryBean;
import net.samagames.persistanceapi.beans.achievements.AchievementProgressBean;
import net.samagames.tools.PersistanceUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class AchievementManager implements IAchievementManager
{
    private Achievement[] achievementsCache;
    private AchievementCategory[] achievementCategoriesCache;
    private ApiImplementation api;

    public AchievementManager(ApiImplementation api)
    {
        this.api = api;
        this.achievementsCache = new Achievement[0];
        this.achievementCategoriesCache = new AchievementCategory[0];

        api.getPlugin().getExecutor().scheduleAtFixedRate(() ->
        {
            try
            {
                List<AchievementCategoryBean> categoryBeanList = api.getGameServiceManager().getAchievementCategories();
                List<AchievementCategory> categories = new ArrayList<>();

                categoryBeanList.forEach(achievementCategoryBean -> categories.add(new AchievementCategory(achievementCategoryBean.getCategoryId(), achievementCategoryBean.getCategoryName(), PersistanceUtils.makeStack(this.api.getPlugin(), achievementCategoryBean.getItemMinecraftId(), achievementCategoryBean.getCategoryName(), achievementCategoryBean.getCategoryDescription()), achievementCategoryBean.getCategoryDescription().split("/n"), achievementCategoryBean.getParentId() < categories.size() && achievementCategoryBean.getParentId() >= 0 ? categories.get(achievementCategoryBean.getParentId()) : null)));

                List<AchievementBean> allAchievements = api.getGameServiceManager().getAchievements();
                int n = allAchievements.size();
                int n2 = categoryBeanList.size();

                Achievement[] achievementsCache = new Achievement[n == 0 ? 0 : Math.max(n, allAchievements.get(n - 1).getAchievementId())];

                for (AchievementBean bean : allAchievements)
                {
                    AchievementCategory category = categories.stream().filter(achievementCategory -> achievementCategory.getID() == bean.getCategoryId()).findFirst().orElse(null);

                    if (bean.getProgressTarget() == 1)
                        achievementsCache[bean.getAchievementId()] = new Achievement(bean.getAchievementId(), bean.getAchievementName(), category, bean.getAchievementDescription().split("/n"));
                    else
                        achievementsCache[bean.getAchievementId()] = new IncrementationAchievement(bean.getAchievementId(), bean.getAchievementName(), category, bean.getAchievementDescription().split("/n"), bean.getProgressTarget());
                }

                AchievementCategory[] achievementCategoriesCache = new AchievementCategory[n2 == 0 ? 0 : Math.max(n2, categories.get(n2 - 1).getID())];
                categories.forEach(achievementCategory -> achievementCategoriesCache[achievementCategory.getID()] = achievementCategory);

                this.achievementsCache = achievementsCache;//Avoid concurrent errors using temporary arrays
                this.achievementCategoriesCache = achievementCategoriesCache;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }, 0, 5, TimeUnit.MINUTES);
    }

    public void loadPlayer(UUID uuid)
    {
        try
        {
            PlayerData playerData = this.api.getPlayerManager().getPlayerData(uuid);
            List<AchievementProgressBean> list = this.api.getGameServiceManager().getAchievementProgresses(playerData.getPlayerBean());
            list.forEach(bean ->
            {
                Achievement achievement = this.getAchievementByID(bean.getAchievementId());
                if (achievement != null)
                    achievement.addProgress(uuid, bean.getProgressId(), bean.getProgress(), bean.getStartDate(), bean.getUnlockDate());
            });
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }

    public void unloadPlayer(UUID player)
    {
        for (Achievement achievement : this.achievementsCache)
        {
            AchievementProgress progress = achievement.getProgress(player);

            if (progress == null)
                continue;

            AchievementProgressBean bean = new AchievementProgressBean(progress.getProgressId(), achievement.getID(), progress.getProgress(), progress.getStartTime(), progress.getUnlockTime(), player);

            try
            {
                if (progress.getProgressId() == -1)
                    this.api.getGameServiceManager().createAchievementProgress(this.api.getPlayerManager().getPlayerData(player).getPlayerBean(), bean.getAchievementId());
                else
                    this.api.getGameServiceManager().updateAchievementProgress(bean);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void incrementAchievement(UUID uuid, IncrementationAchievement incrementationAchievement, int amount)
    {
        incrementationAchievement.increment(uuid, amount);
    }

    @Override
    public void incrementAchievement(UUID uuid, int id, int amount)
    {
        Achievement achievement = this.getAchievementByID(id);
        Preconditions.checkNotNull(achievement, "Achievement with id " + id + " not found");
        if (achievement instanceof IncrementationAchievement)
            ((IncrementationAchievement)achievement).increment(uuid, amount);
        else
            throw new IllegalArgumentException("Achievement is not incrementable");
    }

    @Override
    public void incrementAchievements(UUID uuid, int[] ids, int amount)
    {
        for (int id : ids)
        {
            Achievement achievement = this.getAchievementByID(id);
            if (achievement != null && achievement instanceof IncrementationAchievement)
                ((IncrementationAchievement)achievement).increment(uuid, amount);
        }
    }

    @Override
    public Achievement getAchievementByID(int id)
    {
        for (Achievement achievement : this.achievementsCache)
            if (achievement.getID() == id)
                return achievement;

        return null;
    }

    @Override
    public AchievementCategory getAchievementCategoryByID(int id)
    {
        for (AchievementCategory achievementCategory : this.achievementCategoriesCache)
            if (achievementCategory.getID() == id)
                return achievementCategory;

        return null;
    }

    @Override
    public List<Achievement> getAchievements()
    {
        return Arrays.asList(this.achievementsCache);
    }

    @Override
    public List<AchievementCategory> getAchievementsCategories()
    {
        return Arrays.asList(this.achievementCategoriesCache);
    }

    @Override
    public boolean isUnlocked(UUID uuid, Achievement achievement)
    {
        return achievement.isUnlocked(uuid);
    }

    @Override
    public boolean isUnlocked(UUID uuid, int id)
    {
        Achievement achievement = this.getAchievementByID(id);
        Preconditions.checkNotNull(achievement, "Achievement with id " + id + " not found");
        return achievement.isUnlocked(uuid);
    }
}