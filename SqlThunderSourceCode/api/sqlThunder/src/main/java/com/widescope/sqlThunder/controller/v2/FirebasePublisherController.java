package com.widescope.sqlThunder.controller.v2;


import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.firebase.messaging.*;
import com.widescope.sqlThunder.utils.firebase.ConditionMessageRepresentation;
import com.widescope.sqlThunder.utils.firebase.MulticastMessageRepresentation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin
@RestController
@Schema(title = "Firebase Controller")
@RequestMapping(value = "/firebase")
public class FirebasePublisherController {

    private final FirebaseMessaging fcm;

    public FirebasePublisherController(FirebaseMessaging fcm) {
        this.fcm = fcm;
    }



    @PostConstruct
    public void initialize() {

    }


    @PostMapping("/topics/{topic}")
    public ResponseEntity<String>
    postToTopic(@RequestBody String message, @PathVariable("topic") String topic) throws FirebaseMessagingException {
        Message msg = Message.builder().setTopic(topic).putData("body", message).build();
        String id = fcm.send(msg);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(id);
    }

    @PostMapping("/condition")
    public ResponseEntity<String>
    postToCondition(@RequestBody ConditionMessageRepresentation message ) throws FirebaseMessagingException {
        Message msg = Message.builder().setCondition(message.getCondition()).putData("body", message.getData()).build();
        String id = fcm.send(msg);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(id);
    }


    /*
    * Message msg = Message.builder().setToken("cfX4PTVWL0fGi5A-GGCnt7:APA91bFf2rvJGAjBlQzIxIEsBGbg7DHmh0-PrLT-F-lcIBHvXTOKUVgQvmbJvldPbXojte2JS9oTT5axMneluQGuj9sfT6E2NGq_qA3Obf7YNHgJLIXUF0L57LfrmaybZ3NSRxD-gz_G").putData("body", "test").build();
            FirebaseMessaging.getInstance(app).send(msg);
            * */
    @PostMapping("/clients/{registrationToken}")
    public ResponseEntity<String>
    postToClient(@RequestBody String message, @PathVariable("registrationToken") String registrationToken) throws FirebaseMessagingException {
        Message msg = Message.builder().setToken(registrationToken).putData("body", message).build();
        String id = fcm.send(msg);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(id);
    }

    @PostMapping("/clients")
    public ResponseEntity<List<String>>
    postToClients(@RequestBody MulticastMessageRepresentation message) throws FirebaseMessagingException {
        MulticastMessage msg = MulticastMessage.builder().addAllTokens(message.getRegistrationTokens()).putData("body", message.getData()).build();
        BatchResponse response = fcm.sendEachForMulticast(msg);
        List<String> ids = response.getResponses().stream().map(SendResponse::getMessageId).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ids);
    }

    @PostMapping("/subscriptions/{topic}")
    public ResponseEntity<Void>
    createSubscription(@PathVariable("topic") String topic,@RequestBody List<String> registrationTokens) throws FirebaseMessagingException {
        fcm.subscribeToTopic(registrationTokens, topic);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/subscriptions/{topic}/{registrationToken}")
    public ResponseEntity<Void>
    deleteSubscription(@PathVariable String topic, @PathVariable String registrationToken) throws FirebaseMessagingException {
        fcm.subscribeToTopic(Collections.singletonList(registrationToken), topic);
        return ResponseEntity.ok().build();
    }
}