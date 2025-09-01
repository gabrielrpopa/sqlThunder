package com.widescope.sqlThunder.objects.commonObjects.globals;

import com.widescope.sqlThunder.rest.RestInterface;

import java.util.ArrayList;
import java.util.List;

public class ListOfInts implements RestInterface  {
    private List<Integer> lst;
    public List<Integer> getLst() { return lst; }
    public void setLst(List<Integer> lst) { this.lst = lst; }
    public ListOfInts() {
        this.lst = new ArrayList<>();
    }
    public ListOfInts(List<Integer> lst_) {
        this.lst = lst_;
    }
    public void addInteger(Integer e) { this.lst.add(e); }
}
