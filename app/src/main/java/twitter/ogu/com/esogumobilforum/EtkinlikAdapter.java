package twitter.ogu.com.esogumobilforum;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;


public class EtkinlikAdapter extends BaseAdapter {

    // Declare Variables
    Context context;
    ArrayList baslika=new ArrayList();
    ArrayList datea=new ArrayList();
    ArrayList onamea=new ArrayList();
    ArrayList icerika=new ArrayList();
    ArrayList tariha=new ArrayList();
    ArrayList ksayisia=new ArrayList();
    ArrayList baslikida=new ArrayList();
    String ksections;
    String kname;
    String aq;
    String krul;
    LayoutInflater inflater;

    //ListviewAdapter constructor
    //Gelen değerleri set ediyor
    public EtkinlikAdapter(Context context,ArrayList date, ArrayList oname,ArrayList icerik
                           ,ArrayList tarih,ArrayList ksayisi, ArrayList baslikid, ArrayList baslik,String ksections,String kname,String krul) {
        this.context = context;
        this.datea=date;
        this.onamea=oname;
        this.icerika=icerik;
        this.tariha=tarih;
        this.ksayisia=ksayisi;
        this.baslikida=baslikid;
        this.baslika=baslik;
        this.ksections=ksections;
        this.krul=krul;
        this.kname=kname;
    }

    @Override
    public int getCount() {
        return baslika.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, final ViewGroup parent) {



        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.etkinlikgoruntule, parent, false);//list_item_row dan yeni bir view oluşturuyoruz
        TextView kisisay,tarih,saat,bic,bac,kimmis;
        final Button katil;
        // oluşan itemviewin içindeki alanları Anasayfadan gelen değerler ile set ediyoruz
        kisisay=(TextView)itemView.findViewById(R.id.kisisayisi);
        tarih=(TextView)itemView.findViewById(R.id.etktarih);
        saat=(TextView)itemView.findViewById(R.id.etksaat);
        bic=(TextView)itemView.findViewById(R.id.iceriketk);
        bac=(TextView)itemView.findViewById(R.id.basliketk);
        kimmis=(TextView)itemView.findViewById(R.id.etkwho);
        katil=(Button)itemView.findViewById(R.id.etkkatil);
        String[] splited = datea.get(position).toString().split("\\s+");
        tarih.setText(splited[0]);
        saat.setText(splited[1]);
        aq=baslikida.get(position).toString();
        bic.setText(icerika.get(position).toString());
        bac.setText(baslika.get(position).toString());
        kimmis.setText(onamea.get(position).toString()+" tarafından oluşturuldu!");
        kisisay.setText(ksayisia.get(position).toString());
        SharedPreferences sharedPrefs =context.getSharedPreferences("MyPref", context.MODE_PRIVATE);
        //SharedPreferences sharedPrefs2 =context.getSharedPreferences("MyPref", context.MODE_PRIVATE);
        SharedPreferences.Editor ed;
        if(!sharedPrefs.contains(baslika.get(position).toString())){
            //ed = sharedPrefs.edit();

            //Indicate that the default shared prefs have been set
            // ed.putBoolean(baslika.get(position).toString(), true);

            //Set some default shared pref
            //ed.putString("myDefString", "wowsaBowsa");
            //begodev.com/learn2crack-login-register/etkinlikkayit.php
            //new SendPostRequest().execute();
            //ed.commit();
        }
        else{
            Toast.makeText(context, "Zaten Kayıtlısın!", Toast.LENGTH_SHORT).show();
            katil.setBackgroundResource(R.drawable.katil_aktif_background);
            katil.setText("Katıldın!");
        }
        katil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPrefs =context.getSharedPreferences("MyPref", context.MODE_PRIVATE);
                //SharedPreferences sharedPrefs2 =context.getSharedPreferences("MyPref", context.MODE_PRIVATE);
                SharedPreferences.Editor ed;
                if(!sharedPrefs.contains(baslika.get(position).toString())){
                    ed = sharedPrefs.edit();

                    //Indicate that the default shared prefs have been set
                    ed.putBoolean(baslika.get(position).toString(), true);

                    //Set some default shared pref
                    //ed.putString("myDefString", "wowsaBowsa");
                    //begodev.com/learn2crack-login-register/etkinlikkayit.php
                    new SendPostRequest().execute();
                    ed.commit();
                    katil.setBackgroundResource(R.drawable.katil_aktif_background);
                    katil.setText("Katıldın!");
                }
                else{
                    Toast.makeText(context, "Zaten Kayıtlısın aq", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return itemView;
    }
/**
 isim=pref.getString("name","");
 urlimage=pref.getString("imageUrl","");
 section=pref.getString("section","");
 ozel=pref.getString("surname","");
 *
 * */
public class SendPostRequest extends AsyncTask<String, Void, String> {
    ProgressDialog pDialog;

    protected void onPreExecute(){
        super.onPreExecute();
        // Showing progress dialog
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Adapter wait...");
        pDialog.setCancelable(true);
        pDialog.show();

    }

    protected String doInBackground(String... arg0) {
        try {
            URL url = new URL("http://begodev.com/learn2crack-login-register/etkinlikkayit.php"); // here is your URL path

            JSONObject postDataParams = new JSONObject();
            postDataParams.put("section", ksections);
            postDataParams.put("kname",kname);
            postDataParams.put("kurl", krul);
            postDataParams.put("idetk",aq);


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
