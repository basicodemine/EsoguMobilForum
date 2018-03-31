package twitter.ogu.com.esogumobilforum;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.adeel.library.easyFTP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

/**

 i.putExtra("baslikicerik",baslikicerik[position]);
 i.putExtra("baslikresimurl",baslikresimleri[position]);
 i.putExtra("baslikacan",acan[position]);
 i.putExtra("baslikacanrul",acanurl[position]);
 i.putExtra("basliksaat",saatler[position]);
 i.putExtra("baslikbaslik",basliklar[position]);

**/
public class AnaForumKonuIzle extends Activity {
    String baslikicerik,baslikresimurl,baslikacan,baslikacanurl,basliksaat,baslikbaslik,baslikid;
    TextView tbaslikicerik,tbaslikacan,tbasliksaat,tbaslikbaslik;
    ListView list; //ListView referansı
    Button yolla;
    private SharedPreferences pref;
    EditText yollanacakyorum;
    String yollanacakstring;
    String nestedjsons;
    ArrayList<String> urls=new ArrayList<String>();
    ArrayList<String> names=new ArrayList<String>();
    ArrayList<String> dates=new ArrayList<String>();
    ArrayList<String> yorums=new ArrayList<String>();

    AnaFKonuAdapter adapter; // adapter referansı
    String[] urller;
    String[] yorumlar;
    String[] name;
    String[] date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ana_forum_konu_izle);
        pref = this.getSharedPreferences("MyPref", this.MODE_PRIVATE);
        Intent intent = getIntent();
        baslikid=intent.getStringExtra("baslikid");
        baslikacan=intent.getStringExtra("baslikacan");
        nestedjsons=intent.getStringExtra("nestedjsons");
        baslikacanurl=intent.getStringExtra("baslikacanrul");
        baslikresimurl=intent.getStringExtra("baslikresimurl");
        baslikicerik=intent.getStringExtra("baslikicerik");
        baslikbaslik=intent.getStringExtra("baslikbaslik");
        basliksaat=intent.getStringExtra("basliksaat");
        yolla=(Button)findViewById(R.id.button);
        yollanacakyorum=(EditText)findViewById(R.id.editText);
        tbaslikbaslik=(TextView)findViewById(R.id.textView);
        tbaslikicerik=(TextView)findViewById(R.id.textView2);
        list=(ListView)findViewById(R.id.sehirList) ;
        tbaslikacan=(TextView)findViewById(R.id.textView3);
        tbaslikacan.setText(baslikacan+"\n"+basliksaat);
        tbaslikicerik.setText(baslikicerik);
        tbaslikbaslik.setText(baslikbaslik);

        Log.e("urlneymis  ",nestedjsons);
        ParseJSON();
       new DownloadImage((ImageView)findViewById(R.id.izlebaslikresim)).execute("http://begodev.com/oguforum/"+baslikresimurl.replaceAll("[^\\p{L}\\p{Z}]","")+".jpg");
       new DownloadImage2((ImageView)findViewById(R.id.izlebaslikacanresim)).execute(baslikacanurl);
        yolla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //yolla
                yollanacakstring=yollanacakyorum.getText().toString();
                new SendPostRequest().execute();
            }
        });
    }
    public class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
    public class DownloadImage2 extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImage2(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try {
                URL url = new URL("http://begodev.com/learn2crack-login-register/anakonuyayorum.php"); // here is your URL path

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("baslikid", baslikid);
                postDataParams.put("useid", pref.getString("surname",""));
                postDataParams.put("section",pref.getString("section",""));
                postDataParams.put("body",yollanacakstring);
//yollanacak parametreleri ayarladık en son.
                Log.e("params",postDataParams.toString());

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
            Log.e("response server:",result);
            yollanacakyorum.setText("");
            if(result.contains("101")){
            new GetStudents().execute();
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

        }
        return result.toString();
    }
    private void ParseJSON() {
        if (nestedjsons != null) {
            try {
                // Hashmap for ListView
                urls=new ArrayList<String>();
                yorums=new ArrayList<String>();
                names=new ArrayList<String>();
                dates=new ArrayList<String>();

                // Getting JSON Array node
                JSONArray anaforum = new JSONArray(nestedjsons);
                Log.e("json gelen",anaforum.toString());



                // looping through All Students
                for (int i = 0; i < anaforum.length(); i++) {
                    JSONObject c = anaforum.getJSONObject(i);
                    if(c.getString("baslikid").equals(baslikid)){
                        urls.add(c.getString("usurl"));
                        names.add(c.getString("usname"));
                        dates.add(c.getString("created_at"));
                        yorums.add(c.getString("body"));
                        Log.e("url vs parsed inner:", ""+anaforum.length() );
                    }
                }
                date=new String[dates.size()];
                urller=new String[urls.size()];
                name=new String[names.size()];
                urller=new String[urls.size()];
                yorumlar=new String[yorums.size()];
                urller=urls.toArray(urller);
                yorumlar=yorums.toArray(yorumlar);
                name=names.toArray(name);
                date=dates.toArray(date);
                adapter=new AnaFKonuAdapter(this,yorumlar,urller,date,name);
                list.setAdapter(adapter);//adı üstünde set ediyoruz

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("ServiceHandler", "Couldn't get any data from the url");
        }
    }
    private class GetStudents extends AsyncTask<Void, Void, Void> {

        // Hashmap for ListView
        ArrayList<HashMap<String, String>> studentList;
        ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(AnaForumKonuIzle.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            WebRequest webreq = new WebRequest();

            // Making a request to url and getting response
            String jsonStr = webreq.makeWebServiceCall("http://begodev.com/learn2crack-login-register/anaforumbaslikgetir.php", WebRequest.GET);

            Log.d("Response: ", "> " + jsonStr);
            ParseJSON2(jsonStr);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            ParseJSON();
        }

    }
    private void ParseJSON2(String json) {
        if (json != null) {
            try {
                // Hashmap for ListView

                JSONObject jsonObj = new JSONObject(json);

                // Getting JSON Array node
                JSONArray anaforum = jsonObj.getJSONArray("result");
                // looping through All Students
                for (int i = 0; i < anaforum.length(); i++) {
                    JSONObject c = anaforum.getJSONObject(i);
                    JSONArray prods = c.getJSONArray("post");
                    if(prods != null){
                        nestedjsons=prods.toString();
                        Log.e("Stringe dönenJSON:",nestedjsons);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("ServiceHandler", "Couldn't get any data from the url");
        }
    }
}
