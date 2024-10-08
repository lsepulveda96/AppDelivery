package com.lsepulveda.kotlinudemydelivery.fragments.restaurant

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import com.lsepulveda.kotlinudemydelivery.R
import com.lsepulveda.kotlinudemydelivery.models.Category
import com.lsepulveda.kotlinudemydelivery.models.ResponseHttp
import com.lsepulveda.kotlinudemydelivery.models.User
import com.lsepulveda.kotlinudemydelivery.providers.CategoriesProvider
import com.lsepulveda.kotlinudemydelivery.utils.SharedPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class RestaurantCategoryFragment : Fragment() {

    val TAG = "CategoryFragment"
    var myView : View? = null
    var imageViewCategory : ImageView? = null
    var editTextCategory : EditText? = null
    var buttonCreate : Button? = null

    private var imageFile: File? = null

    var categoriesProvider: CategoriesProvider? = null
    var sharedPref: SharedPref? = null
    var user: User? = null

      override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

          myView = inflater.inflate(R.layout.fragment_restaurant_category, container, false)

          sharedPref = SharedPref(requireActivity())

          imageViewCategory = myView?.findViewById(R.id.imageview_category)
          editTextCategory  = myView?.findViewById(R.id.edittext_category)
          buttonCreate =  myView?.findViewById(R.id.btn_create)

          imageViewCategory?.setOnClickListener{selectImage()}
          buttonCreate?.setOnClickListener{createCategory()}

          getUserFromSession() // para obtener datros del usuario
          categoriesProvider = CategoriesProvider(user?.sessionToken!!)

          return myView
    }


    private fun getUserFromSession(){
        val gson = Gson()
        // si el usuario existe en sesion
        if(!sharedPref?.getData("user").isNullOrBlank()){
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
        }
    }


    private fun createCategory(){
        val name = editTextCategory?.text.toString()

        if(imageFile != null){

            val category = Category(name = name)

            categoriesProvider?.create(imageFile!!, category)?.enqueue(object: Callback<ResponseHttp> {
                override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {

                    Log.d(TAG, "Response: $response")
                    Log.d(TAG, "Response: ${response.body()}")

                    Toast.makeText(requireContext(), response.body()?.message, Toast.LENGTH_SHORT).show()

                    if(response.body()?.isSuccess == true){
                        // si se ejecuta correctamente, limpia el formulario
                        clearForm()
                    }

                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Log.d(TAG, "Error: ${t.message}")
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }else{
            Toast.makeText(requireContext(), "Selecciona una imagen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearForm(){
        editTextCategory?.setText("")
        imageFile = null
        imageViewCategory?.setImageResource(R.drawable.ic_image)
    }

    private val startImageForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult ->

        val resultCode = result.resultCode
        val data = result.data

        // si selecciono una imagen correctamente
        if(resultCode == Activity.RESULT_OK){
            val fileUri = data?.data
            //agregar imagen en el circulo
            imageFile = File(fileUri?.path) // archivo que vamos a guardar como imagen en el servidor
            imageViewCategory?.setImageURI(fileUri)
        }
        else if(resultCode == ImagePicker.RESULT_ERROR){
            Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(requireContext(), "Tarea cancelada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun selectImage(){
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080,1080)
            .createIntent { intent ->
                startImageForResult.launch(intent)
            }
    }
}
