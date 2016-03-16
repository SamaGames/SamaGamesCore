package net.samagames.core.hook;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_9_R1.EntityPlayer;
import net.minecraft.server.v1_9_R1.MinecraftServer;
import net.minecraft.server.v1_9_R1.TileEntitySkull;
import net.samagames.api.SamaGamesAPI;
import net.samagames.restfull.RestAPI;
import net.samagames.restfull.request.Request;
import net.samagames.tools.Reflection;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * This file is a part of the SamaGames Project CodeBase
 * This code is absolutely confidential.
 * Created by Thog
 * (C) Copyright Elydra Network 2014 & 2015
 * All rights reserved.
 */
public class RestCacheLoader extends CacheLoader<String, GameProfile>
{
    @Override
    public GameProfile load(String user) throws Exception
    {
        EntityPlayer player = MinecraftServer.getServer().getPlayerList().getPlayer(user);
        if (player != null)
            return player.getProfile();

        GameProfile profile = MinecraftServer.getServer().getUserCache().getProfile(user);

        if (profile == null)
        {
            UUID uuid = SamaGamesAPI.get().getUUIDTranslator().getUUID(user, false);
            profile = new GameProfile(uuid == null ? UUID.randomUUID() : uuid, user);

            Object result = RestAPI.getInstance().sendRequest("player/skin", new Request().addProperty("playerName", user), SkinResponse.class, "POST");

            if (result instanceof SkinResponse && ((SkinResponse) result).skin != null && !((SkinResponse) result).skin.isEmpty())
            {
                profile.getProperties().put("textures", new Property("textures", Base64Coder.encodeString("{textures:{SKIN:{url:\"" + ((SkinResponse) result).skin + "\"}}}")));
            }
        }

        return profile;
    }

    public static void hook()
    {
        try
        {
            Reflection.setFinalStatic(TileEntitySkull.class.getField("skinCache"), CacheBuilder.newBuilder().maximumSize(5000).expireAfterAccess(4, TimeUnit.HOURS).build(new RestCacheLoader()));
        } catch (ReflectiveOperationException e)
        {
            e.printStackTrace();
        }
    }

    public static class SkinResponse
    {
        private String skin;
    }
}
