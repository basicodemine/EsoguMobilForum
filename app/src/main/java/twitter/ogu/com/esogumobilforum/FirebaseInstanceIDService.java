package twitter.ogu.com.esogumobilforum;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by eGo on 08/11/16.
 */
public class FirebaseInstanceIDService extends FirebaseInstanceIdService {
    String noob;
    SharedPreferences pref;
    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        registerToken(token);
        Log.d("TOKEN Verildi", token);
    }

    private void registerToken(String token){
        OkHttpClient client = new OkHttpClient();
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        RequestBody body = new FormBody.Builder()
                .add("Token", token)
                .build();

        Request request = new Request.Builder()
                .url("http://begodev.com/basgitsin/istekkaydet.php") // Sunucumuzdaki servis. Localhost'um 8080 portu üzerinde çalışıyor.
                .post(body)
                .build();
        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void setNoob(String asd){
        noob=asd;
    }
}
