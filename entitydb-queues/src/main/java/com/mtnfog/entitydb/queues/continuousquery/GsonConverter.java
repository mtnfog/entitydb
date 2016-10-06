/**
 * Copyright © 2016 Mountain Fog, Inc. (support@mtnfog.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * For proprietary licenses contact support@mtnfog.com or visit http://www.mtnfog.com.
 */
package com.mtnfog.entitydb.queues.continuousquery;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import com.google.gson.Gson;
import com.squareup.tape.FileObjectQueue;

public class GsonConverter<T> implements FileObjectQueue.Converter<T> {
	
	private final Gson gson;
	private final Class<T> type;

	public GsonConverter(Gson gson, Class<T> type) {
		this.gson = gson;
		this.type = type;
	}

	@Override
	public T from(byte[] bytes) {
		Reader reader = new InputStreamReader(new ByteArrayInputStream(bytes));
		return gson.fromJson(reader, type);
	}

	@Override
	public void toStream(T object, OutputStream bytes) throws IOException {
		Writer writer = new OutputStreamWriter(bytes);
		gson.toJson(object, writer);
		writer.close();
	}
	
}