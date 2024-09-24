package com.widescope.sqlThunder.tcpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


public class TcpThinClient {
    Socket socket = null;

    public boolean connect(String ipAddress, int port)  {
        try {
            socket = new Socket(ipAddress, port);
            socket.setSoTimeout(10000);
        } catch (IOException e) {
            return false;
        }

        return socket.isConnected();
    }

    public boolean isConnected() {
        return socket.isConnected();
    }

    public void send(String message) throws IOException {
        OutputStream output = socket.getOutputStream();
        byte[] data = message.getBytes();
        output.write(data);
    }

    public String read() throws IOException {
        InputStream input = socket.getInputStream();
        byte[] buf = new byte[65535];
        int length = input.read(buf);
        return new String(buf, 0, length ,StandardCharsets.UTF_8);
    }

    public void disconnect() throws IOException {
        if(socket.isConnected())
            socket.close();
    }
}
