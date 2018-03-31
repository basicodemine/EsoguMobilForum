package twitter.ogu.com.esogumobilforum;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.login.LoginFragment;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import twitter.ogu.com.esogumobilforum.models.ServerRequest;
import twitter.ogu.com.esogumobilforum.models.ServerResponse;
import twitter.ogu.com.esogumobilforum.models.User;

public class SifremiUnuttum extends Activity implements View.OnClickListener{

private AppCompatButton btn_reset;
private EditText et_email,et_code,et_password;
private TextView tv_timer;
private ProgressBar progress;
private boolean isResetInitiated = false;
private String email;
private CountDownTimer countDownTimer;
    View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sifremi_unuttum);
        initViews();
        view = findViewById(R.id.gege);
    }

private void initViews(){

        btn_reset = (AppCompatButton)findViewById(R.id.btn_reset);
        tv_timer = (TextView)findViewById(R.id.timer);
        et_code = (EditText)findViewById(R.id.et_code);
        et_email = (EditText)findViewById(R.id.et_email);
        et_password = (EditText)findViewById(R.id.et_password);
        btn_reset.setOnClickListener(this);
        progress = (ProgressBar)findViewById(R.id.progress);
        et_password.setVisibility(View.GONE);
        et_code.setVisibility(View.GONE);
        tv_timer.setVisibility(View.GONE);


        }

@Override
public void onClick(View v) {
        switch (v.getId()){

        case R.id.btn_reset:

        if(!isResetInitiated) {


        email = et_email.getText().toString();
        if (!email.isEmpty()) {
        progress.setVisibility(View.VISIBLE);
        initiateResetPasswordProcess(email);
        } else {

        Snackbar.make(getView(), "Boşlukları Doldurun !", Snackbar.LENGTH_LONG).show();
        }
        } else {

        String code = et_code.getText().toString();
        String password = et_password.getText().toString();

        if(!code.isEmpty() && !password.isEmpty()){

        finishResetPasswordProcess(email,code,password);
        } else {

        Snackbar.make(getView(), "Boşlukları Doldurun !", Snackbar.LENGTH_LONG).show();
        }

        }

        break;
        }
        }

private void initiateResetPasswordProcess(String email){

        Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        User user = new User();
        user.setEmail(email);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.RESET_PASSWORD_INITIATE);
        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
@Override
public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {

        ServerResponse resp = response.body();
        Snackbar.make(getView(), resp.getMessage(), Snackbar.LENGTH_LONG).show();

        if(resp.getResult().equals(Constants.SUCCESS)){

        Snackbar.make(getView(), resp.getMessage(), Snackbar.LENGTH_LONG).show();
        et_email.setVisibility(View.GONE);
        et_code.setVisibility(View.VISIBLE);
        et_password.setVisibility(View.VISIBLE);
        tv_timer.setVisibility(View.VISIBLE);
        btn_reset.setText("Şifreni Değiştir");
        isResetInitiated = true;
        startCountdownTimer();

        } else {

        Snackbar.make(getView(), resp.getMessage(), Snackbar.LENGTH_LONG).show();

        }
        progress.setVisibility(View.INVISIBLE);
        }

@Override
public void onFailure(Call<ServerResponse> call, Throwable t) {

        progress.setVisibility(View.INVISIBLE);
        Log.d(Constants.TAG,"failed");
        Snackbar.make(getView(), t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();

        }
        });
        }

private void finishResetPasswordProcess(String email,String code, String password){

        Retrofit retrofit = new Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        User user = new User();
        user.setEmail(email);
        user.setCode(code);
        user.setPassword(password);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.RESET_PASSWORD_FINISH);
        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
@Override
public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {

        ServerResponse resp = response.body();
        Snackbar.make(getView(), resp.getMessage(), Snackbar.LENGTH_LONG).show();

        if(resp.getResult().equals(Constants.SUCCESS)){

        Snackbar.make(getView(), resp.getMessage(), Snackbar.LENGTH_LONG).show();
        countDownTimer.cancel();
        isResetInitiated = false;
        goToLogin();

        } else {

        Snackbar.make(getView(), resp.getMessage(), Snackbar.LENGTH_LONG).show();

        }
        progress.setVisibility(View.INVISIBLE);
        }

@Override
public void onFailure(Call<ServerResponse> call, Throwable t) {

        progress.setVisibility(View.INVISIBLE);
        Log.d(Constants.TAG,"failed");
        Snackbar.make(getView(), t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();

        }
        });
        }

private void startCountdownTimer(){
        countDownTimer = new CountDownTimer(120000, 1000) {
public void onTick(long millisUntilFinished) {
        tv_timer.setText("Kalan zaman : " + millisUntilFinished / 1000+" sn");
        }
public void onFinish() {
        Snackbar.make(getView(), "İstedğiniz zaman aşımına uğradı. Tekrar deneyiniz..", Snackbar.LENGTH_LONG).show();
        goToLogin();
        }
        }.start();
        }
private void goToLogin(){
        startActivity(new Intent(SifremiUnuttum.this,GirisSecimEkrani.class));
        }
private View getView(){
    return view;
}
        }
