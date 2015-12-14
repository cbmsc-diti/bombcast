package igarape.cbmsc.bombcast.views;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import igarape.cbmsc.bombcast.R;
import igarape.cbmsc.bombcast.utils.Globals;

// * Created by barcellos on 01/07/15.

public class SocialActivity extends Activity {
    WebView myWebView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

        String UrlSocial = Globals.getUrlSocial();

        myWebView = (WebView) findViewById(R.id.webView1);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.loadUrl(UrlSocial);
        }
}
