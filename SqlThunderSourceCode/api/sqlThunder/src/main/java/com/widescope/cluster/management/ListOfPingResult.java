package com.widescope.cluster.management;

import com.google.gson.Gson;
import com.widescope.cluster.management.healthCheck.PingResult;
import com.widescope.rest.RestInterface;

import java.util.List;

public class ListOfPingResult implements RestInterface {
    private List<PingResult> listResult;
    public ListOfPingResult(final List<PingResult> listResult) {
        this.listResult = listResult;
    }

    public List<PingResult> getListResult() {
        return listResult;
    }

    public void setListResult(List<PingResult> listResult) {
        this.listResult = listResult;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
