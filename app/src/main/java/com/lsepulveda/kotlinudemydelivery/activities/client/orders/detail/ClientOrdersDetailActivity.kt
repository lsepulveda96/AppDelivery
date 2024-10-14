package com.lsepulveda.kotlinudemydelivery.activities.client.orders.detail

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.lsepulveda.kotlinudemydelivery.R
import com.lsepulveda.kotlinudemydelivery.adapters.OrderProductsAdapter
import com.lsepulveda.kotlinudemydelivery.models.Order

class ClientOrdersDetailActivity : AppCompatActivity() {

    val TAG = "ClientOrdersDetail"
    var order : Order?= null
    val gson = Gson()

    var toolbar : Toolbar? = null
    var textViewClient : TextView? = null
    var textViewAddress : TextView? = null
    var textViewDate : TextView? = null
    var textViewTotal : TextView? = null
    var textViewStatus : TextView? = null
    var recyclerViewProducts : RecyclerView? = null

    var adapter : OrderProductsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_orders_detail)

        order = gson.fromJson(intent.getStringExtra("order"), Order::class.java)

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

        adapter = OrderProductsAdapter(this, order?.products!!)
        recyclerViewProducts?.adapter = adapter

        textViewClient?.text = "${order?.client?.name} ${order?.client?.lastname}"
        textViewAddress?.text = order?.address?.address
        textViewDate?.text = "${order?.timestamp}"
        textViewStatus?.text = order?.status

        Log.d(TAG, "Order: ${order.toString()}")

        getTotal()
    }

    private fun getTotal(){

        var total = 0.0

        for(p in order?.products!!){
            total = total + (p.price * p.quantity!!)
        }

        textViewTotal?.text = "${total}$"
    }
}