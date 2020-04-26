package com.plugin.justiceapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.RoundedCornersTransformation
import com.plugin.justiceapp.R
import com.plugin.justiceapp.fragments.dialogs.ToppingPopup
import com.plugin.justiceapp.models.Product
import com.plugin.justiceapp.utils.JusticeUtils
import com.plugin.justiceapp.viewmodels.ProductViewModel
import kotlinx.android.synthetic.main.list_item_product.view.*

class ProductAdapter (private var products : MutableList<Product>, private var context: Context, private var productViewModel: ProductViewModel)
    : RecyclerView.Adapter<ProductAdapter.ViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_product, parent, false))

    override fun getItemCount() = products.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(products[position], context, productViewModel)

    fun updateList(updatedProducts: List<Product>){
        products.clear()
        products.addAll(updatedProducts)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(product: Product, context: Context, productViewModel: ProductViewModel){
            with(itemView){
                product_name.text = product.name
                product_image.load("https://cms.sehatq.com/public/img/article_img/tidak-membuat-gemuk-ini-9-manfaat-jus-alpukat-bagi-kesehatan-1573445329.jpg") {
                    transformations(RoundedCornersTransformation(15.0F))
                }
                product_price.text = JusticeUtils.setToIDR(product.price!!)
                setOnClickListener {
//                productViewModel.addSelectedProduct(product)
                    val p : Product = product
                    val fragmentManager = context as AppCompatActivity
                    ToppingPopup.instance(p).show(fragmentManager.supportFragmentManager, "topping_popup")
                }
            }
        }
    }
}