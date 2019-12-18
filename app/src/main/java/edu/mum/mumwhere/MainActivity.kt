package edu.mum.mumwhere



import android.Manifest
import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.geometry.SpatialReference
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.view.*
import com.esri.arcgisruntime.mapping.view.LocationDisplay.DataSourceStatusChangedListener
import com.esri.arcgisruntime.symbology.SimpleLineSymbol
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol
import com.esri.arcgisruntime.symbology.TextSymbol
import edu.mum.mumwhere.spinner.ItemData
import edu.mum.mumwhere.spinner.SpinnerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.ExecutionException


class MainActivity : AppCompatActivity() {


    private lateinit var mNavigationDrawerItemTitles: Array<String>
    private val wgs84 = SpatialReference.create(4236)
    private lateinit var mMapView: MapView
    private var mLocationDisplay: LocationDisplay? = null
    var graphicsOverlay: GraphicsOverlay? = null
    private var mSpinner: Spinner? = null
    private var mBasemap: Spinner? = null
    lateinit var mapPoint: Point
    private lateinit var  strings1: Array<String>

    private val requestCode = 2
    var reqPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        strings1 = arrayOf("Asia","Australia","America","Belgium","Brazil","Canada","California","Dubai","France","Paris")
        // Get the XML configured vales into the Activity and stored into an String Array
        //strings = getResources().getStringArray(R.array.countries);
        /* Pass three parameters to the ArrayAdapter
        1. The current context,
        2. The resource ID for a built-in layout file containing a TextView to use when instantiating views,
           which are available in android.R.layout
        3. The objects to represent in the values
        */
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, strings1)
        actv2.setAdapter(adapter)
        actv2.threshold = 1

        actv2.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                Toast.makeText(this,"Item selected is " + parent.getItemAtPosition(position),Toast.LENGTH_LONG).show()

            }




        editOptions.visibility = View.INVISIBLE


        val geti = intent

        val checki = geti.getStringExtra("isLogin")

        val spfr = getSharedPreferences("login",Context.MODE_PRIVATE)
        val name= spfr.getString("username","")
        val check = spfr.getString("isLogin","")

        if (checki=="y"){
            editOptions.visibility = View.VISIBLE
        }

        initMap()
        registerChangeBasemap()
        registerCurrentLocation()
        updateOnTouchListener()
        displayPoints()

        supportActionBar?.apply {
         //   setDisplayHomeAsUpEnabled(true)
            //  setHomeButtonEnabled(true)
            // set opening basemap title to Topographic
            title = "MIU Where?"
        }

        // TODO : mapping function to fix the shifting issue
        //-91.96765780448914,41.015310287475586
        //-91.96036219596863,41.0209321975708

    }

    private fun initMap() {

        mNavigationDrawerItemTitles =
            Basemap.Type.values().map { it.name.replace("_", " ").toLowerCase().capitalize() }.toTypedArray()

        // Get the Spinner from layout
        mSpinner = findViewById<View>(R.id.spinner) as Spinner


        // Get the basemap from layout
        mBasemap = findViewById<View>(R.id.basemap) as Spinner


        // inflate MapView from layout
        mMapView = findViewById(R.id.mapView) as MapView

        val map = ArcGISMap(Basemap.Type.IMAGERY,41.01614713668823,-91.96762561798096, 17)

        // add graphics overlay to MapView.
        graphicsOverlay = addGraphicsOverlay(mMapView)


        // set the map to be displayed in the layout's MapView
        mMapView.map = map

    }

    /**
     * Select the Basemap item based on position in the navigation drawer
     *
     * @param position order int in navigation drawer
     */
    private fun selectBasemap(position: Int) {

        // get basemap title by position
        val baseMapTitle = mNavigationDrawerItemTitles[position]
        //supportActionBar?.title = baseMapTitle

        // select basemap by title
        mapView.map.basemap = when (Basemap.Type.valueOf(baseMapTitle.replace(" ", "_").toUpperCase())) {
            Basemap.Type.DARK_GRAY_CANVAS_VECTOR -> Basemap.createDarkGrayCanvasVector()
            Basemap.Type.IMAGERY -> Basemap.createImagery()
            Basemap.Type.IMAGERY_WITH_LABELS -> Basemap.createImageryWithLabels()
            Basemap.Type.IMAGERY_WITH_LABELS_VECTOR -> Basemap.createImageryWithLabelsVector()
            Basemap.Type.LIGHT_GRAY_CANVAS -> Basemap.createLightGrayCanvas()
            Basemap.Type.LIGHT_GRAY_CANVAS_VECTOR -> Basemap.createDarkGrayCanvasVector()
            Basemap.Type.NATIONAL_GEOGRAPHIC -> Basemap.createNationalGeographic()
            Basemap.Type.NAVIGATION_VECTOR -> Basemap.createNavigationVector()
            Basemap.Type.OCEANS -> Basemap.createOceans()
            Basemap.Type.OPEN_STREET_MAP -> Basemap.createOceans()
            Basemap.Type.STREETS -> Basemap.createStreets()
            Basemap.Type.STREETS_NIGHT_VECTOR -> Basemap.createStreetsNightVector()
            Basemap.Type.STREETS_WITH_RELIEF_VECTOR -> Basemap.createStreetsWithReliefVector()
            Basemap.Type.STREETS_VECTOR -> Basemap.createStreetsVector()
            Basemap.Type.TOPOGRAPHIC -> Basemap.createTopographic()
            Basemap.Type.TERRAIN_WITH_LABELS -> Basemap.createTerrainWithLabels()
            Basemap.Type.TERRAIN_WITH_LABELS_VECTOR -> Basemap.createTerrainWithLabelsVector()
            Basemap.Type.TOPOGRAPHIC_VECTOR -> Basemap.createTopographicVector()
        }
    }


    private fun updateOnTouchListener() {
        mapView.setOnTouchListener(object : DefaultMapViewOnTouchListener(this, mapView) {
            override fun onSingleTapConfirmed(event: MotionEvent): Boolean { // create a point from where the user clicked

                val point =
                    android.graphics.Point(event.x.toInt(), event.y.toInt())
                // create a map point from a point
                 mapPoint =
                    mMapView.screenToLocation(point)

                // add/delete/update a new feature if its on the edit mode.
                if (rbAddFeature.isChecked){
                    addFeature(mapPoint)
                }else if (rbUpdateFeature.isChecked){
                    // TODO : implement update feature

                    // identify graphics on the graphics overlay
                    // identify graphics on the graphics overlay
                    val identifyGraphic =
                        mMapView.identifyGraphicsOverlayAsync(
                            graphicsOverlay,
                            point,
                            10.0,
                            false,
                            2
                        )

                    identifyGraphic.addDoneListener {
                        try {
                            val grOverlayResult =
                                identifyGraphic.get()
                            // get the list of graphics returned by identify graphic overlay
                            val graphic =
                                grOverlayResult.graphics
                            // get size of list in results
                            val identifyResultSize = graphic.size
                            if (!graphic.isEmpty()) { // show a toast message if graphic was returned
                                Toast.makeText(
                                    applicationContext,
                                    "Tapped on $identifyResultSize Graphic",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (ie: InterruptedException) {
                            ie.printStackTrace()
                        } catch (ie: ExecutionException) {
                            ie.printStackTrace()
                        }
                    }
                }else if (rbDeleteFeature.isChecked){
                    // TODO : implement delete feature

                    // identify graphics on the graphics overlay
                    // identify graphics on the graphics overlay
                    val identifyGraphic =
                        mMapView.identifyGraphicsOverlayAsync(
                            graphicsOverlay,
                            point,
                            25.0,
                            false,
                            2
                        )


                    identifyGraphic.addDoneListener {
                        try {
                            val grOverlayResult =
                                identifyGraphic.get()
                            // get the list of graphics returned by identify graphic overlay
                            val graphics =
                                grOverlayResult.graphics
                            // get size of list in results
                            val identifyResultSize = graphics.size
                            if (!graphics.isEmpty()) { // show a toast message if graphic was returned

                                graphicsOverlay!!.graphics.removeAll(graphics)

                                Toast.makeText(
                                    applicationContext,
                                    "Deleted successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (ie: InterruptedException) {
                            ie.printStackTrace()
                        } catch (ie: ExecutionException) {
                            ie.printStackTrace()
                        }
                    }

                }

                return super.onSingleTapConfirmed(event)
            }
        })
    }

    private fun displayPoints() {

        // TODO : read all points from building table and add it to the graphicsOverlay.

        // add graphics overlay to MapView.
        val graphicsOverlay: GraphicsOverlay? = addGraphicsOverlay(mMapView)

        val mapPoint: com.esri.arcgisruntime.geometry.Point = Point(-91.96036219596863, 41.0209321975708,wgs84)

        val attributes: MutableMap<String, Any> =
            HashMap()
        attributes["newPlace"] = "Argiro"
        attributes["primcause"] = "Earthquake"

        addBuoyPoints(graphicsOverlay!!, mapPoint, attributes)

        addText(graphicsOverlay!!, mapPoint, attributes)


    }

    private fun registerChangeBasemap() {


        // Populate the list for the Location display options for the spinner's Adapter
        val list: ArrayList<ItemData> = ArrayList<ItemData>()
        list.add(ItemData(resources.getString(R.string.DarkGrayCanvasVector),R.drawable.basemap))
        list.add(ItemData(resources.getString(R.string.Imagery),R.drawable.basemap))
        list.add(ItemData(resources.getString(R.string.ImageryWithLabels),R.drawable.basemap))
        list.add(ItemData(resources.getString(R.string.LightGrayCanvas),R.drawable.basemap))
        list.add(ItemData(resources.getString(R.string.LightGrayCanvasVector),R.drawable.basemap))
        list.add(ItemData(resources.getString(R.string.NationalGeographic),R.drawable.basemap))
        list.add(ItemData(resources.getString(R.string.NavigationVector),R.drawable.basemap))
        list.add(ItemData(resources.getString(R.string.Oceans),R.drawable.basemap))
        list.add(ItemData(resources.getString(R.string.OpenStreetMap),R.drawable.basemap))
        list.add(ItemData(resources.getString(R.string.Streets),R.drawable.basemap))
        list.add(ItemData(resources.getString(R.string.StreetsNightVector),R.drawable.basemap))
        list.add(ItemData(resources.getString(R.string.StreetsVector),R.drawable.basemap))
        list.add(ItemData(resources.getString(R.string.StreetsWithReliefVector),R.drawable.basemap))
        list.add(ItemData(resources.getString(R.string.TerrainWithLabels),R.drawable.basemap))
        list.add(ItemData(resources.getString(R.string.TerrainWithLabelsVector),R.drawable.basemap))
        list.add(ItemData(resources.getString(R.string.Topographic),R.drawable.basemap))
        list.add(ItemData(resources.getString(R.string.TopographicVector),R.drawable.basemap))

        val adapter = SpinnerAdapter(this, R.layout.basemap_layout, R.id.txt, list)
        mBasemap!!.adapter = adapter
        mBasemap!!.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                selectBasemap(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        mBasemap!!.setSelection(1, true)

    }

    private fun registerCurrentLocation() {

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
        val list: ArrayList<ItemData> = ArrayList<ItemData>()
        list.add(ItemData(resources.getString(R.string.stop), R.drawable.locationdisplaydisabled))
        list.add(ItemData(resources.getString(R.string.on), R.drawable.locationdisplayon))
        list.add(ItemData(resources.getString(R.string.recenter), R.drawable.locationdisplayrecenter))
        list.add(ItemData(resources.getString(R.string.navigation), R.drawable.locationdisplaynavigation))
        list.add(ItemData(resources.getString(R.string.compass), R.drawable.locationdisplayheading))

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


    /**
     * Adds a new Feature to a ServiceFeatureTable and applies the changes to the
     * server.
     *
     * @param mapPoint     location to add feature
     * @param featureTable service feature table to add feature
     */
    private fun addFeature(
        mapPoint: com.esri.arcgisruntime.geometry.Point
    ) { // create default attributes for the feature

        var i = Intent(this, EditorActivity::class.java)
        startActivityForResult(i, 1)
    }



      override fun onActivityResult( requestCode: Int,  resultCode:Int,  data:Intent?) {
          super.onActivityResult(requestCode,resultCode,data)

          if (1 == requestCode) {
            if(resultCode == Activity.RESULT_OK){

            Toast.makeText(this.applicationContext, data?.data.toString() , Toast.LENGTH_LONG)


                val attributes: MutableMap<String, Any> =
                    HashMap()
                attributes["newPlace"] =   data?.getStringExtra("username").toString()
                attributes["primcause"] = "Earthquake"

                addBuoyPoints(graphicsOverlay!!, mapPoint, attributes)

                addText(graphicsOverlay!!, mapPoint, attributes)

       }
    }
}


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        // Code to get the title and icon on the option overflow
        if (menu is MenuBuilder) {
            menu.setOptionalIconsVisible(true)
        }
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem):Boolean {
        Toast.makeText(
            applicationContext,
            item.title.toString(),
            Toast.LENGTH_LONG).show()

        if (item.title.toString() == resources.getString(R.string.login_menu)){

        var i = Intent(this, LoginActivity::class.java)
        startActivity(i)
        return super.onOptionsItemSelected(item)
        }



        if (item.title.toString() == resources.getString(R.string.scan_menu)){

            var i = Intent(this, ScanMainActivity::class.java)
            startActivity(i)
            return super.onOptionsItemSelected(item)
        }
        
        
        if (item.title.toString() == "Search") {

            var i = Intent(this, SearchActivity::class.java)
            startActivity(i)
            return super.onOptionsItemSelected(item)
        }




        if (item.title.toString() == resources.getString(R.string.about_menu)){

            var i = Intent(this, AboutActivity::class.java)
            startActivity(i)
            return super.onOptionsItemSelected(item)
        }



        else{  //for now, else will run route activity

            var r = Intent(this, RouteActivity::class.java)
            r.putExtra("sourceY", "41.00612")
            r.putExtra("sourceX", "-91.9849627")

            r.putExtra("destY", "41.005917")
            r.putExtra("destX",  "-91.9767849")
            startActivityForResult(r, 1)
            return super.onOptionsItemSelected(item)
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

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        // Checks the orientation of the screen
        /*
        if (newConfig.orientation === Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show()
        } else if (newConfig.orientation === Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show()
        }*/
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) { // If request is cancelled, the result arrays are empty.
        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Location permission was granted. This would have been triggered in response to failing to start the
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

    private fun addBuoyPoints(graphicOverlay: GraphicsOverlay, point:com.esri.arcgisruntime.geometry.Point, attr: MutableMap<String, Any> ) { //define the buoy locations

        val buoy1Loc = point
        // com.esri.arcgisruntime.geometry.Point(point.x, point.y, wgs84)

        //create a marker symbol
        val buoyMarker =
            SimpleMarkerSymbol(SimpleMarkerSymbol.Style.CIRCLE, Color.RED, 10.0f)
        buoyMarker.outline = SimpleLineSymbol(SimpleLineSymbol.Style.SOLID, Color.BLACK,1.0f)
        //create graphics
        val buoyGraphic1 = Graphic(buoy1Loc, buoyMarker)

        //add the graphics to the graphics overlay
        graphicOverlay.graphics.add(buoyGraphic1)
    }

    private fun addText(graphicOverlay: GraphicsOverlay, point:com.esri.arcgisruntime.geometry.Point, attr: MutableMap<String, Any>) { //create a point geometry

        val bassLocation = point
        //com.esri.arcgisruntime.geometry.Point(point.x, point.y, wgs84)

        //create text symbols
        val bassRockSymbol = TextSymbol(
            20.0f, attr.get("newPlace").toString(), Color.rgb(0, 0, 230),
            TextSymbol.HorizontalAlignment.LEFT, TextSymbol.VerticalAlignment.BOTTOM
        )

        bassRockSymbol.fontWeight = TextSymbol.FontWeight.BOLD
        bassRockSymbol.haloColor = titleColor

        //define a graphic from the geometry and symbol
        val bassRockGraphic = Graphic(bassLocation, bassRockSymbol)

        //add the text to the graphics overlay
        graphicOverlay.graphics.add(bassRockGraphic)



        // get the MapView's LocationDisplay
        mLocationDisplay = mMapView.getLocationDisplay()


        // Listen to changes in the status of the location data source.
        mLocationDisplay?.addDataSourceStatusChangedListener(LocationDisplay.DataSourceStatusChangedListener { dataSourceStatusChangedEvent ->
            // If LocationDisplay started OK, then continue.
            if (dataSourceStatusChangedEvent.isStarted) return@DataSourceStatusChangedListener
            // No error is reported, then continue.
            if (dataSourceStatusChangedEvent.error == null) return@DataSourceStatusChangedListener
            // If an error is found, handle the failure to start.
            // Check permissions to see if failure may be due to lack of permissions.
            val permissionCheck1 =
                ContextCompat.checkSelfPermission(this, reqPermissions[0]) ==
                        PackageManager.PERMISSION_GRANTED
            val permissionCheck2 =
                ContextCompat.checkSelfPermission(this, reqPermissions[1]) ==
                        PackageManager.PERMISSION_GRANTED
            if (!(permissionCheck1 && permissionCheck2)) { // If permissions are not already granted, request permission from the user.
                ActivityCompat.requestPermissions(
                    this,
                    reqPermissions,
                    requestCode
                )
            } else { // Report other unknown failure types to the user - for example, location services may not
                // be enabled on the device.
                val message = String.format(
                    "Error in DataSourceStatusChangedListener: %s", dataSourceStatusChangedEvent
                        .source.locationDataSource.error.message
                )
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
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
        mSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

}
