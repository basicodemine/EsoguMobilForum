package twitter.ogu.com.esogumobilforum;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class istatistik extends Activity {

    private ProgressDialog dialog;
    WebView webView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_istatistik);
        webView = (WebView) findViewById(R.id.istatistik);
        dialog = new ProgressDialog(istatistik.this);
        String url="http://begodev.com/esogumobiladmin/istatistik.php";
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
        dialog.setMessage("İstatistikler Yükleniyor...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        webView.loadUrl(url);
        webView.getSettings().setLoadWithOverviewMode(true);
        //webView.getSettings().setUseWideViewPort(true);



        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }
}
