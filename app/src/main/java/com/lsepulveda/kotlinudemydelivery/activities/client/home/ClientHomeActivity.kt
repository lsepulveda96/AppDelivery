package com.lsepulveda.kotlinudemydelivery.activities.client.home

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
import com.lsepulveda.kotlinudemydelivery.models.User
import com.lsepulveda.kotlinudemydelivery.providers.UsersProvider
import com.lsepulveda.kotlinudemydelivery.utils.SharedPref
import kotlin.math.log

class ClientHomeActivity : AppCompatActivity() {

    private val TAG = "ClientHomeActivity"
  //  var btnLogout: Button? = null
    var sharedPref: SharedPref? = null

    var usersProvider: UsersProvider?= null

    var user: User?= null

    var bottomNaigation: BottomNavigationView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_home)



        sharedPref = SharedPref(this)
        //btnLogout = findViewById(R.id.btn_logout)
        //btnLogout?.setOnClickListener{
          //  // para cerrar sesion
         //   logout()
       // }

        openFragment(ClientCategoriesFragment())

        bottomNaigation = findViewById(R.id.bottom_navigation)
        bottomNaigation?.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.item_home -> {
                    openFragment(ClientCategoriesFragment())
                    true
                }
                R.id.item_orders -> {
                    openFragment(ClientOrdersFragment())
                    true
                }
                R.id.item_profile -> {
                    openFragment(ClientProfileFragment())
                    true
                }

                else -> false
            }
        }
        getUserFromSession()
        usersProvider = UsersProvider(token = user?.sessionToken)
        createToken()
    }

    private fun createToken(){
        usersProvider?.createToken(user!!, this)
    }

    private fun openFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun getUserFromSession(){

        val gson = Gson()

        // si el usuario existe en sesion
        if(!sharedPref?.getData("user").isNullOrBlank()){
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)

        }
    }
}