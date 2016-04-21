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
import net.samagames.core.utils.data.DataProvider;
import net.samagames.core.utils.data.SerializationDataProvider;

import java.util.Map;
import java.util.Set;

public abstract class AsyncDataProviderWrapper<D> extends SerializationDataProvider<D> implements DataProvider<D>, AsyncDataProvider<D> {

	private DataProvider<D> dataProvider;

	public AsyncDataProviderWrapper(Class<? extends D> dataClass) {
		super(dataClass);
	}

	public AsyncDataProviderWrapper(Class<? extends D> dataClass, DataProvider<D> dataProvider) {
		super(dataClass);
		this.dataProvider = dataProvider;
	}

	public AsyncDataProviderWrapper(SerializationDataProvider<D> dataProvider) {
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

	public abstract void dispatch(Runnable runnable);

	public void get( final String key,  final AsyncDataProvider.Callback<D> callback) {
		dispatch(new Runnable() {
			@Override
			public void run() {
				callback.call(get(key));
			}
		});
	}

	@Override
	public D get( String key) {
		return getDataProvider().get(key);
	}

	@Override
	public void get( final String key,  final D def,  final Callback<D> callback) {
		dispatch(new Runnable() {
			@Override
			public void run() {
				D value = get(key);
				callback.call(value != null ? value : def);
			}
		});
	}

	@Override
	public D get( String key,  D def) {
		return getDataProvider().get(key, def);
	}

	@Override
	public void getAll( final Set<String> keys,  final Callback<Map<String, D>> callback) {
		dispatch(new Runnable() {
			@Override
			public void run() {
				callback.call(getAll(keys));
			}
		});
	}

	@Override
	public Map<String, D> getAll( Set<String> keys) {
		return getDataProvider().getAll(keys);
	}

	@Override
	public void put( final String key, final D value, final Callback<Void> callback) {
		dispatch(new Runnable() {
			@Override
			public void run() {
				getDataProvider().put(key, value);
				if (callback != null) { callback.call(null); }
			}
		});
	}

	@Override
	public void put( final String key, final D value) {
		dispatch(new Runnable() {
			@Override
			public void run() {
				getDataProvider().put(key, value);
			}
		});
	}

	@Override
	public void putAll( final Map<String, D> values, final Callback<Void> callback) {
		dispatch(new Runnable() {
			@Override
			public void run() {
				getDataProvider().putAll(values);
				if (callback != null) { callback.call(null); }
			}
		});
	}

	@Override
	public void putAll( final Map<String, D> values) {
		dispatch(new Runnable() {
			@Override
			public void run() {
				getDataProvider().putAll(values);
			}
		});
	}

	@Override
	public void remove( final String key, final Callback<Void> callback) {
		dispatch(new Runnable() {
			@Override
			public void run() {
				getDataProvider().remove(key);
				if (callback != null) { callback.call(null); }
			}
		});
	}

	@Override
	public void remove( final String key) {
		dispatch(new Runnable() {
			@Override
			public void run() {
				getDataProvider().remove(key);
			}
		});
	}

	@Override
	public void contains( final String key,  final Callback<Boolean> callback) {
		dispatch(new Runnable() {
			@Override
			public void run() {
				callback.call(contains(key));
			}
		});
	}

	@Override
	public boolean contains( String key) {
		return getDataProvider().contains(key);
	}

	@Override
	public void keys( final Callback<Set<String>> callback) {
		dispatch(new Runnable() {
			@Override
			public void run() {
				callback.call(keys());
			}
		});
	}

	@Override
	public Set<String> keys() {
		return getDataProvider().keys();
	}

	@Override
	public void entries( final Callback<Set<Map.Entry<String, D>>> callback) {
		dispatch(new Runnable() {
			@Override
			public void run() {
				callback.call(entries());
			}
		});
	}

	@Override
	public Set<Map.Entry<String, D>> entries() {
		return getDataProvider().entries();
	}

}
