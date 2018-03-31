package twitter.ogu.com.esogumobilforum;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import twitter.ogu.com.esogumobilforum.models.ServerRequest;
import twitter.ogu.com.esogumobilforum.models.ServerResponse;
import twitter.ogu.com.esogumobilforum.models.User;

public class GirisSecimEkrani extends AppCompatActivity implements View.OnClickListener{

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "JlDkh0hRgwEuUS0KEuNdd0NBk";
    private static final String TWITTER_SECRET = "fvrTLTTVqUn9nWGwo8plSARS9cIBdzokVoSW51FexNmY04FSE9";
    private Button fb,tw;
    private TwitterLoginButton loginButtont;
    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    private SharedPreferences pref;
    Button kayit,girisyap;
    TextView unuttum;       //bunlar da normal user için.
    View view;
    EditText et_name,et_password,et_email;
    ProgressBar progress;
    LoginButton loginButton;
    //Facebook login button
    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Profile profile = Profile.getCurrentProfile();
            nextActivity(profile);
        }
        @Override
        public void onCancel() {        }
        @Override
        public void onError(FacebookException e) {      }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_giris_secim_ekrani);
        callbackManager = CallbackManager.Factory.create();
        girisyap=(Button)findViewById(R.id.btn_login);
        kayit=(Button)findViewById(R.id.btn_register);
        unuttum=(TextView)findViewById(R.id.btn_forgot);
        et_name = (EditText)findViewById(R.id.et_name);
        et_email = (EditText)findViewById(R.id.et_email);
        et_password = (EditText)findViewById(R.id.et_password);
        fb=(Button)findViewById(R.id.fblogin);
        tw=(Button)findViewById(R.id.twlogin);
        progress = (ProgressBar)findViewById(R.id.progress);
        view = findViewById(R.id.view2);
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        unuttum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GirisSecimEkrani.this,SifremiUnuttum.class));
            }
        });
        girisyap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_email.getText().toString();
                String password = et_password.getText().toString();
                if(!email.isEmpty() && !password.isEmpty()) {

                    progress.setVisibility(View.VISIBLE);
                    loginProcess(email,password);

                } else {

                    Snackbar.make(view, "Boşlukları Doldurun !", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        kayit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GirisSecimEkrani.this,NormalKayit.class));
            }
        });
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                nextActivity(newProfile);
            }
        };
        accessTokenTracker.startTracking();
        profileTracker.startTracking();

         loginButton = (LoginButton)findViewById(R.id.login_button);
        callback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();
                nextActivity(profile);
                Toast.makeText(getApplicationContext(), "Logging in...", Toast.LENGTH_SHORT).show();    }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException e) {
            }
        };
        loginButton.setReadPermissions("user_friends");
        loginButton.registerCallback(callbackManager, callback);
        loginButtont = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButtont.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // The TwitterSession is also available through:
                // Twitter.getInstance().core.getSessionManager().getActiveSession()

                TwitterSession session = result.data;

                // TODO: Remove toast and use the TwitterSession's userID
                // with your app's user model
                String msg = "@" + session.getUserName() + " logged in! (#" + session.getUserId() + ")";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                nextActivity2(session);
            }
            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);
            }
        });

        if(pref.getBoolean(Constants.IS_LOGGED_IN,false)){
            nextActivity3();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        //Facebook login
        Profile profile = Profile.getCurrentProfile();
        TwitterSession session = Twitter.getSessionManager().getActiveSession();
        if(session!=null)
        nextActivity2(session);
        if(profile!=null)   //burayı bi kontrol et :D
        nextActivity(profile);
        if(pref.getBoolean(Constants.IS_LOGGED_IN,false)){
            nextActivity3();
        }
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    protected void onStop() {
        super.onStop();
        //Facebook login
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Make sure that the loginButton hears the result from any
        // Activity that it triggered.
        callbackManager.onActivityResult(requestCode, resultCode, data);
        loginButtont.onActivityResult(requestCode, resultCode, data);
    }

    private void nextActivity(Profile profile){
        if(profile != null){
            Intent main = new Intent(GirisSecimEkrani.this, AnaMenu.class);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("name",profile.getFirstName());
            editor.putString("surname",profile.getId());
            editor.putString("section","facebook");
            editor.putString("imageUrl",profile.getProfilePictureUri(200,200).toString());
            editor.apply();
            editor.commit();

            startActivity(main);
        }
    }
    private void nextActivity2(TwitterSession profile){
        if(profile != null){
            Intent main = new Intent(GirisSecimEkrani.this, AnaMenu.class);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("name",profile.getUserName());
            editor.putString("surname",profile.getUserName());
            editor.putString("section","twitter");
            editor.putString("imageUrl","https://twitter.com/"+profile.getUserName()+"/profile_image?size=original");
            editor.apply();
            editor.commit();
            startActivity(main);
        }
    }
    private void nextActivity3(){
            Intent main = new Intent(GirisSecimEkrani.this, AnaMenu.class);
            startActivity(main);
            finish();

    }
    private void loginProcess(String email,String password){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.LOGIN_OPERATION);
        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new retrofit2.Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {

                ServerResponse resp = response.body();
                Snackbar.make(view, resp.getMessage(), Snackbar.LENGTH_LONG).show();

                if(resp.getResult().equals(Constants.SUCCESS)){
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("name",resp.getUser().getName());
                    editor.putString("surname",resp.getUser().getEmail());
                    editor.putBoolean(Constants.IS_LOGGED_IN,true);
                    editor.putString("section","normal");
                    editor.putString("email",resp.getUser().getEmail());
                    editor.putString("imageUrl","http://begodev.com/oguforum/"+resp.getUser().getEmail()+".jpg");
                    editor.apply();
                    editor.commit();
                    nextActivity3();

                }
                progress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

                progress.setVisibility(View.INVISIBLE);
                Log.d(Constants.TAG,"failed");
                Snackbar.make(view, t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();

            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v == fb) {
            loginButton.performClick();
        }
        if(v==tw){
            loginButtont.performClick();
        }
    }
}
