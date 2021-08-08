package com.example.blue.ui.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.blue.R;
import com.example.blue.databinding.FragmentMainBinding;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link tab2_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class tab2_fragment extends Fragment {

    private AlertDialog.Builder builder;



    public tab2_fragment() {
        // Required empty public constructor
    }

    public static tab2_fragment newInstance() {
        tab2_fragment fragment = new tab2_fragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tab2, container, false);

        Button configure_blue_button = root.findViewById(R.id.configure_blue_button);
        Button send_website_button = root.findViewById(R.id.send_website_button);
        EditText edittext_website_url = root.findViewById(R.id.edittext_website_url);

        configure_blue_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://"+readFromFile(getActivity(),"IP.Blue")));
                startActivity(browserIntent);
            }
        });

        send_website_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = edittext_website_url.getText().toString();
                if(url.length() >= 3){
                    sendMessage(url);
                }
            }
        });

        // Inflate the layout for this fragment
        return root;

    }




    private String readFromFile(Context context, String FileName) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(FileName);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString().replaceAll("\n","");
            }
        }
        catch (FileNotFoundException e) {
            return "";
        } catch (IOException e) {
            return "";

        }

        return ret;
    }



    //ECRIRE DANS UN FICHIER PRIVE DE L'APP
    private void writeToFile(String data, Context context, String FileName) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(FileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


    private void show_blue_ip_popup() {
        builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Enter Blue IP");
        // Set up the input
        final EditText input = new EditText(getActivity());
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
                writeToFile(input.getText().toString(), getActivity(), "IP.Blue");
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

    private void sendMessage(final String msg) {
        //prepare JSON string
        String json = "{ 'type' : 'website', 'url' : \"" + msg + "\", 'battery' : "+get_battery_level()+",'is_charging':"+is_charging()+" ";
        Log.d("MSG", json);

        final Handler handler = new Handler();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    //lis l'ip depuis un fichier, à remplacer par ta méthode pour l'IP, le port ne change pas
                    InetSocketAddress sockAdr = new InetSocketAddress(readFromFile(getActivity(), "IP.Blue"), 8835);
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
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            show_blue_ip_popup();
                        }
                    });

                }
            }
        });

        thread.start();
    }

    private boolean is_charging() {

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getActivity().registerReceiver(null, ifilter);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        return status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;

    }

    private float get_battery_level(){

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getActivity().registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        return level * 100 / (float)scale;
    }



}