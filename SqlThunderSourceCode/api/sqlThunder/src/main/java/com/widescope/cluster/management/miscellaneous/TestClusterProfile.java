package com.widescope.cluster.management.miscellaneous;

public class TestClusterProfile extends ClusterProfile {


    public void generate() {
        this.getActiveList().add(new ClusterNode("http://fake1:9099/sqlThunder", "EXECUTOR"));
        this.getActiveList().add(new ClusterNode("http://fake1:9099/sqlThunder", "EXECUTOR"));
        this.getIgnoreList().add(new ClusterNode("http://fake1:9099/sqlThunder", ""));
    }
}
