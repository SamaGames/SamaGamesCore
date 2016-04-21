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

package net.samagames.core.utils.data.temporary;


import net.samagames.core.utils.data.SerializationDataProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Simple implementation of a temporary data-provider, built on a {@link HashMap}
 */
public class SimpleTemporaryDataProvider<D> extends SerializationDataProvider<D> implements TemporaryDataProvider<D> {

	private final Map<String, D> map;

	public SimpleTemporaryDataProvider(Class<? extends D> dataClass) {
		super(dataClass);
		map = new HashMap<>();
	}

	public SimpleTemporaryDataProvider(Class<? extends D> dataClass, Map<String, D> map) {
		super(dataClass);
		this.map = map;
	}

	@Override
	public D get(String key) {
		return map.get(key);
	}

	public D get(String key, D def) {
		if (contains(key)) {
			return map.get(key);
		}
		return def;
	}

	@Override
	public Map<String, D> getAll(Set<String> keys) {
		Map<String, D> map = new HashMap<>();
		for (String key : keys) {
			map.put(key, this.map.get(key));
		}
		return map;
	}

	@Override
	public void put(String key, D value) {
		map.put(key, value);
	}

	@Override
	public void putAll(Map<String, D> values) {
		map.putAll(values);
	}

	public void remove(String key) {
		map.remove(key);
	}

	public boolean contains(String key) {
		return map.containsKey(key);
	}

	@Override
	public Set<String> keys() {
		return map.keySet();
	}

	@Override
	public Set<Map.Entry<String, D>> entries() {
		return map.entrySet();
	}
}
