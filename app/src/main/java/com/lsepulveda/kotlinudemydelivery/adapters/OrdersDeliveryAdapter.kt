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
import com.lsepulveda.kotlinudemydelivery.activities.client.orders.detail.ClientOrdersDetailActivity
import com.lsepulveda.kotlinudemydelivery.activities.client.products.list.ClientProductListActivity
import com.lsepulveda.kotlinudemydelivery.activities.delivery.home.DeliveryHomeActivity
import com.lsepulveda.kotlinudemydelivery.activities.delivery.home.orders.detail.DeliveryOrdersDetailActivity
import com.lsepulveda.kotlinudemydelivery.activities.restaurant.home.RestaurantHomeActivity
import com.lsepulveda.kotlinudemydelivery.activities.restaurant.orders.detail.RestaurantOrdersDetailActivity
import com.lsepulveda.kotlinudemydelivery.models.Address
import com.lsepulveda.kotlinudemydelivery.models.Category
import com.lsepulveda.kotlinudemydelivery.models.Order
import com.lsepulveda.kotlinudemydelivery.models.Rol
import com.lsepulveda.kotlinudemydelivery.utils.SharedPref

// es reutilizable, solo se tiene que especificar que tipo de lista vamos a usar
class OrdersDeliveryAdapter(val context: Activity, val orders:ArrayList<Order>): RecyclerView.Adapter<OrdersDeliveryAdapter.OrdersViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        // instancia la vista
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_orders_restaurant, parent, false)
        return OrdersViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orders.size
    }


    // especificamos los valores
    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
        val order = orders[position] // cada una de las ordenes

        holder.textviewOrderId.text = "Orden #${order.id}"
        holder.textviewDate.text = "${order.timestamp}"
        holder.textviewAddress.text = "${order.address?.address}"
        holder.textviewClient.text = "${order.client?.name} ${order?.client?.lastname}"

        holder.itemView.setOnClickListener{ goToOrderDetail(order) }
    }

    private fun goToOrderDetail(order: Order){
        val i = Intent(context, DeliveryOrdersDetailActivity::class.java)
        i.putExtra("order", order.toJson())
        context.startActivity(i)
    }

    class OrdersViewHolder(view: View): RecyclerView.ViewHolder(view){

        val textviewOrderId : TextView
        val textviewDate : TextView
        val textviewAddress : TextView
        val textviewClient : TextView

        init {
            textviewOrderId = view.findViewById(R.id.textview_order_id)
            textviewDate = view.findViewById(R.id.textview_date)
            textviewAddress = view.findViewById(R.id.textview_address)
            textviewClient = view.findViewById(R.id.textview_client)
        }
    }
}