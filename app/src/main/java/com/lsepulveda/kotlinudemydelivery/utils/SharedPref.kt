package com.lsepulveda.kotlinudemydelivery.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import java.lang.Exception

class SharedPref(activity: Activity) {

    private var prefs: SharedPreferences? = null

    init {
        prefs = activity.getSharedPreferences("com.lsepulveda.kotlinudemydelivery", Context.MODE_PRIVATE)
    }

    // metodo para almacenar info en sesion del dispositivo
    // llave, valor
    fun save(key: String, objeto: Any) {

        try {

            val gson = Gson()
            val json = gson.toJson(objeto)
            with(prefs?.edit()) {
                this?.putString(key, json)
                this?.commit()
            }

        } catch (e: Exception) {
            Log.d("ERROR", "Err: ${e.message}")
        }

    }

    // metodo para obtener info. antes solo la guardabamos
    // key puede ser id, name, email, etc
    fun getData(key: String): String? {
        val data = prefs?.getString(key, "")
        return data
    }

    // eliminar info de sharedPref, cerrar sesion
    fun remove(key: String){
        prefs?.edit()?.remove(key)?.apply()
    }

}