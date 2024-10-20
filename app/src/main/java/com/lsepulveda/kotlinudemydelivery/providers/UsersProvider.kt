package com.lsepulveda.kotlinudemydelivery.providers

import android.provider.MediaStore.Audio.Media
import com.lsepulveda.kotlinudemydelivery.api.ApiRoutes
import com.lsepulveda.kotlinudemydelivery.models.Category
import com.lsepulveda.kotlinudemydelivery.models.ResponseHttp
import com.lsepulveda.kotlinudemydelivery.models.User
import com.lsepulveda.kotlinudemydelivery.routes.UsersRoutes
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import retrofit2.Call
import java.io.File

// utiliza rutas para lanzar peticion
class UsersProvider(val token: String? = null) {
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

    fun update(file: File, user: User) : Call<ResponseHttp>?{
        val reqFile = RequestBody.create(MediaType.parse("image/*"), file)
        val body = MultipartBody.Part.createFormData("image", file.name, reqFile)
        val requestBody = RequestBody.create(MediaType.parse("text/plain"), user.toJson()) // datos del usuario

        return usersRoutesToken?.update(body, requestBody, token!!)
    }
}