/*
package com.lsepulveda.kotlinudemydelivery.activities.delivery.home.orders.map

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.lsepulveda.kotlinudemydelivery.R
import com.lsepulveda.kotlinudemydelivery.models.Order
import com.maps.route.extensions.drawRouteOnMap
import de.hdodenhof.circleimageview.CircleImageView
import org.w3c.dom.Text

class DeliveryOrdersMapActivity : AppCompatActivity(), OnMapReadyCallback{

    var googleMap : GoogleMap? = null

    val TAG = "DeliveryOrdersMap"

    val PERMISSION_ID = 42
    var fusedLocationClient: FusedLocationProviderClient? = null


    var city = ""
    var country = ""
    var address = ""
    var addressLatLng: LatLng?= null

    var markerDelivery: Marker? = null
    var markerAddress: Marker? = null
    var myLocationLatLng: LatLng?=null

    var order: Order?= null
    var gson = Gson()

    var textViewClient: TextView ?= null
    var textViewAddress: TextView ?= null
    var textViewNeighborhood: TextView ?= null
    var buttonDelivered: Button ?= null
    var circleImageUser: CircleImageView?= null
    var imageViewPhone: ImageView?= null

    val REQUEST_PHONE_CALL = 30


    private val locationCallback = object : LocationCallback(){

        // va obteniendo la ubicacion en tiempo real del delivery
        override fun onLocationResult(locationResult: LocationResult) {

            var lastLocation = locationResult.lastLocation
            myLocationLatLng = LatLng(lastLocation.latitude, lastLocation.longitude)


            val distance = getDistanceBetween(myLocationLatLng!!, addressLatLng!!)

            Log.d(TAG, "Distancia: $distance")

            // elimina el dibujo anterior
            removeDeliveryMarker()
            //re dibuja mapa de delivery. necesito borrar el anterior dibujo
            addDeliveryMarker()
            Log.d("LOCALIZACION", "Callback: $lastLocation")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery_orders_map)

        addressLatLng = LatLng(order?.address?.lat!!, order?.address?.lng!!)

        order = gson.fromJson(intent.getStringExtra("order"), Order::class.java)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment // cast
        mapFragment?.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this) // inicia servicio para encontrar nuestra location

        buttonDelivered = findViewById(R.id.btn_delivered)
        textViewClient = findViewById(R.id.textview_client)
        textViewAddress= findViewById(R.id.textview_address)
        textViewNeighborhood= findViewById(R.id.textview_neighborhood)
        circleImageUser= findViewById(R.id.circleimage_user)
        imageViewPhone= findViewById(R.id.imageview_phone)


        getLastLocation()

        textViewClient?.text = "${order?.client?.name} ${order?.client?.lastname}"
        textViewAddress?.text = order?.address?.address
        textViewNeighborhood?.text = order?.address?.neighborhood

        if(!order?.client?.image.isNullOrBlank()){
            Glide.with(this).load(order?.client?.image).into(circleImageUser!!)
        }

        buttonDelivered?.setOnClickListener{ }
        imageViewPhone?.setOnClickListener{

            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CALL_PHONE), REQUEST_PHONE_CALL)
            }
            else{
                call()
            }
        }
    }

    private fun getDistanceBetween(fromLatLng: LatLng, toLatLng: LatLng) : Float{

        var distance = 0.0f

        val from = Location("")
        val to = Location("")

        from.latitude = fromLatLng.latitude
        from.longitude = fromLatLng.longitude

        to.latitude = toLatLng.latitude
        to.longitude = toLatLng.longitude

        distance = from.distanceTo(to)

        return distance

    }

    private fun call(){
        val i = Intent(Intent.ACTION_CALL)
        i.data = Uri.parse("tel:${order?.client?.phone}")

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Permiso denegado para realizar la llamada", Toast.LENGTH_SHORT).show()
            return
        }

        startActivity(i)
    }

    // ver si anda la facturacion para este metodo de trazado de rutas
    private fun drawRoute(){
        val addressLocation = LatLng(order?.address?.lat!!, order?.address?.lng!!)

        googleMap?.drawRouteOnMap(
            getString(R.string.google_map_api_key),
            source = myLocationLatLng!!,
            destination = addressLocation,
            context = this,
            color = Color.RED,
            polygonWidth = 15, // mas alto, mas ancha la ruta
            boundMarkers = false,
            markers = false
        )
    }

    private fun removeDeliveryMarker(){
        markerDelivery?.remove()
    }


    private fun addDeliveryMarker(){
        markerDelivery = googleMap?.addMarker(
            MarkerOptions().
            position(myLocationLatLng)
                .title("Mi posicion")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.delivery))
        )
    }

    private fun addAddressMarker(){

        val addressLocation = LatLng(order?.address?.lat!!, order?.address?.lng!!)
        markerAddress = googleMap?.addMarker(
            MarkerOptions().
            position(addressLocation)
                .title("Entregar aqui")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.home))
        )
    }




    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.uiSettings?.isZoomControlsEnabled = true
        googleMap?.mapType = GoogleMap.MAP_TYPE_TERRAIN
    }

    // solo se ejecuta una vez
    private fun getLastLocation(){
        if(checkPermission()){
            if(isLocationEnabled()){

                requestNewLocationData() // iniciamos la posicion en tiempo real


                //obtiene localizacion una sola vez
                fusedLocationClient?.lastLocation?.addOnCompleteListener{ task ->

                    var location = task.result // obtiene locacion

                    myLocationLatLng = LatLng(location.latitude, location.longitude)

                    removeDeliveryMarker()
                    addDeliveryMarker()
                    addAddressMarker()
                    drawRoute()

                    googleMap?.moveCamera(CameraUpdateFactory.newCameraPosition(
                        CameraPosition.builder().target(
                            LatLng(location.latitude, location.longitude)
                        ).zoom(15f).build()
                    ))
                }
            }
            else{
                Toast.makeText(this, "Habilita la localizacion", Toast.LENGTH_LONG).show()
                val i = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(i)
            }
        }else{
            requestPermission()
        }
    }

    private fun requestNewLocationData(){
        var locationRequest = LocationRequest.create().apply {
            interval = 100
            fastestInterval = 50
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        // inicializa pos en tiempo real
        fusedLocationClient?.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper()) // escucha location en realtime
    }

    private fun isLocationEnabled() : Boolean{
        var locationManager : LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }



    private fun checkPermission(): Boolean{
        if(ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            return true
        }
        return false
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PERMISSION_ID){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation()
            }
        }

        if(requestCode == REQUEST_PHONE_CALL){
            call()
        }
    }
}
*/


