package com.widescope.scripting.execution;

import com.widescope.scripting.ScriptingHelper;
import com.widescope.sqlThunder.config.AppConstants;

public class ScriptExecutionUtils {

    public static String interpolateVars(final String user,
                                         final String session,
                                         final String auth,
                                         final String requestId,
                                         String scriptContent,
                                         final String updateGlobalVars,
                                         AppConstants appConstants) {

        if(scriptContent.toLowerCase().contains(updateGlobalVars.toLowerCase())) {
            scriptContent = ScriptingHelper.replaceScriptUserSession(	user,
                    session,
                    auth,
                    appConstants.getServerSslEnabled() ? "https":"http",
                    appConstants.getServerPort(),
                    requestId,
                    scriptContent);
        } else {
            scriptContent = ScriptingHelper.addLibraryGlobalParam(	auth,
                    user,
                    session,
                    appConstants.getAdmin(),
                    appConstants.getAdminPasscode(),
                    appConstants.getServerSslEnabled() ? "https":"http",
                    appConstants.getServerPort()  );
        }

        return scriptContent;
    }

}
