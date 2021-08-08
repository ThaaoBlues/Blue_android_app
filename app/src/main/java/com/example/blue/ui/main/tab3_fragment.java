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

import com.example.blue.R;
import com.example.blue.databinding.FragmentMainBinding;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class tab3_fragment extends Fragment {

    private FragmentMainBinding binding;
    private AlertDialog.Builder builder;





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

        Button change_blue_ip_button = root.findViewById(R.id.change_blue_ip_button);


        change_blue_ip_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_blue_ip_popup();
            }
        });



        // Inflate the layout for this fragment
        return root;
    }

    private void show_blue_ip_popup(){
        builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Enter Blue IP");
        // Set up the input
        final EditText input = new EditText(getActivity());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);

        if(input.getParent()!=null){
            ((ViewGroup)input.getParent()).removeView(input);
        }
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                writeToFile(input.getText().toString(),getActivity(),"IP.Blue");
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




}