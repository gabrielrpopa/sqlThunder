/*
 * Copyright 2022-present Infinite Loop Corporation Limited, Inc.
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


package com.widescope.sqlThunder.utils.compression;


import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;


public class InMemCompression {

	/*GZIP IN-MEM COMPRESSION/DECOMPRESSION*/
	public static byte[] compressGZIP(final String str) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		GZIPOutputStream gzipOut = new GZIPOutputStream(byteArrayOutputStream);
		ObjectOutputStream objectOut = new ObjectOutputStream(gzipOut);
		objectOut.writeObject(str);
		objectOut.close();
        return byteArrayOutputStream.toByteArray();
	}
	
	public static byte[] compressGZIP(final byte[] buffer) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		GZIPOutputStream gzipOut = new GZIPOutputStream(byteArrayOutputStream);
		ObjectOutputStream objectOut = new ObjectOutputStream(gzipOut);
		objectOut.write(buffer);
		objectOut.close();
        return byteArrayOutputStream.toByteArray();
	}
	
	public static byte[] decompressGZIP(byte[] bytes) throws IOException {
	    ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
	    GZIPInputStream gzipIn = new GZIPInputStream(bin);
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = gzipIn.read(buf)) > 0)
	        bos.write(buf, 0, len);
	    return bos.toByteArray();
	}

	/*ZLIB IN-MEM COMPRESSION/DECOMPRESSION*/
	public static String compressZlibToPython(String data) throws IOException {
		byte[] input = data.getBytes(StandardCharsets.UTF_8);
		byte[] output = new byte[100];
		Deflater deflater = new Deflater();
		deflater.setInput(input);
		deflater.finish();
		deflater.deflate(output);
		deflater.end();
        return Base64.getEncoder().encodeToString(output);
	}

	
	public static byte[] compressZlib(byte[] data) throws IOException {
		Deflater deflater = new Deflater();
		deflater.setInput(data);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
		deflater.finish();
		byte[] buffer = new byte[1024];
		while (!deflater.finished()) {
			int count = deflater.deflate(buffer);
			outputStream.write(buffer, 0, count);
		}
		outputStream.close();
		return outputStream.toByteArray();
	}

	public static byte[] decompressZlib(byte[] data) throws IOException, DataFormatException {
		Inflater inflater = new Inflater();
		inflater.setInput(data);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
		byte[] buffer = new byte[1024];
		while (!inflater.finished()) {
		    int count = inflater.inflate(buffer);  
		    outputStream.write(buffer, 0, count);  
		}
		outputStream.close();
        return outputStream.toByteArray();
	}
}
