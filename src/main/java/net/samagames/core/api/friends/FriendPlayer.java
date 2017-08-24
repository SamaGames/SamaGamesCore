package net.samagames.core.api.friends;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
