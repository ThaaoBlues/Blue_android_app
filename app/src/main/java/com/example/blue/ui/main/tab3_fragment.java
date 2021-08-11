package com.example.blue.ui.main;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.blue.FloatWidgetService;
import com.example.blue.MainActivity;
import com.example.blue.OfflineUtils;
import com.example.blue.OnlineUtils;
import com.example.blue.R;
import com.example.blue.Utils;



public class tab3_fragment extends Fragment {

    private Utils utils;
    private OnlineUtils onlineutils;
    private OfflineUtils offlineutils;
    private Context mContext;
    public static Intent service_intent;




    public tab3_fragment(Intent mServiceIntent) {
        // Required empty public constructor
        service_intent = mServiceIntent;
    }


    public static tab3_fragment newInstance() {
        tab3_fragment fragment = new tab3_fragment(service_intent);

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_tab3, container, false);

        //init utils constructors
        utils = new Utils(getActivity());
        onlineutils = new OnlineUtils(getActivity());
        offlineutils = new OfflineUtils(getActivity());

        //init buttons
        Button change_blue_ip_button = root.findViewById(R.id.change_blue_ip_button);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch offline_mode_switch = root.findViewById(R.id.offline_mode_switch);
        Button display_bubble_button = root.findViewById(R.id.display_bubble_button);




        //set the good state to the switch by reading the config.blue file
        if(offlineutils.is_offline_mode_activated()){
            offline_mode_switch.setChecked(true);
        }else{
            offline_mode_switch.setChecked(false);
        }


        change_blue_ip_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onlineutils.show_blue_ip_popup();
            }
        });


        display_bubble_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    getActivity().startService(service_intent);
                    getActivity().finish();
                } else if (Settings.canDrawOverlays(getActivity())) {
                    getActivity().startService(service_intent);
                    getActivity().finish();
                } else {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getActivity().getPackageName()));
                    getActivity().startActivity(intent);
                    Toast.makeText(getActivity(), "You need System Alert Window Permission to do this", Toast.LENGTH_SHORT).show();
                }
            }
        });




        offline_mode_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    //checked
                    String json = utils.readFromFile(getActivity(),"config.blue");
                    JSONObject parsed_json = null;
                    try {
                        parsed_json = new JSONObject(json);
                        parsed_json.remove("offline_mode");
                        parsed_json.put("offline_mode",true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    utils.writeToFile(parsed_json.toString(),getActivity(),"config.blue");
                    Toast.makeText(getActivity(), "Offline mode activated.", Toast.LENGTH_SHORT).show();


                }else{
                    //unchecked
                    String json = utils.readFromFile(getActivity(),"config.blue");
                    JSONObject parsed_json = null;
                    try {
                        parsed_json = new JSONObject(json);
                        parsed_json.remove("offline_mode");
                        parsed_json.put("offline_mode",false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    utils.writeToFile(parsed_json.toString(),getActivity(),"config.blue");
                    Toast.makeText(getActivity(), "Offline mode desactivated.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Inflate the layout for this fragment
        return root;
    }



}