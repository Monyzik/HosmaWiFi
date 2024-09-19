package com.example.hosmawifi;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import com.hivemq.client.mqtt.mqtt3.message.subscribe.suback.Mqtt3SubAck;

import java.util.Date;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class BackgroundNotificationService extends Service {

    final String serverUri = "mqtt.flespi.io";

    final int serverPort = 8883;

    final String clientId = "AndroidNotificationService";
    final String subscriptionTopic = "test";
    final String devicesNotifications = "devicesNotifications";

    final String username = "MA4UHx3Oo99wTjJQCbgemm19cvRYvl3kzS4O3yU59B15d8jD9krAY7i2Ryh7O7lf";

    final String password = "";

    private final String TAG = "BackgroundNotificationService";
    private final String CHANNEL_ID = "NOTIFICATION_CHANNEL";

    private NotificationManager notificationManager;

    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;

    Mqtt3AsyncClient client = MqttClient.builder().useMqttVersion3().identifier(clientId)
            .sslWithDefaultConfig().simpleAuth().username(username).password(password.getBytes()).applySimpleAuth()
            .serverHost(serverUri).serverPort(serverPort).automaticReconnect().applyAutomaticReconnect().buildAsync();

    public class LocalBinder extends Binder {
        BackgroundNotificationService getService() {
            return BackgroundNotificationService.this;
        }
    }

    private final IBinder mBinder = new LocalBinder();

    public BackgroundNotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service starting");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "Service destroyed");
        client.disconnect();
        notificationManager.cancelAll();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, 10000);
            }
        };

        handler.postDelayed(runnable, 15000);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "SmartHomeNotifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        client.connect().whenComplete(new BiConsumer<Mqtt3ConnAck, Throwable>() {
            @Override
            public void accept(Mqtt3ConnAck mqtt3ConnAck, Throwable throwable) {
                if (throwable != null) {
                    Log.e(TAG, throwable.toString());
                } else {
                    Log.i(TAG, "Connected to the mqtt broker");
                }
            }
        });
        client.toAsync().subscribeWith().topicFilter(devicesNotifications).qos(MqttQos.EXACTLY_ONCE).callback(new Consumer<Mqtt3Publish>() {
            @Override
            public void accept(Mqtt3Publish mqtt3Publish) {
                String text = new String(mqtt3Publish.getPayloadAsBytes());
                showNotification(text);
            }
        }).send().whenComplete(new BiConsumer<Mqtt3SubAck, Throwable>() {
            @Override
            public void accept(Mqtt3SubAck mqtt3SubAck, Throwable throwable) {
                if (throwable != null) {
                    Log.e("mqttNotification", "can't subscribe to the topic");
                } else {
                    Log.i("mqttNotification", "subscribed");
                }
            }
        });

    }

    private void showNotification(String text) {
        PendingIntent contentIntent = PendingIntent.getActivity(BackgroundNotificationService.this, 0,
                new Intent(BackgroundNotificationService.this, MainActivity.class), PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(BackgroundNotificationService.this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.baseline_home_24)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(getText(R.string.smart_home))
                    .setContentText(text)
                    .setContentIntent(contentIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .build();

        notificationManager.notify((int) System.currentTimeMillis(), notification);
    }
}