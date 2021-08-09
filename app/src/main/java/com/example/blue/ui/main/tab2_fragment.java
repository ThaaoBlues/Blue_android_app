package com.example.blue.ui.main;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.blue.OfflineUtils;
import com.example.blue.OnlineUtils;
import com.example.blue.R;
import com.example.blue.Utils;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link tab2_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class tab2_fragment extends Fragment {

    private Utils utils;
    private OnlineUtils onlineutils;
    private OfflineUtils offlineUtils;


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


        //init utils constructors
        utils = new Utils(getActivity());
        onlineutils = new OnlineUtils(getActivity());
        offlineUtils = new OfflineUtils(getActivity());

        Button configure_blue_button = root.findViewById(R.id.configure_blue_button);
        Button send_website_button = root.findViewById(R.id.send_website_button);
        EditText edittext_website_url = root.findViewById(R.id.edittext_website_url);

        configure_blue_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://"+utils.readFromFile(getActivity(),"IP.Blue")));
                startActivity(browserIntent);
            }
        });

        send_website_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = edittext_website_url.getText().toString();
                if(url.length() >= 3){
                    onlineutils.sendMessage(url,"website");
                }
            }
        });

        // Inflate the layout for this fragment
        return root;

    }


}