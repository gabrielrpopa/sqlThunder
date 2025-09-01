package com.widescope.cluster.management.miscellaneous;

import com.google.gson.Gson;
import com.widescope.sqlThunder.utils.FileUtilWrapper;

import java.util.ArrayList;
import java.util.List;

public class ClusterProfile {

    private List<ClusterNode> activeList = new ArrayList<>();
    public List<ClusterNode> getActiveList() { return activeList; }
    public void setActiveList(final List<ClusterNode> list) { this.activeList = list; }

    private List<ClusterNode> ignoreList = new ArrayList<>();
    public List<ClusterNode> getIgnoreList() { return ignoreList; }
    public void setIgnoreList(final List<ClusterNode> list) { this.ignoreList = list; }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public static ClusterProfile getConfigFile(String environmentProfile) throws Exception {
        String fileName = "./listClusterNodeFile.json";
        ClusterProfile configFileNodes = new ClusterProfile();

        if (!FileUtilWrapper.isFilePresent(fileName)) {
            String listClusterNodeFile = ListClusterNodeFile.generate();
            FileUtilWrapper.writeFile(fileName, listClusterNodeFile);
        } else {
            ListClusterNodeFile listClusterNode = ListClusterNodeFile.toListClusterLoadFile( FileUtilWrapper.getFileContent(fileName) );
            assert listClusterNode != null;
            if(environmentProfile.compareToIgnoreCase("DEV") == 0) {
                configFileNodes = listClusterNode.getDevClusterProf();
            } else if(environmentProfile.compareToIgnoreCase("TEST") == 0) {
                configFileNodes = listClusterNode.getTestClusterProf();
            } else if(environmentProfile.compareToIgnoreCase("QA") == 0) {
                configFileNodes = listClusterNode.getQaClusterProf();
            } else if(environmentProfile.compareToIgnoreCase("PROD") == 0) {
                configFileNodes = listClusterNode.getProdClusterProf();
            }
        }

        return configFileNodes;
    }



}
