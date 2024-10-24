package com.lsepulveda.kotlinudemydelivery.activities.client.adress.list

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Looper
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
import com.google.gson.reflect.TypeToken
import com.lsepulveda.kotlinudemydelivery.R
import com.lsepulveda.kotlinudemydelivery.activities.client.adress.create.ClientAdressCreateActivity
import com.lsepulveda.kotlinudemydelivery.activities.client.payments.form.ClientPaymentFormActivity
import com.lsepulveda.kotlinudemydelivery.adapters.AddressAdapter
import com.lsepulveda.kotlinudemydelivery.adapters.ShoppingBagAdapter
import com.lsepulveda.kotlinudemydelivery.models.Address
import com.lsepulveda.kotlinudemydelivery.models.Order
import com.lsepulveda.kotlinudemydelivery.models.Product
import com.lsepulveda.kotlinudemydelivery.models.ResponseHttp
import com.lsepulveda.kotlinudemydelivery.models.User
import com.lsepulveda.kotlinudemydelivery.providers.AddressProvider
import com.lsepulveda.kotlinudemydelivery.providers.OrdersProvider
import com.lsepulveda.kotlinudemydelivery.utils.SharedPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList
import android.os.Handler
import com.lsepulveda.kotlinudemydelivery.activities.client.home.ClientHomeActivity
import kotlinx.coroutines.*

class ClientAddressListActivity : AppCompatActivity() {

    var fabCreateAddress: FloatingActionButton? = null
    var toolbar: Toolbar? = null

    var recyclerViewAddress : RecyclerView? = null
    var buttonNext: Button? = null
    var adapter : AddressAdapter? = null
    var addressProvider: AddressProvider? = null // lanza la peticion
    var ordersProvider: OrdersProvider? = null
    var sharedPref : SharedPref? = null
    var user: User? = null

    var address = ArrayList<Address>()

    val gson = Gson()

    var selectedProducts = ArrayList<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_adress_list)

        sharedPref = SharedPref(this)

        getProductsFromSharedPref() // obtenemos productos de la sesion

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
        ordersProvider = OrdersProvider(user?.sessionToken!!)

        fabCreateAddress?.setOnClickListener{ goToAddressCreate() }

        getAddress()

        buttonNext?.setOnClickListener{getAddressFromSession()}
    }

    private fun getAddressFromSession(){

        if(!sharedPref?.getData("address").isNullOrBlank()){

            val progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Simulando pago...")
            progressDialog.setCancelable(false) // Evita que el usuario lo cierre
            progressDialog.show()

            // Simular el tiempo de espera de 3 segundos sin bloquear el hilo principal
            Handler(Looper.getMainLooper()).postDelayed({
                // Cerrar el ProgressDialog después de los 3 segundos
                progressDialog.dismiss()

                // Luego de cerrar el ProgressDialog, continúa con la lógica
                val a = gson.fromJson(sharedPref?.getData("address"), Address::class.java) // si user ya seleccion dir
                //goToPaymentForm()
                
                // sharedPref?.remove("order") // temporal, para probar y que no me borre la orden denuevo
                createOrder(a.id!!) // id address
                goToHome()  // Asegúrate de que esto se ejecute después de los 3 segundos
            }, 3000) // 3000 milisegundos = 3 segundos


        }else{
            Toast.makeText(this, "Selecciona una direccion para continuar", Toast.LENGTH_SHORT).show()
        }
    }

    private fun goToHome() {
        val i = Intent(this, ClientHomeActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
    }

    private fun createOrder(idAddress: String){
        val order = Order(
            products = selectedProducts,
            idClient = user?.id!!,
            idAddress = idAddress
        )

        ordersProvider?.create(order)?.enqueue(object: Callback<ResponseHttp>{
            override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {
                // si hubo respuesta del servidor
                if(response.body() != null){
                    Toast.makeText(this@ClientAddressListActivity, "${response.body()?.message}", Toast.LENGTH_LONG).show()
                }else{
                    Toast.makeText(this@ClientAddressListActivity, "Ocurrio un error en la peticion", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                Toast.makeText(this@ClientAddressListActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }

        })
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

    private fun getProductsFromSharedPref() {

        if(!sharedPref?.getData("order").isNullOrBlank()){ // existe una orden en shared pref
            val type = object : TypeToken<ArrayList<Product>>() {}.type
            selectedProducts = gson.fromJson(sharedPref?.getData("order"), type) // pasa un tipo json a un array de product
        }
    }

}