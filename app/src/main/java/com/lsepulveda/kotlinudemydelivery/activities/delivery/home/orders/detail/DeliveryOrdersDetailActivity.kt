package com.lsepulveda.kotlinudemydelivery.activities.delivery.home.orders.detail

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.lsepulveda.kotlinudemydelivery.R
import com.lsepulveda.kotlinudemydelivery.activities.delivery.home.orders.map.DeliveryOrdersMapActivity
import com.lsepulveda.kotlinudemydelivery.adapters.OrderProductsAdapter
import com.lsepulveda.kotlinudemydelivery.models.Order
import com.lsepulveda.kotlinudemydelivery.models.ResponseHttp
import com.lsepulveda.kotlinudemydelivery.models.User
import com.lsepulveda.kotlinudemydelivery.providers.OrdersProvider
import com.lsepulveda.kotlinudemydelivery.providers.UsersProvider
import com.lsepulveda.kotlinudemydelivery.utils.SharedPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeliveryOrdersDetailActivity : AppCompatActivity() {

    val TAG = "DeliveryOrdersDetail"
    var order : Order?= null
    val gson = Gson()

    var toolbar : Toolbar? = null
    var textViewClient : TextView? = null
    var textViewAddress : TextView? = null
    var textViewDate : TextView? = null
    var textViewTotal : TextView? = null
    var textViewStatus : TextView? = null
    var recyclerViewProducts : RecyclerView? = null
    var buttonStartDelivery : Button? = null
    var buttonGoToMap : Button? = null

    var adapter : OrderProductsAdapter? = null

    var usersProvider: UsersProvider? = null
    var ordersProvider: OrdersProvider? = null
    var user : User? = null
    var sharedPref: SharedPref?= null

    var idDelivery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery_orders_detail)

        sharedPref = SharedPref(this)
        order = gson.fromJson(intent.getStringExtra("order"), Order::class.java)

        getUserFromSession()
        usersProvider = UsersProvider(user?.sessionToken!!)
        ordersProvider = OrdersProvider(user?.sessionToken!!)

        toolbar = findViewById(R.id.toolbar)
        toolbar?.setTitleTextColor(ContextCompat.getColor(this, R.color.black))
        toolbar?.title = "Order #${order?.id}"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        textViewClient = findViewById(R.id.textview_client)
        textViewAddress = findViewById(R.id.textview_address)
        textViewDate = findViewById(R.id.textview_date)
        textViewTotal = findViewById(R.id.textview_total)
        textViewStatus = findViewById(R.id.textview_status)

        recyclerViewProducts = findViewById(R.id.recyclerview_products)
        recyclerViewProducts?.layoutManager = LinearLayoutManager(this)
        buttonStartDelivery = findViewById(R.id.btn_start_delivery)
        buttonGoToMap = findViewById(R.id.btn_go_to_map)

        adapter = OrderProductsAdapter(this, order?.products!!)
        recyclerViewProducts?.adapter = adapter

        textViewClient?.text = "${order?.client?.name} ${order?.client?.lastname}"
        textViewAddress?.text = order?.address?.address
        textViewDate?.text = "${order?.timestamp}"
        textViewStatus?.text = order?.status


        Log.d(TAG, "Order: ${order.toString()}")

        getTotal()

        if (order?.status == "DESPACHADO") {
            buttonStartDelivery?.visibility = View.VISIBLE
        }

        if (order?.status == "EN CAMINO") {
            buttonGoToMap?.visibility = View.VISIBLE
        }

            buttonStartDelivery?.setOnClickListener { updateOrder() }
            buttonGoToMap?.setOnClickListener { goToMap() }
        }


    private fun updateOrder(){

        ordersProvider?.updateToOnTheWay(order!!)?.enqueue(object : Callback<ResponseHttp>{
            override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {
                if(response.body() != null){
                    if(response.body()?.isSuccess == true){
                        Toast.makeText(this@DeliveryOrdersDetailActivity, "Entrega iniciada", Toast.LENGTH_SHORT)
                            .show()
                        goToMap()
                    }else{
                        Toast.makeText(this@DeliveryOrdersDetailActivity, "No se pudo asignar el repartidor", Toast.LENGTH_SHORT)
                            .show()
                    }
                }else{
                    Toast.makeText(this@DeliveryOrdersDetailActivity, "No hubo respuesta del servidor", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                Toast.makeText(this@DeliveryOrdersDetailActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }


    private fun goToMap(){
        val i = Intent(this, DeliveryOrdersMapActivity::class.java)
        i.putExtra("order", order?.toJson()) // pasa la orden, para desps obtener la ubicacion

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


    private fun getTotal(){

        var total = 0.0

        for(p in order?.products!!){
            total = total + (p.price * p.quantity!!)
        }

        textViewTotal?.text = "${total}$"
    }
}

