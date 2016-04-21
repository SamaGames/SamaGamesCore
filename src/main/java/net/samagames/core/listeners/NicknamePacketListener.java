package net.samagames.core.listeners;

import com.mojang.authlib.GameProfile;
import io.netty.channel.Channel;
import net.minecraft.server.v1_9_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_9_R1.PacketPlayOutPlayerInfo;
import net.samagames.tools.Reflection;
import net.samagames.tools.TinyProtocol;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
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

    /**
     * Construct a new instance of TinyProtocol, and start intercepting packets for all connected clients and future clients.
     * <p>
     * You can construct multiple instances per plugin.
     *
     * @param plugin - the plugin.
     */
    public NicknamePacketListener(Plugin plugin)
    {
        super(plugin);
        this.random = new Random();
    }

    @Override
    public Object onPacketOutAsync(Player receiver, Channel channel, Object packet) {

        if(packet instanceof PacketPlayOutPlayerInfo)
        {
            PacketPlayOutPlayerInfo p = (PacketPlayOutPlayerInfo)packet;

            PacketPlayOutPlayerInfo newPacket = new PacketPlayOutPlayerInfo();

            try {
                Field a = p.getClass().getDeclaredField("a");
                a.setAccessible(true);
                if(!a.get(p).equals(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER))
                {
                    return super.onPacketOutAsync(receiver, channel, packet);
                }
               // a.set(newPacket, a.get(p));

                Field b = p.getClass().getDeclaredField("b");
                b.setAccessible(true);

                List list = (List) b.get(p);
                if(!receiver.getUniqueId().equals(UUID.fromString("ad345a5e-5ae3-45bf-aba4-94f4102f37c0")))
                {
                    for(Object data : list)
                    {
                        PacketPlayOutPlayerInfo.PlayerInfoData data1 = (PacketPlayOutPlayerInfo.PlayerInfoData) data;
                        GameProfile profile = data1.a();
                        if(profile.getId().equals(UUID.fromString("ad345a5e-5ae3-45bf-aba4-94f4102f37c0")))
                        {
                            List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
                            players.remove(receiver);
                            GameProfile finalGameprofile;
                            if (players.size() > 0)
                            {
                                Player showed = players.get(random.nextInt(players.size()));
                                GameProfile victim = ((CraftPlayer)showed).getHandle().getProfile();

                                finalGameprofile = new GameProfile(victim.getId(), "Michel");
                                finalGameprofile.getProperties().putAll(victim.getProperties());

                            }else{
                                finalGameprofile = new GameProfile(UUID.randomUUID(), "Michel");
                            }

                            Field gameProfile = PacketPlayOutPlayerInfo.PlayerInfoData.class.getDeclaredField("d");

                            Reflection.setFinal(data, gameProfile, finalGameprofile);
                        }
                    }
                }

                packet = p;

            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if (packet instanceof PacketPlayOutNamedEntitySpawn)
        {
            PacketPlayOutNamedEntitySpawn p = (PacketPlayOutNamedEntitySpawn)packet;

            try {
                Field uuid = p.getClass().getDeclaredField("b");
                uuid.setAccessible(true);

                if(!uuid.get(p).equals(UUID.fromString("ad345a5e-5ae3-45bf-aba4-94f4102f37c0")))
                {
                    return super.onPacketOutAsync(receiver, channel, packet);
                }
                if(receiver.getUniqueId().equals(UUID.fromString("ad345a5e-5ae3-45bf-aba4-94f4102f37c0")))
                {
                    return super.onPacketOutAsync(receiver, channel, packet);
                }

                uuid.set(p, UUID.fromString("c59220b1-662f-4aa8-b9d9-72660eb97c10"));

                uuid.setAccessible(false);

               /* net.samagames.core.utils.reflection.minecraft.DataWatcher
                Field dataWatcher = p.getClass().getDeclaredField("i");
                dataWatcher.setAccessible(true);
                DataWatcher dWtacher = (DataWatcher) dataWatcher.get(p);

                dWtacher.set(DataWatcher.a(), "");
                dataWatcher.set(p, dWtacher);
                dataWatcher.setAccessible(false);*/


            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            packet = p;
        }

        return super.onPacketOutAsync(receiver, channel, packet);
    }


}
