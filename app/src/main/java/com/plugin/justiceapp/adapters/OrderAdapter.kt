package com.plugin.justiceapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.plugin.justiceapp.R
import com.plugin.justiceapp.fragments.dialogs.ProcessOrderPopup
import com.plugin.justiceapp.models.OrderCashier
import com.plugin.justiceapp.utils.JusticeUtils
import kotlinx.android.synthetic.main.etc_expandable_child_layout.view.*
import kotlinx.android.synthetic.main.etc_expandable_parent_layout.view.*
import kotlinx.android.synthetic.main.list_item_order.view.*

class OrderAdapter (private var orders : MutableList<OrderCashier>, private var context : Context) : RecyclerView.Adapter<OrderAdapter.ViewHolder>(){
    fun updateRecords(ords : List<OrderCashier>){
        orders.clear()
        orders.addAll(ords)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.list_item_order, parent, false))
    override fun getItemCount() = orders.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(orders[position], context)

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        fun bind(order : OrderCashier, context: Context){
            with(itemView){
                order_detail.parentLayout.setOnClickListener {
                    if (order_detail.isExpanded) { order_detail.collapse()
                    } else { order_detail.expand() }
                }
                order_name.text = order.name.toString().toUpperCase().substring(0, 4)
                var totalQuantity = 0
                var totalPrice = 0
                val products = order.products
                products.let {
                    totalQuantity = it.size
                    totalPrice = it.sumBy {p ->
                        var totalPriceTemp : Int = p.price!!
                        if(p.selectedToppings.isNotEmpty()){
                            var toppingPrice = 0
                            for(t in p.selectedToppings){
                                toppingPrice += t.price!!
                            }
                            totalPriceTemp += toppingPrice
                        }
                        totalPriceTemp
                    }
                    order_detail.secondLayout.order_items_rv.apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = DetailOrderAltAdapter(it, context)
                    }
                }
                order_price.text = JusticeUtils.setToIDR(totalPrice)
                order_detail.parentLayout.order_item.text = "$totalQuantity items"
                setOnClickListener {
                    val fragmentManager = context as AppCompatActivity
                    ProcessOrderPopup.instance(order).show(fragmentManager.supportFragmentManager, "order_popup")
                }
            }
        }
    }
}