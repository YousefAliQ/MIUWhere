package edu.mum.mumwhere


import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.esri.arcgisruntime.geometry.*
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.symbology.SimpleLineSymbol
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol
import com.esri.arcgisruntime.symbology.TextSymbol
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val wgs84 = SpatialReference.create(4236)
    private lateinit var mapView: MapView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // inflate MapView from layout
        mapView = findViewById(R.id.mapView) as MapView

        val map = ArcGISMap(Basemap.Type.IMAGERY,41.01614713668823,-91.96762561798096, 17)

        // set the map to be displayed in the layout's MapView
        mapView.map = map

        // add graphics overlay to MapView.
        val graphicsOverlay: GraphicsOverlay? = addGraphicsOverlay(mapView)
        //add some buoy positions to the graphics overlay
        addBuoyPoints(graphicsOverlay!!)

        //add text symbols and points to graphics overlay
        addText(graphicsOverlay)

        // TODO : mapping function to fix the shifting issue
        //-91.96765780448914,41.015310287475586
        //-91.96036219596863,41.0209321975708
    }

    override fun onPause() {
        super.onPause()
        mapView.pause()
    }
    override fun onResume() {
        super.onResume()
        mapView.resume()
    }
    override fun onDestroy() {
        super.onDestroy()
        mapView.dispose()
    }


    private fun addGraphicsOverlay(mapView: MapView): GraphicsOverlay? { //create the graphics overlay

        val graphicsOverlay = GraphicsOverlay()
        //add the overlay to the map view
        mapView.graphicsOverlays.add(graphicsOverlay)
        return graphicsOverlay
    }

    private fun addBuoyPoints(graphicOverlay: GraphicsOverlay) { //define the buoy locations

        val buoy1Loc =
            Point(-91.95978283882141,41.023335456848145, wgs84)

        //create a marker symbol
        val buoyMarker =
            SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED, 10.0f)
        buoyMarker.outline = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID,Color.BLACK,1.0f)
        //create graphics
        val buoyGraphic1 = Graphic(buoy1Loc, buoyMarker)

        //add the graphics to the graphics overlay
        graphicOverlay.graphics.add(buoyGraphic1)
    }

    private fun addText(graphicOverlay: GraphicsOverlay) { //create a point geometry

        val bassLocation =
            Point(-91.95978283882141,41.023335456848145, wgs84)

        //create text symbols
        val bassRockSymbol = TextSymbol(
            20.0f, "Argiro", Color.rgb(0, 0, 230),
            TextSymbol.HorizontalAlignment.LEFT, TextSymbol.VerticalAlignment.BOTTOM
        )

        bassRockSymbol.fontWeight = TextSymbol.FontWeight.BOLD
        bassRockSymbol.haloColor = titleColor

        //define a graphic from the geometry and symbol
        val bassRockGraphic = Graphic(bassLocation, bassRockSymbol)

        //add the text to the graphics overlay
        graphicOverlay.graphics.add(bassRockGraphic)

    }

}
