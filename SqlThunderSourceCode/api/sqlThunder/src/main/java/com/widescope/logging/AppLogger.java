package com.widescope.logging;

import com.jcraft.jsch.JSchException;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class AppLogger {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(AppLogger.class);

    public static final String main = "Main";
    public static final String ctrl = "Controller";
    public static final String serv = "Service";
    public static final String obj = "Object";
    public static final String thread = "Thread";
    public static final String db = "Database";

    private static String justLogInfo(String log) { LOG.info(log); return log; }
    private static String justLogError(String log) { LOG.error(log); return log; }


    public static void logInfo(String className, String methodName, String objectType, String message) {
        justLogInfo(className + "." +methodName + ", " + objectType + ", message: " + message);
    }

    public static void logInfo(StackTraceElement ste, String objectType, String message) {
        justLogInfo(ste.getClassName() + "." + ste.getMethodName() + ", " + objectType + ", message: " + message);
    }

    public static String logError(String className, String methodName, String objectType, String message) {
        return justLogError(className + "." + methodName + ", " + objectType + ", message: " + message);
    }

    public static String logError(StackTraceElement ste, String objectType, String message) {
        return justLogError(ste.getClassName() + "." + ste.getMethodName() + ", " + objectType + ", message: " + message);
    }


    public static String logInfo(String className, String methodName, String objectType, String... messages) {
        String log = className + "." +methodName + ", " + objectType + ", message: " ;
        StringBuilder strLog = new StringBuilder(log);
        for (String feature : messages) {
            if (!strLog.isEmpty())
                strLog.append(", ");
            strLog.append(feature);
        }

        return justLogInfo(strLog.toString());
    }

    public static String logInfo(StackTraceElement ste, String objectType, String... messages) {
        String log = ste.getClassName() + "." + ste.getMethodName() + ", " + objectType + ", message: " ;
        StringBuilder strLog = new StringBuilder(log);
        for (String feature : messages) {
            if (!strLog.isEmpty())
                strLog.append(", ");
            strLog.append(feature);
        }
        return justLogInfo(strLog.toString());
    }

    public static String logError(String className, String methodName, String objectType, String... messages) {
        String log = className + "." + methodName + ", " + objectType + ", message: " ;
        StringBuilder strLog = new StringBuilder(log);
        for (String feature : messages) {
            if (!strLog.isEmpty())
                strLog.append(", ");
            strLog.append(feature);
        }
        return justLogError(strLog.toString());
    }

    public static String logError(StackTraceElement ste, String objectType, String... messages) {
        String log = ste.getClassName() + "." + ste.getMethodName() + ", " + objectType + ", message: " ;
        StringBuilder strLog = new StringBuilder(log);
        for (String feature : messages) {
            if (!strLog.isEmpty())
                strLog.append(", ");
            strLog.append(feature);
        }
        return justLogError(strLog.toString());
    }


    public static String logException(Exception ex, String className, String methodName, String objectType) {
        return justLogError(className + "." + methodName + ", " + objectType + ", exception: " + ex.getMessage());
    }

    public static String logException(Exception ex, StackTraceElement ste, String objectType) {
        return justLogError(ste.getClassName() + "." + ste.getMethodName() + ", " + objectType + ", exception: " + ex.getMessage());
    }

    public static String logThrowable(Throwable ex, String className, String methodName, String objectType) {
        return justLogError(className + "." + methodName + ", " + objectType + ", Fatal exception: " + ex.getMessage());
    }

    public static String logThrowable(Throwable ex, StackTraceElement ste, String objectType) {
        return justLogError(ste.getClassName() + "." + ste.getMethodName() + ", " + objectType + ", Fatal exception: " + ex.getMessage());
    }

    public static String logDb(SQLException ex, String className, String methodName) {
        return justLogError("Database, " + className + "." + methodName + ", SQL State:" + ex.getSQLState() + ", message: " + ex.getMessage());
    }

    public static String logDb(SQLException ex, StackTraceElement ste) {
        return justLogError("Database, " + ste.getClassName() + "." + ste.getMethodName() + ", SQL State:" + ex.getSQLState() + ", message: " + ex.getMessage());
    }

    public static String logTunnel(JSchException ex, String className, String methodName) {
        return justLogError("Database Tunnel, " + className + "." + methodName  + ", message: " + ex.getMessage());
    }

    public static String logTunnel(JSchException ex, StackTraceElement ste) {
        return justLogError("Database Tunnel, " + ste.getClassName() + "." + ste.getMethodName()  + ", message: " + ex.getMessage());
    }


}
