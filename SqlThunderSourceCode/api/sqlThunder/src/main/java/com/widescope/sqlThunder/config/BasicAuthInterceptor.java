package com.widescope.sqlThunder.config;

import com.widescope.sqlThunder.rest.GenericResponse;
import com.widescope.sqlThunder.rest.RestObject;
import com.widescope.sqlThunder.config.configRepo.ConfigRepoDb;
import com.widescope.sqlThunder.objects.commonObjects.globals.ErrorCode;
import com.widescope.sqlThunder.objects.commonObjects.globals.ErrorSeverity;
import com.widescope.sqlThunder.objects.commonObjects.globals.Sources;
import com.widescope.sqlThunder.utils.StaticUtils;
import com.widescope.sqlThunder.utils.security.SpringSecurityWrapper;
import com.widescope.sqlThunder.utils.user.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;


@Component
public class BasicAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) throws Exception {



        response.setHeader("preHandleFlag", "GREEN");
        String uri = request.getRequestURI();

        if( !ConfigRepoDb.isLocalHost(request) && !ConfigRepoDb.isIpAllowed(request) ) {
            response.setHeader("preHandleFlag", "RED");
            return false;
        }


        if( uri.equalsIgnoreCase("/SqlThunder/elastic-repo/history/stm:copy") ) {

            Enumeration<String> h = request.getHeaderNames();
            //String payload = getPayload(request);
            System.out.println(h);
        }


        /*No user/session headers required*/
        if(checkIfNoSessionAuthApi(uri)) { return true; }
        /*Build-in users headers required, checking done into the end-point*/
        if(checkIfBuiltInAuthApi(uri)) { return true; }

        /*Verify that requests have a BASIC auth account present */
        if(!checkIfAuthCompliant(request))  {
            response.setHeader("preHandleFlag", "RED");
            return false;
        }

        /*Verify that requests have username and (either password for some calls or session for the rest) */
        if(!checkIfCompliant(request))  {
            System.out.println(uri);
            response.setHeader("preHandleFlag", "RED");
            return false;
        }

        /*If we got here, check if native auth is called and let it through, under the condition of presenting userName/password**/
        if(uri.equalsIgnoreCase("/sqlThunder/users/user:login") ||
                uri.equalsIgnoreCase("/sqlThunder/users/mobile/user:login")) {

            String user = request.getHeader("user");
            String userPassword = request.getHeader("password");
            boolean isGreen = !user.trim().isEmpty() || !userPassword.trim().isEmpty();
            if(!isGreen) {
                response.setHeader("preHandleFlag", "RED");
            }
            return isGreen;
        }

        /*This is a check for all other calls where we verify the session and make sure the user has been authenticated
         *before, prior to calling any other end-point */
        if( !isAuthenticated(request) ) {
            response.setHeader("preHandleFlag", "RED");
            return false;
        }

