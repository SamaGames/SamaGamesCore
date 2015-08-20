package net.samagames.core.api.permissions;

import net.samagames.core.APIPlugin;
import net.samagames.permissionsapi.rawtypes.RawPlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This file is a part of the SamaGames project
 * This code is absolutely confidential.
 * (C) Copyright Elydra Network 2015
 * All rights reserved.
 */
class VirtualPlayer implements RawPlayer
{

    private final Player player;
    private final UUID id;
    private final PermissionAttachment attachment;

    public VirtualPlayer(Player p)
    {
        this.player = p;
        this.id = p.getUniqueId();
        this.attachment = player.addAttachment(APIPlugin.getInstance());
    }

    @Override
    public void setPermission(String permission, boolean value)
    {
        if (player != null)
        {
            attachment.setPermission(permission, value);
        }
    }

    @Override
    public UUID getUniqueId()
    {
        return id;
    }

    @Override
    public void clearPermissions()
    {
        ArrayList<String> perms = new ArrayList<>();
        perms.addAll(attachment.getPermissions().keySet().stream().collect(Collectors.toList()));
        perms.forEach(attachment::unsetPermission);
    }
}
