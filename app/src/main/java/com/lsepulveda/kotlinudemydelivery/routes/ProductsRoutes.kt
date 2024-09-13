package com.lsepulveda.kotlinudemydelivery.routes

import com.lsepulveda.kotlinudemydelivery.models.Category
import com.lsepulveda.kotlinudemydelivery.models.Product
import com.lsepulveda.kotlinudemydelivery.models.ResponseHttp
import com.lsepulveda.kotlinudemydelivery.models.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HEAD
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

//import com.lsepulveda.kotlinudemydelivery.models.Category
//import com.lsepulveda.kotlinudemydelivery.models.Product
//import com.lsepulveda.kotlinudemydelivery.models.ResponseHttp
//import com.lsepulveda.kotlinudemydelivery.models.User
//import okhttp3.MultipartBody
//import okhttp3.RequestBody
//import retrofit2.Call
//import retrofit2.http.Body
//import retrofit2.http.Field
//import retrofit2.http.FormUrlEncoded
//import retrofit2.http.GET
//import retrofit2.http.HEAD
//import retrofit2.http.Header
//import retrofit2.http.Multipart
//import retrofit2.http.POST
//import retrofit2.http.PUT
//import retrofit2.http.Part
//import retrofit2.http.Path

// para obtener info de la base de datos.
// rutas de nodejs
interface ProductsRoutes {


    @GET("products/findByCategory/{id_category}")
        fun findByCategory(
        @Path("id_category") idCategory: String,
        @Header("Authorization") token: String
        ): Call<ArrayList<Product>>


    @Multipart
    @POST("products/create")
    fun create(
        @Part images: Array<MultipartBody.Part?>,
        @Part("product") product: RequestBody,
        @Header("Authorization") token: String
        ): Call<ResponseHttp>


}