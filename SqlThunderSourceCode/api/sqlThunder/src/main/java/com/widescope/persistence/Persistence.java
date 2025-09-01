package com.widescope.persistence;


import com.widescope.logging.AppLogger;
import com.widescope.persistence.execution.PersistencePrivilegeList;
import com.widescope.persistence.execution.PersistenceSourceList;
import com.widescope.sqlThunder.utils.FileUtilWrapper;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;


@Component
public class Persistence {

    private final String rootFolder = "./snapshots/";

    Persistence() throws Exception {
        try {
            Files.createDirectories(Path.of(rootFolder));
        }
        catch (IOException e) {
            throw new Exception (AppLogger.logException(e, rootFolder, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj));
        }
    }

    public String
    serialize(String fileContent,
              String subfolder,
              String filename,
              long userId) throws Exception {
        String path = rootFolder + subfolder + "/" + userId + "/" + filename;
        boolean isOk = FileUtilWrapper.overwriteFile(path, filename, fileContent);
        return isOk ? path : null;
    }

    public String
    serialize(byte[] fileContent,
              String subfolder,
              String filename,
              long userId) throws Exception {
        String path = rootFolder + subfolder + "/" + userId + "/" + filename;
        boolean isOk = FileUtilWrapper.overwriteFile(path, filename, fileContent);
        return isOk ? path : "";
    }

    public byte[]
    deserialize(String subfolder, String filename, long userId) throws Exception {
        String fullPath = rootFolder + subfolder + "/" + userId + "/" + filename;
        if(FileUtilWrapper.isFilePresent(fullPath) ) {
            return FileUtilWrapper.readFile(fullPath);
        }
        return new byte[0];
    }


    public String
    deserializeAsString(String subfolder, String filename, long userId) throws Exception {
        String fullPath = rootFolder + subfolder + "/" + userId + "/" + filename;
        return new String(deserialize(fullPath), StandardCharsets.UTF_8);
    }

    public byte[]
    deserialize(String fullPath) throws Exception {
        if(FileUtilWrapper.isFilePresent(fullPath) ) {
            return FileUtilWrapper.readFile(fullPath);
        }
        return new byte[0];
    }

    public String
    deserializeAsString(String fullPath) throws Exception {
       return new String(deserialize(fullPath), StandardCharsets.UTF_8);
    }

    public boolean
    delete(String fullPath) throws Exception {
        return FileUtilWrapper.deleteFile(fullPath);
    }

}
