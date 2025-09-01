package com.widescope.persistence.execution;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.widescope.sqlThunder.rest.RestInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PersistencePrivilegeList implements RestInterface  {

    private List<PersistencePrivilege> lst;
    public List<PersistencePrivilege> getLst() { return lst; }
    public void setLst(List<PersistencePrivilege> lst) { this.lst = lst; }
    public void addItem(final PersistencePrivilege a) { this.lst.add(a) ; }

    public PersistencePrivilegeList() {
        lst = new ArrayList<>();
        lst.add(new PersistencePrivilege(PersistencePrivilege.pTypeNone, "No Privileges") );
        lst.add(new PersistencePrivilege(PersistencePrivilege.pTypeDownload, "Download Privilege") );
        lst.add(new PersistencePrivilege(PersistencePrivilege.pTypeProcess, "Process Privilege") );
        lst.add(new PersistencePrivilege(PersistencePrivilege.pTypeAdmin, "Admin Privilege") );
    }



    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public static PersistencePrivilegeList toPersistencePrivilegeList(String str) {
        Gson gson = new Gson();
        try	{
            return gson.fromJson(str, PersistencePrivilegeList.class);
        }
        catch(JsonSyntaxException ex) {
            return null;
        }
    }

}
