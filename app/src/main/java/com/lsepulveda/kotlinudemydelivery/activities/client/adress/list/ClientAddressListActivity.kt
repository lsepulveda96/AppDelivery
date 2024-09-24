package com.lsepulveda.kotlinudemydelivery.activities.client.adress.list

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.lsepulveda.kotlinudemydelivery.R
import com.lsepulveda.kotlinudemydelivery.activities.client.adress.create.ClientAdressCreateActivity
import com.lsepulveda.kotlinudemydelivery.activities.client.payments.form.ClientPaymentFormActivity
import com.lsepulveda.kotlinudemydelivery.adapters.AddressAdapter
import com.lsepulveda.kotlinudemydelivery.models.Address
import com.lsepulveda.kotlinudemydelivery.models.User
import com.lsepulveda.kotlinudemydelivery.providers.AddressProvider
import com.lsepulveda.kotlinudemydelivery.utils.SharedPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class ClientAddressListActivity : AppCompatActivity() {

    var fabCreateAddress: FloatingActionButton? = null
    var toolbar: Toolbar? = null

    var recyclerViewAddress : RecyclerView? = null
    var buttonNext: Button? = null
    var adapter : AddressAdapter? = null
    var addressProvider: AddressProvider? = null // lanza la peticion
    var sharedPref : SharedPref? = null
    var user: User? = null

    var address = ArrayList<Address>()

    val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_adress_list)

        sharedPref = SharedPref(this)

        fabCreateAddress = findViewById(R.id.fab_address_create)
        toolbar = findViewById(R.id.toolbar)
        buttonNext = findViewById(R.id.btn_next)
        recyclerViewAddress = findViewById(R.id.recyclerview_address)

        recyclerViewAddress?.layoutManager = LinearLayoutManager(this)

        toolbar?.setTitleTextColor(ContextCompat.getColor(this, R.color.black))
        toolbar?.title = "Mis direcciones"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        getUserFromSession()
        addressProvider = AddressProvider(user?.sessionToken!!)

        fabCreateAddress?.setOnClickListener{ goToAddressCreate() }

        getAddress()

        buttonNext?.setOnClickListener{getAddressFromSession()}
    }

    private fun getAddressFromSession(){
        if(!sharedPref?.getData("address").isNullOrBlank()){
            val a = gson.fromJson(sharedPref?.getData("address"), Address::class.java) // si user ya seleccion dir
            goToPaymentForm()
        }else{
            Toast.makeText(this, "Selecciona una direccion para continuar", Toast.LENGTH_SHORT).show()
        }
    }


    private fun goToPaymentForm(){
        val i = Intent(this, ClientPaymentFormActivity::class.java)
        startActivity(i)
    }

    // no es privada asi la puede usar el recycler view
    // para ocultar otra direccion que no seleccione
    fun resetValue(position: Int){
        val viewHolder = recyclerViewAddress?.findViewHolderForAdapterPosition(position) // obtenemos en la clase padre una direccion en particular
        val view = viewHolder?.itemView
        val imageViewCheck = view?.findViewById<ImageView>(R.id.imageview_check)
        imageViewCheck?.visibility = View.GONE
    }

    private fun goToAddressCreate(){
        val i = Intent(this, ClientAdressCreateActivity::class.java)
        startActivity(i)
    }

    // obtener datos de usuario en sesion
    private fun getUserFromSession(){
        val gson = Gson()
        // si el usuario existe en sesion
        if(!sharedPref?.getData("user").isNullOrBlank()){
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
        }
    }

    private fun getAddress(){
        addressProvider?.getAddress(user?.id!!)?.enqueue(object : Callback<ArrayList<Address>>{
            override fun onResponse(
                call: Call<ArrayList<Address>>,
                response: Response<ArrayList<Address>>
            ) {
                if(response.body() != null){ // nos devolvio data de servidor
                    address = response.body()!! // puede venir nulo
                    adapter = AddressAdapter(this@ClientAddressListActivity, address)
                    recyclerViewAddress?.adapter = adapter
                }
            }

            override fun onFailure(call: Call<ArrayList<Address>>, t: Throwable) {
                Toast.makeText(this@ClientAddressListActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }

        })

    }

}