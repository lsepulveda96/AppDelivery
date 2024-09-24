package com.lsepulveda.kotlinudemydelivery.activities.client.shopping_bag

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lsepulveda.kotlinudemydelivery.R
import com.lsepulveda.kotlinudemydelivery.activities.client.adress.list.ClientAddressListActivity
import com.lsepulveda.kotlinudemydelivery.adapters.ShoppingBagAdapter
import com.lsepulveda.kotlinudemydelivery.models.Product
import com.lsepulveda.kotlinudemydelivery.utils.SharedPref

class ClientShoppingBagActivity : AppCompatActivity() {
    var recyclerViewShoppingBag: RecyclerView? = null
    var textViewTotal: TextView? = null
    var buttonNext: Button? = null
    var toolbar: Toolbar? = null

    var adapter: ShoppingBagAdapter? = null
    var sharedPref: SharedPref? = null
    var gson = Gson()
    var selectedProducts = ArrayList<Product>()

    override
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_shopping_bag)

        sharedPref = SharedPref(this)
        recyclerViewShoppingBag = findViewById(R.id.recyclerview_shoppingbag)
        textViewTotal = findViewById(R.id.textview_total)
        buttonNext = findViewById(R.id.btn_next)
        toolbar = findViewById(R.id.toolbar)
        toolbar?.setTitleTextColor(ContextCompat.getColor(this, R.color.black))
        toolbar?.setTitle("Tu orden")

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // para que habilite flecha atras

        recyclerViewShoppingBag?.layoutManager = LinearLayoutManager(this)

        getProductsFromSharedPref()

        buttonNext?.setOnClickListener{goToAddressList()}
    }

    private fun goToAddressList(){
        val i = Intent(this, ClientAddressListActivity::class.java)
        startActivity(i)
    }

    fun setTotal(total: Double){
        textViewTotal?.text = "${total}$"
    }

    private fun getProductsFromSharedPref() {

        if(!sharedPref?.getData("order").isNullOrBlank()){ // existe una orden en shared pref
            val type = object : TypeToken<ArrayList<Product>>() {}.type
            selectedProducts = gson.fromJson(sharedPref?.getData("order"), type) // pasa un tipo json a un array de product

            adapter = ShoppingBagAdapter(this, selectedProducts)
            recyclerViewShoppingBag?.adapter = adapter
        }
    }

}