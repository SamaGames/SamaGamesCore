package net.samagames.core.api.permissions;

import net.samagames.tools.Reflection;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

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
public class Permissible extends PermissibleBase
{
    private CommandSender sender;
    private PermissionEntity entity;
    private Map<String, PermissionAttachmentInfo> permissions;
    private org.bukkit.permissions.Permissible oldpermissible = new PermissibleBase(null);

    public Permissible(CommandSender sender, PermissionEntity entity)
    {
        super(sender);
        this.sender = sender;
        this.entity = entity;
        permissions = new LinkedHashMap<String, PermissionAttachmentInfo>()
        {
            @Override
            public PermissionAttachmentInfo put(String k, PermissionAttachmentInfo v)
            {
                PermissionAttachmentInfo existing = this.get(k);
                if (existing != null)
                {
                    return existing;
                }
                return super.put(k, v);
            }
        };

        try
        {
            Field permissionsField = PermissibleBase.class.getField("permissions");
            Reflection.setField(permissionsField, this, permissions);
        }
        catch (NoSuchFieldException e)
        {
            e.printStackTrace();
        }
    }

    public org.bukkit.permissions.Permissible getOldPermissible()
    {
        return oldpermissible;
    }

    public void setOldPermissible(org.bukkit.permissions.Permissible oldPermissible)
    {
        this.oldpermissible = oldPermissible;
    }

    @Override
    public boolean hasPermission(String permission)
    {
        return entity.hasPermission(permission);
    }

    @Override
    public boolean hasPermission(Permission permission)
    {
        return hasPermission(permission.getName());
    }

    @Override
    public void recalculatePermissions()
    {
        if (oldpermissible == null)
        {
            super.recalculatePermissions();
            return;
        }
        oldpermissible.recalculatePermissions();
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions()
    {
        if (oldpermissible == null)
        {
            return super.getEffectivePermissions();
        }
        return new LinkedHashSet<>(permissions.values());
    }

    @Override
    public boolean isOp()
    {
        if (oldpermissible == null)
        {
            return super.isOp();
        }
        return oldpermissible.isOp();
    }

    @Override
    public void setOp(boolean value)
    {
        if (oldpermissible == null)
        {
            super.setOp(value);
            return;
        }
        oldpermissible.setOp(value);
    }

    @Override
    public boolean isPermissionSet(String permission)
    {
        return true;
    }

    @Override
    public boolean isPermissionSet(Permission perm)
    {
        return true;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin)
    {
        if (oldpermissible == null)
        {
            return super.addAttachment(plugin);
        }
        return oldpermissible.addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int ticks)
    {
        if (oldpermissible == null)
        {
            return super.addAttachment(plugin, ticks);
        }
        return oldpermissible.addAttachment(plugin, ticks);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value)
    {
        if (oldpermissible == null)
        {
            return super.addAttachment(plugin, name, value);
        }
        return oldpermissible.addAttachment(plugin, name, value);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks)
    {
        if (oldpermissible == null)
        {
            return super.addAttachment(plugin, name, value, ticks);
        }
        return oldpermissible.addAttachment(plugin, name, value, ticks);
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment)
    {
        if (oldpermissible == null)
        {
            super.removeAttachment(attachment);
            return;
        }
        oldpermissible.removeAttachment(attachment);
    }

    @Override
    public synchronized void clearPermissions()
    {
        if (oldpermissible == null)
        {
            super.clearPermissions();
            return;
        }
        if (oldpermissible instanceof PermissibleBase)
        {
            PermissibleBase base = (PermissibleBase) oldpermissible;
            base.clearPermissions();
        }
    }
}
