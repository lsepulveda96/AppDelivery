package com.lsepulveda.kotlinudemydelivery.activities.client.update

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import com.lsepulveda.kotlinudemydelivery.R
import com.lsepulveda.kotlinudemydelivery.models.ResponseHttp
import com.lsepulveda.kotlinudemydelivery.models.User
import com.lsepulveda.kotlinudemydelivery.providers.UsersProvider
import com.lsepulveda.kotlinudemydelivery.utils.SharedPref
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ClientUpdateActivity : AppCompatActivity() {

    val TAG = "ClientUpdateActivity"

    var circleImageUser: CircleImageView? = null
    var editTextName: EditText? = null
    var editTextLastname: EditText? = null
    var editTextPhone: EditText? = null
    var buttonUpdate: Button? = null

    var sharedPref: SharedPref? = null
    var user: User? = null

    private var imageFile: File? = null

    var usersProvider : UsersProvider? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client_update)

        sharedPref = SharedPref(this)

        circleImageUser = findViewById(R.id.circle_image_user)
        editTextName = findViewById(R.id.edittext_name)
        editTextLastname = findViewById(R.id.edittext_lastname)
        editTextPhone = findViewById(R.id.edittext_phone)
        buttonUpdate = findViewById(R.id.btn_update)

        // obtiene los datos del usuario
        getUserFromSession()

        usersProvider = UsersProvider(user?.sessionToken)

        editTextName?.setText(user?.name)
        editTextLastname?.setText(user?.lastname)
        editTextPhone?.setText(user?.phone)

        if(!user?.image.isNullOrBlank()){
            // para saber si user tiene imagen almacenada en campo image
            Glide.with(this).load(user?.image).into(circleImageUser!!)
        }

        circleImageUser?.setOnClickListener { selectImage() }
        buttonUpdate?.setOnClickListener { updateData() }

    }

    private fun getUserFromSession(){
        val gson = Gson()
        // si el usuario existe en sesion
        if(!sharedPref?.getData("user").isNullOrBlank()){
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
        }
    }


    private fun updateData() {

        val name = editTextName?.text.toString()
        val lastname = editTextLastname?.text.toString()
        val phone = editTextPhone?.text.toString()

        user?.name = name
        user?.lastname = lastname
        user?.phone = phone

        if(imageFile != null){
            usersProvider?.update(imageFile!!, user!!)?.enqueue(object: Callback<ResponseHttp> {
                override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {
                    Log.d(TAG, "Response: $response")
                    Log.d(TAG, "Response: ${response.body()}")

                    Toast.makeText(this@ClientUpdateActivity, response.body()?.message, Toast.LENGTH_SHORT).show()

                    if(response.body()?.isSuccess == true){
                        saveUserInSession(response.body()?.data.toString())
                    }
                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Log.d(TAG, "Error: ${t.message}")
                    Toast.makeText(this@ClientUpdateActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }else{
            usersProvider?.updateWhithoutImage(user!!)?.enqueue(object: Callback<ResponseHttp> {
                override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {
                    Log.d(TAG, "Response: $response")
                    Log.d(TAG, "Response: ${response.body()}")

                    Toast.makeText(this@ClientUpdateActivity, response.body()?.message, Toast.LENGTH_SHORT).show()

                    if(response.body()?.isSuccess == true){
                        saveUserInSession(response.body()?.data.toString())
                    }

                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Log.d(TAG, "Error: ${t.message}")
                    Toast.makeText(this@ClientUpdateActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

    }

    private fun saveUserInSession(data: String){
        val gson = Gson()
        // tranforma lo que trae la data en objeto usuario
        val user = gson.fromJson(data, User::class.java)
        sharedPref?.save("user", user)
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