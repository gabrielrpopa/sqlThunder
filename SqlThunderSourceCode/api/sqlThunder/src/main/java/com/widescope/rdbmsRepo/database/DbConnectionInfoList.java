package com.widescope.rdbmsRepo.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.widescope.rdbmsRepo.database.SqlRepository.Objects.SqlRepoDatabase;
import com.widescope.rest.RestInterface;
import com.widescope.sqlThunder.utils.FileUtilWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DbConnectionInfoList implements RestInterface {


    private List<DbConnectionInfo> dbConnectionInfoLst;
    public List<DbConnectionInfo> getDbConnectionInfoList() {
        return dbConnectionInfoLst;
    }

    public void setDbConnectionInfoList(List<DbConnectionInfo> dbConnectionInfoLst) {
        this.dbConnectionInfoLst = dbConnectionInfoLst;
    }

    public DbConnectionInfoList() {
        this.dbConnectionInfoLst = new ArrayList<>();
    }
    public void addDbConnectionInfo(DbConnectionInfo dbConnectionInfo) {
        this.dbConnectionInfoLst.add(dbConnectionInfo);
    }

    @Override
    public String toString() {
        try	{
            return new Gson().toJson(this);
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



    public static DbConnectionInfoList toDbConnectionInfoList(String str) {
        Gson gson = new Gson();
        try	{
            return gson.fromJson(str, DbConnectionInfoList.class);
        }
        catch(JsonSyntaxException ex) {
            return null;
        }

    }


    public static DbConnectionInfoList loadDbConnectionInfoList(String fileName) throws IOException {
        DbConnectionInfoList ret = null;
        if(FileUtilWrapper.isFilePresent(fileName)) {
            String content = FileUtilWrapper.readFileToString(fileName);
            ret = DbConnectionInfoList.toDbConnectionInfoList(content);
        } else {
            ret = new DbConnectionInfoList();
            String content = ret.toString();
            FileUtilWrapper.writeFile(fileName, content);
        }
        return ret;
    }

    public void blockPassword() {
        for(DbConnectionInfo x: dbConnectionInfoLst) {
            x.setPassword("**********");
            x.setTunnelRemoteUserPassword("**********");
            x.setTunnelRemoteRsaKey("**********");;
        }
    }


    public static String mock() {
        DbConnectionInfoList ret = new DbConnectionInfoList();
        DbConnectionInfo db = new DbConnectionInfo();
        ret.addDbConnectionInfo(db);
        return ret.toString();
    }



}
