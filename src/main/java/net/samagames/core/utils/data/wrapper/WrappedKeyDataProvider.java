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

package net.samagames.core.utils.data.wrapper;

import net.samagames.core.utils.data.DataProvider;
import net.samagames.core.utils.data.SerializationDataProvider;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class WrappedKeyDataProvider<K, D> extends SerializationDataProvider<D> implements DataProvider<D>, IWrappedKeyDataProvider<K> {

	private DataProvider<D> dataProvider;

	public WrappedKeyDataProvider(Class<? extends D> dataClass) {
		super(dataClass);
	}

	public WrappedKeyDataProvider(Class<? extends D> dataClass, DataProvider<D> dataProvider) {
		super(dataClass);
		this.dataProvider = dataProvider;
	}

	public WrappedKeyDataProvider(SerializationDataProvider<D> dataProvider) {
		super(dataProvider.getDataClass());
		this.dataProvider = dataProvider;
	}

	public DataProvider<D> getDataProvider() {
		if (dataProvider == null) { throw new IllegalStateException("DataProvider not yet set"); }
		return dataProvider;
	}

	public void setDataProvider(DataProvider<D> dataProvider) {
		this.dataProvider = dataProvider;
	}

	
	public String keyToString( K key) {
		return key.toString();
	}

	
	public abstract K stringToKey( String string);

	public D get( K key) {
		return get(keyToString(key));
	}

	@Override
	public D get( String key) {
		return getDataProvider().get(key);
	}

	public D get( K key,  D def) {
		return get(keyToString(key), def);
	}

	@Override
	public D get( String key,  D def) {
		return getDataProvider().get(key, def);
	}

	public Map<K, D> getAllK( Set<K> keys) {
		Set<String> stringKeys = new HashSet<>();
		for (K key : keys) {
			stringKeys.add(keyToString(key));
		}
		Map<String, D> stringMap = getAll(stringKeys);
		Map<K, D> parsedMap = new HashMap<>();
		for (Map.Entry<String, D> stringEntry : stringMap.entrySet()) {
			parsedMap.put(stringToKey(stringEntry.getKey()), stringEntry.getValue());
		}
		return parsedMap;
	}

	@Override
	public Map<String, D> getAll( Set<String> keys) {
		return getDataProvider().getAll(keys);
	}

	public void put( K key, D value) {
		put(keyToString(key), value);
	}

	@Override
	public void put( String key, D value) {
		getDataProvider().put(key, value);
	}

	public void putAllK( Map<K, D> values) {
		Map<String, D> stringMap = new HashMap<>();
		for (Map.Entry<K, D> entry : values.entrySet()) {
			stringMap.put(keyToString(entry.getKey()), entry.getValue());
		}
		putAll(stringMap);
	}

	@Override
	public void putAll( Map<String, D> values) {
		getDataProvider().putAll(values);
	}

	public void remove( K key) {
		remove(keyToString(key));
	}

	@Override
	public void remove( String key) {
		getDataProvider().remove(key);
	}

	public boolean contains( K key) {
		return contains(keyToString(key));
	}

	@Override
	public boolean contains( String key) {
		return getDataProvider().contains(key);
	}

	public Set<K> keysK() {
		Set<K> parsedKeys = new HashSet<>();
		for (String s : keys()) {
			parsedKeys.add(stringToKey(s));
		}
		return parsedKeys;
	}

	@Override
	public Set<String> keys() {
		return getDataProvider().keys();
	}

	public Set<Map.Entry<K, D>> entriesK() {
		Map<K, D> parsedMap = new HashMap<>();
		for (Map.Entry<String, D> stringEntry : entries()) {
			parsedMap.put(stringToKey(stringEntry.getKey()), stringEntry.getValue());
		}
		return parsedMap.entrySet();
	}

	@Override
	public Set<Map.Entry<String, D>> entries() {
		return getDataProvider().entries();
	}
}
