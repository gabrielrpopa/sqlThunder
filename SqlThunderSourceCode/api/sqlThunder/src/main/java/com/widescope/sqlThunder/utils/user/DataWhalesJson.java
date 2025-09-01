package com.widescope.sqlThunder.utils.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.widescope.sqlThunder.utils.FileUtilWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataWhalesJson {

    private List<String> sqlDbRefsList;
    private List<String> mongoClusterList;
    private List<String> elasticClusterList;

    public DataWhalesJson() {
        this.sqlDbRefsList = new ArrayList<>();
        this.mongoClusterList = new ArrayList<>();
        this.elasticClusterList = new ArrayList<>();
    }




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



    public static DataWhalesJson toDataWalesJson(String str) {
        Gson gson = new Gson();
        try	{
            return gson.fromJson(str, DataWhalesJson.class);
        }
        catch(JsonSyntaxException ex) {
            return null;
        }

    }


    public List<String> getSqlDbRefsList() {
        return sqlDbRefsList;
    }
    public void addSqlDbRefs(final String sqlDbRef) { this.sqlDbRefsList.add(sqlDbRef); }
    public void setSqlDbRefsList(List<String> sqlDbRefsList) { this.sqlDbRefsList = sqlDbRefsList; }

    public List<String> getMongoClusterList() {
        return mongoClusterList;
    }
    public void setMongoClusterList(List<String> mongoClusterList) {
        this.mongoClusterList = mongoClusterList;
    }
    public void addMongoCluster(final String mongoCluster) {
        this.mongoClusterList.add(mongoCluster);
    }


    public List<String> getElasticClusterList() {
        return elasticClusterList;
    }
    public void setElasticClusterList(List<String> elasticClusterList) { this.elasticClusterList = elasticClusterList; }
    public void addElasticCluster(final String elasticCluster) {
        this.elasticClusterList.add(elasticCluster);
    }






    public static DataWhalesJson loadDataWhalesJson(String fileName) throws IOException {
        DataWhalesJson ret = null;
        if(FileUtilWrapper.isFilePresent(fileName)) {
            String content = FileUtilWrapper.readFileToString(fileName);
            ret = DataWhalesJson.toDataWalesJson(content);
        } else {
            ret = new DataWhalesJson();
            ret.sqlDbRefsList.add("localhostDbServer");
            ret.mongoClusterList.add("localMongoDb");
            ret.elasticClusterList.add("localElasticCluster");
            String content = ret.toString();
            FileUtilWrapper.writeFile(fileName, content);
        }
        return ret;
    }



}
