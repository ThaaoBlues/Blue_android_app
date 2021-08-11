package com.example.blue;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import com.example.blue.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class OnlineUtils {

    private AlertDialog.Builder builder;
    private Context activity;
    private Utils utils;
    private OfflineUtils offlineutils;

    public OnlineUtils(Context mActivity){
        activity = mActivity;
        utils = new Utils(activity);
        offlineutils = new OfflineUtils(activity);
    }

    public boolean is_online() {


        final boolean[] ret = new boolean[1];

        final Handler handler = new Handler();
        Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread th, Throwable ex) {
                ret[0] = false;
            }
        };
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    //lis l'ip depuis un fichier, à remplacer par ta méthode pour l'IP, le port ne change pas
                    InetSocketAddress sockAdr = new InetSocketAddress(utils.readFromFile(activity, "IP.Blue"), 8835);
                    Socket s = new Socket();
                    s.connect(sockAdr, 2000);
                    s.setSoTimeout(2 * 1000);
                    OutputStream out = s.getOutputStream();
                    PrintWriter output = new PrintWriter(out);
                    output.println("");
                    output.flush();
                    output.close();
                    out.close();
                    s.close();
                    ret[0] = true;
                } catch (IOException e) {

                }
            }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return ret[0];

    }

    public void show_blue_ip_popup() {
        builder = new AlertDialog.Builder(activity);
        builder.setTitle("Enter Blue IP");
        // Set up the input
        final EditText input = new EditText(activity);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);

        if (input.getParent() != null) {
            ((ViewGroup) input.getParent()).removeView(input);
        }
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                utils.writeToFile(input.getText().toString(), activity, "IP.Blue");
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                String json = utils.readFromFile(activity,"config.blue");
                JSONObject parsed_json = null;
                try {
                    parsed_json = new JSONObject(json);
                    parsed_json.remove("offline_mode");
                    parsed_json.put("offline_mode",true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                utils.writeToFile(parsed_json.toString(),activity,"config.blue");
                Toast.makeText(activity, "Offline mode activated.", Toast.LENGTH_SHORT).show();
            }
        });


        builder.show();
    }

    private void show_no_blue_access_popup() {
        builder = new AlertDialog.Builder(activity);
        builder.setTitle("Unable to communicate with your assistant");

        // Set up the buttons
        builder.setPositiveButton("Change assistant IP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                show_blue_ip_popup();
            }
        });
        builder.setNegativeButton("Change to offline mode", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });


        builder.show();
    }


    public void sendMessage(final String msg, String type) {
        //prepare JSON string
        String json;
        switch (type){
            case "website":
                json = "{ \"type\" : \"website\",\"url\" : \"" + msg + "\", \"battery\" : "+utils.get_battery_level()+",\"is_charging\" : "+utils.is_charging()+" }";

                break;
            case "voice_command":
                json = "{ \"type\" : \"voice_command\",\"voice_command\" : \"" + msg + "\", \"battery\" : "+utils.get_battery_level()+",\"is_charging\" : "+utils.is_charging()+" }";
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }

        Log.d("MSG", json);

        final Handler handler = new Handler();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    //lis l'ip depuis un fichier, à remplacer par ta méthode pour l'IP, le port ne change pas
                    InetSocketAddress sockAdr = new InetSocketAddress(utils.readFromFile(activity, "IP.Blue"), 8835);
                    Socket s = new Socket();
                    s.connect(sockAdr, 2000);
                    s.setSoTimeout(2 * 1000);
                    OutputStream out = s.getOutputStream();
                    PrintWriter output = new PrintWriter(out);

                    output.println(json);
                    output.flush();
                    Log.d("MSG", "msg sent");
                    output.close();
                    out.close();
                    s.close();
                } catch (IOException e) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            try{
                                show_no_blue_access_popup();
                            }catch (android.view.WindowManager.BadTokenException g){
                                show_no_blue_access_popup_from_service();
                            }

                        }
                    });

                }
            }
        });

        thread.start();
    }


    private void show_no_blue_access_popup_from_service() {
        builder = new AlertDialog.Builder(activity);

        builder.setTitle("Unable to communicate with your assistant");

        // Set up the buttons
        builder.setPositiveButton("Change assistant IP", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                show_blue_ip_popup_from_service();
            }
        });
        builder.setNegativeButton("Change to offline mode", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });


        AlertDialog alert = builder.create();
        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        alert.show();

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void show_blue_ip_popup_from_service() {
        builder = new AlertDialog.Builder(activity);
        builder.setTitle("Enter Blue IP");
        // Set up the input
        final EditText input = new EditText(activity);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);

        if (input.getParent() != null) {
            ((ViewGroup) input.getParent()).removeView(input);
        }
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                utils.writeToFile(input.getText().toString(), activity, "IP.Blue");
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                String json = utils.readFromFile(activity,"config.blue");
                JSONObject parsed_json = null;
                try {
                    parsed_json = new JSONObject(json);
                    parsed_json.remove("offline_mode");
                    parsed_json.put("offline_mode",true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                utils.writeToFile(parsed_json.toString(),activity,"config.blue");
                Toast.makeText(activity, "Offline mode activated.", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alert = builder.create();
        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        alert.show();

    }

}
