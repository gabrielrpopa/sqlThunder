package com.widescope.sqlThunder.utils.firebase;

import com.google.firebase.messaging.*;
import org.springframework.http.ResponseEntity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseWrapper {
    public static void sendToToken(String deviceToken, String message)  {
        Message msg = Message.builder().setToken(deviceToken).putData("body", message).build();
        try {
            String response = String.valueOf(FirebaseMessaging.getInstance().send(msg));
            System.out.println(response);
        } catch(FirebaseMessagingException ex) {
            System.out.println("Error Sending message: " + ex.getMessage() + " Error Code: " + ex.getErrorCode());
            System.out.println("Detail: " + ex.getHttpResponse().getContent());
        }
    }

    public static void sendToToken(String deviceToken, String message, FirebaseMessaging fcm)  {
        try {
            Message msg = Message.builder().setToken(deviceToken).putData("body", message).build();
            String response = fcm.send(msg);
            ResponseEntity.status(org.springframework.http.HttpStatus.ACCEPTED).body(response);
            System.out.println("Successfully sent message: " + response);
        } catch(FirebaseMessagingException ex) {
            // If you receive a 401 HTTP status code, your Server key is not valid.
            System.out.println("Error Sending message: " + ex.getMessage() + " Error Code: " + ex.getErrorCode());
            System.out.println("Detail: " + ex.getHttpResponse().getContent());
        }
    }

}
