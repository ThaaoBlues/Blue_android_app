package com.example.blue.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.blue.R;
import com.example.blue.databinding.FragmentMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Locale;

import com.example.blue.Utils;
import com.example.blue.OnlineUtils;
import com.example.blue.OfflineUtils;


/**
 * A placeholder fragment containing a simple view.
 */
public class main_fragment extends Fragment {

    private static final int RESULT_OK = 1;
    private FragmentMainBinding binding;
    private SpeechRecognizer speechRecognizer;
    private Utils utils;
    private OnlineUtils onlineutils;
    private OfflineUtils offlineutils;


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

        //init utils constructors
        utils = new Utils(getActivity());
        onlineutils = new OnlineUtils(getActivity());
        offlineutils = new OfflineUtils(getActivity());


        //get different components
        TextView recognized_text_view = root.findViewById(R.id.recognized_text_view);
        FloatingActionButton fab = root.findViewById(R.id.fab);

        //init tts engine
        offlineutils.init_tts();


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
                if(!offlineutils.is_offline_mode_activated()){
                    onlineutils.sendMessage(data.get(0),"voice_command");
                }else{
                    offlineutils.process_voice_command(data.get(0));
                }
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
        if (utils.readFromFile(getActivity(), "IP.Blue") == "") {
            onlineutils.show_blue_ip_popup();
        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
                recognized_text_view.setBackgroundColor(getResources().getColor(R.color.green));
                speechRecognizer.startListening(speechRecognizerIntent);
            }
        });

        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                offlineutils.process_voice_command("test");
                Toast.makeText(getActivity(),"Testing software...",Toast.LENGTH_SHORT).show();
                return false;
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