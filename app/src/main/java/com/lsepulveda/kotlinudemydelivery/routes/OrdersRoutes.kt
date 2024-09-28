package com.lsepulveda.kotlinudemydelivery.routes

import com.lsepulveda.kotlinudemydelivery.models.Address
import com.lsepulveda.kotlinudemydelivery.models.Category
import com.lsepulveda.kotlinudemydelivery.models.Order
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

// para obtener info de la base de datos.
// rutas de nodejs
interface OrdersRoutes {

    @GET("orders/findByStatus/{status}")
        fun getOrdersByStatus(
            @Path("status") idUser: String,
            @Header("Authorization") token: String
        ): Call<ArrayList<Order>>

    // @Multipart // multipart es para enviar archivos
    @POST("orders/create")
    fun create(
        @Body orders: Order,
        @Header("Authorization") token: String
        ): Call<ResponseHttp>


}