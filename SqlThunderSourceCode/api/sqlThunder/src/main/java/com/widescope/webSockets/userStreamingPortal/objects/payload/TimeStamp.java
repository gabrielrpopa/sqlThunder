package com.widescope.webSockets.userStreamingPortal.objects.payload;

import com.widescope.sqlThunder.utils.DateTimeUtils;
import com.widescope.sqlThunder.utils.StringUtils;

public class TimeStamp {
    private long t;
    public TimeStamp() {
        this.t = DateTimeUtils.millisecondsSinceEpoch();
    }
    public long getT() {
        return t;
    }
    public void setT(long t) {
        this.t = t;
    }
}
