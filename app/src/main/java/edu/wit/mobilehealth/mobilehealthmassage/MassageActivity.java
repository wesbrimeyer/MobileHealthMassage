package edu.wit.mobilehealth.mobilehealthmassage;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MassageActivity extends AppCompatActivity implements IBluetoothAlerts {
    private PointListAdapter pointListAdapter;
    private BluetoothService bluetoothService;
    private ArrayList<MassagePoint> arrayList= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_massage);

        ListView listView = findViewById(R.id.ListView01);

        MassagePoint p1 = new MassagePoint("G.B.-21\n(Right Shoulder)",
                "This MassagePoint helps with pain, neck stiffness, shoulder tension, and headaches.",
                getDrawable(R.drawable.gb21),
                4, false);
        MassagePoint p2 = new MassagePoint(
                "G.B.-21\n(Left Shoulder)",
                "This MassagePoint helps with pain, neck stiffness, shoulder tension, and headaches.",
                getDrawable(R.drawable.gb21),
                5, true);
        MassagePoint p3 = new MassagePoint(
                "S.I.-14\n(Right Back)",
                "This MassagePoint is used for pain in the shoulder/back, sudden stiff neck, and activates the meridian.",
                getDrawable(R.drawable.si14),
                6, false);
        MassagePoint p4 = new MassagePoint("S.I.-14\n(Left Back)",
                "This MassagePoint is used for pain in the shoulder/back, sudden stiff neck, and activates the meridian.",
                getDrawable(R.drawable.si14),
                7, true);


        arrayList.add(p1);
        arrayList.add(p2);
        arrayList.add(p3);
        arrayList.add(p4);

        pointListAdapter = new PointListAdapter(getApplicationContext(),R.layout.point_list_item, arrayList);
        listView.setAdapter(pointListAdapter);

        ActionBar topBar = getSupportActionBar();
        if (topBar != null) {
            topBar.setDisplayHomeAsUpEnabled(true);
            topBar.setSubtitle("Searching for device...");
            topBar.setBackgroundDrawable(getDrawable(android.R.color.holo_red_light));
        }

        BluetoothAdapter blt_adapter = BluetoothAdapter.getDefaultAdapter();
        if (blt_adapter == null) return;
        if (!blt_adapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 11);
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        40);
            }

            bluetoothService = BluetoothService.newInstance(this);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 11) {
            bluetoothService = BluetoothService.newInstance(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("BluetoothService","Bluetooth service will unbind");
        bluetoothService.disconnect();
        bluetoothService = null;
    }

    @Override
    public void onDeviceConnected(@NotNull final ArduinoController arduino) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getSupportActionBar().setBackgroundDrawable(getDrawable(android.R.color.holo_green_light));
                getSupportActionBar().setSubtitle("Connected!");
                pointListAdapter.connectArduino(arduino);
            }
        });
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    @Override
    public void onDeviceDisconnected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getSupportActionBar().setBackgroundDrawable(getDrawable(android.R.color.holo_red_light));
                getSupportActionBar().setSubtitle("Searching for device...");
                pointListAdapter.connectArduino(null);
            }
        });
    }

    @Override
    public void plotValue(String value) {
        //Do nothing
    }
}
