package com.example.blue.ui.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.blue.OfflineUtils;
import com.example.blue.OnlineUtils;
import com.example.blue.R;
import com.example.blue.Utils;
import com.example.blue.databinding.FragmentMainBinding;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class tab3_fragment extends Fragment {

    private Utils utils;
    private OnlineUtils onlineutils;
    private OfflineUtils offlineUtils;





    public tab3_fragment() {
        // Required empty public constructor
    }


    public static tab3_fragment newInstance() {
        tab3_fragment fragment = new tab3_fragment();

        return fragment;
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
        offlineUtils = new OfflineUtils(getActivity());

        //init buttons
        Button change_blue_ip_button = root.findViewById(R.id.change_blue_ip_button);


        change_blue_ip_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onlineutils.show_blue_ip_popup();
            }
        });



        // Inflate the layout for this fragment
        return root;
    }



}