package com.lsepulveda.kotlinudemydelivery.activities.delivery.home.orders.map

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
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
            myLocationLatLng = LatLng(lastLocation?.latitude!!, lastLocation?.longitude!!)

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
