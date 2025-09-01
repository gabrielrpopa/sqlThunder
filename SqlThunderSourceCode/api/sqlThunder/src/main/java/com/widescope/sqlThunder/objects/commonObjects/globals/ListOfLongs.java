package com.widescope.sqlThunder.objects.commonObjects.globals;

import com.widescope.sqlThunder.rest.RestInterface;

import java.util.ArrayList;
import java.util.List;

public class ListOfLongs implements RestInterface  {
    private List<Long> lst;
    public List<Long> getLst() { return lst; }
    public void setLst(List<Long> lst) { this.lst = lst; }
    public ListOfLongs() {
        this.lst = new ArrayList<>();
    }
    public ListOfLongs(List<Long> lst_) {
        this.lst = lst_;
    }
    public void addLong(Long e) { this.lst.add(e); }
}
