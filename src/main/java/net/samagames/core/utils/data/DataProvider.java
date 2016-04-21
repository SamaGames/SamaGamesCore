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


import java.util.Map;
import java.util.Set;

/**
 * Base DataProvider interface.
 * <p>
 * Data objects should have a public constructor which takes a {@link String} and a <code>toData</code> method which returns a {@link String}
 */
public interface DataProvider<D> {

	/**
	 * Get the raw value for a key
	 *
	 * @param key key to get
	 * @return the raw {@link String} value, or <code>null</code> if the key doesn't exist
	 */
	D get( String key);

	/**
	 * Get the value for a key, or the default value
	 *
	 * @param key key to get
	 * @param def default value
	 * @return the value, or the default value if the key doesn't exist
	 */
	D get( String key,  D def);

	/**
	 * Get multiple values
	 *
	 * @param keys keys to get
	 * @return Map of values
	 */
	Map<String, D> getAll( Set<String> keys);

	/**
	 * Set the value for a key
	 *
	 * @param key   key to update
	 * @param value value to set
	 */
	void put( String key, D value);

	/**
	 * Set multiple values
	 */
	void putAll( Map<String, D> values);

	//	/**
	//	 * Get the value for the key and update it
	//	 *
	//	 * @param key   key to get/put
	//	 * @param value value to set
	//	 * @return the previous value of the key, or <code>null</code> if the key doesn't exist
	//	 */
	//	<D> D getAndPut( String key, D value);
	//
	//	/**
	//	 * Get the value for the key and update it
	//	 *
	//	 * @param key   key to get/zpdate
	//	 * @param value value to set
	//	 * @param def   default value
	//	 * @return the previous value of the key, or the default value if the key doesn't exist
	//	 */
	//	<D> D getAndPut( String key, D value, @NotNull D def);

	/**
	 * Remove a key
	 *
	 * @param key key to remove
	 */
	void remove( String key);

	/**
	 * Check if a key exists
	 *
	 * @param key key to check
	 * @return <code>true</code> if the key exists, <code>false</code> otherwise
	 */
	boolean contains( String key);

	/**
	 * Get all keys
	 *
	 * @return Set of value keys
	 */
	Set<String> keys();

	/**
	 * Get all entries
	 *
	 * @return Set of entries
	 */
	Set<Map.Entry<String, D>> entries();
}
