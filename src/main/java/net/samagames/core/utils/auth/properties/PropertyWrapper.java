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

import com.google.gson.JsonObject;
import net.samagames.core.utils.ConstructorPopulator;
import net.samagames.core.utils.Wrapper;

public class PropertyWrapper extends Wrapper {

	public PropertyWrapper(Object handle) {
		super(handle);
	}

	public PropertyWrapper(final String name, final String value, final String signature) {
		super(CLASS_RESOLVER.resolveWrapper("net.minecraft.util.com.mojang.authlib.properties.Property", "com.mojang.authlib.properties.Property"), new ConstructorPopulator() {
			@Override
			public Class<?>[] types() {
				return new Class[] {
						String.class,
						String.class,
						String.class };
			}

			@Override
			public Object[] values() {
				return new Object[] {
						name,
						value,
						signature };
			}
		});
	}

	public PropertyWrapper(String name, String value) {
		this(name, value, null);
	}

	public PropertyWrapper(JsonObject jsonObject) {
		this(jsonObject.get("name").getAsString(), jsonObject.get("value").getAsString(), jsonObject.get("signature").getAsString());
	}

	public String getName() {
		return getFieldValue("name");
	}

	public String getValue() {
		return getFieldValue("value");
	}

	public String getSignature() {
		return getFieldValue("signature");
	}

	public JsonObject toJson() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("name", getName());
		jsonObject.addProperty("value", getValue());
		jsonObject.addProperty("signature", getSignature());
		return jsonObject;
	}

}
