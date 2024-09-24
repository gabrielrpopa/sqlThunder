package com.widescope.sqlThunder.tcpServer.types;

public class TcpMessageType {

    public static String jsonrpc = "2.0";


    public static String TcpTypeLogin = "001"; /*NO further definitions in SubType, this is ultimate check*/
    public static String TcpTypeLogout = "002"; /*NO further definitions in SubType, this is ultimate check*/

    public static String TcpTypeRestart = "003"; /*NO further definitions in SubType, this is ultimate check*/
    public static String TcpShutdown = "004"; /*NO further definitions in SubType, this is ultimate check*/

    public static String TcpControl = "004";  /*See further definitions in SubType*/

    public static String TcpTypePing = "008"; /*NO further definitions in SubType, this is ultimate check*/
    public static String TcpTypePong = "009"; /*NO further definitions in SubType, this is ultimate check*/



    public static String TcpTypeAudioStream = "101"; /*See further definitions in SubType*/
    public static String TcpTypeVideoStream = "102"; /*See further definitions in SubType*/
    public static String TcpTypeBinaryStream = "103"; /*See further definitions in SubType*/
    public static String TcpTypeText = "104"; /*See further definitions in SubType*/
    public static String TcpTypeJson = "105";   /*See further definitions in SubType*/

    public static String TcpTypeChatMessage = "201"; /*See further definitions in SubType*/

    public static String TcpTypeError = "999"; /*See further definitions in SubType*/
}
