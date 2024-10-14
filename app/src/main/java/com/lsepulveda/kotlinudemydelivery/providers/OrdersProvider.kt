package com.lsepulveda.kotlinudemydelivery.providers

import com.lsepulveda.kotlinudemydelivery.api.ApiRoutes
import com.lsepulveda.kotlinudemydelivery.models.Address
import com.lsepulveda.kotlinudemydelivery.models.Category
import com.lsepulveda.kotlinudemydelivery.models.Order
import com.lsepulveda.kotlinudemydelivery.models.ResponseHttp
import com.lsepulveda.kotlinudemydelivery.models.User
import com.lsepulveda.kotlinudemydelivery.routes.AddressRoutes
import com.lsepulveda.kotlinudemydelivery.routes.CategoriesRoutes
import com.lsepulveda.kotlinudemydelivery.routes.OrdersRoutes
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import java.io.File

// METODOS PARA RECIBIR LOS DATOS
class OrdersProvider(val token: String) {
    private var ordersRoutes: OrdersRoutes? = null

    init {
        val api = ApiRoutes()
        ordersRoutes = api.getOrdersRoutes(token)
    }

    fun getOrdersByStatus(status: String): Call<ArrayList<Order>>?{
        return ordersRoutes?.getOrdersByStatus(status, token)
    }

    fun getOrdersByClientAndStatus(idClient: String, status: String): Call<ArrayList<Order>>?{
        return ordersRoutes?.getOrdersByClientAndStatus(idClient, status, token)
    }

    fun getOrdersByDeliveryAndStatus(idDelivery: String, status: String): Call<ArrayList<Order>>?{
        return ordersRoutes?.getOrdersByDeliveryAndStatus(idDelivery, status, token)
    }


    fun create( order: Order) : Call<ResponseHttp>?{
        return ordersRoutes?.create(order, token)
    }

    fun updateToDispatched( order: Order) : Call<ResponseHttp>?{
        return ordersRoutes?.updateToDispatched(order, token)
    }

    fun updateToOnTheWay( order: Order) : Call<ResponseHttp>?{
        return ordersRoutes?.updateToOnTheWay(order, token)
    }
}