        return true;

    }
    @Override
    public void postHandle(@NotNull HttpServletRequest request,
                           @NotNull HttpServletResponse response,
                           @NotNull Object handler,
                           ModelAndView modelAndView)  {
        response.setHeader("requestId", request.getHeader("requestId"));
    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request,
                                @NotNull HttpServletResponse response,
                                @NotNull Object handler,
                                Exception exception)  {

    }




    private RestObject generateIpError(HttpServletRequest request) {
        String requestId = request.getHeader("requestId");
        if(requestId == null) requestId = StaticUtils.getUUID();
        return new RestObject(	new GenericResponse("FORBIDDEN"),
                                                            requestId,
                                                "Forbidden IP",
                                                "",
                                                             ErrorCode.ERROR,
                                                             Sources.SQLTHUNDER,
                                                             ErrorSeverity.HIGH,
                                                             "");
    }

    private RestObject generateBasicAuthError(HttpServletRequest request) {
        String requestId = request.getHeader("requestId");
        if(requestId == null) requestId = StaticUtils.getUUID();
        return new RestObject(	new GenericResponse("FORBIDDEN"),
                                                    requestId,
                                                    "NO_BASIC_AUTH_PROVIDED",
                                                    "",
                                                    ErrorCode.ERROR,
                                                    Sources.SQLTHUNDER,
                                                    ErrorSeverity.HIGH,
                                                    "");
    }


    private static boolean checkIfCompliant(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String user = request.getHeader("user");
        if(checkIfNoSessionAuthApi(uri)){
            /*Allow native login, Google auth and Okta auth pass-through*/
            String userPassword = request.getHeader("password");
            return user != null && !user.isEmpty() && userPassword != null && !userPassword.isEmpty();
        } else {
            /*all other calls must have been already authenticated, and present a valid session, now only check for null*/
            String session = request.getHeader("session");
            return user != null && !user.isEmpty() && session != null && !session.isEmpty();
        }
    }

    private static boolean checkIfAuthCompliant(HttpServletRequest request) {
        String authHeader = request.getHeader("authorization");
        String uri = request.getRequestURI();
        if (authHeader != null && authHeader.startsWith("Basic ")) {
            String base64Credentials = authHeader.substring("Basic ".length());
            byte[] decodedCredentials = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(decodedCredentials, StandardCharsets.UTF_8);
            String[] parts = credentials.split(":");
            String username = parts[0];
            String password = parts[1];
            return username.equals(SpringSecurityWrapper.username) && password.equals(SpringSecurityWrapper.userPassword)
                    || (username.equals(SpringSecurityWrapper.adminName) && password.equals(SpringSecurityWrapper.adminPassword));
        } else {
            return false;
        }
    }


    private static boolean isAuthenticated(HttpServletRequest request) {
        String user = request.getHeader("user");
        String session = request.getHeader("session");
        String uri = request.getRequestURI();
        return AuthUtil.isSessionAuthenticated_(user, session, uri);
    }

    private static boolean checkIfNoSessionAuthApi(String uri) {
        return uri.equalsIgnoreCase("/sqlThunder/users/user:login") ||
                uri.equalsIgnoreCase("/sqlThunder/users/mobile/user:login") ||
                uri.equalsIgnoreCase("/sqlThunder/client:ip") ||
                uri.equalsIgnoreCase("/sqlThunder/environment:about") ||
                uri.equalsIgnoreCase("/sqlThunder/google/authenticate") ||
                uri.equalsIgnoreCase("/sqlThunder/google/token") ||
                uri.equalsIgnoreCase("/sqlThunder/google/authenticate-new") ||
                uri.equalsIgnoreCase("/sqlThunder/okta/login") ||
                uri.equalsIgnoreCase("/sqlThunder/cluster/node/ping:pong") ||
                uri.equalsIgnoreCase("/sqlThunder/exchange/generate:uid") ||
                uri.equalsIgnoreCase("/sqlThunder/users/user:register") ||
                uri.equalsIgnoreCase("/sqlThunder/heartbeat") ||
                uri.equalsIgnoreCase("/sqlThunder/timer") ||
                uri.equalsIgnoreCase("/sqlThunder/push/user/queue/multipart") ||
                uri.equalsIgnoreCase("/sqlThunder/push/user/queue/update") ||
                uri.equalsIgnoreCase("/sqlThunder/user/queue/register") ||
                uri.equalsIgnoreCase("/sqlThunder/user/queue/control");
    }


    private static boolean checkIfBuiltInAuthApi(String uri) {
        return uri.equalsIgnoreCase("/sqlThunder/cache/keys:query") ||
                uri.equalsIgnoreCase("/sqlThunder/cache/store:clear") ||
                uri.equalsIgnoreCase("/sqlThunder/cluster/node/test/account:admin") ||
                uri.equalsIgnoreCase("/sqlThunder/cluster/node/test/account:user") ||
                uri.equalsIgnoreCase("/sqlThunder/config/node/test/account:admin") ||
                uri.equalsIgnoreCase("/sqlThunder/config/node/test/account:user") ||
                uri.equalsIgnoreCase("/sqlThunder/scripting/script/adhoc/node:run") ||
                uri.equalsIgnoreCase("/sqlThunder/scripting/script/repo/multipart/node:run") ||
                uri.equalsIgnoreCase("/sqlThunder/scripting/script/adhoc/node:sink") ||
                uri.equalsIgnoreCase("/sqlThunder/scripting/loopback/log:stdin")
                ;
    }


    /*Used for debug purposes*/
    private void printRequest(HttpServletRequest request) {
        System.out.println("#####################");
        System.out.println("URI: " + request.getRequestURI());
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                System.out.println("Header: " + request.getHeader(headerNames.nextElement()));
            }
        }
        System.out.println("#####################");
    }


    private String getPayload(HttpServletRequest request) throws IOException {
        StringBuilder payload = new StringBuilder();
        try(BufferedReader reader = request.getReader()){
            String line;
            while ((line = reader.readLine()) != null){
                payload.append(line);
            }
        }
        return payload.toString();
    }


}
