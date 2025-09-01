package com.widescope.persistence.execution;

public class ExecutedStatementFlag {

    public static int operationSuccess = 0;
    public static int notAllowedAddStatement = 1;
    public static int errorAddStatement = 2;
    public static int notAllowedDeleteStatement = 3;
    public static int errorDeleteStatement = 4;
    public static int notAllowedAddAssociatedUser = 5;
    public static int errorAddAssociatedUser = 6;
    public static int notAllowedDeleteAssociatedUser = 7;
    public static int errorDeleteAssociatedUser = 8;
    public static int notAllowedDeleteOutput = 9;
    public static int operationIgnored = 10;


    public static int executionRecordDeleted = 80;
    public static int accessRecordDeleted = 81;

    public static int unknownError = 999;
}
