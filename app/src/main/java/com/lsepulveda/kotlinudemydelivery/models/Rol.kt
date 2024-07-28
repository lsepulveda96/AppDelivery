package com.lsepulveda.kotlinudemydelivery.models

import com.google.gson.annotations.SerializedName

class Rol (
    @SerializedName("id") val id : String? = null,
    @SerializedName("name") val name : String,
    @SerializedName("image") val image : String? = null,
    @SerializedName("route") val route : String? = null
) {
    override fun toString(): String {
        return "Rol(id=$id, name='$name', image=$image, route=$route)"
    }
}



