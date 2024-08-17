package com.lsepulveda.kotlinudemydelivery.api

import com.lsepulveda.kotlinudemydelivery.routes.UsersRoutes

// clase para invocar a las rutas
class ApiRoutes {
    // ojo si cambia ip
    val API_URL = "http://192.168.0.107:3000/api/"
    val retrofit = RetrofitClient()

    fun getUsersRoutes(): UsersRoutes{
        return retrofit.getClient(API_URL).create(UsersRoutes::class.java)
    }

    fun getUsersRoutesWithToken(token: String): UsersRoutes{
        return retrofit.getClientWithToken(API_URL, token).create(UsersRoutes::class.java)
    }
}