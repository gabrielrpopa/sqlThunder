package com.widescope.sqlThunder.utils.okta;

import com.okta.commons.lang.Assert;
import com.okta.idx.sdk.api.response.TokenResponse;
import com.widescope.logging.AppLogger;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.okta.idx.sdk.api.util.ClientUtil.normalizedIssuerUri;




@Component
public class HomeHelper {

    private final static String className = Thread.currentThread().getStackTrace()[1].getClassName();

    public ModelAndView proceedToHome(	final TokenResponse tokenResponse, 
    									final HttpSession session,
    									final String issuer) {

    	RestTemplate restTemplate = new RestTemplate();
    	
        Map<String, String> claims = new LinkedHashMap<>();

        // success
        ModelAndView mav = new ModelAndView("home");
        mav.addObject("tokenResponse", tokenResponse);

        String user = null;

        try {
            // get user claim info from /v1/userinfo endpoint
            String userInfoUrl = normalizedIssuerUri(issuer, "/v1/userinfo");

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setBearerAuth(tokenResponse.getAccessToken());

            HttpEntity<String> requestEntity = new HttpEntity<>(null, httpHeaders);

            ParameterizedTypeReference<Map<String, String>> responseType =
                    new ParameterizedTypeReference<Map<String, String>>() { };
            ResponseEntity<Map<String, String>> responseEntity =
                    restTemplate.exchange(userInfoUrl, HttpMethod.GET, requestEntity, responseType);

            claims = responseEntity.getBody();
            Assert.notNull(claims, "claims cannot be null");
            user = claims.get("preferred_username");
        } catch (Exception e) {
            AppLogger.logException(e, className, Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj);
        }
        mav.addObject("user", user);
        mav.addObject("claims", claims);

        // store token in session
        session.setAttribute("tokenResponse", tokenResponse);

        return mav;
    }
    
}