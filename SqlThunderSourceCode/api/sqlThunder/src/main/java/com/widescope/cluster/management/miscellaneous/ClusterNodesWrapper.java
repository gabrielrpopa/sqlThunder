package com.widescope.cluster.management.miscellaneous;


import com.widescope.cluster.management.clusterManagement.ClusterDb.ClusterDb;
import com.widescope.cluster.management.clusterManagement.ClusterDb.MachineNode;
import com.widescope.cluster.management.healthCheck.SystemInfo;
import com.widescope.logging.AppLogger;
import com.widescope.sqlThunder.utils.restApiClient.RestApiCluster;

import java.util.ArrayList;
import java.util.List;

public class ClusterNodesWrapper {

    public static List<MachineNode> loadClusterNodeFile(String environmentProfile) {
        List<MachineNode> addedNodes = new ArrayList<>();
        try {
            ClusterDb clDb = ClusterDb.newClusterDb();
            ClusterProfile configFileNodes = ClusterProfile.getConfigFile(environmentProfile);
            removeIgnoredNodes(clDb, configFileNodes);
            addedNodes = addNodes(clDb, configFileNodes);

        } catch(Exception ex) {
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
        }
        return addedNodes;
    }

    private static void removeIgnoredNodes(ClusterDb clDb, ClusterProfile configFileNodes) {
        configFileNodes.getIgnoreList().forEach(x-> {
            try {
                if(!configFileNodes.getIgnoreList().stream().map(ClusterNode::getBaseUrl).toList().contains(x.getBaseUrl())) {
                    clDb.deleteNode(x.getBaseUrl());
                    System.out.println("Deleted ignored node:" + x.getBaseUrl());
                }
            } catch (Exception ex) {
                AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
            }
        });
    }



    private static List<MachineNode>
    addNodes(ClusterDb clDb, ClusterProfile configFileNodes) {
        List<MachineNode> ret = new ArrayList<>();
        configFileNodes.getActiveList().forEach(x-> {
            try {
                if(!configFileNodes.getActiveList().stream().map(ClusterNode::getBaseUrl).toList().contains(x.getBaseUrl())) {
                    String pong = RestApiCluster.ping(x.getBaseUrl());
                    String isRegistered = "N";
                    String isAccepted = "Y";
                    MachineNode mn;
                    if(pong != null && pong.equals("PONG")) {
                        isRegistered = "Y";
                        mn = SystemInfo.getHealthCheck(x.getBaseUrl(), x.getType());
                    } else {
                        mn = new MachineNode(-1, x.getBaseUrl(), x.getType(), isAccepted, isRegistered, "N");
                    }
                    clDb.addNode(x.getBaseUrl(), x.getType(), isAccepted, isRegistered);
                    ret.add(mn);
                }
            } catch (Exception ex) {
                AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
            }
        });

        return ret;
    }

}
