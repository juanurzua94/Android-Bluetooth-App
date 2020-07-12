package com.example.bluetooth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView blueToothDisplayList;
    TextView programState;
    Button searchButton;

    ArrayList<String> devices;
    ArrayAdapter<String> devicesAdapter;

    BluetoothAdapter bluetoothAdapter;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] getResults){
        if(requestCode == 1){
            if(getResults.length > 0 && getResults[0] == PackageManager.PERMISSION_GRANTED){
                programState.setText("Searching...");
                searchButton.setEnabled(false);
                bluetoothAdapter.startDiscovery();
            }
        }
    }

    final private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("ACTION", action);

            if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                programState.setText("Finished");
                searchButton.setEnabled(true);
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                Log.i("WHERE?", "HERE");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String name = device.getName();
                String address = device.getAddress();
                String rssi = Integer.toString(intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE));
                if(name != null || !name.equals("")) {
                    devices.add(name + " " + address + " " + rssi);
                    devicesAdapter.notifyDataSetChanged();
                }
                else {
                    devices.add(address + " " + rssi);
                    devicesAdapter.notifyDataSetChanged();
                }
            }
        }
    };


    public void searchButtonPressed(View view){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else {
            programState.setText("Searching...");
            searchButton.setEnabled(false);
            bluetoothAdapter.startDiscovery();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        blueToothDisplayList = findViewById(R.id.listView);
        programState = findViewById(R.id.stateText);
        searchButton = findViewById(R.id.searchButton);



        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(broadcastReceiver, intentFilter);

        devices = new ArrayList<>();
        devicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, devices);
        blueToothDisplayList.setAdapter(devicesAdapter);
    }
}
