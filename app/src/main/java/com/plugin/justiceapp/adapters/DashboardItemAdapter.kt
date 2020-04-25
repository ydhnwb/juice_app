package com.plugin.justiceapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.plugin.justiceapp.R
import com.plugin.justiceapp.activities.PromptPinActivity
import com.plugin.justiceapp.models.DashboardItem
import kotlinx.android.synthetic.main.list_item_dashboard_menu.view.*

class DashboardItemAdapter (private val menus: MutableList<DashboardItem>, private val context: Context) : RecyclerView.Adapter<DashboardItemAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_dashboard_menu, parent, false)
        )
    }

    override fun getItemCount() = menus.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int)  = holder.bind(menus[position], context, position)

    fun updateList(it: List<DashboardItem>){
        menus.clear()
        menus.addAll(it)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(menu: DashboardItem, context: Context, i : Int){
            with(itemView){
                dashboard_title.text = menu.title
                dashboard_icon.load(menu.icon)
                dashboard_bg.setBackgroundColor(menu.bgColor)
                setOnClickListener {
                    when(i){
                        0 -> context.startActivity(Intent(context, PromptPinActivity::class.java).putExtra("is_cashier", false))
                        1 -> context.startActivity(Intent(context, PromptPinActivity::class.java).putExtra("is_cashier", true))
                        else -> println()
                    }
                }
            }
        }
    }
}