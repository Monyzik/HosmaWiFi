package com.example.hosmawifi;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import com.hivemq.client.mqtt.mqtt3.message.subscribe.suback.Mqtt3SubAck;

import java.util.ArrayList;
import java.util.Date;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DeviceRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public ArrayList<Device> devices;

    Mqtt3AsyncClient client;

    Activity activity;


    public static class DeviceViewHolder extends RecyclerView.ViewHolder {
        private final TextView deviceNameTextView;

        private final ImageView deviceImageView;

        private final SwitchMaterial deviceSwitchMaterial;

        public DeviceViewHolder(View view) {
            super(view);
            deviceSwitchMaterial = view.findViewById(R.id.deviceSwitchMaterial);
            deviceNameTextView = view.findViewById(R.id.deviceNameTextView);
            deviceImageView = view.findViewById(R.id.deviceImageView);
        }

        public ImageView getDeviceImageView() {
            return deviceImageView;
        }

        public SwitchMaterial getSwitchMaterial() {
            return deviceSwitchMaterial;
        }

        public TextView getDeviceNameTextView() {
            return deviceNameTextView;
        }
    }

    public static class SensorViewHolder extends RecyclerView.ViewHolder {

        private final TextView deviceNameTextView, deviceReceivedData;

        private final ImageView deviceImageView;



        public SensorViewHolder(@NonNull View view) {
            super(view);
            deviceNameTextView = view.findViewById(R.id.deviceNameTextView);
            deviceImageView = view.findViewById(R.id.deviceImageView);
            deviceReceivedData = view.findViewById(R.id.deviceReceivedData);
        }

        public ImageView getDeviceImageView() {
            return deviceImageView;
        }

        public TextView getDeviceNameTextView() {
            return deviceNameTextView;
        }

        public TextView getDeviceReceivedData() {
            return deviceReceivedData;
        }
    }


    public static class TeapotViewHolder extends RecyclerView.ViewHolder {
        private final TextView deviceNameTextView, deviceReceivedData;

        private final SwitchMaterial deviceSwitchMaterial;

        private final View progressView;

        private final ImageView deviceImageView;

        public TeapotViewHolder(@NonNull View view) {
            super(view);
            progressView = view.findViewById(R.id.progressView);
            deviceSwitchMaterial = view.findViewById(R.id.deviceSwitchMaterial);
            progressView.setPivotY(0);
            deviceNameTextView = view.findViewById(R.id.deviceNameTextView);
            deviceImageView = view.findViewById(R.id.deviceImageView);
            deviceReceivedData = view.findViewById(R.id.deviceReceivedData);
        }

        public ImageView getDeviceImageView() {
            return deviceImageView;
        }

        public TextView getDeviceNameTextView() {
            return deviceNameTextView;
        }

        public TextView getDeviceReceivedData() {
            return deviceReceivedData;
        }

        public View getProgressView() {
            return progressView;
        }

        public SwitchCompat getDeviceSwitchMaterial() {
            return deviceSwitchMaterial;
        }
    }


    public DeviceRecyclerViewAdapter(ArrayList<Device> dataSet, Mqtt3AsyncClient client, Activity activity) {
        this.devices = dataSet;
        this.client = client;
        this.activity = activity;
    }

    @Override
    public int getItemViewType(int position) {
        return devices.get(position).getViewType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        switch (viewType) {
//            case 0:
//                view = LayoutInflater.from(viewGroup.getContext())
//                        .inflate(R.layout.device_item_for_recycler_view, viewGroup, false);
//                return new ViewHolder(view, viewType);
            case 1:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sensor_item_for_recycler_view, viewGroup, false);
                return new SensorViewHolder(view);
            case 2:
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.teapot_item_for_recycler_view, viewGroup, false);
                return new TeapotViewHolder(view);
            default:
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.device_item_for_recycler_view, viewGroup, false);
                return new DeviceViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        Device device = devices.get(position);

        switch (holder.getItemViewType()) {
            case 1:
                SensorViewHolder sensorViewHolder = (SensorViewHolder) holder;

                sensorViewHolder.getDeviceNameTextView().setText(device.getDeviceName());

                sensorViewHolder.getDeviceImageView().setImageResource(device.getDeviceModeOffDrawable());

                client.toAsync().subscribeWith().topicFilter(device.getSubscribeTopic()).qos(MqttQos.EXACTLY_ONCE).callback(new Consumer<Mqtt3Publish>() {
                    @Override
                    public void accept(Mqtt3Publish mqtt3Publish) {
                        String text = new String(mqtt3Publish.getPayloadAsBytes());
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                sensorViewHolder.getDeviceReceivedData().setText(((int)(Integer.parseInt(text) * device.getCoefficient())) + "Лм");
                            }
                        });
                    }
                }).send().whenComplete(new BiConsumer<Mqtt3SubAck, Throwable>() {
                    @Override
                    public void accept(Mqtt3SubAck mqtt3SubAck, Throwable throwable) {
                        if (throwable != null) {
                            Log.e("mqtt", "can't subscribe to the topic " + device.getSubscribeTopic());
                        } else {
                            Log.i("mqtt", "subscribed to topic" + device.getSubscribeTopic());
                        }
                    }
                });
                int screenWidth = getScreenWidth(activity);
                holder.itemView.getLayoutParams().width = (int) (screenWidth/ 2.2f);
                ((SensorViewHolder) holder).getDeviceImageView().getLayoutParams().width = (int) (screenWidth/ 6);
                ((SensorViewHolder) holder).deviceNameTextView.setTextSize((int) (screenWidth / 80));
                ((SensorViewHolder) holder).deviceReceivedData.setTextSize((int) (screenWidth / 70));


                break;
            case 2:
                TeapotViewHolder teapotViewHolder = (TeapotViewHolder) holder;

                teapotViewHolder.getDeviceNameTextView().setText(device.getDeviceName());

                teapotViewHolder.getDeviceImageView().setImageResource(device.getDeviceModeOffDrawable());

                teapotViewHolder.getDeviceSwitchMaterial().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            client.publishWith().topic(device.getPublishTopic()).payload(device.getMessageToTurnOn().getBytes()).qos(MqttQos.EXACTLY_ONCE).send();
                            teapotViewHolder.getDeviceImageView().setImageResource(device.getDeviceModeOnDrawable());
                            SharedPreferences.Editor editor = activity.getSharedPreferences("save", MODE_PRIVATE).edit();
                            editor.putBoolean(device.getDeviceName(), true);
                            editor.apply();
                        } else {
                            client.publishWith().topic(device.getPublishTopic()).payload(device.getMessageToTurnOff().getBytes()).qos(MqttQos.EXACTLY_ONCE).send();
                            teapotViewHolder.getDeviceImageView().setImageResource(device.getDeviceModeOffDrawable());
                            SharedPreferences.Editor editor = activity.getSharedPreferences("save", MODE_PRIVATE).edit();
                            editor.putBoolean(device.getDeviceName(), false);
                            editor.apply();
                        }
                    }
                });

                client.toAsync().subscribeWith().topicFilter(device.getSubscribeTopic()).qos(MqttQos.EXACTLY_ONCE).callback(new Consumer<Mqtt3Publish>() {
                    @Override
                    public void accept(Mqtt3Publish mqtt3Publish) {
                        String text = new String(mqtt3Publish.getPayloadAsBytes());
                        int temperature = Integer.parseInt(text);

                        if (temperature >= 100) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    teapotViewHolder.getDeviceSwitchMaterial().setChecked(false);
                                }
                            });
                        }
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (temperature >= 75) {
                                    teapotViewHolder.getProgressView().setBackgroundResource(R.color.red_500);
                                } else if (temperature >= 40) {
                                    teapotViewHolder.getProgressView().setBackgroundResource(R.color.yellow_500);
                                } else {
                                    teapotViewHolder.getProgressView().setBackgroundResource(R.color.blue_500);
                                }
                                teapotViewHolder.getProgressView().animate().scaleY((float) temperature / 100f).start();
                                teapotViewHolder.getDeviceReceivedData().setText(temperature + "°С");
                            }
                        });
                    }
                }).send().whenComplete(new BiConsumer<Mqtt3SubAck, Throwable>() {
                    @Override
                    public void accept(Mqtt3SubAck mqtt3SubAck, Throwable throwable) {
                        if (throwable != null) {
                            Log.e("mqtt", "can't subscribe to the topic " + device.getSubscribeTopic());
                        } else {
                            Log.i("mqtt", "subscribed to topic" + device.getSubscribeTopic());
                        }
                    }
                });

                ((TeapotViewHolder) holder).getDeviceImageView().getLayoutParams().width = (int) (getScreenWidth(activity)/ 2.5f);

                break;
            default:
                assert holder instanceof DeviceViewHolder;
                DeviceViewHolder deviceViewHolder = (DeviceViewHolder) holder;

                deviceViewHolder.getDeviceNameTextView().setText(device.getDeviceName());

                SharedPreferences sharedPreferences = activity.getSharedPreferences("save", MODE_PRIVATE);
                if (sharedPreferences.getBoolean(device.getDeviceName(), false)) {
                    deviceViewHolder.getDeviceImageView().setImageResource(device.getDeviceModeOnDrawable());
                    deviceViewHolder.getSwitchMaterial().setChecked(true);
                } else {
                    deviceViewHolder.getDeviceImageView().setImageResource(device.getDeviceModeOffDrawable());
                    deviceViewHolder.getSwitchMaterial().setChecked(false);
                }


                deviceViewHolder.getSwitchMaterial().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            client.publishWith().topic(device.getPublishTopic()).payload(device.getMessageToTurnOn().getBytes()).qos(MqttQos.EXACTLY_ONCE).send();
                            deviceViewHolder.getDeviceImageView().setImageResource(device.getDeviceModeOnDrawable());
                            SharedPreferences.Editor editor= activity.getSharedPreferences("save", MODE_PRIVATE).edit();
                            editor.putBoolean(device.getDeviceName(), true);
                            editor.apply();
                        } else {
                            client.publishWith().topic(device.getPublishTopic()).payload(device.getMessageToTurnOff().getBytes()).qos(MqttQos.EXACTLY_ONCE).send();
                            deviceViewHolder.getDeviceImageView().setImageResource(device.getDeviceModeOffDrawable());
                            SharedPreferences.Editor editor = activity.getSharedPreferences("save", MODE_PRIVATE).edit();
                            editor.putBoolean(device.getDeviceName(), false);
                            editor.apply();
                        }
                    }
                });

                holder.itemView.getLayoutParams().width = (int) (getScreenWidth(activity)/ 2.2f);
                ((DeviceViewHolder) holder).getDeviceImageView().getLayoutParams().width = (int) (getScreenWidth(activity)/ 6);

                break;
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return devices.size();
    }

    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }
}
