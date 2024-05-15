package com.lsepulveda.kotlinudemydelivery.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// clase para crear cliente
class RetrofitClient {
    fun getClient(url: String) :Retrofit{
        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}