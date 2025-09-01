package com.widescope.persistence.execution;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.widescope.sqlThunder.config.configRepo.Constants;
import com.widescope.sqlThunder.rest.RestInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PersistenceSourceList implements RestInterface {

    public
    static
    List<String> sourceList = Arrays.asList(new String[] { Constants.adhocShort, Constants.repoShort});

    public static boolean isSource(String p) {
        return PersistenceSourceList.sourceList.contains(p.toUpperCase());
    }
    public static String genCommaSeparatedSourceList() {
        return PersistenceSourceList.sourceList.stream().collect(Collectors.joining("', '", "'", "'"));
    }

    public PersistenceSourceList() {
        lst = new ArrayList<>();
        lst.add(new PersistenceSource(Constants.adhocShort, "Adhoc Backup"));
        lst.add(new PersistenceSource(Constants.repoShort, "Repo Backup"));
    }

    private List<PersistenceSource> lst;
    public List<PersistenceSource> getLst() { return lst; }
    public void setLst(List<PersistenceSource> lst) { this.lst = lst; }
    public void addItem(final PersistenceSource a) { this.lst.add(a) ; }



    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public static PersistenceSourceList toPersistenceSourceList(String str) {
        Gson gson = new Gson();
        try	{
            return gson.fromJson(str, PersistenceSourceList.class);
        }
        catch(JsonSyntaxException ex) {
            return null;
        }
    }


}
