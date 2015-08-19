package net.samagames.core.api.games.themachine.messages;

import net.samagames.api.games.themachine.messages.ITemplateManager;
import net.samagames.api.games.themachine.messages.templates.*;

public class TemplateManagerImpl implements ITemplateManager
{
    @Override
    public BasicMessageTemplate getBasicMessageTemplate()
    {
        return new BasicMessageTemplate();
    }

    @Override
    public WinMessageTemplate getWinMessageTemplate()
    {
        return new WinMessageTemplate();
    }

    @Override
    public PlayerWinTemplate getPlayerWinTemplate()
    {
        return new PlayerWinTemplate();
    }

    @Override
    public PlayerLeaderboardWinTemplate getPlayerLeaderboardWinTemplate()
    {
        return new PlayerLeaderboardWinTemplate();
    }

    @Override
    public EarningMessageTemplate getEarningMessageTemplate()
    {
        return new EarningMessageTemplate();
    }
}
