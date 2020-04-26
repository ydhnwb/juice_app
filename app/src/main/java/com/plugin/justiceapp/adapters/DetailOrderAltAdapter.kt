package com.plugin.justiceapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.plugin.justiceapp.R
import com.plugin.justiceapp.models.Product
import com.plugin.justiceapp.utils.JusticeUtils
import kotlinx.android.synthetic.main.list_item_detail_order_alt.view.*

class DetailOrderAltAdapter (private var detailOrders : MutableList<Product>, private var context: Context) : RecyclerView.Adapter<DetailOrderAltAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_detail_order_alt, parent, false))
    override fun getItemCount() = detailOrders.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(detailOrders[position], context)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(product: Product, context: Context){
            itemView.product_name.text = product.name
            itemView.product_price.text = JusticeUtils.setToIDR(product.price!!)
            itemView.product_topping_name.text = product.selectedToppings.let {
                if(it.isEmpty()){
                    context.resources.getString(R.string.info_no_topping)
                }else{
                    product.selectedToppings.joinToString { t -> t.name.toString() }
                }
            }
            itemView.product_topping_price.text = product.selectedToppings.let { toppings ->
                if(toppings.isNotEmpty()){
                    var temp = 0
                    for (t in toppings){ temp += t.price!! }
                    JusticeUtils.setToIDR(temp)
                }else{
                    "-"
                }
            }
            itemView.setOnClickListener {
                Toast.makeText(context, product.name, Toast.LENGTH_LONG).show()
            }
        }
    }
}