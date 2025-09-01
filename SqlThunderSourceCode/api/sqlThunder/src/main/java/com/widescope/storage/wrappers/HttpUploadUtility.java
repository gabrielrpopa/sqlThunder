package com.widescope.storage.wrappers;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpUploadUtility {
	private final String boundary;
    private static final String LINE_FEED = "\r\n";
    private final HttpURLConnection httpConn;
    private final String charset;
    private final OutputStream outputStream;
    private final PrintWriter writer;
 
    public HttpUploadUtility(String requestURL, String charset) throws IOException {
    	this.charset = charset;
        
        // creates a unique boundary based on time stamp
        boundary = "===" + System.currentTimeMillis() + "===";
         
        URL url = new URL(requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true); // indicates POST method
        httpConn.setDoInput(true);
        httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        httpConn.setRequestProperty("User-Agent", "CodeJava Agent");
        httpConn.setRequestProperty("Test", "Bonjour");
        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),true);
    }
    
    

    public void addFormField(String name, String value) {
        writer.append("--").append(boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"").append(name).append("\"").append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=").append(charset).append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }
 

    public void addFilePart(String fieldName, File uploadFile)
            throws IOException {
        String fileName = uploadFile.getName();
        writer.append("--").append(boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"").append(fieldName).append("\"; filename=\"").append(fileName).append("\"").append(LINE_FEED);
        writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();
 
        FileInputStream inputStream = new FileInputStream(uploadFile);
        byte[] buffer = new byte[4096];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        inputStream.close();
         
        writer.append(LINE_FEED);
        writer.flush();    
    }
 

    public void addHeaderField(String name, String value) {
        writer.append(name).append(": ").append(value).append(LINE_FEED);
        writer.flush();
    }
     

    public List<String> finish() throws IOException {
        List<String> response = new ArrayList<String>();
 
        writer.append(LINE_FEED).flush();
        writer.append("--").append(boundary).append("--").append(LINE_FEED);
        writer.close();
 
        // checks server's status code first
        int status = httpConn.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                response.add(line);
            }
            reader.close();
            httpConn.disconnect();
        } else {
            throw new IOException("Server returned non-OK status: " + status);
        }
 
        return response;
    }
    
    
    public static List<String> 
    upload(	String requestURL,
    		List<String> files,
    		Map<String, String> headers) {
        String charset = "UTF-8";
        List<String> ret = new ArrayList<>();
        try {
        	HttpUploadUtility multipart = new HttpUploadUtility(requestURL, charset);
        	for (Map.Entry<String, String> set : headers.entrySet()) {
    		    multipart.addHeaderField(set.getKey(), set.getValue());
    		}
            for(String path:files) {
            	File uploadFile = new File(path);
            	multipart.addFilePart("fileUpload", uploadFile);
            }
            List<String> response = multipart.finish();
            System.out.println("SERVER REPLIED:");
            ret.addAll(response);
        } catch (IOException ex) {
            System.err.println(ex);
        }
        return ret;
    }
    
    public static void printMessage(List<String> log) {
    	for(String l: log) {
    		System.out.println(l);
    	}
    }
    
    public static String assemble(List<String> log) {
    	StringBuilder ret = new StringBuilder();
    	for(String l: log) {
    		System.out.println(l);
    		ret.append(l);
    	}
    	return ret.toString();
    }
}
