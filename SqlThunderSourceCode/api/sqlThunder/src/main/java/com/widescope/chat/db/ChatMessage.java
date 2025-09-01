package com.widescope.chat.db;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.widescope.chat.fileService.MessageMetadata;
import com.widescope.chat.fileService.MessageMetadataList;
import com.widescope.logging.AppLogger;
import com.widescope.sqlThunder.rest.RestInterface;
import com.widescope.sqlThunder.utils.StringUtils;
import org.bson.BsonType;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonRepresentation;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


public class ChatMessage  implements RestInterface {

    private List<MessageAttachmentStream> attachList = new ArrayList<MessageAttachmentStream>() ;
    private String text="";
    private long millisecondsEpoch=-1;
    private long fromId=-1;
    private String fromUser="";
    private long toId=-1;
    private String toUser="";  /*this can be group name*/
    private String direction ="";
    private boolean isGroup = false;

    @BsonId()
    @BsonRepresentation(BsonType.OBJECT_ID)
    private String messageId="";
    private boolean isRead = false;

    private String isEncrypt="";
    private int readCounter = -1;
    private String flags = "NXXX";   /*flags[0]-delete(Y/N), flags[1]-?, flags[2]-?, flags[3]-?*/

    public ChatMessage() {

    }
    public ChatMessage(final long fromId,
                       final long toId,
                       final long millisecondsEpoch,
                       final String requestId) {
        this.isEncrypt = "N";
        this.attachList = new ArrayList<>();
        this.text = "";
        this.fromUser = "";
        this.fromId = fromId;
        this.toUser = "";
        this.toId = toId;
        this.setIsGroup(false);
        this.setDirection("IN");
        this.setMessageId(requestId);
        this.isRead = false;
        this.millisecondsEpoch = millisecondsEpoch;
    }



    public ChatMessage(final String t,
                       final MultipartFile[] l,
                       final MessageMetadataList metadataList,
                       final String fromUser,
                       final long fromId,
                       final String toUser,
                       final long toId,
                       final long millisecondsEpoch,
                       final String isEncrypt,
                       final String messageId,  /*this is request Id*/
                       final int countUsers
                       ) throws IOException {

        this.isEncrypt = isEncrypt;
        this.attachList = new ArrayList<>();
        this.text = Objects.requireNonNullElse(t, "");
        this.fromUser = fromUser;
        this.fromId = fromId;
        this.toUser = toUser;
        this.toId = toId;
        this.setIsGroup(false);
        this.setDirection("IN");
        this.setMessageId(messageId);
        this.setReadCounter(countUsers);
        this.isRead = false;
        this.millisecondsEpoch = millisecondsEpoch;

        for(MultipartFile attachment: l) {
            final String type = attachment.getContentType();
            final byte[] content= attachment.getBytes();
            MessageMetadata mMetadata;
            try {
                Map<String, MessageMetadata> m =metadataList.getMessageMetadata().stream().collect(Collectors.toMap(MessageMetadata::getName, Function.identity()));
                mMetadata = m.get(attachment.getOriginalFilename());
            } catch(Exception ignored) {
                mMetadata = new MessageMetadata();
                mMetadata.setType(attachment.getContentType());
                mMetadata.setName(attachment.getOriginalFilename());
                mMetadata.setLastModified(millisecondsEpoch);
            }

            String str = new String(content);

            if(type != null && content.length > 0) {
                // Image types
                if(type.equalsIgnoreCase("image/jpeg")
                        || type.equalsIgnoreCase("image/apng")
                        || type.equalsIgnoreCase("image/avif")
                        || type.equalsIgnoreCase("image/gif")
                        || type.equalsIgnoreCase("image/webp")
                        || type.equalsIgnoreCase("image/png")
                        || type.equalsIgnoreCase("image/svg+xml")) {
                    str = Base64.getEncoder().encodeToString(content);
                } else if(type.equalsIgnoreCase("application/pdf") ) {
                    str = Base64.getEncoder().encodeToString(content);
                } else if(type.equalsIgnoreCase("application/vnd.ms-powerpoint")
                        || type.equalsIgnoreCase("application/msword")
                        || type.equalsIgnoreCase("application/vnd.ms-access")
                        || type.equalsIgnoreCase("application/vnd.ms-excel") ) {
                    str = new String(content);
                } else {
                    str = new String(content);
                }
            }

            this.attachList.add(new MessageAttachmentStream(mMetadata, str, null));
        }
    }



