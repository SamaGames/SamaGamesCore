package net.samagames.core.api.options;

import net.samagames.api.options.ServerOptions;

/**
 * Created by Silva on 15/11/2015.
 */
public class IServerOptions implements ServerOptions {

    private boolean displayRanks;
    private boolean activeNature;

    public IServerOptions()
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
