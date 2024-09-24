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
import com.lsepulveda.kotlinudemydelivery.activities.client.shopping_bag.ClientShoppingBagActivity
import com.lsepulveda.kotlinudemydelivery.models.Product
import com.lsepulveda.kotlinudemydelivery.utils.SharedPref


// es reutilizable, solo se tiene que especificar que tipo de lista vamos a usar
class ShoppingBagAdapter(val context: Activity, val products:ArrayList<Product>): RecyclerView.Adapter<ShoppingBagAdapter.ShoppingBagViewHolder>() {

    val sharedPref = SharedPref(context)

    init{
        (context as ClientShoppingBagActivity).setTotal(getTotal())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingBagViewHolder {
        // instancia la vista
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_shopping_bag, parent, false)
        return ShoppingBagViewHolder(view)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    // especificamos los valores
    override fun onBindViewHolder(holder: ShoppingBagViewHolder, position: Int) {
        val product = products[position] // cada uno de los productos
        holder.textviewName.text = product.name
        holder.textviewCounter.text =  "${product.quantity}"
        holder.textviewPrice.text = "${product.price * product.quantity!!}$"
        Glide.with(context).load(product.image1).into(holder.imageViewProduct)

        holder.imageViewAdd.setOnClickListener{addItem(product, holder)}
        holder.imageViewRemove.setOnClickListener{removeItem(product, holder)}
        holder.imageViewDelete.setOnClickListener{deleteItem(position)}
        // presiona todo el cardview
//        holder.itemView.setOnClickListener{ goToDetail(product) }
    }

    private fun deleteItem(position: Int){
        products.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeRemoved(position, products.size)
        sharedPref.save("order", products)
        (context as ClientShoppingBagActivity).setTotal(getTotal())
    }

    // si producto existe en shared pref, edita su cantidad (no crea uno nuevo)
    private fun getIndexOf(idProduct: String): Int{
        var pos = 0

        for (p in products){
            if(p.id == idProduct){
                return pos
            }
            pos++
        }
        return -1
    }

    private fun addItem(product: Product, holder: ShoppingBagViewHolder) {

        val index = getIndexOf(product.id!!)
        product.quantity = product.quantity!! + 1
        products[index].quantity = product.quantity

        holder.textviewCounter.text = "${product.quantity}"
        holder.textviewPrice.text =  "${product.quantity!! * product.price}"

        sharedPref.save("order", products) // guarda en "order" los productos
        (context as ClientShoppingBagActivity).setTotal(getTotal())
    }

    private fun removeItem(product: Product, holder: ShoppingBagViewHolder) {

        if(product.quantity!! > 1){ // para que elija 1 o mas productos
            val index = getIndexOf(product.id!!)
            product.quantity = product.quantity!! - 1
            products[index].quantity = product.quantity

            holder.textviewCounter.text = "${product.quantity}"
            holder.textviewPrice.text =  "${product.quantity!! * product.price}"

            sharedPref.save("order", products) // guarda en "order" los productos
            (context as ClientShoppingBagActivity).setTotal(getTotal())
        }
    }

    private fun getTotal(): Double{
        var total = 0.0
        for(p in products){
            total = total + (p.quantity!! * p.price)
        }
        return total
    }


    private fun goToDetail(product: Product){
        val i = Intent(context, ClientProductsDetailActivity::class.java) // AL TOCAR UN ITEM, LO ENVIA AL DETALLE
        i.putExtra("product", product.toJson())
        context.startActivity(i)
    }

    class ShoppingBagViewHolder(view: View): RecyclerView.ViewHolder(view){

        val textviewName : TextView
        val textviewPrice : TextView
        val textviewCounter : TextView
        val imageViewProduct : ImageView
        val imageViewAdd : ImageView
        val imageViewRemove : ImageView
        val imageViewDelete : ImageView


        init {
            textviewName = view.findViewById(R.id.textview_name)
            textviewPrice = view.findViewById(R.id.textview_price)
            imageViewProduct = view.findViewById(R.id.imageview_product)
            textviewCounter = view.findViewById(R.id.textview_counter)
            imageViewAdd = view.findViewById(R.id.imageview_add)
            imageViewRemove = view.findViewById(R.id.imageview_remove)
            imageViewDelete = view.findViewById(R.id.imageview_delete)

        }
    }
}