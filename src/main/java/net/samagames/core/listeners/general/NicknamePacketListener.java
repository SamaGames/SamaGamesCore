package net.samagames.core.listeners.general;

import com.mojang.authlib.GameProfile;
import io.netty.channel.Channel;
import net.minecraft.server.v1_9_R2.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_9_R2.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_9_R2.PacketPlayOutScoreboardTeam;
import net.samagames.core.APIPlugin;
import net.samagames.core.ApiImplementation;
import net.samagames.core.api.player.PlayerData;
import net.samagames.tools.Reflection;
import net.samagames.tools.TinyProtocol;
import net.samagames.tools.npc.nms.CustomNPC;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 13/04/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class NicknamePacketListener extends TinyProtocol
{

    private Random random;

    private ApiImplementation api;

    private Class playerInfoData;

    /**
     * Construct a new instance of TinyProtocol, and start intercepting packets for all connected clients and future clients.
     * <p>
     * You can construct multiple instances per plugin.
     *
     * @param plugin - the plugin.
     */
    public NicknamePacketListener(APIPlugin plugin)
    {
        super(plugin);
        this.api = plugin.getAPI();
        this.random = new Random();

        Class<?>[] declaredClasses = PacketPlayOutPlayerInfo.class.getDeclaredClasses();

        for (Class class_ : declaredClasses)
        {
            if (class_.getTypeName().contains("PlayerInfoData"))
            {
                playerInfoData = class_;
                break;
            }
        }
    }

    @Override
    public Object onPacketOutAsync(Player receiver, Channel channel, Object packet) {
        if(packet instanceof PacketPlayOutPlayerInfo)
        {
            PacketPlayOutPlayerInfo p = (PacketPlayOutPlayerInfo)packet;
            try {

                Field a = p.getClass().getDeclaredField("a");
                a.setAccessible(true);

                Field b = p.getClass().getDeclaredField("b");
                b.setAccessible(true);

                Method playerProfile = playerInfoData.getDeclaredMethod("a");

                //TODO when player disconnect, his playerdata are deleted before call the remove
                //So you need to add a list with all nickname and remove player when disconnect from here
                List list = (List) b.get(p);
                for(Object data : list)
                {
                    //PacketPlayOutPlayerInfo.PlayerInfoData data1 = (PacketPlayOutPlayerInfo.PlayerInfoData) data;
                   // GameProfile profile = data.a();
                    GameProfile profile = (GameProfile) playerProfile.invoke(data);

                    Entity entity = null;
                    for (World world : this.api.getPlugin().getServer().getWorlds())
                        for (Entity e : world.getEntities())
                            if (e.getUniqueId().equals(profile.getId()))
                                entity = e;
                    if (entity != null && ((CraftEntity)entity).getHandle() instanceof CustomNPC)
                        continue ;

                    PlayerData playerData = api.getPlayerManager().getPlayerData(profile.getId());
                    if (playerData != null && playerData.hasNickname() &&
                            !profile.getId().equals(receiver.getUniqueId()) &&
                            !profile.getName().equals(receiver.getName()))
                    {
                        GameProfile gameProfile = playerData.getFakeProfile();
                        Field field = playerProfile.getClass().getDeclaredField("d");
                        Reflection.setFinal(data, field, gameProfile);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if (packet instanceof PacketPlayOutNamedEntitySpawn)
        {
            PacketPlayOutNamedEntitySpawn p = (PacketPlayOutNamedEntitySpawn)packet;

            try {
                Field uuid = p.getClass().getDeclaredField("b");
                uuid.setAccessible(true);
                UUID id = (UUID) uuid.get(p);

                if (id.equals(receiver.getUniqueId()))
                {
                    return super.onPacketOutAsync(receiver, channel, packet);
                }

                Entity entity = null;
                for (World world : this.api.getPlugin().getServer().getWorlds())
                    for (Entity e : world.getEntities())
                        if (e.getUniqueId().equals(id))
                            entity = e;
                if (entity != null && ((CraftEntity)entity).getHandle() instanceof CustomNPC)
                    return super.onPacketOutAsync(receiver, channel, packet);

                PlayerData playerData = api.getPlayerManager().getPlayerData(id);
                if (playerData == null || !playerData.hasNickname())
                {
                    return super.onPacketOutAsync(receiver, channel, packet);
                }

                uuid.set(p, playerData.getFakeUUID());

                uuid.setAccessible(false);

               /* net.samagames.core.utils.reflection.minecraft.DataWatcher
                Field dataWatcher = p.getClass().getDeclaredField("i");
                dataWatcher.setAccessible(true);
                DataWatcher dWtacher = (DataWatcher) dataWatcher.get(p);

                dWtacher.set(DataWatcher.a(), "");
                dataWatcher.set(p, dWtacher);
                dataWatcher.setAccessible(false);*/

            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }else if (packet instanceof PacketPlayOutScoreboardTeam)
        {
            PacketPlayOutScoreboardTeam p = (PacketPlayOutScoreboardTeam) packet;
            try {

                Field h = p.getClass().getDeclaredField("h");
                h.setAccessible(true);
                List<String> players = (List<String>) h.get(p);
                List<String> newPlayers = new ArrayList<>();
                for (String member : players)
                {
                    String newMember = member;
                    PlayerData playerData = api.getPlayerManager().getPlayerDataByName(member);
                    if (playerData != null)
                    {
                        if (!playerData.getPlayerID().equals(receiver.getUniqueId()) && playerData.hasNickname())
                        {
                            newMember = playerData.getCustomName();
                        }
                    }
                    newPlayers.add(newMember);
                }

                h.set(p, newPlayers);
                h.setAccessible(false);

            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

            packet = p;
        }

        return super.onPacketOutAsync(receiver, channel, packet);
    }

}
