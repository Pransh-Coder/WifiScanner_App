package com.example.wifiscannerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    WifiManager wifiManager;
    WifiReceiver wifiReceiver;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;

    List<ScanResult>scanResultList = new ArrayList<>();
    //List<WifiInfo> wifiInfoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView=findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new WifiReceiver();


        registerReceiver(wifiReceiver,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        if (!wifiManager.isWifiEnabled()){
            Toast.makeText(this, "Your Wifi is disabled....You need to enable it", Toast.LENGTH_SHORT).show();
            wifiManager.setWifiEnabled(true);
        }
        /*if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},0);
        }
        else {
            scanWifiList();
        }*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Toast.makeText(MainActivity.this, "version> = marshmallow", Toast.LENGTH_SHORT).show();
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "location turned off", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                Toast.makeText(MainActivity.this, "location turned on", Toast.LENGTH_SHORT).show();
                scanWifiList();
            }
        } else {
            Toast.makeText(MainActivity.this, "scanning", Toast.LENGTH_SHORT).show();
            scanWifiList();
        }

    }

    public void scanWifiList() {
        //scanResultList = new ArrayList<>();
        wifiManager.startScan();
        scanResultList=wifiManager.getScanResults();
        Log.e("length",""+scanResultList.size());
        //wifiInfoList= (List<WifiInfo>)wifiManager .getConnectionInfo();//
        adapter = new RecyclerAdapterItems(getApplicationContext(),scanResultList,this);
        recyclerView.setAdapter(adapter);
    }

    class WifiReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }
    @Override
    public void onStop() {
        super.onStop();
        this.unregisterReceiver(wifiReceiver);
    }
    @Override
    public void onPause() {
        super.onPause();
        this.unregisterReceiver(wifiReceiver);

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(wifiReceiver,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "permission granted", Toast.LENGTH_SHORT).show();
                scanWifiList();
            } /*else {
                Toast.makeText(MainActivity.this, "permission not granted", Toast.LENGTH_SHORT).show();
            }*/
           /* else if (Build.VERSION.SDK_INT >= 23 && !shouldShowRequestPermissionRationale(permissions[0])) {
                // User selected the Never Ask Again Option Change settings in app settings manually
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Change Permissions in Settings");
                alertDialogBuilder
                        .setMessage("" +
                                "\nClick SETTINGS to Manually Set\n"+"Permissions to see Wifi List")
                        .setCancelable(false)
                        .setPositiveButton("SETTINGS", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivityForResult(intent, 1000);     // Comment 3.
                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            } else {
                // User selected Deny Dialog to EXIT App ==> OR <== RETRY to have a second chance to Allow Permissions
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setTitle("Second Chance");
                    alertDialogBuilder
                            .setMessage("Click RETRY to Set Permissions to Allow\n\n"+"Click EXIT to the Close App")
                            .setCancelable(false)
                            .setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Integer.parseInt(WRITE_EXTERNAL_STORAGE));
                                    Intent i = new Intent(MainActivity.this,MainActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                }
                            })
                            .setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    finish();
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }*/
        }
    }
}