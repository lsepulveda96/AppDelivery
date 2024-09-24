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
import com.google.gson.Gson
import com.lsepulveda.kotlinudemydelivery.R
import com.lsepulveda.kotlinudemydelivery.activities.client.adress.list.ClientAddressListActivity
import com.lsepulveda.kotlinudemydelivery.activities.client.home.ClientHomeActivity
import com.lsepulveda.kotlinudemydelivery.activities.client.products.list.ClientProductListActivity
import com.lsepulveda.kotlinudemydelivery.activities.delivery.home.DeliveryHomeActivity
import com.lsepulveda.kotlinudemydelivery.activities.restaurant.home.RestaurantHomeActivity
import com.lsepulveda.kotlinudemydelivery.models.Address
import com.lsepulveda.kotlinudemydelivery.models.Category
import com.lsepulveda.kotlinudemydelivery.models.Rol
import com.lsepulveda.kotlinudemydelivery.utils.SharedPref

// es reutilizable, solo se tiene que especificar que tipo de lista vamos a usar
class AddressAdapter(val context: Activity, val address:ArrayList<Address>): RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    val sharedPref = SharedPref(context)
    val gson = Gson()
    var prev = 0
    var positionAddressSession = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        // instancia la vista
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_address, parent, false)
        return AddressViewHolder(view)
    }

    override fun getItemCount(): Int {
        return address.size
    }

    // especificamos los valores
    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val a = address[position] // cada una de las direcciones

        if(!sharedPref.getData("address").isNullOrBlank()){ // si el usuario ya eligio una dir de la lista
            val adr = gson.fromJson(sharedPref.getData("address"), Address::class.java)

            if(adr.id == a.id){
                positionAddressSession = position
                holder.imageViewCheck.visibility = View.VISIBLE // si existe, muestra en esa posicion el check
            }
        }

        holder.textviewAddress.text = a.address
        holder.textviewNeighborhood.text = a.neighborhood

        holder.itemView.setOnClickListener{
            (context as ClientAddressListActivity).resetValue(prev)
            (context as ClientAddressListActivity).resetValue(positionAddressSession)
            prev = position
            holder.imageViewCheck.visibility = View.VISIBLE // muestra el check de cada direccion a seleccionar
            saveAddress(a.toJson())
        }
    }

    private fun saveAddress(data: String){
        val ad = gson.fromJson(data, Address::class.java)
        sharedPref.save("address", ad)
    }

    class AddressViewHolder(view: View): RecyclerView.ViewHolder(view){

        val textviewAddress : TextView
        val textviewNeighborhood : TextView
        val imageViewCheck : ImageView

        init {
            textviewAddress = view.findViewById(R.id.textview_address)
            textviewNeighborhood = view.findViewById(R.id.textview_neighborhood)
            imageViewCheck = view.findViewById(R.id.imageview_check)
        }
    }
}