package com.lsepulveda.kotlinudemydelivery.activities.delivery.home.orders.map

import com.lsepulveda.kotlinudemydelivery.R
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.github.nkzawa.socketio.client.Socket
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.gson.Gson
import com.maps.route.extensions.drawRouteOnMap

import com.lsepulveda.kotlinudemydelivery.models.Order
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.Exception
import java.net.URI.create
import com.google.android.gms.maps.model.LatLng
import com.lsepulveda.kotlinudemydelivery.activities.delivery.home.DeliveryHomeActivity
import com.lsepulveda.kotlinudemydelivery.models.ResponseHttp
import com.lsepulveda.kotlinudemydelivery.models.SocketEmit
import com.lsepulveda.kotlinudemydelivery.models.User
import com.lsepulveda.kotlinudemydelivery.providers.OrdersProvider
import com.lsepulveda.kotlinudemydelivery.utils.SharedPref
import com.lsepulveda.kotlinudemydelivery.utils.SocketHandler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat


class DeliveryOrdersMapActivity : AppCompatActivity(), OnMapReadyCallback {

    val TAG = "DeliveryOrdersMap"
    var googleMap: GoogleMap? = null

    var toolbar : Toolbar? = null

    val PERMISSION_ID = 42
    var fusedLocationClient: FusedLocationProviderClient? = null

    var city = ""
    var country = ""
    var address = ""
    var addressLatLng: LatLng? = null

    var markerDelivery: Marker? = null
    var markerAddress: Marker? = null
    var myLocationLatLng: LatLng? = null

    var order: Order? = null
    var gson = Gson()

    var textViewClient: TextView? = null
    var textViewAddress: TextView? = null
    var textViewNeighborhood: TextView? = null
    var buttonDelivered: Button? = null
    var circleImageUser: CircleImageView? = null
    var imageViewPhone: ImageView? = null

    val REQUEST_PHONE_CALL = 30

    var ordersProvider: OrdersProvider? = null

    var user: User? = null
    var sharedPref: SharedPref? = null

    var distanceBetween = 0.0f

    var socket: Socket? = null

