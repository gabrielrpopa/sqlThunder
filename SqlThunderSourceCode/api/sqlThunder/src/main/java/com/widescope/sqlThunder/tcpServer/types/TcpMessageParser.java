package com.widescope.sqlThunder.tcpServer.types;

public class TcpMessageParser {

    public static JSONResponse getMessage(final JSONRequest message) {

        if (message.getT().equals(TcpMessageType.TcpTypeLogin)) {
            JSONError error = JSONError.makeJSONError(0, "");
            return JSONResponse.genericErrorMessage(error);
        } else if (message.getT().equals(TcpMessageType.TcpTypeLogout)) {
            JSONError error = JSONError.makeJSONError(0, "");
            return JSONResponse.genericErrorMessage(error);
        }

        JSONError error = JSONError.makeJSONError(0, "");
        return JSONResponse.genericErrorMessage(error);
   }
}
