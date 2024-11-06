package com.widescope.sqlThunder.config;

import com.widescope.rest.GenericResponse;
import com.widescope.rest.RestObject;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Enumeration;


@Component
public class BasicAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) throws Exception {
        String uri = request.getRequestURI();
        String sessionId = request.getHeader("requestId");
        if(sessionId != null) System.out.println("#### sessionId: " + sessionId);

        if( !ConfigRepoDb.isLocalHost(request) && !ConfigRepoDb.isIpAllowed(request) ) {
            RestObject ret = generateIpError(request);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, ret.toString());
            return false;
        }

        /*No user/session headers required*/
        if(checkIfNoSessionAuthApi(uri)) { return true;  }
        /*Build-in users headers required, checking done into the end-point*/
        if(checkIfBuiltInAuthApi(uri)) { return true;  }

        /*Verify that requests have a BASIC auth account present */
        if(!checkIfAuthCompliant(request))  { return false; }
        /*Verify that requests have username and (either password for some calls or session for the rest) */
        if(!checkIfCompliant(request))  { return false; }

        /*If we got here, check if native auth is called and let it through, under the condition of presenting userName/password**/
        if(uri.equalsIgnoreCase("/sqlThunder/users/user:login")) {
            String user = request.getHeader("user");
            String userPassword = request.getHeader("password");
            return !user.trim().isEmpty() || !userPassword.trim().isEmpty();
        }

        /*This is a check for all other calls where we verify the session and make sure the user has been authenticated
         *before, prior to calling any other end-point */
        if( !isAuthenticated(request) ) {
            RestObject ret = generateBasicAuthError(request);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, ret.toString());
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
        return AuthUtil.isSessionAuthenticated_(user, session);
    }

    private static boolean checkIfNoSessionAuthApi(String uri) {
        return uri.equalsIgnoreCase("/sqlThunder/users/user:login") ||
                uri.equalsIgnoreCase("/users/mobile/user:login") ||
                uri.equalsIgnoreCase("/google/authenticate") ||
                uri.equalsIgnoreCase("/google/token") ||
                uri.equalsIgnoreCase("/google/authenticate-new") ||
                uri.equalsIgnoreCase("/okta/login") ||
                uri.equalsIgnoreCase("/sqlThunder/cluster/node/ping:pong") ||
                uri.equalsIgnoreCase("/exchange/generate:uid") ||
                uri.equalsIgnoreCase("/users/user:register")
                ;
    }


    private static boolean checkIfBuiltInAuthApi(String uri) {
        return uri.equalsIgnoreCase("/cache/keys:query") ||
                uri.equalsIgnoreCase("/cache/store:clear") ||
                uri.equalsIgnoreCase("/cluster/node/test/account:admin") ||
                uri.equalsIgnoreCase("/scripting/script/adhoc/node:run") ||
                uri.equalsIgnoreCase("/scripting/script/repo/multipart/node:run") ||
                uri.equalsIgnoreCase("/scripting/loopback/log:stdin")
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



}
