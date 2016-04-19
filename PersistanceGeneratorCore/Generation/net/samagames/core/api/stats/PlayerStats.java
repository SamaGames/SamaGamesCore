package net.samagames.core.api.stats;

import java.lang.Override;
import java.util.UUID;
import net.samagames.api.stats.IPlayerStats;
import net.samagames.core.ApiImplementation;
import net.samagames.core.api.player.PlayerData;
import net.samagames.core.api.stats.games.DimensionStatistics;
import net.samagames.core.api.stats.games.HeroBattleStatistics;
import net.samagames.core.api.stats.games.JukeBoxStatistics;
import net.samagames.core.api.stats.games.QuakeStatistics;
import net.samagames.core.api.stats.games.UHCRunStatistics;
import net.samagames.core.api.stats.games.UppervoidStatistics;

public class PlayerStats implements IPlayerStats {
  private UUID playerUUID;

  private ApiImplementation api;

  private boolean[] statsToLoad;

  private DimensionStatistics dimensionstatistics;

  private HeroBattleStatistics herobattlestatistics;

  private JukeBoxStatistics jukeboxstatistics;

  private QuakeStatistics quakestatistics;

  private UHCRunStatistics uhcrunstatistics;

  private UppervoidStatistics uppervoidstatistics;

  public PlayerStats(ApiImplementation api, PlayerData player, boolean[] statsToLoad) {
    this.api = api;
    this.playerUUID = player.getPlayerID();
    this.statsToLoad = statsToLoad;
    boolean global = statsToLoad[0];
    if (global || statsToLoad[1]) 
        this.dimensionstatistics = new DimensionStatistics(player);
    if (global || statsToLoad[2]) 
        this.herobattlestatistics = new HeroBattleStatistics(player);
    if (global || statsToLoad[3]) 
        this.jukeboxstatistics = new JukeBoxStatistics(player);
    if (global || statsToLoad[4]) 
        this.quakestatistics = new QuakeStatistics(player);
    if (global || statsToLoad[5]) 
        this.uhcrunstatistics = new UHCRunStatistics(player);
    if (global || statsToLoad[6]) 
        this.uppervoidstatistics = new UppervoidStatistics(player);
  }

  public ApiImplementation getApi() {
    return api;
  }

  public UUID getPlayerUUID() {
    return playerUUID;
  }

  @Override
  public void updateStats() {
    if (dimensionstatistics != null)
           dimensionstatistics.update();
    if (herobattlestatistics != null)
           herobattlestatistics.update();
    if (jukeboxstatistics != null)
           jukeboxstatistics.update();
    if (quakestatistics != null)
           quakestatistics.update();
    if (uhcrunstatistics != null)
           uhcrunstatistics.update();
    if (uppervoidstatistics != null)
           uppervoidstatistics.update();
  }

  @Override
  public boolean refreshStats() {
    if (dimensionstatistics != null)
           dimensionstatistics.refresh();
    if (herobattlestatistics != null)
           herobattlestatistics.refresh();
    if (jukeboxstatistics != null)
           jukeboxstatistics.refresh();
    if (quakestatistics != null)
           quakestatistics.refresh();
    if (uhcrunstatistics != null)
           uhcrunstatistics.refresh();
    if (uppervoidstatistics != null)
           uppervoidstatistics.refresh();
    return true;
  }

  @Override
  public DimensionStatistics getDimensionStatistics() {
    return dimensionstatistics;
  }

  public void setDimensionStatistics(DimensionStatistics dimensionstatistics) {
    this.dimensionstatistics = dimensionstatistics;
  }

  @Override
  public HeroBattleStatistics getHeroBattleStatistics() {
    return herobattlestatistics;
  }

  public void setHeroBattleStatistics(HeroBattleStatistics herobattlestatistics) {
    this.herobattlestatistics = herobattlestatistics;
  }

  @Override
  public JukeBoxStatistics getJukeBoxStatistics() {
    return jukeboxstatistics;
  }

  public void setJukeBoxStatistics(JukeBoxStatistics jukeboxstatistics) {
    this.jukeboxstatistics = jukeboxstatistics;
  }

  @Override
  public QuakeStatistics getQuakeStatistics() {
    return quakestatistics;
  }

  public void setQuakeStatistics(QuakeStatistics quakestatistics) {
    this.quakestatistics = quakestatistics;
  }

  @Override
  public UHCRunStatistics getUHCRunStatistics() {
    return uhcrunstatistics;
  }

  public void setUHCRunStatistics(UHCRunStatistics uhcrunstatistics) {
    this.uhcrunstatistics = uhcrunstatistics;
  }

  @Override
  public UppervoidStatistics getUppervoidStatistics() {
    return uppervoidstatistics;
  }

  public void setUppervoidStatistics(UppervoidStatistics uppervoidstatistics) {
    this.uppervoidstatistics = uppervoidstatistics;
  }
}
