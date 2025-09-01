package com.widescope.sqlThunder.utils.security;

import com.widescope.sqlThunder.utils.user.UserAuth;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpHeaders;

import java.nio.charset.StandardCharsets;


public class SpringSecurityWrapper {

    public static final String username = "Z#9cses9";
    public static final String userPassword = "AbCdE123EfR4567p9m7n9b5g7d8j1rPKyH4VtmtpC2c";

    public static final String adminName = "UBKnuk3";
    public static final String adminPassword = "Nx1ek2QpRqnCtv49JwZ6rrLK918MdrPKyH4VtmtpC2";

    /**
     * Example:
     * HttpHeaders headers = getHeaders();
     * headers.add("Authorization", "Basic " + base64ClientCredentials);
     * @return
     */
    public static HttpHeaders getAuthorizationHeaders(String user, String password){
        String plainCredentials= user + ":" + password;
        String base64Credentials = new String(Base64.encodeBase64(plainCredentials.getBytes()));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Credentials);
        return headers;
    }

    public static HttpHeaders getAuthorizationUserHeaders() {
        String plainCredentials= SpringSecurityWrapper.username + ":" + SpringSecurityWrapper.userPassword;
        String base64Credentials = new String(Base64.encodeBase64(plainCredentials.getBytes()));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Credentials);
        return headers;
    }

    public static HttpHeaders getAuthorizationAdminHeaders() {
        String plainCredentials= SpringSecurityWrapper.adminName + ":" + SpringSecurityWrapper.adminPassword;
        String base64Credentials = new String(Base64.encodeBase64(plainCredentials.getBytes()));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Credentials);
        return headers;
    }


    public static String getAuthorizationCustomUser(String user, String password){
        String plainCredentials= user + ":" + password;
        String base64Credentials = new String(Base64.encodeBase64(plainCredentials.getBytes()));
        return "Basic " + base64Credentials;
    }

    public static String getAuthorizationInternalUser() {
        String plainCredentials= SpringSecurityWrapper.username + ":" + SpringSecurityWrapper.userPassword;
        String base64Credentials = new String(Base64.encodeBase64(plainCredentials.getBytes()));
        return "Basic " + base64Credentials;
    }

    public static String getAuthorizationAdmin() {
        String plainCredentials= SpringSecurityWrapper.adminName + ":" + SpringSecurityWrapper.adminPassword;
        String base64Credentials = new String(Base64.encodeBase64(plainCredentials.getBytes()));
        return "Basic " + base64Credentials;
    }


    public static String encodeDefaultUserAuthorization() {
        String plainCredentials= SpringSecurityWrapper.username + ":" + SpringSecurityWrapper.userPassword;
        return new String(Base64.encodeBase64(plainCredentials.getBytes()));
    }

    public static String encodeUserAuthorization(final String userName, final String session) {
        String plainCredentials= userName + ":" + session;
        return new String(Base64.encodeBase64(plainCredentials.getBytes()));
    }

    /*
    import base64
    Python: emb = base64.b64decode("the string")
    * */
    public static UserAuth decodeUserAuthorization(final String basicAuth) {
        String base64Credentials = basicAuth.substring("Basic ".length());
        byte[] decodedCredentials = java.util.Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(decodedCredentials, StandardCharsets.UTF_8);
        String[] parts = credentials.split(":");
        return new UserAuth(parts[0], parts[1]);
    }


    public static String getAdminAuthorization() {
        String plainCredentials= SpringSecurityWrapper.adminName + ":" + SpringSecurityWrapper.adminPassword;
        return new String(Base64.encodeBase64(plainCredentials.getBytes()));
    }

    public static String getAuthorization(final String username, final String userPassword) {
        String plainCredentials= username + ":" + userPassword;
        return new String(Base64.encodeBase64(plainCredentials.getBytes()));
    }

}
