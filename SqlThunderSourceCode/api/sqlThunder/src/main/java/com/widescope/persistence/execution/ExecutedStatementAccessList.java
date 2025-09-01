package com.widescope.persistence.execution;

import java.util.ArrayList;
import java.util.List;

public class ExecutedStatementAccessList {

    private List<ExecutedStatementAccess> lst;
    public List<ExecutedStatementAccess> getLst() { return lst; }
    public void setLst(List<ExecutedStatementAccess> lst) { this.lst = lst; }
    public void setLst(final ExecutedStatementAccess a) { this.lst.add(a) ; }

    public ExecutedStatementAccessList() {
        lst = new ArrayList<>();
    }
    public ExecutedStatementAccessList(final List<ExecutedStatementAccess> lst_) {
        this.lst = lst_;
    }

}
