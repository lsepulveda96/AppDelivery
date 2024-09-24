package com.lsepulveda.kotlinudemydelivery.activities.client.adress.create

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.lsepulveda.kotlinudemydelivery.R
import com.lsepulveda.kotlinudemydelivery.activities.client.adress.list.ClientAddressListActivity
import com.lsepulveda.kotlinudemydelivery.activities.client.adress.map.ClientAddressMapActivity
import com.lsepulveda.kotlinudemydelivery.models.Address
import com.lsepulveda.kotlinudemydelivery.models.ResponseHttp
import com.lsepulveda.kotlinudemydelivery.models.User
import com.lsepulveda.kotlinudemydelivery.providers.AddressProvider
import com.lsepulveda.kotlinudemydelivery.utils.SharedPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClientAdressCreateActivity : AppCompatActivity() {

    val TAG = "ClientAdressCreate"
    var toolbar : Toolbar? = null
    var editTextRefPoint : EditText? = null
    var editTextAddress : EditText? = null
    var editTextNeighborhood : EditText? = null
    var buttonCreateAddress : Button? = null

    var addressLat = 0.0
    var addressLng = 0.0

    var addressProvider: AddressProvider? = null
    var sharedPref : SharedPref? = null
    var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_adress_create)

        toolbar = findViewById(R.id.toolbar)
        editTextRefPoint = findViewById(R.id.edittext_ref_point)
        editTextAddress = findViewById(R.id.edittext_address)
        editTextNeighborhood = findViewById(R.id.edittext_neighboorhood)
        buttonCreateAddress = findViewById(R.id.btn_create_address)

        sharedPref = SharedPref(this)
        getUserFromSession()
        addressProvider = AddressProvider(user?.sessionToken!!)

        toolbar?.setTitleTextColor(ContextCompat.getColor(this, R.color.black))
        toolbar?.title = "Nueva direccion"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        editTextRefPoint?.setOnClickListener{goToAddressMap()}
        buttonCreateAddress?.setOnClickListener{createAddress()}
    }

    private fun createAddress(){

        val address = editTextAddress?.text.toString()
        val neighborhood = editTextNeighborhood?.text.toString()

        if(isValidForm(address, neighborhood)){
            // lanzar la peticion al servidor node.js
            val addressModel = Address(
                address = address,
                neighborhood = neighborhood,
                idUser = user?.id!!,
                lat = addressLat,
                lng = addressLng
            )
            addressProvider?.create(addressModel)?.enqueue(object: Callback<ResponseHttp>{
                override fun onResponse(
                    call: Call<ResponseHttp>,
                    response: Response<ResponseHttp>
                ) {
                    if(response.body() != null){
                        Toast.makeText(this@ClientAdressCreateActivity, response.body()?.message, Toast.LENGTH_SHORT).show()
                        goToAddressList()
                    }else{
                        Toast.makeText(this@ClientAdressCreateActivity, "Ocurrio un error en la peticion", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Toast.makeText(this@ClientAdressCreateActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }

            })
        }
    }

    private fun goToAddressList(){
        val i = Intent(this, ClientAddressListActivity::class.java)
        startActivity(i)
    }


    private fun isValidForm(address: String, neighborhood: String) : Boolean{
        if(address.isNullOrBlank()){
            Toast.makeText(this, "Ingresa la direccion", Toast.LENGTH_SHORT).show()
            return false
        }

        if(neighborhood.isNullOrBlank()){
            Toast.makeText(this, "Ingresa el barrio", Toast.LENGTH_SHORT).show()
            return false
        }

        if(addressLat == 0.0){ // no es latitud validad para trabajar
            Toast.makeText(this, "selecciona punto de referencia", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->

        if (result.resultCode == Activity.RESULT_OK){
            val data = result.data
            val city = data?.getStringExtra("city")
            val address = data?.getStringExtra("address")
            val country = data?.getStringExtra("country")
            addressLat = data?.getDoubleExtra("lat", 0.0)!!
            addressLng = data?.getDoubleExtra("lng", 0.0)!!

            editTextRefPoint?.setText("$address $city")

            Log.d(TAG, "city: $city")
            Log.d(TAG, "address :$address")
            Log.d(TAG, "country: $country")
            Log.d(TAG, "lat: $addressLat")
            Log.d(TAG, "lng: $addressLng")

        }
    }

    private fun goToAddressMap(){
        val i = Intent(this, ClientAddressMapActivity::class.java)
        resultLauncher.launch(i)// envia una data la actividad hijo. (pantalla de create)
    }

    // obtener datos de usuario en sesion
    private fun getUserFromSession(){
        val gson = Gson()
        // si el usuario existe en sesion
        if(!sharedPref?.getData("user").isNullOrBlank()){
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
        }
    }
}