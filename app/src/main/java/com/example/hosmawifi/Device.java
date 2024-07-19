package com.example.hosmawifi;

import java.util.UUID;

public class Device {

    private final UUID id = UUID.randomUUID();

    private String subscribeTopic = "test";
    private String publishTopic = "test";

    private int viewType = 0;

    private String messageToTurnOn = "1";
    private String messageToTurnOff = "0";

    private int deviceModeOnDrawable = R.drawable.baseline_question_mark;
    private int deviceModeOffDrawable = R.drawable.baseline_question_mark;

    private String deviceName = null;


    public Device(String publishTopic, String messageToTurnOn, String messageToTurnOff, int deviceModeOnDrawable, int deviceModeOffDrawable, String deviceName) {
        this.viewType = 0;
        this.publishTopic = publishTopic;
        this.messageToTurnOn = messageToTurnOn;
        this.messageToTurnOff = messageToTurnOff;
        this.deviceModeOnDrawable = deviceModeOnDrawable;
        this.deviceModeOffDrawable = deviceModeOffDrawable;
        this.deviceName = deviceName;
    }

    public Device(String subscribeTopic, int deviceModeOffDrawable, String deviceName) {
        this.viewType = 1;
        this.subscribeTopic = subscribeTopic;
        this.deviceModeOffDrawable = deviceModeOffDrawable;
        this.deviceName = deviceName;
    }

    public Device(String subscribeTopic, String publishTopic, String messageToTurnOn, String messageToTurnOff, int deviceModeOnDrawable, int deviceModeOffDrawable, String deviceName) {
        this.viewType = 2;
        this.subscribeTopic = subscribeTopic;
        this.publishTopic = publishTopic;
        this.messageToTurnOn = messageToTurnOn;
        this.messageToTurnOff = messageToTurnOff;
        this.deviceModeOnDrawable = deviceModeOnDrawable;
        this.deviceModeOffDrawable = deviceModeOffDrawable;
        this.deviceName = deviceName;
    }

    public UUID getId() {
        return id;
    }

    public String getPublishTopic() {
        return publishTopic;
    }

    public String getSubscribeTopic() {
        return subscribeTopic;
    }

    public int getViewType() {
        return viewType;
    }

    public String getMessageToTurnOn() {
        return messageToTurnOn;
    }

    public String getMessageToTurnOff() {
        return messageToTurnOff;
    }

    public int getDeviceModeOnDrawable() {
        return deviceModeOnDrawable;
    }

    public int getDeviceModeOffDrawable() {
        return deviceModeOffDrawable;
    }

    public String getDeviceName() {
        return deviceName;
    }
}