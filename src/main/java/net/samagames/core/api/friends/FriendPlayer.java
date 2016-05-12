package net.samagames.core.api.friends;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Silvanosky on 18/02/2016.
 */
public class FriendPlayer {

    private UUID uuid;

    private List<UUID> friends;

    public FriendPlayer(UUID uuid)
    {
        this.uuid = uuid;
        this.friends = new ArrayList<>();
    }

    public FriendPlayer(UUID uuid, UUID... players)
    {
        this(uuid);

        for (UUID friend : players)
        {
            friends.add(friend);
        }
    }

    public FriendPlayer(UUID uuid,List<UUID> players)
    {
        this(uuid);

        friends.addAll(players);
    }

    public void addFriend(UUID uuid)
    {
        friends.add(uuid);
    }

    public void removeFriend(UUID uuid)
    {
        friends.remove(uuid);
    }

    public boolean areFriend(UUID uuid)
    {
        return friends.contains(uuid);
    }

    public UUID getUuid() {
        return uuid;
    }

    public List<UUID> getFriends() {
        return friends;
    }
}
