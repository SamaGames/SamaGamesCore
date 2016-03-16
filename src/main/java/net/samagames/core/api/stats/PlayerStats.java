package net.samagames.core.api.stats;

import net.samagames.api.stats.IPlayerStats;
import net.samagames.api.stats.games.IJukeBoxStats;
import net.samagames.core.ApiImplementation;
import net.samagames.core.api.player.PlayerData;
import net.samagames.core.api.stats.games.*;

import java.util.UUID;

public class PlayerStats implements IPlayerStats
{
    private UUID playerUUID;

    private ApiImplementation api;

    private DimensionStats dimensionStats;
    private HeroBattleStats heroBattleStats;
    private JukeBoxStats jukeBoxStats;
    private QuakeStats quakeStats;
    private UHCRunStats uhcRunStats;
    private UppervoidStats uppervoidStats;

    public PlayerStats(ApiImplementation api, UUID player)
    {
        this.api = api;
        this.playerUUID = player;
    }

    @Override
    public void updateStats() {
        if(dimensionStats != null)
            dimensionStats.update();

        if(heroBattleStats != null)
            heroBattleStats.update();

        if(jukeBoxStats != null)
            jukeBoxStats.update();

        if(quakeStats != null)
            quakeStats.update();

        if(uhcRunStats != null)
            uhcRunStats.update();

        if(uppervoidStats != null)
            uppervoidStats.update();
    }

    @Override
    public boolean refreshStats() {
        updateStats();

        if(dimensionStats != null)
            dimensionStats.refresh();

        if(heroBattleStats != null)
            heroBattleStats.refresh();

        if(jukeBoxStats != null)
            jukeBoxStats.refresh();

        if(quakeStats != null)
            quakeStats.refresh();

        if(uhcRunStats != null)
            uhcRunStats.refresh();

        if(uppervoidStats != null)
            uppervoidStats.refresh();

        return true;
    }

    public UUID getPlayerUUID()
    {
        return this.playerUUID;
    }

    @Override
    public DimensionStats getDimensionStats() {
        return dimensionStats;
    }

    public void setDimensionStats(DimensionStats dimensionStats) {
        this.dimensionStats = dimensionStats;
    }

    @Override
    public HeroBattleStats getHeroBattleStats() {
        return heroBattleStats;
    }

    public void setHeroBattleStats(HeroBattleStats heroBattleStats) {
        this.heroBattleStats = heroBattleStats;
    }

    @Override
    public IJukeBoxStats getJukeBoxStats() {
        return jukeBoxStats;
    }

    public void setJukeBoxStats(JukeBoxStats jukeBoxStats)
    {
        this.jukeBoxStats = jukeBoxStats;
    }

    @Override
    public QuakeStats getQuakeStats() {
        return quakeStats;
    }

    public void setQuakeStats(QuakeStats quakeStats) {
        this.quakeStats = quakeStats;
    }

    @Override
    public UHCRunStats getUHCRunStats() {
        return uhcRunStats;
    }

    public void setUhcRunStats(UHCRunStats uhcRunStats) {
        this.uhcRunStats = uhcRunStats;
    }

    @Override
    public UppervoidStats getUppervoidStats() {
        return uppervoidStats;
    }

    public void setUppervoidStats(UppervoidStats uppervoidStats) {
        this.uppervoidStats = uppervoidStats;
    }

    public ApiImplementation getApi()
    {
        return api;
    }
}
