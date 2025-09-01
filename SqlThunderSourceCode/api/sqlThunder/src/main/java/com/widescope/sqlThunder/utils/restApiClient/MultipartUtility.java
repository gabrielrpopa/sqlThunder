package com.widescope.sqlThunder.utils.restApiClient;

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
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;



public class MultipartUtility {
    private final String boundary;
    private static final String LINE_FEED = "\r\n";
    private final HttpURLConnection httpConn;
    private final String charset;
    private final OutputStream outputStream;
    private final PrintWriter writer;
 
    /**
     * This constructor initializes a new HTTP POST request with content type
     * is set to multipart/form-data
      */
    public MultipartUtility(String requestURL, 
    						String charset) throws IOException {
        this.charset = charset;
         
        // creates a unique boundary based on time stamp
        boundary = "===" + System.currentTimeMillis() + "===";
         
        URL url = new URL(requestURL);
        httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true); // indicates POST method
        httpConn.setDoInput(true);
        httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        
        outputStream = httpConn.getOutputStream();
        writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);
    }
 
    /**
     * Adds a form field to the request
     * @param name field name
     * @param value field value
     */
    public void addFormField(String name, String value) {
        writer.append("--").append(boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"").append(name).append("\"").append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=").append(charset).append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }
 
    /**
     * Adds a upload file section to the request
     * @param fieldName name attribute in <input type="file" name="..." />
     * @param uploadFile a File to be uploaded
     * @throws IOException
     */
    public void addFilePart(String fieldName, File uploadFile) throws IOException {
        String fileName = uploadFile.getName();
        writer.append("--").append(boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"").append(fieldName).append("\"; filename=\"").append(fileName).append("\"")
              .append(LINE_FEED);
        
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
 
    /**
     * Adds a header field to the request.
     * @param name - name of the header field
     * @param value - value of the header field
     */
    public void addHeaderField(String name, String value) {
        writer.append(name).append(": ").append(value).append(LINE_FEED);
        writer.flush();
    }
     
    /**
     * Completes the request and receives response from the server.
     * @return a list of Strings as response in case the server returned
     * status OK, otherwise an exception is thrown.
     *   */
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
    
    

    public static int uploadOneShotTxtFile(	final String fromEmail, /*email of the sender*/
											final String toEmail, /*email of the sender*/
											final String fileName, /**/
											final String fileType,
											final String fromCompanyUid,
											final String toCompanyUid,
											final String fileUid,
											final String fileKey,
											final String startPeriodValid,
											final String endPeriodValid,
											final String fromCompanyPassword,
											final String url,
    										final String filePath) {
    	
    	String charset = "UTF-8";
    	String param = "value";
    	File textFile = new File(filePath);
    	
    	String boundary = Long.toHexString(System.currentTimeMillis()); // Just generate some unique random value.
    	String CRLF = "\r\n"; // Line separator required by multipart/form-data.
    	
    	try {
    		URLConnection connection = new URL(url).openConnection();
        	connection.setDoOutput(true);
        	connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        	OutputStream output = connection.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);
            
         // Send normal param.
            writer.append("--").append(boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"param\"").append(CRLF);
            writer.append("Content-Type: text/plain; charset=").append(charset).append(CRLF);
            writer.append(CRLF).append(param).append(CRLF).flush();
            
            
            writer.append("fromEmail" + ": ").append(fromEmail).append(LINE_FEED);
            writer.flush();
            
            writer.append("toEmail" + ": ").append(toEmail).append(LINE_FEED);
            writer.flush();
            
            writer.append("fileName" + ": ").append(fileName).append(LINE_FEED);
            writer.flush();
            
            writer.append("fileType" + ": ").append(fileType).append(LINE_FEED);
            writer.flush();
            
            writer.append("fromCompanyUid" + ": ").append(fromCompanyUid).append(LINE_FEED);
            writer.flush();
            
            writer.append("toCompanyUid" + ": ").append(toCompanyUid).append(LINE_FEED);
            writer.flush();

            writer.append("fileUid" + ": ").append(fileUid).append(LINE_FEED);
            writer.flush();

            writer.append("fileKey" + ": ").append(fileKey).append(LINE_FEED);
            writer.flush();

            writer.append("startPeriodValid" + ": ").append(startPeriodValid).append(LINE_FEED);
            writer.flush();

            writer.append("endPeriodValid" + ": ").append(endPeriodValid).append(LINE_FEED);
            writer.flush();

            writer.append("fromCompanyPassword" + ": ").append(fromCompanyPassword).append(LINE_FEED);
            writer.flush();
	



			

            
			
			
         // Send text file.
            writer.append("--").append(boundary).append(CRLF);
            writer.append("Content-Disposition: form-data; name=\"textFile\"; filename=\"").append(textFile.getName()).append("\"").append(CRLF);
            writer.append("Content-Type: text/plain; charset=").append(charset).append(CRLF); // Text file itself must be saved in this charset!
            writer.append(CRLF).flush();
            Files.copy(textFile.toPath(), output);
            output.flush(); // Important before continuing with writer!
            writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.
         // End of multipart/form-data.
            writer.append("--").append(boundary).append("--").append(CRLF).flush();
            
            
         // Request is lazily fired whenever you need to obtain information about response.
            int responseCode = ((HttpURLConnection) connection).getResponseCode();
            System.out.println(responseCode); // Should be 200
            
            return responseCode;
            
    	} catch(Exception ex) {
    		return -1;
    	}
    }
    	
    
    
    public static int uploadOneShotBinaryFile(	final String fromEmail, /*email of the sender*/
												final String toEmail, /*email of the sender*/
												final String fileName, /**/
												final String fileType,
												final String fromCompanyUid,
												final String toCompanyUid,
												final String fileUid,
												final String fileKey,
												final String startPeriodValid,
												final String endPeriodValid,
												final String fromCompanyPassword,
												final String url,
												final String filePath) {

		String charset = "UTF-8";
		String param = "value";
		File binaryFile = new File(filePath);
		
		String boundary = Long.toHexString(System.currentTimeMillis()); // Just generate some unique random value.
		String CRLF = "\r\n"; // Line separator required by multipart/form-data.
		
		try {
			URLConnection connection = new URL(url).openConnection();
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			
			OutputStream output = connection.getOutputStream();
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, charset), true);
			
			// Send normal param.
			writer.append("--").append(boundary).append(CRLF);
			writer.append("Content-Disposition: form-data; name=\"param\"").append(CRLF);
			writer.append("Content-Type: text/plain; charset=").append(charset).append(CRLF);
			writer.append(CRLF).append(param).append(CRLF).flush();
			
			
			
			writer.append("fromEmail" + ": ").append(fromEmail).append(LINE_FEED);
            writer.flush();
            
            writer.append("toEmail" + ": ").append(toEmail).append(LINE_FEED);
            writer.flush();
            
            writer.append("fileName" + ": ").append(fileName).append(LINE_FEED);
            writer.flush();
            
            writer.append("fileType" + ": ").append(fileType).append(LINE_FEED);
            writer.flush();
            
            writer.append("fromCompanyUid" + ": ").append(fromCompanyUid).append(LINE_FEED);
            writer.flush();
            
            writer.append("toCompanyUid" + ": ").append(toCompanyUid).append(LINE_FEED);
            writer.flush();

            writer.append("fileUid" + ": ").append(fileUid).append(LINE_FEED);
            writer.flush();

            writer.append("fileKey" + ": ").append(fileKey).append(LINE_FEED);
            writer.flush();

            writer.append("startPeriodValid" + ": ").append(startPeriodValid).append(LINE_FEED);
            writer.flush();

            writer.append("endPeriodValid" + ": ").append(endPeriodValid).append(LINE_FEED);
            writer.flush();

            writer.append("fromCompanyPassword" + ": ").append(fromCompanyPassword).append(LINE_FEED);
            writer.flush();
            
			
			// Send binary file.
		    writer.append("--").append(boundary).append(CRLF);
		    writer.append("Content-Disposition: form-data; name=\"binaryFile\"; filename=\"").append(binaryFile.getName()).append("\"").append(CRLF);
		    writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(binaryFile.getName())).append(CRLF);
		    writer.append("Content-Transfer-Encoding: binary").append(CRLF);
		    writer.append(CRLF).flush();
		    Files.copy(binaryFile.toPath(), output);
		    output.flush(); // Important before continuing with writer!
		    writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.

			
			
			// Request is lazily fired whenever you need to obtain information about response.
			int responseCode = ((HttpURLConnection) connection).getResponseCode();
			System.out.println(responseCode); // Should be 200
			
			return responseCode;
		
		} catch(Exception ex) {
			return -1;
		}
	}
    
    
}
