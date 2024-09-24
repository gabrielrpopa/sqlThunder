package com.widescope.cluster.management.miscellaneous;


import com.widescope.cluster.management.clusterManagement.ClusterDb.ClusterDb;
import com.widescope.cluster.management.clusterManagement.ClusterDb.MachineNode;
import com.widescope.cluster.management.healthCheck.PingResult;
import com.widescope.logging.AppLogger;
import com.widescope.sqlThunder.utils.FileUtilWrapper;
import java.util.List;

public class ClusterNodesWrapper {

    public static void  loadClusterNodeFile(final List<MachineNode> cluster) {
        try {
            ClusterDb clDb = ClusterDb.newClusterDb();
            List<MachineNode> allNodes = clDb.getAllNodesFromDb();
            cluster.forEach(x-> {
                try {
                    if(!allNodes.stream().map(MachineNode::getBaseUrl).toList().contains(x.getBaseUrl())) {
                        x.setIsRegistered("N"); x.setIsAccepted("N");
                        clDb.addNode(x.getBaseUrl(), x.getType(), x.getIsAccepted(), x.getIsAccepted());
                        System.out.println("Found new node:" + x.getBaseUrl());
                    }

                } catch (Exception ex) {
                    AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
                }
            });

            String fileName = "./listClusterNodeFile.json";
            if (FileUtilWrapper.isFilePresent(fileName)) {
                ListClusterNodeFile c = ListClusterNodeFile.toListClusterLoadFile( FileUtilWrapper.getFileContent(fileName) );
                assert c != null;

                c.getList().forEach(x-> {
                    try {
                        if(!c.getList().stream().map(ClusterNode::getHost).toList().contains(x.getHost())) {
                            clDb.addNode(x.getHost(), x.getType(), "N", "N");
                            System.out.println("Found new node:" + x.getHost());
                        }
                    } catch (Exception ex) {
                        AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
                    }
                });
            }

        } catch(Exception ex) {
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
        }
    }


    public static void  addClusterNodes(final List<PingResult> pingClusterHttp) {
        try {
            ClusterDb clDb = new ClusterDb();
            pingClusterHttp.forEach(x-> {
                try {
                    clDb.addNode(x.getBaseUrl(), "SHARD", "Y", "Y");
                } catch (Exception ex) {
                    AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
                }
            });
        } catch(Exception ex) {
            AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj);
        }
    }
}
