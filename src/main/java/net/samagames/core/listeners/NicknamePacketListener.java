package net.samagames.core.listeners;

import net.samagames.tools.TinyProtocol;
import org.bukkit.plugin.Plugin;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Geekpower14 on 13/04/2015.
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class NicknamePacketListener extends TinyProtocol
{


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
    }

    /*@Override
    public Object onPacketOutAsync(Player reciever, Channel channel, Object packet) {

        if(packet instanceof PacketPlayOutPlayerInfo)
        {
            PacketPlayOutPlayerInfo p = (PacketPlayOutPlayerInfo)packet;

            PacketPlayOutPlayerInfo newPacket = new PacketPlayOutPlayerInfo();

            try {
                Field a = p.getClass().getDeclaredField("a");
                a.setAccessible(true);
                if(!a.get(p).equals(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER))
                {
                    return super.onPacketOutAsync(reciever, channel, packet);
                }
               // a.set(newPacket, a.get(p));

                Field b = p.getClass().getDeclaredField("b");
                b.setAccessible(true);

                List<PacketPlayOutPlayerInfo.PlayerInfoData> list = (List<PacketPlayOutPlayerInfo.PlayerInfoData>) b.get(p);
                for(PacketPlayOutPlayerInfo.PlayerInfoData data : list)
                {
                    if(reciever.getUniqueId().equals(UUID.fromString("ad345a5e-5ae3-45bf-aba4-94f4102f37c0")))
                    {
                        continue;
                    }

                    GameProfile profile = data.a();
                    if(profile.getId().equals(UUID.fromString("ad345a5e-5ae3-45bf-aba4-94f4102f37c0")))
                    {
                        //list.remove(data);
                        Field gameProfile = PacketPlayOutPlayerInfo.PlayerInfoData.class.getDeclaredField("d");
                        gameProfile.setAccessible(true);
                        Reflection.setFinalStatic(gameProfile,data, new GameProfile(UUID.fromString("c59220b1-662f-4aa8-b9d9-72660eb97c10"), "Aure_wesh"));
                        list.add(data);
                    }
                }
                Reflection.setFinalStatic(b, p, list);

                packet = p;

                //Todo: check si joueur caché

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
                    return super.onPacketOutAsync(reciever, channel, packet);
                }
                if(reciever.getUniqueId().equals(UUID.fromString("ad345a5e-5ae3-45bf-aba4-94f4102f37c0")))
                {
                    return super.onPacketOutAsync(reciever, channel, packet);
                }

                uuid.set(p, UUID.fromString("c59220b1-662f-4aa8-b9d9-72660eb97c10"));

                uuid.setAccessible(false);

                Field dataWatcher = p.getClass().getDeclaredField("i");
                dataWatcher.setAccessible(true);
                DataWatcher dWtacher = (DataWatcher) dataWatcher.get(p);

                dWtacher.a(4, "");
                dataWatcher.set(p, dWtacher);
                dataWatcher.setAccessible(false);


            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            packet = p;
        }

        return super.onPacketOutAsync(reciever, channel, packet);
    }*/


}
