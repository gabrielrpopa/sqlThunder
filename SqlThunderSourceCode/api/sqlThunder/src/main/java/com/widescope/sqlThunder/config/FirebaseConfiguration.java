package com.widescope.sqlThunder.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.google.firebase.IncomingHttpResponse;
import com.google.firebase.auth.AuthErrorCode;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.messaging.*;
import com.widescope.logging.AppLogger;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.core.io.ClassPathResource;

@Configuration
@EnableConfigurationProperties(FirebaseProperties.class)
public class FirebaseConfiguration {

    private final FirebaseProperties firebaseProperties;

    public static FirebaseMessaging fbm;

    public FirebaseConfiguration(FirebaseProperties firebaseProperties) {
        this.firebaseProperties = firebaseProperties;
    }

    @Bean
    GoogleCredentials googleCredentials() throws Exception {

        try {
            if (firebaseProperties.getServiceAccount() != null) {
                try( InputStream is = firebaseProperties.getServiceAccount().getInputStream()) {
                    return GoogleCredentials.fromStream(is);
                }
            }
            else {
                return GoogleCredentials.getApplicationDefault();
            }
        }
        catch (IOException ex) {
            String errorMessage = "#### Please update file firebase-service-account.json with your owm key ";
            throw new Exception(AppLogger.logError(Thread.currentThread().getStackTrace()[1].getClassName(), Thread.currentThread().getStackTrace()[1].getMethodName(), AppLogger.obj, errorMessage + ", "+ ex.getMessage())) ;
        }
    }

    @Bean
    FirebaseApp firebaseApp(GoogleCredentials credentials) throws Exception {
        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(new ClassPathResource("firebase-service-account.json").getInputStream());
        FirebaseOptions options = FirebaseOptions.builder().setCredentials(googleCredentials).build();
        try {
            return FirebaseApp.initializeApp(options);
        } catch(Exception  ex) {
            throw new Exception(AppLogger.logException(ex, Thread.currentThread().getStackTrace()[1], AppLogger.obj)) ;
        }
    }

    @Bean
    FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }

}
