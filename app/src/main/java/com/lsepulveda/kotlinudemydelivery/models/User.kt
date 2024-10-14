package com.lsepulveda.kotlinudemydelivery.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

class User(
    // machea variables con campos en db, deben coincidir los nombres entre comillas
    @SerializedName("id") val id : String? = null,
    @SerializedName("name") var name : String,
    @SerializedName("lastname") var lastname : String,
    @SerializedName("email") val email : String,
    @SerializedName("phone") var phone : String,
    @SerializedName("password") val password : String,
    @SerializedName("image") var image : String? = null,
    // val puede tener otro nombre
    @SerializedName("session_token") val sessionToken : String? = null,
    // ? = null puede ir nulo, al momento de crearlo no lo especificamos
    @SerializedName("is_available") val isAvailable : Boolean? = null,
    @SerializedName("roles") val roles : ArrayList<Rol>? = null,
) {
    override fun toString(): String {
        return "$name $lastname"
    }

    fun toJson():String{
        return Gson().toJson(this)
    }
}
