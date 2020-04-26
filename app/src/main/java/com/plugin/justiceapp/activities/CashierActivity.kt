package com.plugin.justiceapp.activities

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.plugin.justiceapp.R
import com.plugin.justiceapp.adapters.OrderAdapter
import com.plugin.justiceapp.databases.AppDatabase
import com.plugin.justiceapp.models.LocalOrder
import com.plugin.justiceapp.models.OrderCashier
import com.plugin.justiceapp.viewmodels.OrderState
import com.plugin.justiceapp.viewmodels.OrderViewModel

import kotlinx.android.synthetic.main.activity_cashier.*
import kotlinx.android.synthetic.main.content_cashier.*
import kotlinx.android.synthetic.main.etc_no_branch.*
import kotlinx.android.synthetic.main.etc_waiting_for_transaction.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class CashierActivity : AppCompatActivity() {
    private val orderViewModel : OrderViewModel by viewModel()
    private val db : AppDatabase by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cashier)
        setSupportActionBar(toolbar)
        setupComponent()
        fetchFromLocal()
        orderViewModel.listenToPivotOrder().observe(this, Observer { handleDataFlow(it) })
        orderViewModel.listenState().observe(this, Observer { handleState(it) })
        orderViewModel.listenToOrders().observe(this, Observer {
            rv_order.adapter?.let { a->
                if(getBranch() == null){
                    noBranchViewVisbility(true)
                    emptyViewVisibility(false)
                }else{
                    if(a is OrderAdapter){
                        if(it.isEmpty()){
                            a.updateRecords(it)
                            emptyViewVisibility(true)
                            noBranchViewVisbility(false)
                        }else{
                            a.updateRecords(it)
                            emptyViewVisibility(false)
                            noBranchViewVisbility(false)
                        }
                    }
                }
            }
        })
    }


    private fun setupComponent(){
        rv_order.apply {
            layoutManager = LinearLayoutManager(this@CashierActivity).apply {
                reverseLayout = true
                stackFromEnd = true
            }
            adapter = OrderAdapter(mutableListOf(),this@CashierActivity)
        }
    }

    private fun handleState(it: OrderState){
        when(it){
            is OrderState.SuccessCreateOrder -> {
                delete(it.generatedId)
                toast(resources.getString(R.string.info_success_order))
            }
            is OrderState.FailedCreateOrder -> showAlert(resources.getString(R.string.info_cannot_order))
            is OrderState.ShowToast -> toast(it.message)
            is OrderState.AttachToRecycler -> {}
            is OrderState.ClearLocalDatabase -> { db.clearAllTables() }
        }
    }


    private fun handleDataFlow(order: OrderCashier){
        getBranch()?.let {
            val orderInJson = Gson().toJson(order)
            db.localOrderDao().insert(LocalOrder(orderInJson =  orderInJson, generatedId = order.generatedId.toString(), branch = getBranch()!!))
            val localOrders : List<LocalOrder> = db.localOrderDao().getAllLocalOrder(it)
            val convertedOrder = mutableListOf<OrderCashier>()
            for(lo in localOrders){ convertedOrder.add(Gson().fromJson(lo.orderInJson, OrderCashier::class.java)) }
            orderViewModel.updateOrderValue(convertedOrder)
        }
    }

    private fun fetchFromLocal(){
        getBranch()?.let {
            val localOrders : List<LocalOrder> = db.localOrderDao().getAllLocalOrder(it)
            val convertedOrder = mutableListOf<OrderCashier>()
            for(lo in localOrders){ convertedOrder.add(Gson().fromJson(lo.orderInJson, OrderCashier::class.java)) }
            orderViewModel.updateOrderValue(convertedOrder)
        }
    }

    private fun delete(generatedId: String){
        getBranch()?.let {
            db.localOrderDao().deleteByGeneratedId(generatedId)
            val localOrders : MutableList<LocalOrder> = db.localOrderDao().getAllLocalOrder(it) as MutableList<LocalOrder>
            val convertedOrder = mutableListOf<OrderCashier>()
            for(lo in localOrders){ convertedOrder.add(Gson().fromJson(lo.orderInJson, OrderCashier::class.java)) }
            orderViewModel.updateOrderValue(convertedOrder)
        }
    }

    private fun showAlert(message : String){
        val alertDialog = AlertDialog.Builder(this).apply {
            setMessage(message)
            setPositiveButton(resources.getString(R.string.info_understand)) { dialog, _ ->
                dialog.dismiss()
            }
            create()
        }
        alertDialog.show()
    }

    override fun onResume() {
        super.onResume()
        if(getBranch() == null){
            showAlert(resources.getString(R.string.info_no_branch_selected))
            noBranchViewVisbility(true)
            emptyViewVisibility(false)
        }else{
            val selectedBranch = getBranch()!!
            if(selectedBranch.isNotEmpty()){
//                val i = JusticeUtils.getCurrentBranch(this@MainActivity)
//                val t = selectedBranch != i
                orderViewModel.bindPusher(selectedBranch, false)
            }
        }
    }

    private fun noBranchViewVisbility(state : Boolean){
        if(state){ etc_no_branch_view.visibility = View.VISIBLE }else{ etc_no_branch_view.visibility = View.GONE }
    }

    private fun emptyViewVisibility(state : Boolean){
        if(state){
            etc_waiting_for_transaction_view.visibility = View.VISIBLE
        }else{
            etc_waiting_for_transaction_view.visibility = View.GONE
        }
    }

    private fun toast(m : String) = Toast.makeText(this, m, Toast.LENGTH_LONG).show()
    private fun getBranch() : String? = intent.getStringExtra("branch")


}
