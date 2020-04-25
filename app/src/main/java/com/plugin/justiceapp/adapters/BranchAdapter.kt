package com.plugin.justiceapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.plugin.justiceapp.R
import com.plugin.justiceapp.activities.EtalaseActivity
import com.plugin.justiceapp.models.Branch
import kotlinx.android.synthetic.main.list_item_branch.view.*

class BranchAdapter (private val branches: MutableList<Branch>, private val context: Context) : RecyclerView.Adapter<BranchAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_branch, parent, false
            )
        )
    }

    override fun getItemCount() = branches.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(branches[position], context)

    fun updateList(it: List<Branch>){
        branches.clear()
        branches.addAll(it)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(branch: Branch, context: Context){
            with(itemView){
                branch_name.text = branch.name
                setOnClickListener {
                    context as AppCompatActivity
                    val x = context.intent.getBooleanExtra("is_cashier", false)
                    if(!x){
                        context.startActivity(Intent(context, EtalaseActivity::class.java).apply {
                            putExtra("branch", branch.id.toString())
                        })
                    }
                }
            }
        }
    }
}