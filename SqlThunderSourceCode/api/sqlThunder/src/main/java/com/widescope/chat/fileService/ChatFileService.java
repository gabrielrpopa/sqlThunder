package com.widescope.chat.fileService;


import com.widescope.chat.db.ChatMessage;
import com.widescope.sqlThunder.utils.FileUtilWrapper;
import com.widescope.sqlThunder.utils.compression.InMemCompression;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class ChatFileService {

    private static final String error1 = "Main Chat Folder does not exist and cannot be created";
    private static final String error2 = "Something got fundamentally wrong when creating folder for the message";
    private static final String error3 = "Something got fundamentally wrong when saving the message";



    private String path = "";
    public ChatFileService(String _path) throws Exception {
        boolean isCreated = FileUtilWrapper.createFolder(_path);
        if(!isCreated)
            throw new Exception(error1);

        this.path = _path;
    }

    public String saveMessage(  final String fromUser,
                                final String toUser,
                                final String requestId,
                                final ChatMessage m) throws Exception {
        String folder = this.path + "/" + fromUser + "/" + toUser  ;
        File dir = new File(folder);
        if( !dir.exists() ) {
            if(!dir.mkdirs()) throw new Exception(error2);
        }


        byte[] zip = InMemCompression.compressGZIP(m.toString());
        String filePath = folder + "//" + requestId;
        if( FileUtilWrapper.writeFile(filePath, zip) ) {
            return filePath;
        } else {
            throw new Exception(error3);
        }
    }

    public ChatMessage readMessage( final String fromUser,
                                    final String toUser,
                                    final String requestId) throws Exception {
        String file = this.path + "/" + fromUser + "/" + toUser + "/" + requestId ;
        byte[] ret = InMemCompression.decompressGZIP(FileUtilWrapper.readFile(file));
        String str = new String(ret, StandardCharsets.UTF_8);
        return ChatMessage.toChatMessage(str);
    }

    public boolean delete(final String fromUser,
                          final String toUser,
                          final String requestId) throws Exception {
        String file = this.path + "/" + fromUser + "/" + toUser + "/" + requestId ;
        File file2Delete = new File(file);
        return file2Delete.delete();
    }

}
