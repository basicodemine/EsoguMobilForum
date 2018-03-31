package twitter.ogu.com.esogumobilforum;

import android.*;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.adeel.library.easyFTP;

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
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import twitter.ogu.com.esogumobilforum.models.ServerRequest;
import twitter.ogu.com.esogumobilforum.models.ServerResponse;
import twitter.ogu.com.esogumobilforum.models.User;

public class ForumInsert extends Activity {
    Button yolla;
    ProgressBar progress;
    EditText baslik;
    EditText icerik;
    Button ressec;
    String imgDecodableString;
    String ic,bs,id,sc;
    ImageView gosterbakamsecileni;
    View view;
    private SharedPreferences pref;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_insert);
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        gosterbakamsecileni=(ImageView)findViewById(R.id.secilenresim);
        view = findViewById(R.id.asdlolxD);
        ressec=(Button)findViewById(R.id.resimyukle1);
        yolla=(Button)findViewById(R.id.yolla);
        progress=(ProgressBar)findViewById(R.id.progress);
        baslik=(EditText)findViewById(R.id.baslikana);
        icerik=(EditText)findViewById(R.id.icerikana);
        ressec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shouldAskPermissions()) {
                    askPermissions();
                }
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, 1);
            }
        });
        yolla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            registerProcess();
            }
        });
    }
    private void registerProcess(){
        ic=icerik.getText().toString();
        bs=baslik.getText().toString();
        sc=pref.getString("section","");
        if(sc.equals("facebook")||sc.equals("twitter")){id=pref.getString("surname","");
        }
        else{id=pref.getString("email","");}
        //aşağıdaki kodu asyncye taşı
        new SendPostRequest().execute();

        }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                Log.e("path:::",imgDecodableString);
                cursor.close();
                // Set the Image in ImageView after decoding the String
                gosterbakamsecileni.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
                gosterbakamsecileni.setTag("var");

            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }

    }
    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(23)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        int requestCode = 200;
        requestPermissions(permissions, requestCode);
    }
    public class SendPostRequest extends AsyncTask<String, Void, String> {
        ProgressDialog pDialog;

        protected void onPreExecute(){
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ForumInsert.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        protected String doInBackground(String... arg0) {

            try {
                FTPClient con = null;
                try
                {
                    con = new FTPClient();
                    con.connect("begodev.com");

                    if (con.login("oguforum@begodev.com","asdqwe123"))
                    {
                        con.enterLocalPassiveMode(); // important!
                        con.setFileType(FTP.BINARY_FILE_TYPE);
                        String data = imgDecodableString;

                        FileInputStream in = new FileInputStream(new File(data));
                        boolean result = con.storeFile(bs.replaceAll("\\s+","")+".jpg", in);
                        in.close();
                        if (result) Log.v("upload result", "succeeded");
                        con.logout();
                        con.disconnect();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                URL url = new URL("http://begodev.com/learn2crack-login-register/anaforumbaslikac.php"); // here is your URL path

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("title", bs);
                postDataParams.put("usid", id);
                postDataParams.put("section",sc);
                postDataParams.put("body", ic);
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
                Toast.makeText(ForumInsert.this, "Başlığınız Başarı ile Açıldı!", Toast.LENGTH_SHORT).show();
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

        }
        return result.toString();
    }
}
