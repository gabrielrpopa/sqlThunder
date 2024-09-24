package com.widescope.cluster.management.miscellaneous;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.List;

public class ListClusterNodeFile {

    private List<ClusterNode> list = new ArrayList<>();
    public List<ClusterNode> getList() { return list; }
    public void setList(final List<ClusterNode> list) { this.list = list; }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }


    public static ListClusterNodeFile toListClusterLoadFile (String str) {
        Gson gson = new Gson();
        try	{
            return gson.fromJson(str, ListClusterNodeFile.class);
        }
        catch(JsonSyntaxException ex) {
            return null;
        }
    }



    public static String generate() {
        ListClusterNodeFile g = new ListClusterNodeFile();
        g.list.add(new ClusterNode("http://192.168.0.175:9099/sqlThunder", "SHARD"));
        g.list.add(new ClusterNode("http://192.168.0.119:9099/sqlThunder", "SHARD"));
        return g.toString();
    }
}
