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


import java.util.zip.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class PayloadCompression
{
	
	
	public static byte[] GZIPCompress(String str) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		GZIPOutputStream gzipOut = new GZIPOutputStream(byteArrayOutputStream);
		ObjectOutputStream objectOut = new ObjectOutputStream(gzipOut);
		objectOut.writeObject(str);
		objectOut.close();
        return byteArrayOutputStream.toByteArray();
	}
	
	
	
	public static String GZIPDecompress(byte[] compressedPayload) throws IOException, ClassNotFoundException {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressedPayload);
		GZIPInputStream gzipIn = new GZIPInputStream(byteArrayInputStream);
		ObjectInputStream objectIn = new ObjectInputStream(gzipIn);
		String decompressedPayload = (String) objectIn.readObject();
		objectIn.close();
		return decompressedPayload;
	}
	
		
	
	
	public static byte[] ZIPCompress(final String str) throws IOException {
		byte[] compressedPayload = null;
		Deflater compresser = new Deflater();
	    compresser.setInput(str.getBytes());
	    compresser.finish();
	    compresser.deflate(compressedPayload);
	    compresser.end();
		return compressedPayload;
	}
	
	
	
	public static String ZIPDecompress(final byte[] compressedPayload) throws IOException, ClassNotFoundException, DataFormatException
	{
		int compressedDataLength = compressedPayload.length;
		byte[] decompressedPayload = new byte[compressedDataLength * 4]; // Compression ratio is about 30%, make the buffer twice big as compressed buffer
		Inflater decompresser = new Inflater();
	    decompresser.setInput(compressedPayload, 0, compressedDataLength);
	    int resultLength = decompresser.inflate(decompressedPayload);
	    decompresser.end();
	    
	    
	    byte[] newdecompressedPayload = java.util.Arrays.copyOf(decompressedPayload, resultLength);
	    String decompressedPayloadString = java.util.Arrays.toString(newdecompressedPayload);
		return decompressedPayloadString;
	}
	
}
