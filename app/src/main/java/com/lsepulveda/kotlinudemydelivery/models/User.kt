package com.lsepulveda.kotlinudemydelivery.models

import com.google.gson.annotations.SerializedName

class User(
    // machea variables con campos en db, deben coincidir los nombres entre comillas
    @SerializedName("id") val id : String? = null,
    @SerializedName("name") val name : String,
    @SerializedName("lastname") val lastname : String,
    @SerializedName("email") val email : String,
    @SerializedName("phone") val phone : String,
    @SerializedName("password") val password : String,
    @SerializedName("image") val image : String? = null,
    // val puede tener otro nombre
    @SerializedName("session_token") val sessionToken : String? = null,
    // ? = null puede ir nulo, al momento de crearlo no lo especificamos
    @SerializedName("is_available") val isAvailable : Boolean? = null,
)
{
    override fun toString(): String {
        return "User(id='$id', name='$name', lastname='$lastname', email='$email', phone='$phone', password='$password', image='$image', sessionToken='$sessionToken', isAvailable=$isAvailable)"
    }
}