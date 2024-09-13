package com.lsepulveda.kotlinudemydelivery.fragments.client

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.lsepulveda.kotlinudemydelivery.R
import com.lsepulveda.kotlinudemydelivery.adapters.CategoriesAdapter
import com.lsepulveda.kotlinudemydelivery.models.Category
import com.lsepulveda.kotlinudemydelivery.models.User
import com.lsepulveda.kotlinudemydelivery.providers.CategoriesProvider
import com.lsepulveda.kotlinudemydelivery.utils.SharedPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ClientCategoriesFragment : Fragment() {

    val TAG = "CategoriesFragment" // es constante en vez de variable
    var myView : View? = null
    var recyclerViewCategory : RecyclerView? = null
    var categoriesProvider : CategoriesProvider? = null
    var adapter : CategoriesAdapter? = null
    var user: User? = null
    var sharedPref: SharedPref? = null
    var categories = ArrayList<Category>() // lista vacia
    var toolbar: Toolbar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_client_categories, container, false)

        toolbar = myView?.findViewById(R.id.toolbar)
        toolbar?.setTitleTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        toolbar?.title = "Categorias"
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        recyclerViewCategory = myView?.findViewById(R.id.recyclerview_categories)
        recyclerViewCategory?.layoutManager = LinearLayoutManager(requireContext()) // elementos de manera vertical
        sharedPref = SharedPref(requireActivity())

        getUserFromSession()

        categoriesProvider = CategoriesProvider(user?.sessionToken!!)

        getCategories()

        return myView
    }

    private fun getCategories(){
        categoriesProvider?.getAll()?.enqueue(object : Callback<ArrayList<Category>> {
            override fun onResponse(
                call: Call<ArrayList<Category>>,
                response: Response<ArrayList<Category>>
            ) {
               if (response.body() != null){
                   categories = response.body()!!
                   adapter = CategoriesAdapter(requireActivity(), categories)
                   recyclerViewCategory?.adapter = adapter
               }
            }

            override fun onFailure(call: Call<ArrayList<Category>>, t: Throwable) {
                Log.d(TAG, "Error: ${t.message}")
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
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