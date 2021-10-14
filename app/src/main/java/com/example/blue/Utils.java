package com.example.blue;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;

public class Utils {

    private Context activity;

    public Utils(Context mActivity){

        activity = mActivity;

    }


    public void check_init_config_file(){
        if(readFromFile(activity,"config.blue") == ""){
            writeToFile("{\"offline_mode\" : false }",activity,"config.blue");
        }
    }

    public boolean is_charging() {

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = activity.registerReceiver(null, ifilter);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        return status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;

    }

    public float get_battery_level(){

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = activity.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        return level * 100 / (float)scale;
    }

    public String get_time(){
        Calendar rightNow = Calendar.getInstance();
        //int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY); // return the hour in 24 hrs format (ranging from 0-23)

        return Integer.toString(rightNow.get(Calendar.HOUR))+":"+ Integer.toString(rightNow.get(Calendar.MINUTE)); // return the hour in 12 hrs format (ranging from 0-11)

    }

    public String get_date(){
        Calendar rightNow = Calendar.getInstance();

        return Integer.toString(rightNow.get(Calendar.DAY_OF_MONTH))+"/"+ Integer.toString(rightNow.get(Calendar.MONTH))+"/"+Integer.toString(rightNow.get(Calendar.YEAR)); // return the hour in 12 hrs format (ranging from 0-11)

    }



    public String readFromFile(Context context, String FileName) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(FileName);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString().replaceAll("\n", "");
            }
        } catch (FileNotFoundException e) {
            return "";
        } catch (IOException e) {
            return "";

        }

        return ret;
    }


    //ECRIRE DANS UN FICHIER PRIVE DE L'APP
    public void writeToFile(String data, Context context, String FileName) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(FileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


}
