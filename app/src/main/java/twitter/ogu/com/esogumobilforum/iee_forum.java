package twitter.ogu.com.esogumobilforum;

import android.os.Bundle;
import android.app.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class iee_forum extends Activity {
    //its menu over here
    ImageButton go_Forum;
    private SharedPreferences pref;
    SharedPreferences sharedpreferences;
    TextView takipet;
    String ksections,kname,krul;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iee_forum);
        sharedpreferences = getSharedPreferences("Kulüp", Context.MODE_PRIVATE);
        takipet=(TextView)findViewById(R.id.takip);
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        go_Forum=(ImageButton)findViewById(R.id.imagefrm);
        kname=pref.getString("name","");
        krul=pref.getString("imageUrl","");
        ksections=pref.getString("section","");
        go_Forum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(iee_forum.this,kulup_etkinlik_goster.class);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("kulupsection", "iee");
                editor.commit();
                startActivity(i);
            }
        });
        SharedPreferences sharedPrefs =getSharedPreferences("MyPrefss", MODE_PRIVATE);
        //SharedPreferences sharedPrefs2 =context.getSharedPreferences("MyPref", context.MODE_PRIVATE);
        SharedPreferences.Editor ed;
        if(sharedPrefs.contains("iee")){
            takipet.setBackgroundResource(R.drawable.aktif_takipet);
            takipet.setText("    Takiptesin!");
        }

        takipet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPrefs =getSharedPreferences("MyPrefss", MODE_PRIVATE);
                //SharedPreferences sharedPrefs2 =context.getSharedPreferences("MyPref", context.MODE_PRIVATE);
                SharedPreferences.Editor ed;
                if(!sharedPrefs.contains("iee")){
                    ed = sharedPrefs.edit();

                    //Indicate that the default shared prefs have been set
                    ed.putBoolean("iee", true);

                    //Set some default shared pref
                    //ed.putString("myDefString", "wowsaBowsa");
                    //begodev.com/learn2crack-login-register/etkinlikkayit.php
                    new SendPostRequest().execute();
                    ed.commit();
                    takipet.setBackgroundResource(R.drawable.aktif_takipet);
                    takipet.setText("Takiptesin!");
                }
                else{
                    Toast.makeText(iee_forum.this, "Zaten Kayıtlısın!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public class SendPostRequest extends AsyncTask<String, Void, String> {
        ProgressDialog pDialog;

        protected void onPreExecute(){
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(iee_forum.this);
            pDialog.setMessage("Adapter wait...");
            pDialog.setCancelable(true);
            pDialog.show();

        }

        protected String doInBackground(String... arg0) {
            try {
                URL url = new URL("http://begodev.com/learn2crack-login-register/kulupkayit.php"); // here is your URL path

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("section", ksections);
                postDataParams.put("krul",krul);
                postDataParams.put("kname", kname);
                postDataParams.put("kusec","iee");


//yollanacak parametreleri ayarladık en son.
                Log.e("param adapterr!",postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }
                    in.close();
                    return sb.toString();
                }
                else {
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if(result.contains("101")){
                if (pDialog.isShowing())
                    pDialog.dismiss();
            }
        }
    }
    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
            Log.e("resp:",result.toString());

        }
        return result.toString();
    }

}

