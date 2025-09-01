package com.widescope.rdbmsRepo.database.elasticsearch.repo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.widescope.rdbmsRepo.database.mongodb.MongoResultSet;
import com.widescope.sqlThunder.rest.RestInterface;
import org.json.simple.JSONObject;

public class ElasticResultSet implements RestInterface  {

    private Object jo;
    public Object getJo() { return jo; }
    public void setJo(Object jo) { this.jo = jo; }


    public ElasticResultSet() {

    }

    public ElasticResultSet(final Object object) {
        this.jo = object;
    }


    @Override
    public String toString() {
        return new Gson().toJson(this);
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



    public static ElasticResultSet toElasticResultSet(String str) {
        Gson gson = new Gson();
        try	{
            return gson.fromJson(str, ElasticResultSet.class);
        }
        catch(JsonSyntaxException ex) {
            return null;
        }
    }


}
