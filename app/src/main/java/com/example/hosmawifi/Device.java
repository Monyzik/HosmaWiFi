package com.example.hosmawifi;

import java.util.UUID;

public class Device {

    private static final UUID id = UUID.randomUUID();

    private final int DEVICE_MODE_ON_DRAWABLE = R.drawable.baseline_question_mark;
    private final int DEVICE_MODE_OFF_DRAWABLE = R.drawable.baseline_question_mark;


    protected String deviceName;

    public Device(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getMessageToTurnOff() {
        return "0";
    }

    public String getMessageToTurnOn() {
        return "1";
    }

    public int getDeviceModeOnDrawable() {
        return DEVICE_MODE_ON_DRAWABLE;
    }

    public int getDeviceModeOffDrawable() {
        return DEVICE_MODE_OFF_DRAWABLE;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public UUID getId() {
        return id;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}