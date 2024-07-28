package com.lsepulveda.kotlinudemydelivery.activities.restaurant.home


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.lsepulveda.kotlinudemydelivery.R
import com.lsepulveda.kotlinudemydelivery.activities.MainActivity
import com.lsepulveda.kotlinudemydelivery.fragments.client.ClientCategoriesFragment
import com.lsepulveda.kotlinudemydelivery.fragments.client.ClientOrdersFragment
import com.lsepulveda.kotlinudemydelivery.fragments.client.ClientProfileFragment
import com.lsepulveda.kotlinudemydelivery.fragments.restaurant.RestaurantCategoryFragment
import com.lsepulveda.kotlinudemydelivery.fragments.restaurant.RestaurantOrdersFragment
import com.lsepulveda.kotlinudemydelivery.fragments.restaurant.RestaurantProductFragment
import com.lsepulveda.kotlinudemydelivery.models.User
import com.lsepulveda.kotlinudemydelivery.utils.SharedPref
import kotlin.math.log

class RestaurantHomeActivity : AppCompatActivity() {

    private val TAG = "RestaurantHomeActivity"
    //  var btnLogout: Button? = null
    var sharedPref: SharedPref? = null

    var bottomNaigation: BottomNavigationView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_home)



        sharedPref = SharedPref(this)
        //btnLogout = findViewById(R.id.btn_logout)
        //btnLogout?.setOnClickListener{
        //  // para cerrar sesion
        //   logout()
        // }

        openFragment(RestaurantOrdersFragment())

        bottomNaigation = findViewById(R.id.bottom_navigation)
        bottomNaigation?.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.item_home -> {
                    openFragment(RestaurantOrdersFragment())
                    true
                }
                R.id.item_category -> {
                    openFragment(RestaurantCategoryFragment())
                    true
                }
                R.id.item_product -> {
                    openFragment(RestaurantProductFragment())
                    true
                }
                // por defecto a todas
                R.id.item_profile -> {
                    openFragment(ClientProfileFragment())
                    true
                }

                else -> false
            }
        }
        getUserFromSession()
    }

    private fun openFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    private fun logout(){
        sharedPref?.remove("user")
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }
    private fun getUserFromSession(){

        val gson = Gson()

        // si el usuario existe en sesion
        if(!sharedPref?.getData("user").isNullOrBlank()){
            val user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
            Log.d(TAG, "Usuario: $user")
        }
    }
}