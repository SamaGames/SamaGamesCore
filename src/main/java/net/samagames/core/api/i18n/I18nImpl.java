package net.samagames.core.api.i18n;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.samagames.api.i18n.I18n;
import net.samagames.api.i18n.ProjectNames;
import net.samagames.core.ApiImplementation;
import net.samagames.persistanceapi.beans.internationalization.SentenceBean;
import net.samagames.persistanceapi.beans.players.PlayerBean;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class I18nImpl implements I18n
{
    private final ApiImplementation api;
    private final Table<Integer, Integer, String> sentences;
    private final Map<UUID, Integer> playerLanguagesCache;

    public I18nImpl(ApiImplementation api)
    {
        this.api = api;
        this.sentences = HashBasedTable.create();
        this.playerLanguagesCache = new HashMap<>();
    }

    public void setLanguagesToLoad(ProjectNames project, boolean value)
    {
        try
        {
            for (SentenceBean sentenceBean : this.api.getGameServiceManager().getGameSentences(project.intValue()))
                this.sentences.put(sentenceBean.getSentenceId(), sentenceBean.getLanguageId(), sentenceBean.getSentenceText());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String localize(int sentenceId, UUID player, Object... params) throws Exception
    {
        if (!this.sentences.containsRow(sentenceId))
            throw new Exception("Tryied to localize an unknown sentence!");

        int playerLanguageId = this.playerLanguagesCache.containsKey(player) ? this.playerLanguagesCache.get(player) : 1;

        return String.format(this.sentences.get(sentenceId, playerLanguageId), params);
    }

    public void loadPlayer(UUID player)
    {
        try
        {
            if (this.playerLanguagesCache.containsKey(player))
                this.unloadPlayer(player);

            this.playerLanguagesCache.put(player, this.api.getGameServiceManager().getPlayer(player, new PlayerBean()).getLanguageId());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void unloadPlayer(UUID player)
    {
        this.playerLanguagesCache.remove(player);
    }
}