    private val locationCallback = object: LocationCallback() {

        override fun onLocationResult(locationResult: LocationResult) {
            // OBTENEMOS LA LOCALIZACION EN TIEMPO REAL
            var lastLocation = locationResult.lastLocation
            myLocationLatLng = LatLng(lastLocation.latitude, lastLocation.longitude)
            emitPosition()

//            googleMap?.moveCamera(CameraUpdateFactory.newCameraPosition(
//                CameraPosition.builder().target(
//                    LatLng(myLocationLatLng?.latitude!!, myLocationLatLng?.longitude!!)
//                ).zoom(15f).build()
//            ))

            distanceBetween = getDistanceBetween(myLocationLatLng!!, addressLatLng!!)

            Log.d(TAG, "Distancia: $distanceBetween")

            removeDeliveryMarker()
            addDeliveryMarker()
            Log.d("LOCALIZACION", "Callback: $lastLocation")

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery_orders_map)

        sharedPref = SharedPref(this)

        getUserFromSession()
        order = gson.fromJson(intent.getStringExtra("order"), Order::class.java)

        ordersProvider = OrdersProvider(user?.sessionToken!!)

        addressLatLng = LatLng(order?.address?.lat!!, order?.address?.lng!!)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        textViewClient = findViewById(R.id.textview_client)
        textViewAddress = findViewById(R.id.textview_address)
        textViewNeighborhood = findViewById(R.id.textview_neighborhood)
        circleImageUser = findViewById(R.id.circleimage_user)
        imageViewPhone = findViewById(R.id.imageview_phone)
        buttonDelivered = findViewById(R.id.btn_delivered)

        getLastLocation()

        textViewClient?.text = "${order?.client?.name} ${order?.client?.lastname}"
        textViewAddress?.text = order?.address?.address
        textViewNeighborhood?.text = order?.address?.neighborhood

        if (!order?.client?.image.isNullOrBlank()) {
            Glide.with(this).load(order?.client?.image).into(circleImageUser!!)
        }

        toolbar = findViewById(R.id.toolbar)
        toolbar?.setTitleTextColor(ContextCompat.getColor(this, R.color.black))
        toolbar?.title = "Order #${order?.id}"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        buttonDelivered?.setOnClickListener {

            // si esta cerca de la posicion de entrega, que actualize la orden a entregado
            if (distanceBetween <= 350) {
                updateOrder()
            }
            else {
                // esto es dentro del delivery. que igualmente entrege el pedido a modo de simulacion
                // y pase a pantalla principal, update a campo como entregado y vuelta al menu principal
                //Toast.makeText(this, "Acercate mas al lugar de entrega", Toast.LENGTH_LONG).show()

                mostrarConfirmacion(this)
            }

        }
        imageViewPhone?.setOnClickListener {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CALL_PHONE), REQUEST_PHONE_CALL)
            }
            else {
                call()
            }

        }

        connectSocket()

    }

    private fun mostrarConfirmacion(context: Context) {
        val builder = AlertDialog.Builder(context)

        // Configuramos el mensaje del diálogo
        builder.setMessage("Este no es el lugar de entrega, finalizar entrega de todas maneras?")
            .setCancelable(false) // Impide que el usuario cierre el diálogo al tocar fuera de él.

            // Opción "Continuar"
            .setPositiveButton("Continuar") { dialog, id ->
                // Acción al presionar "Continuar"
                updateOrder()
            }

            // Opción "Cancelar"
            .setNegativeButton("Cancelar") { dialog, id ->
                // Acción al presionar "Cancelar" (solo cierra el diálogo)
                dialog.dismiss()
            }

        // Crear y mostrar el diálogo
        val alert = builder.create()
        alert.show()
    }

    // emitir posicion a Socket IO
    private fun emitPosition() {
        val data = SocketEmit(
            id_order = order?.id!!,
            lat = myLocationLatLng?.latitude!!,
            lng = myLocationLatLng?.longitude!!
        )

        // emite esa data al evento 'position', el que esta en el server
        socket?.emit("position", data.toJson())
    }

    private fun connectSocket() {
        SocketHandler.setSocket()
        socket = SocketHandler.getSocket()
        socket?.connect()
    }

    override fun onDestroy() {
        super.onDestroy()

        // si los servicios de locacion estan activos, detiene actualizaciones de locacion
        if (locationCallback != null && fusedLocationClient != null) {
            fusedLocationClient?.removeLocationUpdates(locationCallback)
        }

        socket?.disconnect()
    }

    private fun updateOrder() {
        ordersProvider?.updateToDelivered(order!!)?.enqueue(object: Callback<ResponseHttp> {
            override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {

                if (response.body() != null) {

                    Toast.makeText(this@DeliveryOrdersMapActivity, "${response.body()?.message}", Toast.LENGTH_LONG).show()

                    if (response.body()?.isSuccess == true) {
                        goToHome()
                    }

                }

            }

            override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                Toast.makeText(this@DeliveryOrdersMapActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun goToHome() {
        val i = Intent(this, DeliveryHomeActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
    }

    private fun getDistanceBetween(fromLatLng: LatLng, toLatLng: LatLng): Float {
        var distance = 0.0f

        val from = Location("")
        val to = Location("")

        from.latitude = fromLatLng.latitude
        from.longitude = fromLatLng.longitude

        to.latitude = toLatLng.latitude
        to.longitude = toLatLng.longitude

        distance = from.distanceTo(to)

        return distance
    }

    private fun call() {
        val i = Intent(Intent.ACTION_CALL)
        i.data = Uri.parse("tel:${order?.client?.phone}")

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permiso denegado para realizar la llamada", Toast.LENGTH_SHORT).show()
            return
        }

        startActivity(i)
    }


    private fun drawRoute() {
        val addressLocation = LatLng(order?.address?.lat!!, order?.address?.lng!!)

        googleMap?.drawRouteOnMap(
            getString(R.string.google_map_api_key),
            source = myLocationLatLng!!,
            destination = addressLocation,
            context = this,
            color = Color.BLACK,
            polygonWidth = 12,
            boundMarkers = false,
            markers = false
        )
    }

    private fun removeDeliveryMarker() {
        markerDelivery?.remove()
    }

    private fun addDeliveryMarker() {
        markerDelivery = googleMap?.addMarker(
            MarkerOptions()
                .position(myLocationLatLng)
                .title("Mi posicion")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.delivery))
        )
    }

    private fun addAddressMarker() {
        val addressLocation = LatLng(order?.address?.lat!!, order?.address?.lng!!)

        markerAddress = googleMap?.addMarker(
            MarkerOptions()
                .position(addressLocation)
                .title("Entregar aqui")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.home))
        )
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.uiSettings?.isZoomControlsEnabled = true
    }

    private fun updateLatLng(lat: Double, lng: Double) {
        order?.lat = lat
        order?.lng = lng

        ordersProvider?.updateLatLng(order!!)?.enqueue(object: Callback<ResponseHttp> {
            override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {

                if (response.body() != null) {

//                    Toast.makeText(this@DeliveryOrdersMapActivity, "${response.body()?.message}", Toast.LENGTH_LONG).show()

                }

            }

            override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                Toast.makeText(this@DeliveryOrdersMapActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun getLastLocation() {
        if (checkPermission()) {

            if (isLocationEnabled()) {

                requestNewLocationData() //INICIAMOS LA POSICION EN TIEMPO REAL

                fusedLocationClient?.lastLocation?.addOnCompleteListener { task ->
                    //OBTIENE LA LOCALIZACION UNA SOLA VEZ
                    var location = task.result

                    if (location != null) {
                        myLocationLatLng = LatLng(location.latitude, location.longitude)

                        updateLatLng(location.latitude, location.longitude)

                        removeDeliveryMarker()
                        addDeliveryMarker()
                        addAddressMarker()
                        drawRoute()

                        googleMap?.moveCamera(CameraUpdateFactory.newCameraPosition(
                            CameraPosition.builder().target(
                                LatLng(location.latitude, location.longitude)
                            ).zoom(15f).build()
                        ))
                    }

                }

            }
            else {
                Toast.makeText(this, "Habilita la localizacion", Toast.LENGTH_LONG).show()
                val i = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(i)
            }

        }
        else {
            requestPermissions()
        }
    }

    private fun requestNewLocationData() {
        val locationRequest = LocationRequest.create().apply {
            interval = 100
            fastestInterval = 50
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient?.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper()) // INICIALIZA LA POSICION

    }



    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }

        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation()
            }
        }

        if (requestCode == REQUEST_PHONE_CALL) {
            call()
        }

    }

    private fun getUserFromSession() {

        val gson = Gson()

        if (!sharedPref?.getData("user").isNullOrBlank()) {
            // SI EL USARIO EXISTE EN SESION
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
        }

    }
}