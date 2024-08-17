package com.lsepulveda.kotlinudemydelivery.fragments.restaurant

import android.app.Activity
import android.os.Bundle
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
import com.lsepulveda.kotlinudemydelivery.R
import java.io.File

class RestaurantCategoryFragment : Fragment() {

    var myView : View? = null
    var imageViewCategory : ImageView? = null
    var editTextCategory : EditText? = null
    var buttonCreate : Button? = null

    private var imageFile: File? = null
      override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

          myView = inflater.inflate(R.layout.fragment_restaurant_category, container, false)

          imageViewCategory = myView?.findViewById(R.id.imageview_category)
          editTextCategory  = myView?.findViewById(R.id.edittext_category)
          buttonCreate =  myView?.findViewById(R.id.btn_create)

          imageViewCategory?.setOnClickListener{selectImage()}
          buttonCreate?.setOnClickListener{createCategory()}

          return myView
    }

    private fun createCategory(){
        val category = editTextCategory?.text.toString()

        if(imageFile != null){

        }else{
            Toast.makeText(requireContext(), "Selecciona una imagen", Toast.LENGTH_SHORT).show()
        }
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
