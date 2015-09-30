package net.samagames.core.hook;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.TileEntitySkull;
import net.samagames.api.SamaGamesAPI;
import net.samagames.tools.Reflection;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

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
            profile = new GameProfile(SamaGamesAPI.get().getUUIDTranslator().getUUID(user, false), user);
            String skinURL = "http://textures.minecraft.net/texture/cd6be915b261643fd13621ee4e99c9e541a551d80272687a3b56183b981fb9a";
            profile.getProperties().put("textures", new Property("textures", Base64Coder.encodeString("{textures:{SKIN:{url:\"" + skinURL + "\"}}}")));
        }

        return profile;
    }

    public static void hook()
    {
        try
        {
            Reflection.setFinalStatic(TileEntitySkull.class.getField("skinCache"), CacheBuilder.newBuilder().maximumSize(5000).expireAfterAccess(60, TimeUnit.MINUTES).build(new RestCacheLoader()));
        } catch (ReflectiveOperationException e)
        {
            e.printStackTrace();
        }
    }
}
