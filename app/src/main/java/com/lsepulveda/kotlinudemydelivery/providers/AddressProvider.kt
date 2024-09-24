package com.lsepulveda.kotlinudemydelivery.providers

import com.lsepulveda.kotlinudemydelivery.api.ApiRoutes
import com.lsepulveda.kotlinudemydelivery.models.Address
import com.lsepulveda.kotlinudemydelivery.models.Category
import com.lsepulveda.kotlinudemydelivery.models.ResponseHttp
import com.lsepulveda.kotlinudemydelivery.models.User
import com.lsepulveda.kotlinudemydelivery.routes.AddressRoutes
import com.lsepulveda.kotlinudemydelivery.routes.CategoriesRoutes
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import java.io.File

// METODOS PARA RECIBIR LOS DATOS
class AddressProvider(val token: String) {
    private var addressRoutes: AddressRoutes? = null

    init {
        val api = ApiRoutes()
        addressRoutes = api.getAddressRoutes(token)
    }

    fun getAddress(idUser: String): Call<ArrayList<Address>>?{
        return addressRoutes?.getAddress(idUser, token)
    }

    fun create( address: Address) : Call<ResponseHttp>?{
        return addressRoutes?.create(address, token)
    }
}