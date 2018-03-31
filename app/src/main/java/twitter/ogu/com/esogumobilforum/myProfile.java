package twitter.ogu.com.esogumobilforum;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class myProfile extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private SharedPreferences pref;
    String isim;
    String urlimage;
    String section;
    String ozel;
    Context thiscontext;
    ImageView profilresmi;
    TextView isimcontainer;
    ListView list; //ListView referansı
    AnaForumAdapter adapter; // adapter referansı
    String[] basliklar;
    String[] baslikicerik;
    String[] baslikid;
    String[] acan;
    String[] acanurl;
    String nestedjsons;
    private SwipeRefreshLayout swipeRefreshLayout;
    int[] likelar; int[] yorumlar; String[] saatler; String[] baslikresimleri;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_profile,container,false);
        thiscontext = container.getContext();
        list=(ListView)view.findViewById(R.id.listviewim);
        // profilresmi=(ImageView)view.findViewById(R.id.profilresmi);
        //isimcontainer=(TextView)view.findViewById(R.id.profilegetname);
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipe_refresh_layout);

        return view;
    }
 //bu arada şeyi unutmayak hacı başlık açanın imgurlsi section normal ise http eklenecek... veya oraya triggerla
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        pref = thiscontext.getSharedPreferences("MyPref", thiscontext.MODE_PRIVATE);
        isim=pref.getString("name","");
        urlimage=pref.getString("imageUrl","");
        section=pref.getString("section","");
        ozel=pref.getString("surname","");
        //isimcontainer.setText(isim);
        swipeRefreshLayout.setOnRefreshListener(this);
        //new DownloadImage2((ImageView)view. findViewById(R.id.profilresmi)).execute(urlimage);
        Log.e("gelenler:",urlimage+section+ozel+isim);
        new GetStudents().execute();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(), AnaForumKonuIzle.class);
                i.putExtra("baslikicerik",baslikicerik[position]);
                i.putExtra("baslikid",baslikid[position]);
                i.putExtra("baslikresimurl",baslikresimleri[position]);
                i.putExtra("baslikacan",acan[position]);
                i.putExtra("baslikacanrul",acanurl[position]);
                i.putExtra("basliksaat",saatler[position]);
                i.putExtra("baslikbaslik",basliklar[position]);
                i.putExtra("nestedjsons",nestedjsons);
                Log.e("yollanan intent test:",basliklar[position]+acanurl[position]);
                startActivity(i);
            }


        });
    }

    @Override
    public void onRefresh() {
        new GetStudents().execute();
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
    private class GetStudents extends AsyncTask<Void, Void, Void> {

        // Hashmap for ListView
        ArrayList<HashMap<String, String>> studentList;
        ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            swipeRefreshLayout.setRefreshing(true);
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(true);// bug mug olursa diye
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            WebRequest webreq = new WebRequest();
            // Making a request to url and getting response
            String jsonStr = webreq.makeWebServiceCall("http://begodev.com/learn2crack-login-register/anaforumbaslikgetir.php", WebRequest.GET);

            Log.d("Response: ", "> " + jsonStr);
            ParseJSON(jsonStr);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            /*
            *  i.putExtra("baslikicerik",baslikicerik[position]);
                i.putExtra("baslikid",baslikid[position]);
                i.putExtra("baslikresimurl",baslikresimleri[position]);
                i.putExtra("baslikacan",acan[position]);
                i.putExtra("baslikacanrul",acanurl[position]);
                i.putExtra("basliksaat",saatler[position]);
                i.putExtra("baslikbaslik",basliklar[position]);
                i.putExtra("nestedjsons",nestedjsons);*/
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            if(basliklar!=null) {
                adapter = new AnaForumAdapter(getActivity(), basliklar, likelar, yorumlar, saatler, baslikresimleri, acanurl, acan,baslikicerik,baslikid,nestedjsons);
                list.setAdapter(adapter);//adı üstünde set ediyoruz
            }
            swipeRefreshLayout.setRefreshing(false);
        }

    }
    private void ParseJSON(String json) {
        if (json != null) {
            try {
                // Hashmap for ListView

                JSONObject jsonObj = new JSONObject(json);

                // Getting JSON Array node
                JSONArray anaforum = jsonObj.getJSONArray("result");
                Log.e("json gelen",anaforum.toString());
                basliklar=new String[anaforum.length()];
                baslikicerik=new String[anaforum.length()];
                acan=new String[anaforum.length()];
                acanurl=new String[anaforum.length()];
                saatler=new String[anaforum.length()];
                baslikresimleri=new String[anaforum.length()];
                yorumlar=new int[anaforum.length()];
                likelar=new int[anaforum.length()];
                baslikid=new String[anaforum.length()];


                // al parçala götür(verisaysına göre dinamik hale getir)
                for (int i = 0; i < anaforum.length(); i++) {
                    JSONObject c = anaforum.getJSONObject(i);
                    baslikid[i] = c.getString("baslikid");
                    basliklar[i] = c.getString("title");
                    baslikicerik[i] = c.getString("body");
                    acanurl[i] = c.getString("useracanurl");
                    acan[i] = c.getString("useracanname");
                    baslikresimleri[i]=basliklar[i].replaceAll("\\s+","");
                    String section1 = c.getString("section");
                    yorumlar[i] = 2;
                    saatler[i] = c.getString("created_at");
                    likelar[i] = Integer.parseInt(c.getString("suku"));
                    Log.e("adaptere gidenler:",basliklar[i]+saatler[i]+acanurl[i]);
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
