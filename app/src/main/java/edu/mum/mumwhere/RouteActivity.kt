package edu.mum.mumwhere

import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.esri.arcgisruntime.geometry.CoordinateFormatter
import kotlinx.android.synthetic.main.route_layout.*

class RouteActivity : AppCompatActivity() {

    lateinit var LatCode:String
    lateinit var LongCode:String

    lateinit var DLatCode:String
    lateinit var DLongCode:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.route_layout)

        wview.settings.javaScriptEnabled = true;
        wview.settings.builtInZoomControls = true;

        // To open links clicked by the user in the WebView instead of Default Browser
        wview.webViewClient = WebViewClient()

        wview.addJavascriptInterface(this,"myinterface");

        LatCode = intent.getStringExtra("sourceY")
        LongCode = intent.getStringExtra("sourceX")

        DLatCode= intent.getStringExtra("destY")
        DLongCode = intent.getStringExtra("destX")


        /*
        LatCode = "41.00612"
        LongCode = "-91.9849627"

        DLatCode= "41.005917"
        DLongCode = "-91.9767849"
        */

        wview.loadUrl("file:///android_asset/route.html")



    }


    @JavascriptInterface
    fun getLat(): Double {
        return LatCode.toDouble()
    }


    @JavascriptInterface
    fun getLong(): Double {
        return LongCode.toDouble()
    }



    @JavascriptInterface
    fun getDLat(): Double {
        return DLatCode.toDouble()
    }



    @JavascriptInterface
    fun getDLong(): Double {
        return DLongCode.toDouble()
    }


    }