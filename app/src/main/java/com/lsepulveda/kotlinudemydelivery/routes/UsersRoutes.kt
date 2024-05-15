package com.lsepulveda.kotlinudemydelivery.routes

import com.lsepulveda.kotlinudemydelivery.models.ResponseHttp
import com.lsepulveda.kotlinudemydelivery.models.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UsersRoutes {
    @POST("users/create")
    // call <retorno que devuelve servidor> ej succes, message, data
    fun register(@Body user: User) : Call<ResponseHttp>
}