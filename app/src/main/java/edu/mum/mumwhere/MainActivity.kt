package edu.mum.mumwhere


import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.geometry.SpatialReference
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.mapping.view.LocationDisplay
import com.esri.arcgisruntime.mapping.view.LocationDisplay.DataSourceStatusChangedListener
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.symbology.SimpleLineSymbol
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol
import com.esri.arcgisruntime.symbology.TextSymbol
import edu.mum.mumwhere.spinner.ItemData
import edu.mum.mumwhere.spinner.SpinnerAdapter
import java.util.*


class MainActivity : AppCompatActivity() {

    private val wgs84 = SpatialReference.create(4236)
    private lateinit var mMapView: MapView
    private var mLocationDisplay: LocationDisplay? = null
    private var mSpinner: Spinner? = null

    private val requestCode = 2
    var reqPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get the Spinner from layout
        // Get the Spinner from layout
        mSpinner = findViewById<View>(R.id.spinner) as Spinner

        // inflate MapView from layout
        mMapView = findViewById(R.id.mapView) as MapView

        val map = ArcGISMap(Basemap.Type.IMAGERY,41.01614713668823,-91.96762561798096, 17)

        // set the map to be displayed in the layout's MapView
        mMapView.map = map

        // add graphics overlay to MapView.
        val graphicsOverlay: GraphicsOverlay? = addGraphicsOverlay(mMapView)
        //add some buoy positions to the graphics overlay
        addBuoyPoints(graphicsOverlay!!)

        //add text symbols and points to graphics overlay
        addText(graphicsOverlay)

        // TODO : mapping function to fix the shifting issue
        //-91.96765780448914,41.015310287475586
        //-91.96036219596863,41.0209321975708


        // get the MapView's LocationDisplay
        mLocationDisplay = mMapView.getLocationDisplay()


        // Listen to changes in the status of the location data source.
        // Listen to changes in the status of the location data source.
        mLocationDisplay?.addDataSourceStatusChangedListener(DataSourceStatusChangedListener { dataSourceStatusChangedEvent ->
            // If LocationDisplay started OK, then continue.
            if (dataSourceStatusChangedEvent.isStarted) return@DataSourceStatusChangedListener
            // No error is reported, then continue.
            if (dataSourceStatusChangedEvent.error == null) return@DataSourceStatusChangedListener
            // If an error is found, handle the failure to start.
            // Check permissions to see if failure may be due to lack of permissions.
            val permissionCheck1 =
                ContextCompat.checkSelfPermission(this@MainActivity, reqPermissions[0]) ==
                        PackageManager.PERMISSION_GRANTED
            val permissionCheck2 =
                ContextCompat.checkSelfPermission(this@MainActivity, reqPermissions[1]) ==
                        PackageManager.PERMISSION_GRANTED
            if (!(permissionCheck1 && permissionCheck2)) { // If permissions are not already granted, request permission from the user.
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    reqPermissions,
                    requestCode
                )
            } else { // Report other unknown failure types to the user - for example, location services may not
                // be enabled on the device.
                val message = String.format(
                    "Error in DataSourceStatusChangedListener: %s", dataSourceStatusChangedEvent
                        .source.locationDataSource.error.message
                )
                Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
                // Update UI to reflect that the location display did not actually start
                mSpinner!!.setSelection(0, true)
            }
        })


        // Populate the list for the Location display options for the spinner's Adapter
        // Populate the list for the Location display options for the spinner's Adapter
        val list: ArrayList<ItemData> = ArrayList<ItemData>()
        list.add(ItemData("Stop", R.drawable.locationdisplaydisabled))
        list.add(ItemData("On", R.drawable.locationdisplayon))
        list.add(ItemData("Re-Center", R.drawable.locationdisplayrecenter))
        list.add(ItemData("Navigation", R.drawable.locationdisplaynavigation))
        list.add(ItemData("Compass", R.drawable.locationdisplayheading))

        val adapter = SpinnerAdapter(this, R.layout.spinner_layout, R.id.txt, list)
        mSpinner!!.adapter = adapter
        mSpinner!!.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 ->  // Stop Location Display
                        if (mLocationDisplay!!.isStarted()) mLocationDisplay?.stop()
                    1 ->  // Start Location Display
                        if (!mLocationDisplay!!.isStarted()) mLocationDisplay?.startAsync()
                    2 -> {
                        // Re-Center MapView on Location
                        // AutoPanMode - Default: In this mode, the MapView attempts to keep the location symbol on-screen by
                        // re-centering the location symbol when the symbol moves outside a "wander extent". The location symbol
                        // may move freely within the wander extent, but as soon as the symbol exits the wander extent, the MapView
                        // re-centers the map on the symbol.
                        mLocationDisplay?.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER)
                        if (!mLocationDisplay!!.isStarted()) mLocationDisplay?.startAsync()
                    }
                    3 -> {
                        // Start Navigation Mode
                        // This mode is best suited for in-vehicle navigation.
                        mLocationDisplay!!.setAutoPanMode(LocationDisplay.AutoPanMode.NAVIGATION)
                        if (!mLocationDisplay!!.isStarted()) mLocationDisplay?.startAsync()
                    }
                    4 -> {
                        // Start Compass Mode
                        // This mode is better suited for waypoint navigation when the user is walking.
                        mLocationDisplay!!.setAutoPanMode(LocationDisplay.AutoPanMode.COMPASS_NAVIGATION)
                        if (!mLocationDisplay!!.isStarted()) mLocationDisplay?.startAsync()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun onPause() {
        super.onPause()
        mMapView.pause()
    }
    override fun onResume() {
        super.onResume()
        mMapView.resume()
    }
    override fun onDestroy() {
        super.onDestroy()
        mMapView.dispose()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) { // If request is cancelled, the result arrays are empty.
        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { // Location permission was granted. This would have been triggered in response to failing to start the
// LocationDisplay, so try starting this again.
            mLocationDisplay!!.startAsync()
        } else { // If permission was denied, show toast to inform user what was chosen. If LocationDisplay is started again,
// request permission UX will be shown again, option should be shown to allow never showing the UX again.
// Alternative would be to disable functionality so request is not shown again.
            Toast.makeText(
                this@MainActivity,
                resources.getString(R.string.location_permission_denied),
                Toast.LENGTH_SHORT
            ).show()
            // Update UI to reflect that the location display did not actually start
            mSpinner!!.setSelection(0, true)
        }
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
