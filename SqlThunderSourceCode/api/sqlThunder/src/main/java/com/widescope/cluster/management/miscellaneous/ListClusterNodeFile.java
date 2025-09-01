package com.widescope.cluster.management.miscellaneous;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.List;

public class ListClusterNodeFile {

    private DevClusterProfile devClusterProf = new DevClusterProfile();
    public DevClusterProfile getDevClusterProf() { return devClusterProf; }
    public void setDevClusterProf(DevClusterProfile devClusterProf) { this.devClusterProf = devClusterProf; }


    private TestClusterProfile testClusterProf = new TestClusterProfile();
    public TestClusterProfile getTestClusterProf() { return testClusterProf; }
    public void setTestClusterProf(TestClusterProfile testClusterProf) { this.testClusterProf = testClusterProf; }


    private QaClusterProfile qaClusterProf = new QaClusterProfile();
    public QaClusterProfile getQaClusterProf() { return qaClusterProf; }
    public void setQaClusterProf(QaClusterProfile qaClusterProf) { this.qaClusterProf = qaClusterProf; }

    private ProdClusterProfile prodClusterProf = new ProdClusterProfile();
    public ProdClusterProfile getProdClusterProf() { return prodClusterProf; }
    public void setProdClusterProf(ProdClusterProfile prodClusterProf) { this.prodClusterProf = prodClusterProf; }


    public ListClusterNodeFile () {

    }


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
        ListClusterNodeFile lCluster = new ListClusterNodeFile();
        lCluster.devClusterProf.generate();
        lCluster.testClusterProf.generate();
        lCluster.qaClusterProf.generate();
        lCluster.prodClusterProf.generate();
        return lCluster.toString();
    }









}
