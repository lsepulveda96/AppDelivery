package com.lsepulveda.kotlinudemydelivery.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.lsepulveda.kotlinudemydelivery.R
import com.lsepulveda.kotlinudemydelivery.activities.client.home.ClientHomeActivity
import com.lsepulveda.kotlinudemydelivery.models.ResponseHttp
import com.lsepulveda.kotlinudemydelivery.models.User
import com.lsepulveda.kotlinudemydelivery.providers.UsersProvider
import com.lsepulveda.kotlinudemydelivery.utils.SharedPref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    // para pasarseslo al log, y mostrar mesajes. para verlo en el logcat
    val TAG = "RegisterActivity"

    // ninguna variable puede ser nula
    var imageViewGoToLogin: ImageView? = null
    var editTextName: EditText? = null
    var editTextLastname: EditText? = null
    var editTextEmail: EditText? = null
    var editTextPhone: EditText? = null
    var editTextPassword: EditText? = null
    var editTextConfirmPassword: EditText? = null
    var buttonRegister: Button? = null

    // para utilizar los metodos de la clase UserProvider
    var usersProvider = UsersProvider()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        editTextName = findViewById(R.id.edittext_name)
        editTextLastname = findViewById(R.id.edittext_lastname)
        editTextEmail = findViewById(R.id.edittext_email)
        editTextPhone = findViewById(R.id.edittext_phone)
        editTextConfirmPassword = findViewById(R.id.edittext_confirm_password)
        editTextPassword = findViewById(R.id.edittext_password)
        buttonRegister = findViewById(R.id.btn_register)
        imageViewGoToLogin = findViewById(R.id.imageview_go_to_login)


        imageViewGoToLogin?.setOnClickListener { goToLogin() }
        buttonRegister?.setOnClickListener { register() }
    }

    private fun register() {
        val name = editTextName?.text.toString()
        val lastname = editTextLastname?.text.toString()
        val email = editTextEmail?.text.toString()
        val phone = editTextPhone?.text.toString()
        val password = editTextPassword?.text.toString()
        val confirmPassword = editTextConfirmPassword?.text.toString()

        if (isValidForm(name = name, phone = phone, lastname = lastname, email = email, password = password, confirmPassword = confirmPassword)) {

            val user = User(
                name = name,
                lastname = lastname,
                email = email,
                phone = phone,
                password = password
            )

            // ? si registrer devuelve nulo no se ejecuta el enqueue
            usersProvider.register(user)?.enqueue(object : Callback<ResponseHttp>{
                // metodos requeridos override, onResponse en caso de que el servidor retorne algo
                override fun onResponse(
                    call: Call<ResponseHttp>,
                    response: Response<ResponseHttp>
                ) {

                    if(response.body()?.isSuccess == true){
                        saveUserInSession(response.body()?.data.toString())
                        Toast.makeText(this@RegisterActivity, response.body()?.message, Toast.LENGTH_SHORT).show()
                        goToClientHome()
                    }
                    // inicio codigo para capturar error mail numbero repetido
                    else if (response.code() == 409) {
                        // Manejar error de duplicación
                        val errorMessage = response.errorBody()?.string() // Extraer mensaje de error del servidor
                        if (errorMessage?.contains("El email ya está registrado") == true) {
                            findViewById<TextInputLayout>(R.id.emailLayout).error = "Este email ya está registrado"
                        } else if (errorMessage?.contains("El número de teléfono ya está registrado") == true) {
                            findViewById<TextInputLayout>(R.id.phoneLayout).error = "Este número ya está registrado"
                        }
                    } else {
                        // Otro tipo de error
                        Toast.makeText(this@RegisterActivity, "Error al registrar. Inténtelo de nuevo", Toast.LENGTH_LONG).show()
                    }
                    // fin codigo para capturar error mail numbero repetido


                    // imprime el mensaje que retorno el servidor en message
                    //Toast.makeText(this@RegisterActivity, response.body()?.message, Toast.LENGTH_SHORT).show()

                    Log.d(TAG, "Response: ${response}")
                    Log.d(TAG, "Response: ${response.body()}")
                }

                // onFailure en caso de que retorne error
                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Log.d(TAG, "Se produjo un error ${t.message}")
                    Toast.makeText(this@RegisterActivity, "Se produjo un error ${t.message}", Toast.LENGTH_SHORT).show()
                }

            })
        }
    }

    private fun saveUserInSession(data: String){
        val sharedPref = SharedPref(this)
        val gson = Gson()
        // tranforma lo que trae la data en objeto usuario
        val user = gson.fromJson(data, User::class.java)
        sharedPref.save("user", user)
    }

    private fun goToClientHome(){
        val i = Intent(this, SaveImageActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // eliminar historial de pantallas
        startActivity(i)
    }

    fun String.isEmailValid() : Boolean{
        // para saber si es formato valido de mail  @ .com etc
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }


    private fun isValidForm(
        name: String,
        lastname: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String
    ): Boolean {

        var isValid = true

        // Limpiar errores previos
        findViewById<TextInputLayout>(R.id.nameLayout).error = null
        findViewById<TextInputLayout>(R.id.lastnameLayout).error = null
        findViewById<TextInputLayout>(R.id.emailLayout).error = null
        findViewById<TextInputLayout>(R.id.phoneLayout).error = null
        findViewById<TextInputLayout>(R.id.passwordLayout).error = null
        findViewById<TextInputLayout>(R.id.confirm_passwordLayout).error = null

        if (name.isBlank()) {
            findViewById<TextInputLayout>(R.id.nameLayout).error = "Debes ingresar el nombre"
            isValid = false
        }

        if (lastname.isBlank()) {
            findViewById<TextInputLayout>(R.id.lastnameLayout).error = "Debes ingresar el apellido"
            isValid = false
        }

        if (phone.isBlank()) {
            findViewById<TextInputLayout>(R.id.phoneLayout).error = "Debes ingresar el teléfono"
            isValid = false
        }

        if (email.isBlank()) {
            findViewById<TextInputLayout>(R.id.emailLayout).error = "Debes ingresar el email"
            isValid = false
        }

        if (!email.isEmailValid()) {
            findViewById<TextInputLayout>(R.id.emailLayout).error = "El email no es válido"
            isValid = false
        }

        if (password.isBlank()) {
            findViewById<TextInputLayout>(R.id.passwordLayout).error = "Debes ingresar la contraseña"
            isValid = false
        }

        if (confirmPassword.isBlank()) {
            findViewById<TextInputLayout>(R.id.confirm_passwordLayout).error = "Debes ingresar la confirmación de contraseña"
            isValid = false
        }

        if (password != confirmPassword) {
            findViewById<TextInputLayout>(R.id.confirm_passwordLayout).error = "Las contraseñas no coinciden"
            isValid = false
        }

        return isValid

        /* if (name.isBlank()) {
             Toast.makeText(this, "Debes ingresar el nombre", Toast.LENGTH_SHORT).show()
             return false
         }

         if (lastname.isBlank()) {
             Toast.makeText(this, "Debes ingresar el apellido", Toast.LENGTH_SHORT).show()
             return false
         }

         if (phone.isBlank()) {
             Toast.makeText(this, "Debes ingresar el telefono", Toast.LENGTH_SHORT).show()
             return false
         }

         if (email.isBlank()) {
             Toast.makeText(this, "Debes ingresar el email", Toast.LENGTH_SHORT).show()
             return false
         }

         if (password.isBlank()) {
             Toast.makeText(this, "Debes ingresar el contraseña", Toast.LENGTH_SHORT).show()
             return false
         }

         if (confirmPassword.isBlank()) {
             Toast.makeText(this, "Debes ingresar el la confirmacion de contraseña", Toast.LENGTH_SHORT).show()
             return false
         }

         if (!email.isEmailValid()) {
             Toast.makeText(this, "El email no es valido", Toast.LENGTH_SHORT).show()
             return false
         }

         if (password != confirmPassword) {
             Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
             return false
         }

         return true*/
    }


    private fun goToLogin(){
        // antes en java
        // val i = new Intent()
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }
}