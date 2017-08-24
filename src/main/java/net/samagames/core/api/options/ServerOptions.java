package net.samagames.core.api.options;

import net.samagames.api.options.IServerOptions;

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
public class ServerOptions implements IServerOptions {

    private boolean displayRanks;
    private boolean activeNature;

    public ServerOptions()
    {
        displayRanks = true;
        activeNature = true;
    }

    @Override
    public boolean hasRankTabColor() {
        return displayRanks;
    }

    @Override
    public void setRankTabColorEnable(boolean enable)
    {
        displayRanks = enable;
    }

    @Override
    public boolean hasActiveNature()
    {
        return activeNature;
    }

    @Override
    public void setActiveNature(boolean enable)
    {
        activeNature = enable;
    }
}
