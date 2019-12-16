package edu.mum.mumwhere

import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.route_layout.*

class RouteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.route_layout)

        wview.settings.javaScriptEnabled = true;
        wview.settings.builtInZoomControls = true;

        // To open links clicked by the user in the WebView instead of Default Browser
        wview.webViewClient = WebViewClient()

        wview.addJavascriptInterface(this,"myinterface");

        wview.loadUrl("file:///android_asset/route.html")

    }


    @JavascriptInterface
    fun displayMsg(name: String, pass: String) {
        Toast.makeText(applicationContext, "$name logged in", Toast.LENGTH_LONG).show()
    }




    }