    public ChatMessage(final String t,
                       final MessageAttachmentStream[] l,
                       final String fromUser,
                       final long fromId,
                       final String toUser,
                       final long toId,
                       final long millisecondsEpoch,
                       final String isEncrypt,
                       final String messageId,
                       final int countUsers) throws IOException {
        this.isEncrypt = isEncrypt;
        this.attachList = new ArrayList<>();
        this.text = Objects.requireNonNullElse(t, "");
        this.fromUser = fromUser;
        this.fromId = fromId;
        this.toUser = toUser;
        this.toId = toId;
        this.setIsGroup(false);
        this.setDirection("IN");
        this.setMessageId(messageId);
        this.setReadCounter(countUsers);
        this.isRead = false;
        this.millisecondsEpoch = millisecondsEpoch;
        Collections.addAll(this.attachList, l);
    }


    public ChatMessage(final String txt,
                       final String fromUser,
                       final long fromId,
                       final String toUser,
                       final long toId,
                       final long millisecondsEpoch,
                       final String isEncrypt) {
        this.isEncrypt = isEncrypt;
        this.attachList = new ArrayList<>();
        this.text = Objects.requireNonNullElse(txt, "");
        this.fromUser = fromUser;
        this.fromId = fromId;
        this.toUser = toUser;
        this.toId = toId;
        this.setIsGroup(false);
        this.setDirection("IN");
        this.setMessageId(StringUtils.generateUniqueString());
        this.isRead = false;
        this.millisecondsEpoch = millisecondsEpoch;

    }


    public List<MessageAttachmentStream> getAttachList() {
        return attachList;
    }

    public void setAttachList(List<MessageAttachmentStream> aList) {
        this.attachList = aList;
    }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }


    public long getMillisecondsEpoch() { return millisecondsEpoch; }
    public void setMillisecondsEpoch(long millisecondsEpoch) { this.millisecondsEpoch = millisecondsEpoch; }

    public long getFromId() { return this.fromId; }
    public void setFromId(long fromId) { this.fromId = fromId; }

    public String getFromUser() { return this.fromUser; }
    public void setFromUser(String fromUser) { this.fromUser = fromUser; }

    public long getToId() { return toId; }
    public void setToId(long toId) { this.toId = toId; }
    public String getToUser() { return toUser; }
    public void setToUser(String toUser) { this.toUser = toUser; }

    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }
    public boolean getIsGroup() { return isGroup; }
    public void setIsGroup(boolean group) { isGroup = group; }
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    public boolean getIsRead() { return isRead; }
    public void setIsRead(boolean isRead) { this.isRead = isRead; }
    public String getIsEncrypt() { return isEncrypt; }
    public void setIsEncrypt(String isEncrypt) { this.isEncrypt = isEncrypt; }

    public int getReadCounter() { return readCounter; }
    public void setReadCounter(int readCounter) { this.readCounter = readCounter; }
    public void decrementCounter() { this.readCounter--; }

    public String getFlags() { return flags; }
    public void setFlags(String flags) { this.flags = flags; }


    @Override
    public String toString() {
        try	{
            Gson gson = new Gson();
            return gson.toJson(this);
        }
        catch(Exception ex) {
            return null;
        }
    }

    public String toStringPretty() {
        try	{
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(this);
        }
        catch(Exception ex) {
            return null;
        }
    }



    public static ChatMessage toChatMessage(String str) {
        Gson gson = new Gson();
        try	{
            return gson.fromJson(str, ChatMessage.class);
        }
        catch(JsonSyntaxException ex) {
            return null;
        }

    }


    public static void serialize(ChatMessage person, String fileName) throws Exception {
        try {
            var fileOutputStream = new FileOutputStream(fileName);
            var objOutputStream = new ObjectOutputStream(fileOutputStream);
            objOutputStream.writeObject(person);
            fileOutputStream.close();
            objOutputStream.close();
        } catch (IOException e) {
            throw new Exception(AppLogger.logException(e, Thread.currentThread().getStackTrace()[1], AppLogger.obj));
        }

    }



}
