package com.example.blue;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.*;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OfflineUtils {

    private Context activity;
    private Utils utils;
    private static TextToSpeech tts = null;


    public OfflineUtils(Context mActivity){

        activity = mActivity;
        utils = new Utils(activity);

    }




    public boolean is_offline_mode_activated(){
        String json = utils.readFromFile(activity,"config.blue");
        JSONObject parsed_json = null;
        try {
            parsed_json = new JSONObject(json);
            return parsed_json.getBoolean("offline_mode");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void download_offline_files(){

        final String[] locale = {Locale.getDefault().toString().substring(0, 2)};

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(activity);
        //check if the current language is supported
        String url ="https://raw.githubusercontent.com/ThaaoBlues/Blue/main/language-files/supported_languages.txt";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Boolean found = false;

                        //loop throught array of supported languages
                        String[] languages = response.split("/");
                        for(int i = 0; i < languages.length;i++){
                            if(locale[0].equals(languages[i])){
                                found = true;
                                break;
                            }
                        }
                        if(!found){
                            locale[0] = "fr";
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        })/*{
            @Override
            public Map<String, String> getHeaders(){
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("User-agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.107 Safari/537.36 OPR/78.0.4093.112");
                return headers;
            }
        }*/;

        // Add the request to the RequestQueue.
        queue.add(stringRequest);


        url ="https://raw.githubusercontent.com/ThaaoBlues/Blue/main/language-files/"+locale[0]+"/skills.blue";

        // Request a string response from the provided URL.
        stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        utils.writeToFile(response,activity,"skills.blue");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });



        // Add the request to the RequestQueue.
        queue.add(stringRequest);

        url ="https://raw.githubusercontent.com/ThaaoBlues/Blue/main/language-files/"+locale[0]+"/unnecessary.blue";

        // Request a string response from the provided URL.
        stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        utils.writeToFile(response,activity,"unnecessary.blue");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

        Toast.makeText(activity,"Offline modules files updated.",Toast.LENGTH_SHORT).show();


    }



    public void process_voice_command(String voice_command){

        int ret = starred_sentences_ratio(voice_command);
        int ret2 = full_sentences_ratio(voice_command);
        String response = "";
        String module = "";

        if(ret != -1){
            module = get_module_by_index(ret);
        }else if( ret2 != -1){
            module = get_module_by_index(ret2);
            // to avoid different cases of return
            ret = ret2;
        }else{
            //Not recognized sentence, ask if wanna talk or to connect full blue machine
            speak("Je ne sais pas encore faire cela.");
            return;
        }

        Log.d("MODULE",module);
        switch (module){

            case "test":
                speak("test réussi !");
                break;
            case "google-search":
                google_search(voice_command,ret);
                break;

            case "open_website":
                open_website(voice_command,ret);

            case "youtube":
                youtube_search(voice_command,ret);
                break;

            case "say":
                say(voice_command,ret);
                break;

            case "maps":
                maps(voice_command,ret);
                break;

            case "camera":
                open_camera(voice_command,ret);
                break;

            case "countdown":
                countdown(voice_command,ret);
                break;

            case "twitch":
                twitch(voice_command,ret);
                break;

            case "heure":
                say_time(voice_command,ret);
                break;

            case "date":
                say_date(voice_command,ret);
                break;
            default:
                speak("Ce module n'est pas disponible " +
                        "sur l'application seule, connectez vous à un " +
                        "assistant Blue pour en bénéficier.");
                break;
        }

    }

    public void init_tts(){
        tts = new TextToSpeech(activity, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    tts.setLanguage(Locale.getDefault());
                }
            }

        });
    }

    public void speak(String sentence){

        tts.speak(sentence, TextToSpeech.QUEUE_FLUSH,null,"0000000");
    }


    private int starred_sentences_ratio(String voice_command){
        String[] sentences = get_sentences();
        //loop sentences
        for(int i = 0;i < sentences.length;i++){
            String[] sentences_decl = sentences[i].split("/");
            for(int j = 0; j < sentences_decl.length;j++) {
                String[] starred_split = sentences_decl[j].split("[*]");
                //loop sentences split by star (*)
                for (int h = 0; h < starred_split.length; h++) {
                    if (string_ratio(starred_split[h], voice_command) >= 80) {

                        //return the right module index ( basically the line in the file)
                        // if arrived at end of the starred sentence and
                        // it still match
                        if (j == starred_split.length - 1) {
                            return i;
                        }
                    } else {
                        break;
                    }

                }
            }
        }

        return -1;
    }

    private int full_sentences_ratio(String voice_command){
        String[] sentences = get_sentences();

        //loop sentences
        for(int i = 0;i < sentences.length;i++){
            String[] sentences_decl = sentences[i].split("/");
            for(int j = 0; j < sentences_decl.length;j++){
                if(string_ratio(sentences_decl[j],voice_command) > 68 ){
                    Toast.makeText(activity,""+i,Toast.LENGTH_SHORT).show();
                    return i;
                }
            }

        }
        return -1;
    }

    private String get_module_by_index(int index){
        String[] sentences = utils.readFromFile(activity,"skills.blue").split("&#10;");
        String module = sentences[index].substring(0,sentences[index].indexOf(':'));
        return module;
    }

    private String[] get_sentences(){
        String[] sentences = utils.readFromFile(activity,"skills.blue").split("&#10;");
        for(int i = 0; i<sentences.length;i++){
            sentences[i] = sentences[i].replace(sentences[i].substring(0,sentences[i].indexOf(':')+1),"");
        }
        return sentences;
    }


    public int string_ratio(String sentence,String voice_command){

        if(sentence.equals("")||sentence.length() <= 1){
            return 0;
        }

        int count = 0;
        String[] sentence_char_array = sentence.split("");
        String[] voice_command_char_array = voice_command.split("");
        int length;

        //take the shortest of the 2 string length
        if(sentence_char_array.length >= voice_command_char_array.length){
            length = voice_command_char_array.length;
        }else{
            length = sentence_char_array.length;
        }


        for(int i = 0; i<length;i++){
            if(sentence_char_array[i].equals(voice_command_char_array[i])){
                count ++;
            }
        }
        Log.d("WORD",""+sentence+"!!"+voice_command);
        Log.d("END","END WORD : "+count*100/(length));

        return count*100/(length);

    }

    private String strip_voice_command(String voice_command,int module_index){
        String[] words = get_sentences()[module_index].split("/");
        for(int i=0; i < words.length;i++){
            voice_command = voice_command.replace(words[i].replaceAll("[*]",""),"");
        }
        return voice_command;
    }


    //MODULES

    private void google_search(String voice_command,int module_index){
        //remove all unusefull words
        voice_command = strip_voice_command(voice_command,module_index);

        String escapedQuery = null;
        try {
            escapedQuery = URLEncoder.encode(voice_command, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Uri uri = Uri.parse("http://www.google.com/search?q=" + escapedQuery);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    private void open_website(String voice_command,int module_index){
        //remove all unusefull words and chars
        voice_command = voice_command.toLowerCase();
        voice_command = strip_voice_command(voice_command,module_index);
        voice_command = voice_command.replace(" ","");

        Uri uri = Uri.parse("http://www."+voice_command);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);

    }


    private void youtube_search(String voice_command,int module_index){

    }

    private void say(String voice_command,int module_index){
        speak(voice_command.replaceFirst(voice_command.split(" ")[0],""));
    }

    private void maps(String voice_command,int module_index){

    }

    private void say_date(String voice_command,int module_index){
        speak(utils.get_date());
    }

    private void say_time(String voice_command,int module_index){
        speak(utils.get_time());
    }

    private void twitch(String voice_command, int module_index){

    }

    private void countdown(String voice_command, int module_index){

    }

    private void open_camera(String voice_comma,int module_index){
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        activity.startActivity(intent);
    }

}
