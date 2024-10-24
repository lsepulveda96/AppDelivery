package com.lsepulveda.kotlinudemydelivery.providers

import android.app.Activity
import android.provider.MediaStore.Audio.Media
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.lsepulveda.kotlinudemydelivery.api.ApiRoutes
import com.lsepulveda.kotlinudemydelivery.models.Category
import com.lsepulveda.kotlinudemydelivery.models.ResponseHttp
import com.lsepulveda.kotlinudemydelivery.models.User
import com.lsepulveda.kotlinudemydelivery.routes.UsersRoutes
import com.lsepulveda.kotlinudemydelivery.utils.SharedPref
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

// utiliza rutas para lanzar peticion
class UsersProvider(val token: String? = null) {

    val TAG = "UsersProvider"
    private var usersRoutes: UsersRoutes? = null
    private var usersRoutesToken: UsersRoutes? = null

    // constructor de la clase. primero que se ejecuta al instanciar la clase
    init{
        val api = ApiRoutes()
        usersRoutes = api.getUsersRoutes()

        if(token != null){
            usersRoutesToken = api.getUsersRoutesWithToken(token!!)
        }

    }

    // token para enviar notificaciones de dispositivo a dispositivo, sino el sistema de notificaciones envia la misma notificacion indiferentemente a todos los usuarios
    fun createToken(user: User, context: Activity){

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            val sharedPref = SharedPref(context)
            user.notificationToken = token
            sharedPref.save("user", user)

            // enviando notificacion a nodejs
            // sin el enqueue no anda
            updateNotificationToken(user)?.enqueue(object : Callback<ResponseHttp>{
                override fun onResponse(
                    call: Call<ResponseHttp>,
                    response: Response<ResponseHttp>
                ) {
                    if(response.body() == null){
                        Log.d(TAG, "Hubo un error al crear el token de notificacion")
                    }
                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Toast.makeText(context, "Error ${t.message}", Toast.LENGTH_SHORT).show()
                }

            })

            Log.d(TAG, "TOKEN DE NOTIFICACIONES: $token" )
        })
    }


    fun getDeliveryMan(): Call<ArrayList<User>>?{
        return usersRoutesToken?.getDeliveryMan(token!!)
    }

    // "?" metodo puede retornar o no un null
    fun register(user: User) : Call<ResponseHttp>? {
        return usersRoutes?.register(user)
    }

    fun login(email: String, password: String) : Call<ResponseHttp>? {
        return usersRoutes?.login(email, password)
    }

    fun updateWhithoutImage(user: User) : Call<ResponseHttp>? {
        return usersRoutesToken?.updateWithoutImage(user, token!!)
    }

    fun updateNotificationToken(user: User) : Call<ResponseHttp>? {
        return usersRoutesToken?.updateNotificationToken(user, token!!)
    }

    fun update(file: File, user: User) : Call<ResponseHttp>?{
        val reqFile = RequestBody.create(MediaType.parse("image/*"), file)
        val body = MultipartBody.Part.createFormData("image", file.name, reqFile)
        val requestBody = RequestBody.create(MediaType.parse("text/plain"), user.toJson()) // datos del usuario

        return usersRoutesToken?.update(body, requestBody, token!!)
    }
}