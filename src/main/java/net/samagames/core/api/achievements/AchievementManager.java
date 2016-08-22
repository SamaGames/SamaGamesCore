package net.samagames.core.api.achievements;

import com.google.common.base.Preconditions;
import com.sun.javafx.UnmodifiableArrayList;
import net.samagames.api.achievements.*;
import net.samagames.core.ApiImplementation;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

//TODO
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

    }

    @Override
    public void incrementAchievement(Player player, IncrementationAchievement achievement)
    {

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
