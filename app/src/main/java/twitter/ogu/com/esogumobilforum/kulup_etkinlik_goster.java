package twitter.ogu.com.esogumobilforum;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import twitter.ogu.com.esogumobilforum.R;

public class kulup_etkinlik_goster extends Activity implements SwipeRefreshLayout.OnRefreshListener {
    Button yarat;
    String ksections,isim,urlimage;
    private String TAG = kulup_etkinlik_goster.class.getSimpleName();
    private ProgressDialog pDialog;
    SharedPreferences myPrefs;
    ListView iceirkgosteren;
    String kulupsectioncheck;
    EtkinlikAdapter adapter;
    ArrayList baslika;
    ArrayList datea;
    ArrayList onamea;
    ArrayList icerika;
    ArrayList tariha;
    ArrayList ksayisia;
    ArrayList baslikida;
    private SharedPreferences pref;
    private SwipeRefreshLayout swipeRefreshLayout;

    // private ListView lv;
    private static String url = "http://begodev.com/learn2crack-login-register/etkinlikgetir.php";
    ArrayList<HashMap<String, String>> contactList ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kulup_etkinlik_goster);
        yarat=(Button)findViewById(R.id.yarata);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout2);
        swipeRefreshLayout.setOnRefreshListener(kulup_etkinlik_goster.this);
        contactList = new ArrayList<>();
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        ksections=pref.getString("section","");
        isim=pref.getString("name","");
        urlimage=pref.getString("imageUrl","");
        iceirkgosteren=(ListView)findViewById(R.id.mimago);
        myPrefs = getSharedPreferences("Kulüp", MODE_PRIVATE);
        kulupsectioncheck=myPrefs.getString("kulupsection", "");
        new GetContacts().execute();
        yarat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(kulup_etkinlik_goster.this,kulup_etkinlik_yarat.class);
                startActivity(i);
            }
        });

    }
    /**
     * Async task class to get json by making HTTP call
     */
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(kulup_etkinlik_goster.this);
            pDialog.setMessage("Hacking...");
            swipeRefreshLayout.setRefreshing(true);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray contacts = jsonObj.getJSONArray("result");
                     baslika=new ArrayList();
                     datea=new ArrayList();
                     onamea=new ArrayList();
                     icerika=new ArrayList();
                     tariha=new ArrayList();
                     ksayisia=new ArrayList();
                     baslikida=new ArrayList();
                    // looping through All Contacts
                    for (int i = 0; i < contacts.length(); i++) {
                        JSONObject c = contacts.getJSONObject(i);

                        String baslikid = c.getString("idetk");
                        String olusturanname = c.getString("oname");
                        String icerik = c.getString("icerik");
                        String olusturanurl = c.getString("ourl");
                        String baslik = c.getString("baslik");
                        String kulupsection = c.getString("kulupsection");
                        String osection = c.getString("olusturansection");
                        String ksayisi = c.getString("katilimcisayisi");
                        String nezaman = c.getString("nezaman");
                        String created_at = c.getString("created_at");

                        // Phone node is JSON Object
                        //JSONObject phone = c.getJSONObject("joinedusers");
                        //String innerbaslikid = phone.getString("kname");
                        //String innerksection = phone.getString("ksection");
                        //String kurl = phone.getString("krul");
                        //String cr_at = phone.getString("created_at");
                        Log.e("testlast",baslikid+olusturanname+baslik+icerik);
                        //ArrayList baslika=new ArrayList();
                        //ArrayList datea=new ArrayList();
                        //ArrayList onamea=new ArrayList();
                        //ArrayList icerika=new ArrayList();
                        //ArrayList tariha=new ArrayList();
                        //ArrayList ksayisia=new ArrayList();
                        //ArrayList baslikida=new ArrayList();
                        if(kulupsectioncheck.equals(kulupsection)){
                            baslika.add(baslik);
                            datea.add(nezaman);
                            onamea.add(olusturanname);
                            icerika.add(icerik);
                            tariha.add(created_at);
                            ksayisia.add(ksayisi);
                            baslikida.add(baslikid);

                        }
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            swipeRefreshLayout.setRefreshing(false);
            adapter=new EtkinlikAdapter(kulup_etkinlik_goster.this,datea,onamea,icerika,tariha,ksayisia,baslikida,baslika,ksections,isim,urlimage);
            iceirkgosteren.setAdapter(adapter);


        }
/**
        baslika.add(baslik);
        datea.add(nezaman);
        onamea.add(olusturanname);
        icerika.add(icerik);
        tariha.add(created_at);
        ksayisia.add(ksayisi);
        baslikida.add(baslikid);*/
    }

    @Override
    public void onRefresh() {
        new GetContacts().execute();
    }
}
