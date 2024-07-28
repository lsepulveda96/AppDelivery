package com.lsepulveda.kotlinudemydelivery.adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lsepulveda.kotlinudemydelivery.R
import com.lsepulveda.kotlinudemydelivery.activities.client.home.ClientHomeActivity
import com.lsepulveda.kotlinudemydelivery.activities.delivery.home.DeliveryHomeActivity
import com.lsepulveda.kotlinudemydelivery.activities.restaurant.home.RestaurantHomeActivity
import com.lsepulveda.kotlinudemydelivery.models.Rol
import com.lsepulveda.kotlinudemydelivery.utils.SharedPref

class RolesAdapter(val context: Activity, val roles:ArrayList<Rol>): RecyclerView.Adapter<RolesAdapter.RolesViewHolder>() {

    val sharedPref = SharedPref(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RolesViewHolder {
        // instancia la vista
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_roles, parent, false)
        return RolesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return roles.size
    }

    override fun onBindViewHolder(holder: RolesViewHolder, position: Int) {
        val rol = roles[position] // cada rol
        holder.textviewRol.text = rol.name
        Glide.with(context).load(rol.image).into(holder.imageViewRol)

        // presiona todo el cardview
        holder.itemView.setOnClickListener{ goToRol(rol) }
    }

    private fun goToRol(rol: Rol){
        if(rol.name == "RESTAURANTE"){ // NOMBRE DESDE DB
            sharedPref.save("rol", "RESTAURANTE") // ALMACENA EN SESION EL ROL Q SELECCIONO
            val i = Intent(context, RestaurantHomeActivity::class.java)
            context.startActivity(i)
        }
        else if(rol.name == "CLIENTE"){
            sharedPref.save("rol", "CLIENTE") // ALMACENA EN SESION EL ROL Q SELECCIONO
            val i = Intent(context, ClientHomeActivity::class.java)
            context.startActivity(i)
        }
        else if(rol.name == "REPARTIDOR"){
            sharedPref.save("rol", "REPARTIDOR") // ALMACENA EN SESION EL ROL Q SELECCIONO
            val i = Intent(context, DeliveryHomeActivity::class.java)
            context.startActivity(i)
        }
    }

    class RolesViewHolder(view: View): RecyclerView.ViewHolder(view){

        val textviewRol : TextView
        val imageViewRol : ImageView

        init {
            textviewRol = view.findViewById(R.id.textview_rol)
            imageViewRol = view.findViewById(R.id.imageview_rol)
        }
    }
}