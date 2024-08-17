package com.lsepulveda.kotlinudemydelivery.routes

import com.lsepulveda.kotlinudemydelivery.models.ResponseHttp
import com.lsepulveda.kotlinudemydelivery.models.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.HEAD
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

interface UsersRoutes {
    @POST("users/create")
    // call <retorno que devuelve servidor> ej succes, message, data
    fun register(@Body user: User) : Call<ResponseHttp>

    // iguales que en el backend
    @FormUrlEncoded
    @POST("users/login")
    fun login(@Field("email") email : String, @Field("password") password: String) : Call<ResponseHttp>

    @Multipart
    @PUT("users/update")
    fun update(
        @Part image: MultipartBody.Part,
        @Part("user") user: RequestBody,
        @Header("Authorization") token: String
        ): Call<ResponseHttp>

    @PUT("users/updateWithoutImage")
    fun updateWithoutImage(
        @Body user: User,
        @Header("Authorization") token: String
    ): Call<ResponseHttp>
}