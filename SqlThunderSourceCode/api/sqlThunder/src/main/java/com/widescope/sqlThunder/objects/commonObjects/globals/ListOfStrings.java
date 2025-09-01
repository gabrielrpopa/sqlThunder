package com.widescope.sqlThunder.objects.commonObjects.globals;

import com.widescope.sqlThunder.rest.RestInterface;

import java.util.ArrayList;
import java.util.List;

public class ListOfStrings implements RestInterface {
    private List<String> lst;
    public List<String> getLst() { return lst; }
    public void setLst(List<String> lst) { this.lst = lst; }
    public ListOfStrings() { this.lst = new ArrayList<>(); }
    public ListOfStrings(List<String> lst_) { this.lst = lst_; }
    public void addString(String e) { this.lst.add(e); }
}
