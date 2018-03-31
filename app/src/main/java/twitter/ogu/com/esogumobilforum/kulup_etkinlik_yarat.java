package twitter.ogu.com.esogumobilforum;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class kulup_etkinlik_yarat extends Activity {
    private DatePicker datePicker;
    private Calendar calendar;
    private TextView dateView,hourView;
    EditText baslik,icerik;
    String saat;
    String tarih;
    Button setdate,sethour;
    String isim;
    String urlimage;
    String section;
    String eb,ea;
    String ozel;
    Button olustur;
    private SharedPreferences pref;
    SharedPreferences myPrefs;
    String kulupsection;
    private int year, month, day;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kulup_etkinlik_yarat);
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        dateView = (TextView) findViewById(R.id.dateshow);
        calendar = Calendar.getInstance();
        baslik=(EditText)findViewById(R.id.etadi);
        olustur=(Button)findViewById(R.id.olusturgari);
        icerik=(EditText)findViewById(R.id.etac);
        setdate=(Button)findViewById(R.id.setdate);
        year = calendar.get(Calendar.YEAR);
        sethour=(Button)findViewById(R.id.sethour);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hourView=(TextView)findViewById(R.id.hourshow);
        myPrefs = getSharedPreferences("Kulüp", MODE_PRIVATE);
        kulupsection=myPrefs.getString("kulupsection", "");
        isim=pref.getString("name","");
        urlimage=pref.getString("imageUrl","");
        section=pref.getString("section","");
        ozel=pref.getString("surname","");
        Log.e("gelenler:",urlimage+section+ozel+isim+kulupsection);
        showDate(year, month+1, day);
        sethour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setHour();
            }
        });
        setdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate(v);
            }
        });
        olustur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eb=baslik.getText().toString();
                ea=icerik.getText().toString();
                tarih=dateView.getText().toString();
                new SendPostRequest().execute();
            }
        });
    }
    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
        Toast.makeText(getApplicationContext(), "ca",
                Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2+1, arg3);
                }
            };

    private void showDate(int year, int month, int day) {
        dateView.setText(new StringBuilder().append(year).append("-")
                .append(month).append("-").append(day));
    }
    private void setHour(){
        final NumberPicker picker = new NumberPicker(this);
        picker.setMinValue(00);
        picker.setMaxValue(24);
        picker.setValue(12);
        final NumberPicker picker2 = new NumberPicker(this);
        picker2.setMinValue(00);
        picker2.setMaxValue(60);
        picker2.setValue(30);

        final FrameLayout layout = new FrameLayout(this);
        layout.addView(picker, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.START));
        layout.addView(picker2, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.END));

        new AlertDialog.Builder(this)
                .setMessage("SAAT/DAKİKA")
                .setView(layout)
                .setPositiveButton("Saati Ayarla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    hourView.setText(""+picker.getValue()+":"+picker2.getValue());
                    saat=""+picker.getValue()+":"+picker2.getValue();
                    }
                })
                .setNegativeButton("Vazgeç", null)
                .show();
    }
    public class SendPostRequest extends AsyncTask<String, Void, String> {
        ProgressDialog pDialog;

        protected void onPreExecute(){
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(kulup_etkinlik_yarat.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        protected String doInBackground(String... arg0) {
            try {
                URL url = new URL("http://begodev.com/learn2crack-login-register/etkinlikolustur.php"); // here is your URL path
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("osection", section);
                postDataParams.put("ksection", kulupsection);
                postDataParams.put("oname",isim);
                postDataParams.put("ourl", urlimage);
                postDataParams.put("ebaslik",eb );
                postDataParams.put("ebody", ea);
                postDataParams.put("date", tarih);
                postDataParams.put("time", saat);

                //yollanacak parametreleri ayarladım en son.
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
            if(result.contains("101")){
                if (pDialog.isShowing())
                    pDialog.dismiss();
                new sendpush().execute();
                Toast.makeText(kulup_etkinlik_yarat.this, "Başlığınız Başarı ile Açıldı!"+result, Toast.LENGTH_SHORT).show();
                finish();
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
    public class sendpush extends AsyncTask<String, Void, String> {
        ProgressDialog pDialog;

        protected void onPreExecute(){
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(kulup_etkinlik_yarat.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            //pDialog.show();

        }

        protected String doInBackground(String... arg0) {
            try {
                URL url = new URL("http://begodev.com/basgitsin/yollapanpa.php"); // here is your URL path
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("body", "Sanırım "+kulupsection+" kulübü bir etkinlik düzenliyor: "+ea +" Göz atmak için tıkla!");

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
            if(result.contains("101")){
                if (pDialog.isShowing())
                    pDialog.dismiss();
                //Toast.makeText(kulup_etkinlik_yarat.this, "Başlığınız Başarı ile Açıldı!"+result, Toast.LENGTH_SHORT).show();
                //finish();
            }
        }
    }
    public String getPostDataString2(JSONObject params) throws Exception {

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

