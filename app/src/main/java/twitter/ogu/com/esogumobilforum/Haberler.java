package twitter.ogu.com.esogumobilforum;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Haberler extends Activity {
    WebView webim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_haberler);
        webim=(WebView)findViewById(R.id.webView);
        webim.getSettings().setJavaScriptEnabled(true);
        webim.getSettings().getLoadsImagesAutomatically();
        webim.getSettings().getUseWideViewPort();
//set the WebViewClient before calling loadUrl
        webim.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webim.loadUrl("javascript:(function() { " +
                        "var head = document.getElementsByClassName('col-xs-12 col-sm-12 col-md-12 col-lg-12 main-area margin-top-20')[0];"
                        +"var foot = document.getElementsByClassName('col-xs-12 col-md-12 col-sm-12 col-lg-12 footerTop')[0];"
                        +"var headder = document.getElementsByClassName('footer col-xs-12 col-md-12 col-sm-12 col-lg-12')[0];"

                        +"var header = document.getElementsByTagName('header')[0];"
                        + "head.parentNode.removeChild(head);" + "foot.parentNode.removeChild(foot);"+"header.parentNode.removeChild(header);"+"headder.parentNode.removeChild(headder);"+
                        "})()");
            }
        });

        webim.loadUrl("https://ogu.edu.tr/Mobile/Web/");
    }
}
