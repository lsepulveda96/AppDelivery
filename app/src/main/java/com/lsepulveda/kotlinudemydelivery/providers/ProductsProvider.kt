package com.lsepulveda.kotlinudemydelivery.providers

import com.lsepulveda.kotlinudemydelivery.api.ApiRoutes
import com.lsepulveda.kotlinudemydelivery.models.Product
import com.lsepulveda.kotlinudemydelivery.models.ResponseHttp
import com.lsepulveda.kotlinudemydelivery.routes.ProductsRoutes
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import java.io.File

//import com.lsepulveda.kotlinudemydelivery.api.ApiRoutes
//import com.lsepulveda.kotlinudemydelivery.models.Product
//import com.lsepulveda.kotlinudemydelivery.models.ResponseHttp
//import com.lsepulveda.kotlinudemydelivery.models.User
//import com.lsepulveda.kotlinudemydelivery.routes.ProductsRoutes
//import okhttp3.MediaType
//import okhttp3.MultipartBody
//import okhttp3.RequestBody
//import retrofit2.Call
//import java.io.File

// METODOS PARA RECIBIR LOS DATOS
class ProductsProvider(val token: String) {
    private var productsRoutes: ProductsRoutes? = null

    init {
        val api = ApiRoutes()
        productsRoutes = api.getProductsRoutes(token)
    }

    fun findByCategory(idCategory: String): Call<ArrayList<Product>>?{
        return productsRoutes?.findByCategory(idCategory, token)
    }

    fun create(files: List<File>, product: Product) : Call<ResponseHttp>?{

        val images = arrayOfNulls<MultipartBody.Part>(files.size)

        for (i in 0 until files.size){
            val reqFile = RequestBody.create(MediaType.parse("image/*"), files[i])
            images[i] = MultipartBody.Part.createFormData("image", files[i].name, reqFile)
        }

        val requestBody = RequestBody.create(MediaType.parse("text/plain"), product.toJson()) // datos del usuario
        return productsRoutes?.create(images, requestBody, token)
    }
}