package com.lsepulveda.kotlinudemydelivery.activities.client.products.list

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.lsepulveda.kotlinudemydelivery.R
import com.lsepulveda.kotlinudemydelivery.adapters.ProductsAdapter
import com.lsepulveda.kotlinudemydelivery.models.Product
import com.lsepulveda.kotlinudemydelivery.models.User
import com.lsepulveda.kotlinudemydelivery.providers.ProductsProvider
import com.lsepulveda.kotlinudemydelivery.utils.SharedPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ClientProductListActivity : AppCompatActivity() {

    val TAG = "ClientProducts"
    var recyclerViewProducts: RecyclerView? = null
    var adapter: ProductsAdapter? = null
    var user: User? = null

    var sharedPref: SharedPref? = null

    var productsProvider : ProductsProvider? = null
    var products: ArrayList<Product> = ArrayList()

    var idCategory: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_product_list)

        sharedPref = SharedPref(this)
        idCategory = intent.getStringExtra("idCategory")
        getUserFromSession()

        productsProvider = ProductsProvider(user?.sessionToken!!)
        recyclerViewProducts = findViewById(R.id.recyclerview_products)
        recyclerViewProducts?.layoutManager = GridLayoutManager(this, 2) // numero de columnas

        getProducts()

    }

    private fun getProducts(){
        productsProvider?.findByCategory(idCategory!!)?.enqueue(object:
            Callback<ArrayList<Product>> {
            override fun onResponse(
                call: Call<ArrayList<Product>>,
                response: Response<ArrayList<Product>>
            ) {
                if(response.body() != null){
                    products = response.body()!!
                    adapter = ProductsAdapter(this@ClientProductListActivity, products)
                    recyclerViewProducts?.adapter = adapter
                }
            }

            override fun onFailure(call: Call<ArrayList<Product>>, t: Throwable) {
                Toast.makeText(this@ClientProductListActivity, t.message, Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Error: ${t.message}")
            }

        })
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