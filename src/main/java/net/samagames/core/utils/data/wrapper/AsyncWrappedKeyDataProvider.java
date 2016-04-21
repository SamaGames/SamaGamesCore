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


import net.samagames.core.utils.data.AsyncDataProvider;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AsyncWrappedKeyDataProvider<K, D> extends WrappedKeyDataProvider<K, D> implements IWrappedKeyDataProvider<K>, AsyncDataProvider<D> {

	public AsyncWrappedKeyDataProvider(Class<? extends D> dataClass) {
		super(dataClass);
	}

	public AsyncWrappedKeyDataProvider(Class<? extends D> dataClass, AsyncDataProvider<D> dataProvider) {
		super(dataClass, dataProvider);
	}

	@Override
	public AsyncDataProvider<D> getDataProvider() {
		return (AsyncDataProvider<D>) super.getDataProvider();
	}

	public void setDataProvider(AsyncDataProvider<D> dataProvider) {
		super.setDataProvider(dataProvider);
	}

	
	public String keyToString( K key) {
		return key.toString();
	}

	
	public abstract K stringToKey( String string);

	public void get( K key,  Callback<D> callback) {
		get(keyToString(key), callback);
	}

	@Override
	public void get( String key,  Callback<D> callback) {
		getDataProvider().get(key, callback);
	}

	public void get( K key,  D def,  Callback<D> callback) {
		get(keyToString(key), def, callback);
	}

	@Override
	public void get( String key,  D def,  Callback<D> callback) {
		getDataProvider().get(key, def, callback);
	}

	public void getAllK( Set<K> keys,  final Callback<Map<K, D>> callback) {
		Set<String> stringKeys = new HashSet<>();
		for (K key : keys) {
			stringKeys.add(keyToString(key));
		}
		getAll(stringKeys, new Callback<Map<String, D>>() {
			@Override
			public void call(Map<String, D> stringMap) {
				Map<K, D> parsedMap = new HashMap<>();
				for (Map.Entry<String, D> stringEntry : stringMap.entrySet()) {
					parsedMap.put(stringToKey(stringEntry.getKey()), stringEntry.getValue());
				}
				callback.call(parsedMap);
			}
		});
	}

	@Override
	public void getAll( Set<String> keys,  Callback<Map<String, D>> callback) {
		getDataProvider().getAll(keys, callback);
	}

	public void put( K key, D value, Callback<Void> callback) {
		put(keyToString(key), value, callback);
	}

	@Override
	public void put( String key, D value, Callback<Void> callback) {
		getDataProvider().put(key, value, callback);
	}

	public void putAllK( Map<K, D> values, Callback<Void> callback) {
		Map<String, D> stringMap = new HashMap<>();
		for (Map.Entry<K, D> entry : values.entrySet()) {
			stringMap.put(keyToString(entry.getKey()), entry.getValue());
		}
		putAll(stringMap, callback);
	}

	@Override
	public void putAll( Map<String, D> values, Callback<Void> callback) {
		getDataProvider().putAll(values, callback);
	}

	public void remove( K key, Callback<Void> callback) {
		remove(keyToString(key), callback);
	}

	@Override
	public void remove( String key, Callback<Void> callback) {
		getDataProvider().remove(key, callback);
	}

	public void contains( K key,  Callback<Boolean> callback) {
		contains(keyToString(key), callback);
	}

	@Override
	public void contains( String key,  Callback<Boolean> callback) {
		getDataProvider().contains(key, callback);
	}

	public void keysK( final Callback<Set<K>> callback) {
		keys(new Callback<Set<String>>() {
			@Override
			public void call(Set<String> value) {
				Set<K> parsedKeys = new HashSet<>();
				for (String s : value) {
					parsedKeys.add(stringToKey(s));
				}
				callback.call(parsedKeys);
			}
		});
	}

	@Override
	public void keys( Callback<Set<String>> callback) {
		getDataProvider().keys(callback);
	}

	public void entriesK( final Callback<Set<Map.Entry<K, D>>> callback) {
		entries(new Callback<Set<Map.Entry<String, D>>>() {
			@Override
			public void call(Set<Map.Entry<String, D>> value) {
				Map<K, D> parsedMap = new HashMap<>();
				for (Map.Entry<String, D> stringEntry : value) {
					parsedMap.put(stringToKey(stringEntry.getKey()), stringEntry.getValue());
				}
				callback.call(parsedMap.entrySet());
			}
		});
	}

	@Override
	public void entries( Callback<Set<Map.Entry<String, D>>> callback) {
		getDataProvider().entries(callback);
	}

}
