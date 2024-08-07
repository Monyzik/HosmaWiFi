package com.example.hosmawifi;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.renderscript.ScriptGroup;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient;
import com.hivemq.client.mqtt.mqtt3.message.connect.connack.Mqtt3ConnAck;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import com.hivemq.client.mqtt.mqtt3.message.subscribe.suback.Mqtt3SubAck;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


public class ConsoleFragment extends Fragment {

    final String serverUri = "mqtt.flespi.io";

    final int serverPort = 8883;

    final String clientId = "AndroidClient";
    final String subscriptionTopic = "test";

    final String username = "MA4UHx3Oo99wTjJQCbgemm19cvRYvl3kzS4O3yU59B15d8jD9krAY7i2Ryh7O7lf";

    final String password = "";

    Mqtt3AsyncClient client = MqttClient.builder().useMqttVersion3().identifier(clientId)
            .sslWithDefaultConfig().simpleAuth().username(username).password(password.getBytes()).applySimpleAuth()
            .serverHost(serverUri).serverPort(serverPort).buildAsync();

    ArrayList<Device> devices = new ArrayList<>();

    RecyclerView deviceRecyclerView;

    TextView dataReceiver;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private BackgroundNotificationService mBoundService;


    public ConsoleFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConsoleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConsoleFragment newInstance(String param1, String param2) {
        ConsoleFragment fragment = new ConsoleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requireActivity().startService(new Intent(requireActivity(), BackgroundNotificationService.class));
        client.connect().whenComplete(new BiConsumer<Mqtt3ConnAck, Throwable>() {
            @Override
            public void accept(Mqtt3ConnAck mqtt3ConnAck, Throwable throwable) {
                if (throwable != null) {
                    Log.e("MQTT connection error: ", throwable.toString());
                } else {
                    Log.i("MQTT connection", "OK");
                }
            }
        });
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.POST_NOTIFICATIONS},101);
        }

        devices.add(new Device("testTopic", "1", "0", R.drawable.baseline_lightbulb_yellow, R.drawable.baseline_lightbulb, getString(R.string.light)));
        devices.add(new Device("thermometerReceiver", R.drawable.baseline_device_thermostat_24, getString(R.string.temperature)));
        devices.add(new Device("receiver", R.drawable.baseline_water_drop_24, getString(R.string.humidity)));

        devices.add(new Device("teapotTopic", "TeapotSwitchTopic", "1", "0", R.drawable.teapot, R.drawable.teapot, "Teapot"));

        devices.add(new Device("testTopic", "1", "0", R.drawable.baseline_lightbulb_yellow, R.drawable.baseline_lightbulb, "Light"));
        devices.add(new Device("receiver", R.drawable.baseline_home_24, "Smart Home"));
        devices.add(new Device("testTopic", "1", "0", R.drawable.baseline_lightbulb_yellow, R.drawable.baseline_lightbulb, "Light"));
        devices.add(new Device("receiver", R.drawable.baseline_home_24, "Smart Home"));

        deviceRecyclerView = view.findViewById(R.id.deviceRecyclerView);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (devices.get(position).getViewType() == 2) {
                    return 2;
                }
                return 1;
            }
        });

        deviceRecyclerView.setLayoutManager(gridLayoutManager);
        DeviceRecyclerViewAdapter deviceRecyclerViewAdapter = new DeviceRecyclerViewAdapter(devices, client, requireActivity());
        deviceRecyclerView.setAdapter(deviceRecyclerViewAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_console, container, false);
    }
}