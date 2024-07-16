package com.example.hosmawifi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;

public class DeviceRecyclerViewAdapter extends RecyclerView.Adapter<DeviceRecyclerViewAdapter.ViewHolder> {

    public interface OnItemSwitchListener {
        void OnItemSwitch(Device device, boolean isChecked);
    }

    public ArrayList<Device> devices;

    OnItemSwitchListener listener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView deviceNameTextView;

        private final ImageView deviceImageView;

        private final SwitchMaterial deviceSwitchMaterial;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            deviceSwitchMaterial = view.findViewById(R.id.deviceSwitchMaterial);
            deviceNameTextView = view.findViewById(R.id.deviceNameTextView);
            deviceImageView = view.findViewById(R.id.deviceImageView);
        }

        public void bind(final Device device, final OnItemSwitchListener listener) {
            deviceSwitchMaterial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    listener.OnItemSwitch(device, isChecked);
                    if (isChecked) {
                        deviceImageView.setImageResource(device.getDeviceModeOnDrawable());
                    } else {
                        deviceImageView.setImageResource(device.getDeviceModeOffDrawable());
                    }
                }
            });
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


    public DeviceRecyclerViewAdapter(ArrayList<Device> dataSet, OnItemSwitchListener listener) {
        this.devices = dataSet;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.device_item_for_recycler_view, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        Device device = devices.get(position);

        viewHolder.bind(device, listener);

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getDeviceNameTextView().setText(device.getDeviceName());

        viewHolder.getDeviceImageView().setImageResource(device.getDeviceModeOffDrawable());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return devices.size();
    }
}
