package com.lsepulveda.kotlinudemydelivery.activities

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.lsepulveda.kotlinudemydelivery.R
import com.lsepulveda.kotlinudemydelivery.activities.client.home.ClientHomeActivity
import com.lsepulveda.kotlinudemydelivery.activities.delivery.home.DeliveryHomeActivity
import com.lsepulveda.kotlinudemydelivery.activities.restaurant.home.RestaurantHomeActivity
import com.lsepulveda.kotlinudemydelivery.models.ResponseHttp
import com.lsepulveda.kotlinudemydelivery.models.User
import com.lsepulveda.kotlinudemydelivery.providers.UsersProvider
import com.lsepulveda.kotlinudemydelivery.utils.SharedPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    // lateinit = se va a inicializar despues
    // val = variables estaticas
    // var = variables dinamicas

    var imageViewGoToRegister : ImageView? = null //null safety. debemos validar cuaqluier nulo que se pueda presentar
    var editTextEmail: EditText? = null
    var editTextPassword: EditText? = null
    var buttonLogin: Button? = null
    var usersProvider = UsersProvider()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageViewGoToRegister = findViewById(R.id.imageview_go_to_register);

        /*
        antes en java:
        if(imageviewGoToRegister != null){
            imageviewGoToRegister?.setOnClickListener{}
        }
         */

        //ahora en kotlin
        //si la variable viene en null no va a ejecutar el metodo setOnClickListener
        editTextEmail = findViewById(R.id.edittext_email)
        editTextPassword = findViewById(R.id.edittext_password)
        buttonLogin = findViewById(R.id.btn_login)

        imageViewGoToRegister?.setOnClickListener { goToRegister() }
        buttonLogin?.setOnClickListener { login() }

        // si hay usuario en sesion, no vuelve a mostrar login
        getUserFromSession()
    }

    private fun goToRegister(){
        // para pasar de una actividad a otra
        val i = Intent(this, RegisterActivity::class.java)
        startActivity(i)
    }

    private fun login(){
        val email = editTextEmail?.text.toString() // NULL POINTER EXCEPTION
        val password = editTextPassword?.text.toString()

        if(isValidForm(email,password)){
            usersProvider.login(email, password)?.enqueue(object : Callback<ResponseHttp>{
                override fun onResponse(
                    call: Call<ResponseHttp>,
                    response: Response<ResponseHttp>
                ) {

                    Log.d("MainActivity", "Response: ${response.body()}")

                    if(response.body()?.isSuccess == true){
                        // el servidor retorno succes true
                        Toast.makeText(this@MainActivity, response.body()?.message, Toast.LENGTH_SHORT).show()
                        //para slmacenar usuario en sesion
                        saveUserInSession(response.body()?.data.toString())

                    }else{
                        Toast.makeText(this@MainActivity, "Los datos no son correctos", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Log.d("MainActivity", "Hubo un error ${t.message}")
                    Toast.makeText(this@MainActivity, "Hubo un error ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }else{
            Toast.makeText(this, "El formulario no es valido", Toast.LENGTH_SHORT).show()
        }


        // se usa $ para concatenar el valor del string, tambn se puede hacer de la otra manera
        // para objetos que no son primitivos se usa ej ${persona}
        //Toast.makeText(this, "El email es: $email", Toast.LENGTH_LONG).show()
        //Toast.makeText(this, "El password es: $password", Toast.LENGTH_LONG).show()


        //Log.d("MainActivity", "El password es: $password")
    }


    private fun goToClientHome(){
        val i = Intent(this, ClientHomeActivity::class.java)
        i.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK // eliminar historial de pantallas
        startActivity(i)
    }

    private fun goToRestaurantHome(){
        val i = Intent(this, RestaurantHomeActivity::class.java)
        i.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK // eliminar historial de pantallas
        startActivity(i)
    }

    private fun goToDeliveryHome(){
        val i = Intent(this, DeliveryHomeActivity::class.java)
        i.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK // eliminar historial de pantallas
        startActivity(i)
    }

    private fun goToSelectRol(){
        val i = Intent(this, SelectRolesActivity::class.java)
        i.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK // eliminar historial de pantallas
        startActivity(i)
    }

    private fun saveUserInSession(data: String){
        val sharedPref = SharedPref(this)
        val gson = Gson()
        // tranforma lo que trae la data en objeto usuario
        val user = gson.fromJson(data, User::class.java)
        sharedPref.save("user", user)

        if(user.roles?.size!! > 1){ // si el usuario tiene mas de un rol
            goToSelectRol()
        }else{ // el usuario solo tiene un rol. cliente
            goToClientHome()
        }
    }

    fun String.isEmailValid() : Boolean{
        // para saber si es formato valido de mail  @ .com etc
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }


    private fun isValidForm(email: String, password: String) : Boolean{
        // si campo email esta vacion, retorna formulario no valido
        if(email.isBlank()) {
            return false
        }
        if(password.isBlank()) {
            return false
        }
        // para saber si tiene formato email
        if(!email.isEmailValid()) {
            return false
        }
        return true
    }

    private fun getUserFromSession(){

        val sharedPref = SharedPref(this)
        val gson = Gson()

        // si el usuario existe en sesion
        if(!sharedPref.getData("user").isNullOrBlank()){
//            val user = gson.fromJson(sharedPref.getData("user"), User::class.java)

            if(!sharedPref.getData("rol").isNullOrBlank()){
                // SI EL USUARIO SELECCIONO EL ROL
                val rol = sharedPref.getData("rol")?.replace("\"", "")

                if(rol == "RESTAURANTE"){
                    goToRestaurantHome()
                }
                else if(rol == "CLIENTE"){
                    goToClientHome()
                }
                else if(rol == "REPARTIDOR"){
                    goToDeliveryHome()
                }
            } else {
                goToClientHome() // si no selecciona ningun rol
            }
        }
    }


}