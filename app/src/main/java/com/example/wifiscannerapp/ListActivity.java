package com.example.wifiscannerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    WifiManager wifiManager;
    MainActivity.WifiReceiver wifiReceiver;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;

    //List<ScanResult> scanResultList = new ArrayList<>();

    //https://developer.android.com/guide/topics/connectivity/wifi-scan#wifi-scan-restrictions

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        recyclerView=findViewById(R.id.recyclerView2);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        /*if (!wifiManager.isWifiEnabled()){
            Toast.makeText(this, "Your Wifi is disabled....You need to enable it", Toast.LENGTH_SHORT).show();
            wifiManager.setWifiEnabled(true);
        }*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Toast.makeText(ListActivity.this, "version> = marshmallow", Toast.LENGTH_SHORT).show();
            if (ContextCompat.checkSelfPermission(ListActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(ListActivity.this, "location turned off", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(ListActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                Toast.makeText(ListActivity.this, "location turned on", Toast.LENGTH_SHORT).show();
                //scanWifiList();
            }
        } else {
            Toast.makeText(ListActivity.this, "scanning", Toast.LENGTH_SHORT).show();
            //scanWifiList();
        }

        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    scanSuccess();
                } else {
                    // scan failure handling
                    scanFailure();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        this.registerReceiver(wifiScanReceiver, intentFilter);

        boolean success = wifiManager.startScan();
        if (!success) {
            // scan failure handling
            scanFailure();
        }
    }
    private void scanSuccess() {
        List<ScanResult> results = wifiManager.getScanResults();
        //... use new scan results ...
        //scanResultList=wifiManager.getScanResults();
        Log.e("length",""+results.size());
        Toast.makeText(this, "New Scan Results", Toast.LENGTH_SHORT).show();
        //wifiInfoList= (List<WifiInfo>)wifiManager .getConnectionInfo();//
        adapter = new RecyclerAdapterItems(this,results,this);
        recyclerView.setAdapter(adapter);
    }

    private void scanFailure() {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        List<ScanResult> results = wifiManager.getScanResults();
        //... potentially use older scan results ...
        Toast.makeText(this, "Old Scan results"+results.size(), Toast.LENGTH_SHORT).show();
        adapter = new RecyclerAdapterItems(this,results,this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(ListActivity.this, "permission granted", Toast.LENGTH_SHORT).show();
                //scanWifiList();
            } else {
                Toast.makeText(ListActivity.this, "permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem switchId = menu.findItem(R.id.switchId);
        switchId.setActionView(R.layout.switch_layout);
        SwitchCompat switchOnOff = switchId.getActionView().findViewById(R.id.wifiSwitch);
        switchOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    Log.d("onCheckedChangedIf:",""+wifiManager.isWifiEnabled());
                    Toast.makeText(ListActivity.this, "ON", Toast.LENGTH_SHORT).show();
                    wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    wifiManager.setWifiEnabled(true);
                }
                else {
                    Log.e("onCheckedChanged :",""+wifiManager.isWifiEnabled());
                    Toast.makeText(ListActivity.this, "OFF", Toast.LENGTH_SHORT).show();
                    wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    wifiManager.setWifiEnabled(false);
                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.logout){
            // do something
           /* sharePrefs.removeAllSP();
            Log.e("cleared sharedPrefrence","data");
            addFragment(PhoneAuthenticationFragment.class.getSimpleName(),false,null);*/
            Toast.makeText(this, "Logout!!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}