package com.example.hosmawifi;

public class Light extends Device {

    private final int DEVICE_MODE_ON_DRAWABLE = R.drawable.baseline_lightbulb_yellow;
    private final int DEVICE_MODE_OFF_DRAWABLE = R.drawable.baseline_lightbulb;
    private final String MESSAGE_TO_TURN_ON_DEVICE = "1";
    private final String MESSAGE_TO_TURN_OFF_DEVICE = "0";
    public Light(String deviceName) {
        super(deviceName);
    }

    public int getDeviceModeOffDrawable() {
        return DEVICE_MODE_OFF_DRAWABLE;
    }

    public int getDeviceModeOnDrawable() {
        return DEVICE_MODE_ON_DRAWABLE;
    }
}
