package com.example.wifiscannerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    WifiManager wifiManager;
    WifiReceiver wifiReceiver;
    SwitchCompat switchOnOff;
    SwitchCompat switchCompat;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;

    LinearLayout linearLayout;
    TextView wifiName,capability;
    ImageView imageView;

    //FloatingActionButton disconnect;
    CircleImageView disconnect;

    int flag=0;
    int val;

    List<ScanResult>scanResultList = new ArrayList<>();     //the list of access points found in the most recent scan
    //List<WifiInfo> wifiInfoList = new ArrayList<>();

    //https://developer.android.com/guide/topics/connectivity/wifi-scan#wifi-scan-restrictions

/*    Handler handler = new Handler();

    // Define the code block to be executed
    //It is used for checking the wifi state infinetly after 2 sec
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // Insert custom code here
            isWifiEnabled();
            // Repeat every 2 seconds
            handler.postDelayed(runnable, 200);
        }
    };*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView=findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        switchCompat = findViewById(R.id.wifiSwitch);

        linearLayout = findViewById(R.id.Linear);
        wifiName = findViewById(R.id.wifiname2);
        capability = findViewById(R.id.capibility2);
        imageView =findViewById(R.id.imageView2);

        disconnect = findViewById(R.id.disconnect);

        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new WifiReceiver();

        registerReceiver(wifiReceiver,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));


        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        val =  isWifiEnabled();
                    }
                });
            }
        };timer.schedule(doAsynchronousTask, 0, 2000);


      /*  if (!wifiManager.isWifiEnabled()){
            Toast.makeText(this, "Your Wifi is disabled....You need to enable it", Toast.LENGTH_SHORT).show();
            wifiManager.setWifiEnabled(true);       //Return whether Wi-Fi is enabled or disabled.

        }*/
        /*if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},0);
        }
        else {
            scanWifiList();
        }*/

    /*    if (wifiManager.isWifiEnabled()){
            Toast.makeText(this, "Enabled!! wifi ", Toast.LENGTH_SHORT).show();
            //flag = 1;
        }
        else if(!wifiManager.isWifiEnabled()){
            //flag=3;
            Toast.makeText(this, "Disabled!! wifi ", Toast.LENGTH_SHORT).show();
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
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    Log.d("onCheckedChangedIf:",""+wifiManager.isWifiEnabled());
                    Toast.makeText(MainActivity.this, "ON", Toast.LENGTH_SHORT).show();
                    wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    wifiManager.setWifiEnabled(true);
                }
                else {
                    Log.e("onCheckedChanged :",""+wifiManager.isWifiEnabled());
                    Toast.makeText(MainActivity.this, "OFF", Toast.LENGTH_SHORT).show();
                    wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    wifiManager.setWifiEnabled(false);
                }
            }
        });

        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (wifiManager != null && wifiManager.isWifiEnabled()) {
                    /*int netId = wifiManager.getConnectionInfo().getNetworkId();
                    wifiManager.disableNetwork(netId);*/
                    boolean b = wifiManager.disconnect();
                    Log.e("disconnect",""+b);
                }
            }
        });
    }

    public void scanWifiList() {
        //scanResultList = new ArrayList<>();
        wifiManager.startScan();
        scanResultList=wifiManager.getScanResults();
        Log.e("length",""+scanResultList.size());
        //wifiInfoList= (List<WifiInfo>)wifiManager .getConnectionInfo();//
        adapter = new RecyclerAdapterItems(getApplicationContext(),scanResultList,this);
        recyclerView.setAdapter(adapter);

       /* for (int i = 0; i < scanResultList.size(); i++){

            if (scanResultList.get(i).SSID.equalsIgnoreCase("D-Link")){
                //Toast.makeText(this, "found!!", Toast.LENGTH_SHORT).show();
                Log.e("scanWifiList", "Found!!");
                String capp = scanResultList.get(i).capabilities;
                Log.e("Capabilities", ""+capp);
            }
        }*/
    }


    class WifiReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();

            if (activeNetInfo != null  && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                Toast.makeText(context, "Wifi Connected!", Toast.LENGTH_SHORT).show();
                linearLayout.setVisibility(View.VISIBLE);

                WifiInfo info = wifiManager.getConnectionInfo ();
                String ssid  = info.getSSID();
                int len = ssid.length();
                String winam = ssid.substring(1,len-1);
                String capp="";
                for (int i = 0; i < scanResultList.size(); i++){

                    if (scanResultList.get(i).SSID.equalsIgnoreCase(winam)){
                        //Toast.makeText(this, "found!!", Toast.LENGTH_SHORT).show();
                        Log.e("scanWifiList", "Found!!");
                        capp = scanResultList.get(i).capabilities;
                        Log.e("Capabilities", ""+capp);

                        if (scanResultList.get(i).level <= 0 && scanResultList.get(i).level >= -50) {
                            //Best signal
                            imageView.setBackgroundResource(R.drawable.signal_4);

                        } else if (scanResultList.get(i).level < -50 && scanResultList.get(i).level >= -70) {
                            //Good signal
                            imageView.setBackgroundResource(R.drawable.signal_3);

                        } else if (scanResultList.get(i).level < -70 && scanResultList.get(i).level >= -80) {
                            //Low signal
                            imageView.setBackgroundResource(R.drawable.signal_2);

                        } else if (scanResultList.get(i).level < -80 && scanResultList.get(i).level >= -100) {
                            //Very weak signal
                            imageView.setBackgroundResource(R.drawable.signal_1);

                        }
                    }
                }
                wifiName.setText(winam);
                capability.setText(capp);
                //flag=1;
            } else {
                Toast.makeText(context, "Wifi Not Connected!", Toast.LENGTH_SHORT).show();
                linearLayout.setVisibility(View.GONE);
                //flag=3;
            }
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(wifiReceiver);
    }
    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(wifiReceiver);

    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(wifiReceiver,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(wifiReceiver,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    public int isWifiEnabled() {
        if (wifiManager.isWifiEnabled()){
            //flag = 1;
            Log.e("flag value",""+flag);
            switchCompat.setChecked(true);
            Toast.makeText(this, "Wifi is Enabled!!", Toast.LENGTH_SHORT).show();
            registerReceiver(wifiReceiver,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        }
        else if(!wifiManager.isWifiEnabled()){
            //flag = 3;
            Log.e("flag value",""+flag);
            switchCompat.setChecked(false);
            Toast.makeText(this, "Wifi is Disabled!!", Toast.LENGTH_SHORT).show();
        }
        Log.e("isWifiEnabled", ""+flag );
        return flag;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "permission granted", Toast.LENGTH_SHORT).show();
                scanWifiList();
            } else {
                Toast.makeText(MainActivity.this, "permission not granted", Toast.LENGTH_SHORT).show();
            }
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        /*MenuItem switchId = menu.findItem(R.id.switchId);
        switchId.setActionView(R.layout.switch_layout);
        switchOnOff = switchId.getActionView().findViewById(R.id.wifiSwitch);
        if (flag==1){
            switchOnOff.setChecked(true);
            Log.e("onCreateOptionsMenu:", "checked!!");
        }
        else if (flag==3){
            switchOnOff.setChecked(false);
            Log.e("onCreateOptionsMenu:", "checked!!");
        }
        switchOnOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    Log.d("onCheckedChanged..If:",""+wifiManager.isWifiEnabled());
                    Toast.makeText(MainActivity.this, "ON", Toast.LENGTH_SHORT).show();
                    wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    wifiManager.setWifiEnabled(true);
                }
                else {
                    Log.e("onCheckedChanged..Else:",""+wifiManager.isWifiEnabled());
                    Toast.makeText(MainActivity.this, "OFF", Toast.LENGTH_SHORT).show();
                    wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    wifiManager.setWifiEnabled(false);
                }
            }
        });*/
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