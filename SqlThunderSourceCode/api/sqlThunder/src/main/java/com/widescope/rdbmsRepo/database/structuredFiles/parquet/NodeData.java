package com.widescope.rdbmsRepo.database.structuredFiles.parquet;


public class NodeData {
	public int id;

    /** Prediction. */
    public double prediction;

    /** Left child id. */
    public int leftChildId;

    /** Right child id. */
    public int rightChildId;

    /** Threshold. */
    public double threshold;

    /** Feature index. */
    public int featureIdx;

    /** Is leaf node. */
    public boolean isLeafNode;
    
    
    
    
}
