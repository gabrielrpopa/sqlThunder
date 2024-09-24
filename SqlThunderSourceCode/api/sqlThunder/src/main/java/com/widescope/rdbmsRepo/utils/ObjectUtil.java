/*
 * Copyright 2024-present Infinite Loop Corporation Limited, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.widescope.rdbmsRepo.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectUtil {
	
	
	public static byte[] ObjectToByteArray(Object object) throws IOException
	{
		byte[] ret = null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ObjectOutputStream out = null;
            out = new ObjectOutputStream(bos);
            out.writeObject(object);
            out.flush();
            ret = bos.toByteArray();
        } catch (Exception ignored) {}
		return ret;
	}
	
	
	public static Object byteArrayToObject(byte[] ba) throws IOException
	{
		Object o = null;
		ByteArrayInputStream bis = new ByteArrayInputStream(ba);
        try (ObjectInput in = new ObjectInputStream(bis)) {
            o = in.readObject();
        } catch (Exception ignored) {}
        return o;
	}

}
