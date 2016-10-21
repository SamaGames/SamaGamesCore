package net.samagames.core.api.games;

import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.IPearlManager;
import net.samagames.core.ApiImplementation;
import net.samagames.tools.chat.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;

import java.util.Random;

/**
 *                )\._.,--....,'``.
 * .b--.        /;   _.. \   _\  (`._ ,.
 * `=,-,-'~~~   `----(,_..'--(,_..'`-.;.'
 *
 * Created by Jérémy L. (BlueSlime) on 21/10/2016
 */
class PearlManager implements IPearlManager
{
    private enum RankChances
    {
        NORMAL(20, 25, 20, 20),
        VIP(25, 25, 25, 15),
        VIPPLUS(25, 25, 20, 15);

        private final int oneStarPercentage;
        private final int twoStarsPercentage;
        private final int threeStarsPercentage;
        private final int fourStarsPercentage;

        RankChances(int oneStarPercentage, int twoStarsPercentage, int threeStarsPercentage, int fourStarsPercentage)
        {
            this.oneStarPercentage = oneStarPercentage;
            this.twoStarsPercentage = twoStarsPercentage;
            this.threeStarsPercentage = threeStarsPercentage;
            this.fourStarsPercentage = fourStarsPercentage;
        }

        /**
         * Return a randomized stars count calculated with
         * the percentages.
         *
         * Note: We don't need a 5 stars percentage because of
         * the total of the percentage have to be equals to 100
         * (obvious).
         *
         * @return A randomized stars count
         */
        public int getRandomizedStars()
        {
            int random = new Random().nextInt(100);

            if (random <= this.oneStarPercentage)
                return 1;
            else if (random <= this.oneStarPercentage + this.twoStarsPercentage)
                return 2;
            else if (random <= this.oneStarPercentage + this.twoStarsPercentage + this.threeStarsPercentage)
                return 3;
            else if (random <= this.oneStarPercentage + this.twoStarsPercentage + this.threeStarsPercentage + this.fourStarsPercentage)
                return 4;
            else
                return 5;
        }

        public static RankChances getByRankId(int rankId)
        {
            if (rankId == 1)
                return NORMAL;
            else if (rankId == 2)
                return VIP;
            else
                return VIPPLUS;
        }
    }

    private final ApiImplementation api;

    PearlManager(ApiImplementation api)
    {
        this.api = api;
    }

    @Override
    public void runGiveAlgorythm(Player player, int gameTime, boolean win)
    {
        int playerRankId = (int) this.api.getPlayerManager().getPlayerData(player.getUniqueId()).getPlayerBean().getGroupId();

        int rankMultiplier = 0;

        if (playerRankId > 1)
            rankMultiplier = Integer.parseInt("1." + (playerRankId < 6 ? 5 : playerRankId - 1));

        if (gameTime < 10)
            gameTime = 10;

        int pearlChance = (int) ((gameTime / 2) * rankMultiplier * (win ? 1.2 : 0.8));
        int random = new Random().nextInt(100);

        if (random <= pearlChance)
        {
            int stars = RankChances.getByRankId(playerRankId).getRandomizedStars();

            Jedis jedis = this.api.getBungeeResource();
            jedis.lpush("pearls:" + player.getUniqueId().toString(), String.valueOf(stars));
            jedis.close();

            player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
            player.sendMessage(ChatUtils.getCenteredText(ChatColor.GREEN + "Vous avez trouvé une perle de niveau " + stars));
            player.sendMessage(ChatUtils.getCenteredText(ChatColor.GREEN + "Echangez-la auprès de Graou dans le Hub !"));
            player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        }
    }
}
