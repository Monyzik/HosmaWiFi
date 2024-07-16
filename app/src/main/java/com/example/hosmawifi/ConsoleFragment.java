package com.example.hosmawifi;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConsoleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
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

        devices.add(new Device("Not light"));
        devices.add(new Light("Light"));

        // При создании отдельного recycler view для датчиков, данный TextView необходимо удалить.
        dataReceiver = view.findViewById(R.id.dataReceiver);

//        client = new MQTTClient(requireContext(), serverUri, clientId);
//        client.SetMqttEventHandler(this);
//        client.Connect(username, password, this);


        deviceRecyclerView = view.findViewById(R.id.deviceRecyclerView);
        deviceRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        DeviceRecyclerViewAdapter deviceRecyclerViewAdapter = new DeviceRecyclerViewAdapter(devices, new DeviceRecyclerViewAdapter.OnItemSwitchListener() {
            @Override
            public void OnItemSwitch(Device device, boolean isChecked) {
                if (isChecked) {
                    Date d = new Date();
//                    client.publishWith().topic("test").payload(String.valueOf(d.getTime()).getBytes()).qos(MqttQos.EXACTLY_ONCE).send();
                    client.publishWith().topic("test").payload(device.getMessageToTurnOn().getBytes()).qos(MqttQos.EXACTLY_ONCE).send();
                } else {
                    client.publishWith().topic("test").payload(device.getMessageToTurnOff().getBytes()).qos(MqttQos.EXACTLY_ONCE).send();
                }
            }
        });
        deviceRecyclerView.setAdapter(deviceRecyclerViewAdapter);

        client.toAsync().subscribeWith().topicFilter("receiver").qos(MqttQos.EXACTLY_ONCE).callback(new Consumer<Mqtt3Publish>() {
            @Override
            public void accept(Mqtt3Publish mqtt3Publish) {
                String payload = new String(mqtt3Publish.getPayloadAsBytes());
                System.out.println(payload);
                Log.i("time", String.valueOf(new Date().getTime() - Long.parseLong(payload)));
            }
        }).send().whenComplete(new BiConsumer<Mqtt3SubAck, Throwable>() {
            @Override
            public void accept(Mqtt3SubAck mqtt3SubAck, Throwable throwable) {
                if (throwable != null) {
                    Log.e("mqtt", "can't subscribe to the topic");
                } else {
                    Log.i("mqtt", "subscribed");
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_console, container, false);
    }
}