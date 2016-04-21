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

package net.samagames.core.utils.data.parser;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class DataParser<T> {

	protected final Class<? extends T> dataClass;

	public DataParser(Class<? extends T> dataClass) {
		this.dataClass = dataClass;
	}

	public T parse(String string) {
		try {
			Constructor<? extends T> constructor = dataClass.getConstructor(String.class);
			return constructor.newInstance(string);
		} catch (NoSuchMethodException e) {
			throw new ParseException(dataClass + " is missing String constructor", e);
		} catch (InstantiationException e) {
			throw new ParseException("Could not instantiate " + dataClass, e);
		} catch (IllegalAccessException e) {
			throw new ParseException("Constructor for " + dataClass + " is not accessible", e);
		} catch (InvocationTargetException e) {
			throw new ParseException("Exception while creating new instance of " + dataClass, e.getCause());
		}
	}

}
