package com.widescope.persistence.execution;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.widescope.sqlThunder.rest.RestInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AccessRefPrivilegeList implements RestInterface  {



    private List<AccessRefPrivilege> lst;
    public List<AccessRefPrivilege> getLst() { return lst; }
    public void setLst(List<AccessRefPrivilege> lst) { this.lst = lst; }
    public void addItem(final AccessRefPrivilege a) { this.lst.add(a) ; }

    public AccessRefPrivilegeList() {

    }



    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public static AccessRefPrivilegeList toAccessRefPrivilegeList(String str) {
        Gson gson = new Gson();
        try	{
            return gson.fromJson(str, AccessRefPrivilegeList.class);
        }
        catch(JsonSyntaxException ex) {
            return null;
        }
    }

}
