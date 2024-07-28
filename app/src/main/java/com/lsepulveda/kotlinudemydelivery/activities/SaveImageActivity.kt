package com.lsepulveda.kotlinudemydelivery.activities

import android.app.Activity
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import com.lsepulveda.kotlinudemydelivery.R
import com.lsepulveda.kotlinudemydelivery.activities.client.home.ClientHomeActivity
import com.lsepulveda.kotlinudemydelivery.models.ResponseHttp
import com.lsepulveda.kotlinudemydelivery.models.User
import com.lsepulveda.kotlinudemydelivery.providers.UsersProvider
import com.lsepulveda.kotlinudemydelivery.utils.SharedPref
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class SaveImageActivity : AppCompatActivity() {

    val TAG = "SaveImageActivity"
    var circleImageUser: CircleImageView? = null
    var btnNext: Button? = null
    var btnConfirm: Button? = null

    private var imageFile: File? = null

    var usersProvider = UsersProvider()

    var user: User? = null
    var sharedPref: SharedPref? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_image)

        sharedPref = SharedPref(this)

        // obtener datos del usuario
        getUserFromSession()

        circleImageUser = findViewById(R.id.circle_image_user)
        btnNext = findViewById(R.id.btn_next)
        btnConfirm = findViewById(R.id.btn_confirm)

        circleImageUser?.setOnClickListener{
            selectImage()
        }

        btnNext?.setOnClickListener{goToClientHome()}
        btnConfirm?.setOnClickListener{ saveImage()}
    }

    private fun saveImage() {

        if(imageFile!=null && user != null){
            usersProvider.update(imageFile!!, user!!)?.enqueue(object: Callback<ResponseHttp>{
                override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {
                    Log.d(TAG, "Response: $response")
                    Log.d(TAG, "Response: ${response.body()}")

                    saveUserInSession(response.body()?.data.toString())
                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Log.d(TAG, "Error: ${t.message}")
                    Toast.makeText(this@SaveImageActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }else{
            Toast.makeText(this, "Se requiere imagen y datos de sesion validos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserInSession(data: String){
        val gson = Gson()
        // tranforma lo que trae la data en objeto usuario
        val user = gson.fromJson(data, User::class.java)
        sharedPref?.save("user", user)
        goToClientHome()
    }

    private fun goToClientHome(){
        val i = Intent(this, ClientHomeActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // eliminar historial de pantallas
        startActivity(i)
    }

    private fun getUserFromSession(){

        val gson = Gson()

        // si el usuario existe en sesion
        if(!sharedPref?.getData("user").isNullOrBlank()){
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
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
            circleImageUser?.setImageURI(fileUri)
        }
        else if(resultCode == ImagePicker.RESULT_ERROR){
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(this, "Tarea cancelada", Toast.LENGTH_SHORT).show()
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