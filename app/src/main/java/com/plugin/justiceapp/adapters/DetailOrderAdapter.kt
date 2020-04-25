package com.plugin.justiceapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.plugin.justiceapp.R
import com.plugin.justiceapp.models.Product
import com.plugin.justiceapp.utils.JusticeUtils
import com.plugin.justiceapp.viewmodels.ProductViewModel
import kotlinx.android.synthetic.main.list_item_detail_order.view.*

class DetailOrderAdapter (private var selectedProducts : MutableList<Product>, private var context: Context, private var productViewModel: ProductViewModel) :
    RecyclerView.Adapter<DetailOrderAdapter.ViewHolder>(){

    fun updateList(updatedSelectedProduct : List<Product>){
        selectedProducts.clear()
        selectedProducts.addAll(updatedSelectedProduct)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.list_item_detail_order, parent, false))

    override fun getItemCount() = selectedProducts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(selectedProducts[position], context, productViewModel, position)

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        fun bind(product: Product, context: Context, productViewModel: ProductViewModel, pos : Int){
            var totalPrice = product.price!!
            itemView.detail_order_name.text = product.name
            product.selectedToppings.isNotEmpty().let{
                if(it){
                    val toppingPrice = product.selectedToppings.sumBy { t -> t.price!! }
                    totalPrice += toppingPrice
                    itemView.detail_order_more.text = "toppings: ${product.selectedToppings.joinToString { t -> t.name.toString() }}"
                }else{
                    itemView.detail_order_more.text = "No topping"
                }
            }
            itemView.detail_order_price.text = JusticeUtils.setToIDR(totalPrice)
            itemView.detail_order_delete_product.setOnClickListener { productViewModel.betaDeleteSelectedProduct(pos) }
        }
    }
}