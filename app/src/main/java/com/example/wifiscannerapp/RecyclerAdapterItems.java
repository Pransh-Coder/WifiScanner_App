package com.example.wifiscannerapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.WIFI_SERVICE;

public class RecyclerAdapterItems extends RecyclerView.Adapter<RecyclerAdapterItems.ViewHolder> {

    Context context;
    List<ScanResult> scanResultList = new ArrayList<>();
    List<WifiInfo> wifiInfoList = new ArrayList<>();
    AnimationDrawable wifiAnimation;
    Activity activity;
    //public String pass;
    WifiConfiguration wifiConfig;
    //WifiManager wifiManager;


    public RecyclerAdapterItems(Context context, List<ScanResult> scanResultList,Activity activity) {
        this.context = context;
        this.scanResultList = scanResultList;
        this.activity = activity;
    }

    public RecyclerAdapterItems(Context context, List<ScanResult> scanResultList, List<WifiInfo> wifiInfoList) {
        this.context = context;
        this.scanResultList = scanResultList;
        this.wifiInfoList = wifiInfoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.wifiname.setText("Wifi Name: " + scanResultList.get(position).SSID);           // name
        holder.Ipaddress.setText("IP Adress: " + scanResultList.get(position).BSSID);         //ip
        holder.capbility.setText("Capibilities : " + scanResultList.get(position).capabilities);
        holder.level.setText("Level : " + scanResultList.get(position).level);
        holder.frequency.setText("Level : " + scanResultList.get(position).frequency);

        // For setting Animation
        /*holder.imageView.setBackgroundResource(R.drawable.animation);
        wifiAnimation = (AnimationDrawable) holder.imageView.getBackground();
        wifiAnimation.start();*/

        if (scanResultList.get(position).level <= 0 && scanResultList.get(position).level >= -50) {
            //Best signal
            holder.imageView.setBackgroundResource(R.drawable.signal_4);

        } else if (scanResultList.get(position).level < -50 && scanResultList.get(position).level >= -70) {
            //Good signal
            holder.imageView.setBackgroundResource(R.drawable.signal_3);

        } else if (scanResultList.get(position).level < -70 && scanResultList.get(position).level >= -80) {
            //Low signal
            holder.imageView.setBackgroundResource(R.drawable.signal_2);

        } else if (scanResultList.get(position).level < -80 && scanResultList.get(position).level >= -100) {
            //Very weak signal
            holder.imageView.setBackgroundResource(R.drawable.signal_1);

        } else {
            // no signals
            Toast.makeText(context, "No Signal!!", Toast.LENGTH_SHORT).show();
        }
        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           /*     wifiConfig = new WifiConfiguration();
                wifiConfig.SSID = String.format("\"%s\"", scanResultList.get(position).SSID);
                //alertDialogDemo();
                if (wifiConfig.preSharedKey==null&&pass==null){
                   *//* CustomDialog customDialog = new CustomDialog(activity);
                    customDialog.show();*//*
                   alertDialogDemo();
                }
                else {
                    wifiConfig.preSharedKey = String.format("\"%s\"", pass);
                }
                //alertDialogDemo();
                WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
                //remember id
                int netId = wifiManager.addNetwork(wifiConfig);
                wifiManager.disconnect();
                wifiManager.enableNetwork(netId, true);
                wifiManager.reconnect();*/

                if (scanResultList.get(position).capabilities.toUpperCase().contains("WEP")){
                    // WEP Network
                    connectToWifi(scanResultList.get(position).SSID);
                }
                else if (scanResultList.get(position).capabilities.toUpperCase().contains("WPA")||scanResultList.get(position).capabilities.contains("WPA2")){
                    // WPA or WPA2 Network
                    connectToWifi(scanResultList.get(position).SSID);
                }
                else {
                    Toast.makeText(context, "Not Secured!!!", Toast.LENGTH_SHORT).show();
                    //open network
                    wifiConfig = new WifiConfiguration();
                    wifiConfig.SSID = "\"" + scanResultList.get(position).SSID + "\"";
                    wifiConfig.hiddenSSID = true;
                    wifiConfig.priority = 0xBADBAD;
                    wifiConfig.status = WifiConfiguration.Status.ENABLED;
                    wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

                    WifiManager wifiManager = (WifiManager)context.getApplicationContext().getSystemService(WIFI_SERVICE);

                    //remember id
                    int id = wifiManager.addNetwork(wifiConfig);
                    wifiManager.disconnect();
                    wifiManager.enableNetwork(id, true);
                    wifiManager.reconnect();
                }
                //connectToWifi(scanResultList.get(position).SSID);
            }
        });
    }

    @Override
    public int getItemCount() {
        return scanResultList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView wifiname, Ipaddress, capbility, level, frequency;

        ImageView imageView;

        ConstraintLayout constraintLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            wifiname = itemView.findViewById(R.id.wifiname);
            Ipaddress = itemView.findViewById(R.id.ipAddress);
            capbility = itemView.findViewById(R.id.capibility);
            level = itemView.findViewById(R.id.leveltxt);
            frequency = itemView.findViewById(R.id.freq);
            imageView = itemView.findViewById(R.id.imageView);
            constraintLayout = itemView.findViewById(R.id.constraintView2);
        }
    }

   /* @Override
    public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        wifiAnimation.start();
    }*/

    public void alertDialogDemo() {
        // get alert_dialog.xml view
        LayoutInflater li = LayoutInflater.from(activity);
        View promptsView = li.inflate(R.layout.alert_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                activity);

        // set alert_dialog.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.userInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // get user input and set it to result
                        // edit text
                        Toast.makeText(context, "Entered: " + userInput.getText().toString(), Toast.LENGTH_LONG).show();
                        /*pass = userInput.getText().toString();
                        wifiConfig.preSharedKey = String.format("\"%s\"",userInput.getText().toString());*/
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void alert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context.getApplicationContext());
        alertDialog.setTitle("PASSWORD");
        alertDialog.setMessage("Enter Password");

        final EditText input = new EditText(context.getApplicationContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setIcon(R.drawable.signal_1);

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                       String password = input.getText().toString();
                        Toast.makeText(context, ""+password, Toast.LENGTH_SHORT).show();
                    }
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }
    private void popUpEditText() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Comments");

        final EditText input = new EditText(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // do something here on OK

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }
    public void finallyConnect(String networkPass, String networkSSID) {

        final ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setCancelable(false);
        dialog.setMessage("Please Wait!!");
        dialog.setTitle("Connecting To Wifi...");
        dialog.show();

        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", networkSSID);
        wifiConfig.preSharedKey = String.format("\"%s\"", networkPass);

        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);

        // remember id
        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        dialog.dismiss();
        wifiManager.reconnect();

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"\"" + networkSSID + "\"\"";
        conf.preSharedKey = "\"" + networkPass + "\"";
        wifiManager.addNetwork(conf);
    }

    //this function is called first from the click listner to pass the ssid
    public void connectToWifi(final String wifiSSID) {

        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.connect);

        dialog.setTitle("Connect to Network");
        TextView textSSID = (TextView) dialog.findViewById(R.id.textSSID1);

        Button dialogButton = (Button) dialog.findViewById(R.id.okButton);
        final EditText pass = (EditText) dialog.findViewById(R.id.textPassword);
        textSSID.setText(wifiSSID);    // setting the wifi name on top of dialog

        // if button is clicked, connect to the network;
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String checkPassword = pass.getText().toString();           // getting the password from edittext
                finallyConnect(checkPassword, wifiSSID);                    // then calling finally connect function by passing both password and ssid [of wifi network to which you want to connect]
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
