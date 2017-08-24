package net.samagames.core.api.games.themachine.messages;

import net.samagames.api.games.themachine.messages.ITemplateManager;
import net.samagames.api.games.themachine.messages.templates.*;

/*
 * This file is part of SamaGamesCore.
 *
 * SamaGamesCore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SamaGamesCore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SamaGamesCore.  If not, see <http://www.gnu.org/licenses/>.
 */
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
