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

package net.samagames.core.utils.data.serializer;

import com.google.gson.Gson;

public class GsonDataSerializer<D> extends DataSerializer<D> {

	public GsonDataSerializer() {
	}

	@Override
	public String serialize(D object) {
		return new Gson().toJson(object);
//		JsonObject jsonObject = new JsonObject();
//
//		try {
//			for (Field field : object.getClass().getDeclaredFields()) {
//				if (String.class.isAssignableFrom(field.getType())) {
//					jsonObject.addProperty(field.getName(), (String) field.get(object));
//				} else if (Character.class.isAssignableFrom(field.getType())) {
//					jsonObject.addProperty(field.getName(), (Character) field.get(object));
//				} else if (Number.class.isAssignableFrom(field.getType())) {
//					jsonObject.addProperty(field.getName(), (Number) field.get(object));
//				} else if (Boolean.class.isAssignableFrom(field.getType())) {
//					jsonObject.addProperty(field.getName(), (Boolean) field.get(object));
//				} else if (JsonElement.class.isAssignableFrom(field.getType())) {
//					jsonObject.add(field.getName(), (JsonElement) field.get(object));
//				} else {
//					throw new IllegalArgumentException("Unknown field type: " + field.getType());
//				}
//			}
//		} catch (Exception e) {
//			throw new SerializerException("Failed to serialize " + object.getClass() + " to Json", e);
//		}
//
//		return jsonObject.toString();
	}
}
