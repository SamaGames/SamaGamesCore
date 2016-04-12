/*
 * Copyright 2015-2016 inventivetalent. All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification, are
 *  permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this list of
 *        conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright notice, this list
 *        of conditions and the following disclaimer in the documentation and/or other materials
 *        provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 *  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 *  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 *  CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *  The views and conclusions contained in the software and documentation are those of the
 *  authors and contributors and should not be interpreted as representing official policies,
 *  either expressed or implied, of anybody else.
 */

package net.samagames.core.utils.auth.properties;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.samagames.core.utils.Wrapper;
import net.samagames.core.utils.reflection.resolver.MethodResolver;
import net.samagames.core.utils.reflection.resolver.ResolverQuery;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class PropertyMapWrapper extends Wrapper {

	static Class<?> Multimap           = CLASS_RESOLVER.resolveSilent("net.minecraft.util.com.google.common.collect.Multimap", "com.google.common.collect.Multimap");
	static Class<?> ForwardingMutlimap = CLASS_RESOLVER.resolveSilent("net.minecraft.util.com.google.common.collect.ForwardingMultimap", "com.google.common.collect.ForwardingMultimap");

	static MethodResolver MultimapMethodResolver           = new MethodResolver(Multimap);
	static MethodResolver ForwardingMultimapMethodResolver = new MethodResolver(ForwardingMutlimap);

	public PropertyMapWrapper() {
		super(Type.GENERAL, "net.minecraft.util.com.mojang.authlib.properties.PropertyMap", "com.mojang.authlib.properties.PropertyMap");
	}

	public PropertyMapWrapper(Object handle) {
		super(handle);
	}

	public PropertyMapWrapper(JsonArray jsonArray) {
		this();
		for (Iterator<JsonElement> iterator = jsonArray.iterator(); iterator.hasNext(); ) {
			JsonElement next = iterator.next();
			if (next instanceof JsonObject) {
				JsonObject jsonObject = next.getAsJsonObject();
				put(jsonObject.get("name").getAsString(), new PropertyWrapper(jsonObject.get("name").getAsString(), jsonObject.get("value").getAsString(), jsonObject.get("signature").getAsString()));
			}
		}
	}

	@Deprecated
	public PropertyMapWrapper(JSONArray jsonArray) {
		this();
		for (Iterator iterator = jsonArray.iterator(); iterator.hasNext(); ) {
			Object next = iterator.next();
			if (next instanceof JSONObject) {
				JSONObject jsonObject = (JSONObject) next;
				put((String) jsonObject.get("name"), new PropertyWrapper((String) jsonObject.get("name"), (String) jsonObject.get("value"), (String) jsonObject.get("signature")));
			}
		}
	}

	public void putAll(PropertyMapWrapper wrapper) {
		ForwardingMultimapMethodResolver.resolveWrapper(new ResolverQuery("putAll", Multimap)).invoke(getHandle(), wrapper.getHandle());
	}

	public void put(String key, PropertyWrapper wrapper) {
		ForwardingMultimapMethodResolver.resolveWrapper("put").invoke(getHandle(), key, wrapper.getHandle());
	}

	public Collection valuesHandle() {
		return (Collection) MultimapMethodResolver.resolveWrapper("values").invoke(getHandle());
	}

	public Collection<PropertyWrapper> values() {
		List<PropertyWrapper> wrappers = new ArrayList<>();
		for (Object handle : valuesHandle()) {
			wrappers.add(new PropertyWrapper(handle));
		}
		return wrappers;
	}

	public void clear() {
		MultimapMethodResolver.resolveWrapper("clear").invoke(getHandle());
	}

	public JsonArray toJson() {
		JsonArray jsonArray = new JsonArray();
		for (PropertyWrapper wrapper : values()) {
			jsonArray.add(wrapper.toJson());
		}
		return jsonArray;
	}

}
