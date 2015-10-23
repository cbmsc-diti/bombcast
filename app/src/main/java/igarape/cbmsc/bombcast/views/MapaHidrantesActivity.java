package igarape.cbmsc.bombcast.views;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import igarape.cbmsc.bombcast.R;

/**
 * Created by barcellos on 23/10/15.
 */
public class MapaHidrantesActivity extends Activity {
    protected WebView myWebView;
    String UrlSocial = "http://www.cbm.sc.gov.br/";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);


        myWebView = (WebView) findViewById(R.id.webView1);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.loadUrl(UrlSocial);
    }
}

