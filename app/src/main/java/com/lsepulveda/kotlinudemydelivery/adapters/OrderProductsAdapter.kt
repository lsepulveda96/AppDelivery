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
import com.lsepulveda.kotlinudemydelivery.activities.client.products.detail.ClientProductsDetailActivity
import com.lsepulveda.kotlinudemydelivery.activities.client.shopping_bag.ClientShoppingBagActivity
import com.lsepulveda.kotlinudemydelivery.models.Product
import com.lsepulveda.kotlinudemydelivery.utils.SharedPref


// es reutilizable, solo se tiene que especificar que tipo de lista vamos a usar
class OrderProductsAdapter(val context: Activity, val products:ArrayList<Product>): RecyclerView.Adapter<OrderProductsAdapter.OrderProductsViewHolder>() {

    val sharedPref = SharedPref(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderProductsViewHolder {
        // instancia la vista
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_order_products, parent, false)
        return OrderProductsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    // especificamos los valores
    override fun onBindViewHolder(holder: OrderProductsViewHolder, position: Int) {
        val product = products[position] // cada uno de los productos

        holder.textviewName.text = product.name


        if(product.quantity != null){
            holder.textviewQuantity.text = "${product.quantity!!}"
        }
        Glide.with(context).load(product.image1).into(holder.imageViewProduct)

    }


    class OrderProductsViewHolder(view: View): RecyclerView.ViewHolder(view){

        val imageViewProduct : ImageView
        val textviewName : TextView
        val textviewQuantity : TextView

        init {
            imageViewProduct = view.findViewById(R.id.imageview_product)
            textviewName = view.findViewById(R.id.textview_name)
            textviewQuantity = view.findViewById(R.id.textview_quantity)
        }
    }
}