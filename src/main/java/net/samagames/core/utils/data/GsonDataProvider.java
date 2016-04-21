/*
 * Copyright 2016 inventivetalent. All rights reserved.
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

package net.samagames.core.utils.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.samagames.core.utils.data.persistent.PersistentDataProvider;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GsonDataProvider<D> extends SerializationDataProvider<D> implements PersistentDataProvider<D> {

	protected final Class<? extends D> dataClass;
	protected final JsonObject         jsonObject;

	public GsonDataProvider(Class<? extends D> dataClass, JsonObject jsonObject) {
		super(dataClass);
		this.dataClass = dataClass;
		this.jsonObject = jsonObject;
	}

	@Override
	public D get(String key) {
		return getParser().parse(jsonObject.get(key).getAsString());
	}

	@Override
	public D get(String key, D def) {
		if (jsonObject.has(key)) {
			return getParser().parse(jsonObject.get(key).getAsString());
		}
		return def;
	}

	@Override
	public Map<String, D> getAll( Set<String> keys) {
		Map<String, D> map = new HashMap<>();
		for (String key : keys) {
			map.put(key, getParser().parse(jsonObject.get(key).getAsString()));
		}
		return map;
	}

	@Override
	public void put( String key, D value) {
		jsonObject.addProperty(key, getSerializer().serialize(value));
	}

	@Override
	public void putAll( Map<String, D> values) {
		for (Map.Entry<String, D> entry : values.entrySet()) {
			jsonObject.addProperty(entry.getKey(), getSerializer().serialize(entry.getValue()));
		}
	}

	@Override
	public void remove(String key) {
		jsonObject.remove(key);
	}

	@Override
	public boolean contains(String key) {
		return jsonObject.has(key);
	}

	@Override
	public Set<String> keys() {
		Set<String> keys = new HashSet<>();
		for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			keys.add(entry.getKey());
		}
		return keys;
	}

	@Override
	public Set<Map.Entry<String, D>> entries() {
		Map<String, D> entries = new HashMap<>();
		for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			entries.put(entry.getKey(), getParser().parse(entry.getValue().getAsString()));
		}
		return entries.entrySet();
	}
}
