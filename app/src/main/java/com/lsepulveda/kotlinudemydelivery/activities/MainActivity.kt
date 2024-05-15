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
import com.lsepulveda.kotlinudemydelivery.R

class MainActivity : AppCompatActivity() {

    // lateinit = se va a inicializar despues
    // val = variables estaticas
    // var = variables dinamicas

    var imageViewGoToRegister : ImageView? = null //null safety. debemos validar cuaqluier nulo que se pueda presentar
    var editTextEmail: EditText? = null
    var editTextPassword: EditText? = null
    var buttonLogin: Button? = null

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
            Toast.makeText(this, "El formulario es valido", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "El formulario no es valido", Toast.LENGTH_SHORT).show()
        }


        // se usa $ para concatenar el valor del string, tambn se puede hacer de la otra manera
        // para objetos que no son primitivos se usa ej ${persona}
        //Toast.makeText(this, "El email es: $email", Toast.LENGTH_LONG).show()
        //Toast.makeText(this, "El password es: $password", Toast.LENGTH_LONG).show()

        Log.d("MainActivity", "El email es: $email")
        Log.d("MainActivity", "El password es: $password")
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


}