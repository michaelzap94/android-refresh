package com.example.firstanimations;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class Bluetooth extends AppCompatActivity {

    private static final String TAG = "Bluetooth";

    ArrayAdapter arrayAdapter;
    ArrayList<String> devicesFound = new ArrayList<>();
    ArrayList<String> addressesIDOfDevicesFound = new ArrayList<>();//contains an ID of the devicesFound INFO

    ListView listViewBluetooth;
    TextView statusTextViewBluetooth;
    Button searchButtonBluetooth;

    BluetoothAdapter bluetoothAdapter;

    //=====================================================================================
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        //When we get any of these actions, how do we handle it.
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "onReceive: Action: "+action);
            switch (action){
                case BluetoothDevice.ACTION_FOUND:
                    //Get the device, once FOUND.
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    String deviceName = device.getName();
                    String deviceAddress = device.getAddress();//IT WILL BE UNIQUE
                    //gives the RSSI -> STRENGHT of connection. The smaller the stronger.
                    int deviceRSSI = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE);
                    //add deviceInfo to the devicesFound Array
                    String deviceInfo = (deviceName != null)? deviceName + " - RSSI: " + deviceRSSI + " dBm" : deviceAddress + " - RSSI: " + deviceRSSI + " dBm";
                    //BUT only add it if it's not in the ARRAY addressesIDOfDevicesFound already:
                    //Otherwise, we may have the same device appear multiple times
                    if(!addressesIDOfDevicesFound.contains(deviceAddress)){
                        addressesIDOfDevicesFound.add(deviceAddress);
                        devicesFound.add(deviceInfo);
                    }
                    //update arrayAdapter/ notify of changes
                    arrayAdapter.notifyDataSetChanged();

                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED: endSearch();
                    break;
            }

        }
    };
    //=====================================================================================

    public void startSearch(View view) {

        //Clear existing list of devices
        devicesFound.clear();
        addressesIDOfDevicesFound.clear();//UNIQUE for each device
        //update arrayAdapter/ notify of changes - CLEARED devicesFound array
        arrayAdapter.notifyDataSetChanged();

        statusTextViewBluetooth.setText("Searching...");
        searchButtonBluetooth.setEnabled(false);
        // Request discover from BluetoothAdapter
        bluetoothAdapter.startDiscovery();
    }

    public void endSearch() {

        statusTextViewBluetooth.setText("Finished");
        searchButtonBluetooth.setEnabled(true);
        // If we're already discovering, stop it
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        listViewBluetooth = findViewById(R.id.listViewBluetooth);
        statusTextViewBluetooth = findViewById(R.id.statusTextViewBluetooth);
        searchButtonBluetooth = findViewById(R.id.searchButtonBluetooth);

        //init arrayAdapter
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, devicesFound);
        listViewBluetooth.setAdapter(arrayAdapter);

        //BLUETOOTH=====================================================================================================
        //Allows us to work with the BluetoothAdapter;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //What actions we can accept, we also need to add a broadcastReceiver to handle the Intents we receive
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);//Tells us when we have found a device
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        //We need to register the receiver: broadcastReceiver && intentFilter;
        registerReceiver(broadcastReceiver,intentFilter);
        //Discover bluetooth devices - ADD Permision 'BLUETOOTH' && 'BLUETOOTH_ADMIN' to Manifest
        //bluetoothAdapter.startDiscovery();//I WILL DO IT WHEN searchButtonBluetooth is CLICKED ⬆
    }
}
