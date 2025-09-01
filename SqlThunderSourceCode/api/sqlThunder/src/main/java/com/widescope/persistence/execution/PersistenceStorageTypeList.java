package com.widescope.persistence.execution;

import com.widescope.sqlThunder.rest.RestInterface;

import java.util.ArrayList;
import java.util.List;

public class PersistenceStorageTypeList implements RestInterface {

    private List<PersistenceStorageType> lst;
    public List<PersistenceStorageType> getLst() { return lst; }
    public void setLst(List<PersistenceStorageType> lst) { this.lst = lst; }
    public void addItem(final PersistenceStorageType a) { this.lst.add(a) ; }

    public PersistenceStorageTypeList() {
        lst = new ArrayList<>();
    }
    public PersistenceStorageTypeList(final List<PersistenceStorageType> lst_) {
        this.lst = lst_;
    }

}
