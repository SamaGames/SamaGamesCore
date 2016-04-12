package net.samagames.core.api.parties;

import net.samagames.api.parties.IParty;

import java.util.List;
import java.util.UUID;

/**
 * Created by Silvanosky on 10/04/2016.
 */
public class Party implements IParty {

    private UUID party;
    private UUID leader;

    private List<UUID> players;

    public Party(UUID party, UUID leader, List<UUID> players)
    {
        this.party = party;
        this.leader = leader;
        this.players = players;
    }

    @Override
    public UUID getParty()
    {
        return party;
    }

    @Override
    public UUID getLeader() {
        return leader;
    }

    @Override
    public void setLeader(UUID leader) {
        this.leader = leader;
    }

    @Override
    public boolean containsPlayer(UUID player)
    {
        return players.contains(player);
    }

    @Override
    public List<UUID> getPlayers() {
        return players;
    }

}
