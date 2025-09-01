package com.widescope.persistence.execution;

import com.widescope.sqlThunder.rest.RestInterface;

import java.util.ArrayList;
import java.util.List;

public class PersistenceGroupList implements RestInterface  {

    private List<PersistenceGroup> lst;
    public List<PersistenceGroup> getLst() { return lst; }
    public void setLst(List<PersistenceGroup> lst) { this.lst = lst; }
    public void addItem(final PersistenceGroup a) { this.lst.add(a) ; }

    public PersistenceGroupList() {
        lst = new ArrayList<>();
    }
    public PersistenceGroupList(final List<PersistenceGroup> lst_) {
        this.lst = lst_;
    }

}
