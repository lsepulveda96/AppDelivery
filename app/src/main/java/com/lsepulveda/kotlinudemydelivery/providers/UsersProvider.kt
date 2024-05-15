package com.lsepulveda.kotlinudemydelivery.providers

import com.lsepulveda.kotlinudemydelivery.api.ApiRoutes
import com.lsepulveda.kotlinudemydelivery.models.ResponseHttp
import com.lsepulveda.kotlinudemydelivery.models.User
import com.lsepulveda.kotlinudemydelivery.routes.UsersRoutes
import retrofit2.Call

// utiliza rutas para lanzar peticion
class UsersProvider {
    private var usersRoutes: UsersRoutes? = null

    // constructor de la clase. primero que se ejecuta al instanciar la clase
    init{
        val api = ApiRoutes()
        usersRoutes = api.getUsersRoutes()
    }

    // "?" metodo puede retornar o no un null
    fun register(user: User) : Call<ResponseHttp>? {
        return usersRoutes?.register(user)
    }
}