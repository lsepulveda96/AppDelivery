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
import com.lsepulveda.kotlinudemydelivery.activities.client.products.detail.ClientProductsDetailActivity
import com.lsepulveda.kotlinudemydelivery.activities.client.products.list.ClientProductListActivity
import com.lsepulveda.kotlinudemydelivery.models.Product
import com.lsepulveda.kotlinudemydelivery.utils.SharedPref


// es reutilizable, solo se tiene que especificar que tipo de lista vamos a usar
class ProductsAdapter(val context: Activity, val products:ArrayList<Product>): RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder>() {

    val sharedPref = SharedPref(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        // instancia la vista
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_product, parent, false)
        return ProductsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    // especificamos los valores
    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        val product = products[position] // cada uno de los productos
        holder.textviewName.text = product.name
        holder.textviewPrice.text = "${product.price}$"
        Glide.with(context).load(product.image1).into(holder.imageViewProduct)

        // presiona todo el cardview
        holder.itemView.setOnClickListener{ goToDetail(product) }
    }

    private fun goToDetail(product: Product){
        val i = Intent(context, ClientProductsDetailActivity::class.java) // AL TOCAR UN ITEM, LO ENVIA AL DETALLE
        i.putExtra("product", product.toJson())
        context.startActivity(i)
    }

    class ProductsViewHolder(view: View): RecyclerView.ViewHolder(view){

        val textviewName : TextView
        val textviewPrice : TextView
        val imageViewProduct : ImageView

        init {
            textviewName = view.findViewById(R.id.textview_name)
            textviewPrice = view.findViewById(R.id.textview_price)
            imageViewProduct = view.findViewById(R.id.imageview_product)

        }
    }
}