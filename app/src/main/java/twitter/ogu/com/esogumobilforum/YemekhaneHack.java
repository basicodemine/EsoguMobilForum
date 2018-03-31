package twitter.ogu.com.esogumobilforum;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class YemekhaneHack extends ActionBarActivity {
    private TextView stickyView;
    private ListView listView;
    private View heroImageView;
    private View stickyViewSpacer;
    private int MAX_ROWS = 20;
    private ProgressDialog progressDialog;
    WebView yemek;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yemekhane_hack);
        yemek=(WebView)findViewById(R.id.webView);
        yemek.loadUrl("http://yemekhane.ogu.edu.tr/");
        yemek.getSettings().setJavaScriptEnabled(true);
        /* Initialise list view, hero image, and sticky view */

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}
