package twitter.ogu.com.esogumobilforum;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.facebook.login.LoginManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import twitter.ogu.com.esogumobilforum.models.ServerRequest;
import twitter.ogu.com.esogumobilforum.models.ServerResponse;
import twitter.ogu.com.esogumobilforum.models.User;

public class AnaMenu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    String isim;
    String urlimage;
    ImageView navfoto;
    String section;
    String ozel;
    private TextView tv_name,tv_email,tv_message;
   // private SharedPreferences pref;
    private AppCompatButton btn_change_password,btn_logout;
    private EditText et_old_password,et_new_password;
    private AlertDialog dialog;
    private ProgressBar progress;
    private SharedPreferences pref;
    private FTKayit ccc=new FTKayit();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ana_menu);
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        navfoto=(ImageView)findViewById(R.id.navfoto);
        isim=pref.getString("name","");
        urlimage=pref.getString("imageUrl","");
        section=pref.getString("section","");
        ozel=pref.getString("surname","");
        Log.e("gelenler:",urlimage+section+ozel+isim);
        FirebaseMessaging.getInstance().subscribeToTopic("test"); // Kullanıcıyı bildirim almaya abone ediyoruz.
        FirebaseInstanceId.getInstance().getToken(); // Kullanıcının token id'sini bu şekilde de alabilirsiniz.
        if(section.equals("facebook")||section.equals("twitter")){
            Log.e("surname:",ozel);
            ccc.registerProcess(isim,urlimage,ozel,section);}
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            startActivity(new Intent(AnaMenu.this,ForumInsert.class));
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        RelativeLayout drawer2 = (RelativeLayout) findViewById(R.id.top_parent);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        TextView navisim=(TextView)header.findViewById(R.id.navisim);
        TextView navsoyad=(TextView)header.findViewById(R.id.navsoyad);
        navsoyad.setText("ESOGÜ Mobil Platformuna Hoşgeldin!\n       ");
        navisim.setText("Merhaba "+isim+",");
        new DownloadImage((ImageView)header. findViewById(R.id.navfoto)).execute(urlimage);
        Fragment fragment;
        fragment=new myProfile();
        FragmentTransaction ft;
        ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.flContent,fragment);
        ft.commit();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ana_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if(section.equals("facebook")){
                logoutf();}
            else if(section.equals("twitter")){
                logoutt();}
            else if(section.equals("normal")){
                logoutn();}
            logoutf();
            logoutn();
            logoutt();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_robotik) {
        Intent a=new Intent(AnaMenu.this,robotik_forum.class);
        startActivity(a);
        }
        if (id == R.id.nav_bbtk) {
            Intent a=new Intent(AnaMenu.this,bbtk_forum.class);
            startActivity(a);
        }
        if (id == R.id.nav_ieee) {
            Intent a=new Intent(AnaMenu.this,iee_forum.class);
            startActivity(a);
        }
        if (id == R.id.nav_reset) {
            showDialog();
        }
        if (id == R.id.nav_istatistik) {
        startActivity(new Intent(AnaMenu.this,istatistik.class));
        }
        if (id == R.id.nav_haber) {
        startActivity(new Intent(AnaMenu.this,Haberler.class));
        }
        if (id == R.id.nav_yemek) {
        startActivity(new Intent(AnaMenu.this,YemekhaneHack.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
    void logoutf(){
            LoginManager.getInstance().logOut();
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("name","");
        editor.putString("surname","");
        editor.putString("section","");
        editor.putString("imageUrl","");
        editor.putBoolean(Constants.IS_LOGGED_IN,false);
        editor.apply();
        editor.commit();
            Intent login = new Intent(AnaMenu.this, GirisSecimEkrani.class);
            startActivity(login);
            finish();
    };
    void logoutt(){
        TwitterSession twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("name","");
        editor.putString("surname","");
        editor.putString("section","");
        editor.putString("imageUrl","");
        editor.putBoolean(Constants.IS_LOGGED_IN,false);
        editor.apply();
        editor.commit();
        if (twitterSession != null) {
            ClearCookies(getApplicationContext());
            Twitter.getSessionManager().clearActiveSession();
            Twitter.logOut();
            Intent login = new Intent(AnaMenu.this, GirisSecimEkrani.class);
            startActivity(login);
            finish();
        }
    };
    void logoutn(){
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("name","");
        editor.putString("surname","");
        editor.putString("section","");
        editor.putString("imageUrl","");
        editor.putBoolean(Constants.IS_LOGGED_IN,false);
        editor.apply();
        editor.commit();
        Intent login = new Intent(AnaMenu.this, GirisSecimEkrani.class);
        startActivity(login);
        finish();
    };
    public static void ClearCookies(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager cookieSyncMngr=CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager=CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }
    private void showDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_change_password, null);
        et_old_password = (EditText)view.findViewById(R.id.et_old_password);
        et_new_password = (EditText)view.findViewById(R.id.et_new_password);
        tv_message = (TextView)view.findViewById(R.id.tv_message);
        progress = (ProgressBar)view.findViewById(R.id.progress);
        builder.setView(view);
        builder.setTitle("Change Password");
        builder.setPositiveButton("Change Password", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String old_password = et_old_password.getText().toString();
                String new_password = et_new_password.getText().toString();
                if(!old_password.isEmpty() && !new_password.isEmpty()){

                    progress.setVisibility(View.VISIBLE);
                    changePasswordProcess(pref.getString(Constants.EMAIL,""),old_password,new_password);

                }else {

                    tv_message.setVisibility(View.VISIBLE);
                    tv_message.setText("Fields are empty");
                }
            }
        });
    }
    private void changePasswordProcess(String email,String old_password,String new_password){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        User user = new User();
        user.setEmail(email);
        user.setOld_password(old_password);
        user.setNew_password(new_password);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.CHANGE_PASSWORD_OPERATION);
        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {

                ServerResponse resp = response.body();
                if(resp.getResult().equals(Constants.SUCCESS)){
                    progress.setVisibility(View.GONE);
                    tv_message.setVisibility(View.GONE);
                    dialog.dismiss();
                    //Snackbar.make(, resp.getMessage(), Snackbar.LENGTH_LONG).show();

                }else {
                    progress.setVisibility(View.GONE);
                    tv_message.setVisibility(View.VISIBLE);
                    tv_message.setText(resp.getMessage());

                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

                Log.d(Constants.TAG,"failed");
                progress.setVisibility(View.GONE);
                tv_message.setVisibility(View.VISIBLE);
                tv_message.setText(t.getLocalizedMessage());

            }
        });
    }
}
