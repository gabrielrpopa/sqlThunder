package com.widescope.sqlThunder.controller.v2;


import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.firebase.messaging.*;
import com.widescope.sqlThunder.utils.firebase.ConditionMessageRepresentation;
import com.widescope.sqlThunder.utils.firebase.MulticastMessageRepresentation;
import io.swagger.v3.oas.annotations.Operation;
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



    @CrossOrigin(origins = "*")
    @PostMapping("/topics/{topic}")
    @Operation(summary = "Send message to firebase topic")
    public ResponseEntity<String>
    postToTopic(@RequestBody final String message,
                @PathVariable("topic") final String topic) throws FirebaseMessagingException {
        Message msg = Message.builder().setTopic(topic).putData("body", message).build();
        String id = fcm.send(msg);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(id);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/condition")
    @Operation(summary = "Send message to firebase queues enforced by condition")
    public ResponseEntity<String>
    postToCondition(@RequestBody final ConditionMessageRepresentation message ) throws FirebaseMessagingException {
        Message msg = Message.builder().setCondition(message.getCondition()).putData("body", message.getData()).build();
        String id = fcm.send(msg);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(id);
    }


    @CrossOrigin(origins = "*")
    @PostMapping("/clients/{registrationToken}")
    @Operation(summary = "Send message to a subscribing device")
    public ResponseEntity<String>
    postToClient(@RequestBody final String message,
                 @PathVariable("registrationToken") final String registrationToken) throws FirebaseMessagingException {
        Message msg = Message.builder().setToken(registrationToken).putData("body", message).build();
        String id = fcm.send(msg);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(id);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/clients")
    @Operation(summary = "Send message to a subscribing list of device")
    public ResponseEntity<List<String>>
    postToClients(@RequestBody final MulticastMessageRepresentation message) throws FirebaseMessagingException {
        MulticastMessage msg = MulticastMessage.builder().addAllTokens(message.getRegistrationTokens()).putData("body", message.getData()).build();
        BatchResponse response = fcm.sendEachForMulticast(msg);
        List<String> ids = response.getResponses().stream().map(SendResponse::getMessageId).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ids);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/subscriptions/{topic}")
    @Operation(summary = "Create subscription for a device")
    public ResponseEntity<Void>
    createSubscription(@PathVariable("topic") final String topic,
                       @RequestBody final List<String> registrationTokens) throws FirebaseMessagingException {
        fcm.subscribeToTopic(registrationTokens, topic);
        return ResponseEntity.ok().build();
    }

    @CrossOrigin(origins = "*")
    @DeleteMapping("/subscriptions/{topic}/{registrationToken}")
    @Operation(summary = "Delete subscription for a device")
    public ResponseEntity<Void>
    deleteSubscription(@PathVariable final String topic,
                       @PathVariable final String registrationToken) throws FirebaseMessagingException {
        fcm.subscribeToTopic(Collections.singletonList(registrationToken), topic);
        return ResponseEntity.ok().build();
    }
}