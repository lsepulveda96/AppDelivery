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
import com.lsepulveda.kotlinudemydelivery.activities.client.products.list.ClientProductListActivity
import com.lsepulveda.kotlinudemydelivery.activities.delivery.home.DeliveryHomeActivity
import com.lsepulveda.kotlinudemydelivery.activities.restaurant.home.RestaurantHomeActivity
import com.lsepulveda.kotlinudemydelivery.models.Category
import com.lsepulveda.kotlinudemydelivery.models.Rol
import com.lsepulveda.kotlinudemydelivery.utils.SharedPref

// es reutilizable, solo se tiene que especificar que tipo de lista vamos a usar
class CategoriesAdapter(val context: Activity, val categories:ArrayList<Category>): RecyclerView.Adapter<CategoriesAdapter.CategoriesViewHolder>() {

    val sharedPref = SharedPref(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        // instancia la vista
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_categories, parent, false)
        return CategoriesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    // especificamos los valores
    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        val category = categories[position] // cada una de las categorias
        holder.textviewCategory.text = category.name
        Glide.with(context).load(category.image).into(holder.imageViewCategory)

        // presiona todo el cardview
        holder.itemView.setOnClickListener{ goToProducts(category) }
    }

    private fun goToProducts(category: Category){
        val i = Intent(context, ClientProductListActivity::class.java)
        i.putExtra("idCategory", category.id)
        context.startActivity(i)
    }

    class CategoriesViewHolder(view: View): RecyclerView.ViewHolder(view){

        val textviewCategory : TextView
        val imageViewCategory : ImageView

        init {
            textviewCategory = view.findViewById(R.id.textview_category)
            imageViewCategory = view.findViewById(R.id.imageview_category)
        }
    }
}