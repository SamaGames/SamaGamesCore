package net.samagames.core.api.achievements;

import com.google.common.base.Preconditions;
import com.sun.javafx.UnmodifiableArrayList;
import net.samagames.api.achievements.*;
import net.samagames.core.ApiImplementation;

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
            /* TODO Modify persistance
            try
            {
                List<AchievementCategoryBean> categoryBeanList = api.getGameServiceManager().getAllAchievementCategory();
                List<AchievementCategory> categories = new ArrayList<>();

                categoryBeanList.forEach(achievementCategoryBean -> categories.add(new AchievementCategory(achievementCategoryBean.getCategoryId(), achievementCategoryBean.getCategoryName(), PersistanceUtils.strToItem(achievementCategoryBean.getItemMinecraftId(), achievementCategoryBean.getCategoryName()), achievementCategoryBean.getCategoryDescription().split("/n"), categories.get(achievementCategoryBean.getParentId()))));

                List<AchievementBean> allItemDescription = api.getGameServiceManager().getAllAchievement();
                int n = allItemDescription.size();
                int n2 = categoryBeanList.size();

                Achievement[] achievementsCache = new Achievement[Math.max(n, allItemDescription.get(n - 1).getAchievementId())];
                for (AchievementBean bean : allItemDescription)
                {
                    AchievementCategory category = categories.stream().filter(achievementCategory -> achievementCategory.getID() == bean.getParentId()).findFirst().orElse(null);
                    if (bean.getProgressTarget() == 1)
                        achievementsCache[bean.getItemId()] = new Achievement(bean.getAchievementId(), bean.getAchievementName(), category, bean.getAchievementDescription().split("/n"));
                    else
                        achievementsCache[bean.getItemId()] = new IncrementationAchievement(bean.getAchievementId(), bean.getAchievementName(), category, bean.getAchievementDescription().split("/n"), bean.getProgressTarget());
                }

                AchievementCategory[] achievementCategoriesCache = new AchievementCategory[Math.max(n2, categories.get(n2 - 1).getID())];
                categories.forEach(achievementCategory -> achievementCategoriesCache[achievementCategory.getID()] = achievementCategory);

                this.achievementsCache = achievementsCache;//Avoid concurrent errors using temporary arrays
                this.achievementCategoriesCache = achievementCategoriesCache;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            //*/
        }, 0, 5, TimeUnit.MINUTES);
    }

    public void loadPlayer(UUID uuid)
    {
        /* TODO Modify persistance
        try
        {
            PlayerData playerData = this.api.getPlayerManager().getPlayerData(uuid);
            List<AchievementProgressBean> list = this.api.getGameServiceManager().getAchievementProgress(playerData.getPlayerBean());
            list.forEach(bean ->
            {
                Achievement achievement = this.getAchievementByID(bean.getAchievementId());
                if (achievement != null)
                    achievement.addProgress(uuid, bean.getProgressId(), bean.getProgress(), bean.getStartTime(), bean.getUnlockTime());
            });
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
        //*/
    }

    public void unloadPlayer(UUID player)
    {
        /* TODO Modify persistance
        for (Achievement achievement : this.achievementsCache)
        {
            AchievementProgress progress = achievement.getProgress(player);
            if (progress == null)
                continue ;
            AchievementProgressBean bean = new AchievementProgressBean(progress.getProgressId(), achievement.getID(), progress.getProgress(), progress.getStartTime(), progress.getUnlockTime(), player);
            if (progress.getProgressId() == -1)
                this.api.getGameServiceManager().createAchievementProgress(bean);
            else
                this.api.getGameServiceManager().updateAchievementProgress(bean);
        }*/
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
        return new UnmodifiableArrayList<>(this.achievementsCache, this.achievementsCache.length);
    }

    @Override
    public List<AchievementCategory> getAchievementsCategories()
    {
        return new UnmodifiableArrayList<>(this.achievementCategoriesCache, this.achievementCategoriesCache.length);
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
