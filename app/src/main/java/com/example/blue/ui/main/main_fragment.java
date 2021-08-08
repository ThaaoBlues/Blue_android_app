package com.example.blue.ui.main;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.blue.MainActivity;
import com.example.blue.R;
import com.example.blue.databinding.FragmentMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
import java.util.ArrayList;
import java.util.Locale;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import com.example.blue.ui.main.SectionsPagerAdapter;
import com.example.blue.databinding.ActivityMainBinding;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import android.os.Handler;
import android.speech.RecognizerIntent;
import android.text.InputType;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class main_fragment extends Fragment {

    private static final int RESULT_OK = 1;
    private FragmentMainBinding binding;
    private AlertDialog.Builder builder;
    private SpeechRecognizer speechRecognizer;


    public static main_fragment newInstance() {
        main_fragment fragment = new main_fragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentMainBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //get different components
        TextView recognized_text_view = root.findViewById(R.id.recognized_text_view);
        FloatingActionButton fab = root.findViewById(R.id.fab);


        //=======================================
        //Speech recognition stuff
        //=======================================
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            checkPermission();
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getActivity());

        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

                recognized_text_view.setBackgroundColor(getResources().getColor(R.color.white));


            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                recognized_text_view.setText(data.get(0));
                sendMessage(data.get(0));
            }

            @Override
            public void onPartialResults(Bundle bundle) {

                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                recognized_text_view.setText(data.get(0));

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });


        //=======================================
        //buttons stuff
        //=======================================
        if (readFromFile(getActivity(), "IP.Blue") == "") {
            show_blue_ip_popup();
        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
                recognized_text_view.setBackgroundColor(getResources().getColor(R.color.green));
                speechRecognizer.startListening(speechRecognizerIntent);
            }
        });


        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        speechRecognizer.destroy();
        binding = null;
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
        String json = "{ 'type' : 'voice_command', 'voice_command' : \"" + msg + "\", 'battery' : "+get_battery_level()+",'is_charging':"+is_charging()+" ";
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

    private String readFromFile(Context context, String FileName) {

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
    private void writeToFile(String data, Context context, String FileName) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(FileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


    // AUDIO PERMISSIONS STUFF
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_SHORT).show();
        }
    }


}