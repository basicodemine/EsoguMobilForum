package twitter.ogu.com.esogumobilforum;

import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.adeel.library.easyFTP;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import twitter.ogu.com.esogumobilforum.models.ServerRequest;
import twitter.ogu.com.esogumobilforum.models.ServerResponse;
import twitter.ogu.com.esogumobilforum.models.User;

/**
 * Created by eGo on 06/11/16.
 */
public class FTKayit {
    public void registerProcess(String name, String email, String password, String section){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestInterface requestInterface = retrofit.create(RequestInterface.class);
        User user = new User();
        user.setName(name);   //isim
        user.setEmail(email); //img url
        user.setPassword(password); //id
        ServerRequest request = new ServerRequest();
        Log.e("section kayıttaki:",section);

        if(section.equals("facebook")) {
            request.setOperation(Constants.REGISTER_OPERATIONF);
            Log.e("section kayıttaki:","facebookasd");
        }
        if(section.equals("twitter")) {
            request.setOperation(Constants.REGISTER_OPERATIONT);
            Log.e("section kayıttaki:","twitterasd");
        }
        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);
        //aşağıdaki kodu asyncye taşı
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {

                ServerResponse resp = response.body();
                Log.e("gelen",resp.getMessage());

            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

                Log.d(Constants.TAG,"hata");
                Log.d(Constants.TAG,t.getLocalizedMessage());

            }
        });
    }
}
