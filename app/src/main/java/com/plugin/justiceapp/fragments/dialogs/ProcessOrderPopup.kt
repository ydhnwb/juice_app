package com.plugin.justiceapp.fragments.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.google.gson.Gson
import com.plugin.justiceapp.R
import com.plugin.justiceapp.adapters.DetailOrderAltAdapter
import com.plugin.justiceapp.databases.AppDatabase
import com.plugin.justiceapp.models.LocalOrder
import com.plugin.justiceapp.models.OrderCashier
import com.plugin.justiceapp.utils.JusticeUtils
import com.plugin.justiceapp.viewmodels.OrderState
import com.plugin.justiceapp.viewmodels.OrderViewModel
import kotlinx.android.synthetic.main.popup_process_order.view.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProcessOrderPopup : DialogFragment(){
    companion object {
        fun instance(order : OrderCashier, branch: Int?) : ProcessOrderPopup {
            val args = Bundle()
            args.putParcelable("order", order)
            branch?.let { args.putInt("branch", it) }
            return ProcessOrderPopup().apply {
                arguments = args
            }
        }
    }

    private val orderViewModel: OrderViewModel by viewModel()
    private val db : AppDatabase by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.popup_process_order,container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        db = Room.databaseBuilder(activity!!.applicationContext, AppDatabase::class.java, "justice_cashier").allowMainThreadQueries().build()
        view.process_order_rv.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter =  DetailOrderAltAdapter(mutableListOf(), requireActivity())
        }
        val o : OrderCashier = arguments?.getParcelable("order")!!
        val order = OrderCashier().apply {
            generatedId = o.generatedId
            name = o.name
            branch = o.branch
            products = o.products
        }
        var totalQuantity = 0
        var totalPrice = 0
        order.products.let {
            totalQuantity = it.size
            totalPrice = it.sumBy { p ->
                var totalPriceTemp: Int = p.price!!
                if (p.selectedToppings.isNotEmpty()) {
                    var toppingPrice = 0
                    for (t in p.selectedToppings) {
                        toppingPrice += t.price!!
                    }
                    totalPriceTemp += toppingPrice
                }
                totalPriceTemp
            }
        }
        view.process_order_price.text = JusticeUtils.setToIDR(totalPrice)
        view.process_order_rv.adapter = order.products.let { DetailOrderAltAdapter(it, requireActivity()) }
//        orderViewModel = ViewModelProvider(activity!!).get(OrderViewModel::class.java)
        orderViewModel.listenState().observe(viewLifecycleOwner, Observer {
            when(it){
                is OrderState.IsLoading -> {
                    if(it.state){
                        view.process_order_process.isEnabled = false
                        view.process_order_cancel.isEnabled = false
                        view.process_order_et_buyername.isEnabled = false
                        view.loading.visibility = View.VISIBLE
                    }else{
                        view.process_order_process.isEnabled = true
                        view.process_order_cancel.isEnabled = true
                        view.process_order_et_buyername.isEnabled = true
                        view.loading.visibility = View.GONE
                    }
                }
                is OrderState.FailedCreateOrder -> this.dismiss()
                is OrderState.SuccessCreateOrder -> this.dismiss()
            }
        })
        view.process_order_cancel.setOnClickListener {
            delete(order.generatedId.toString())
            this.dismiss()
        }
        view.process_order_process.setOnClickListener {
            if(JusticeUtils.getCurrentBranch(requireActivity()) != 0){
                view.process_order_in_buyername.error = null
                val buyerName = view.process_order_et_buyername.text.toString().trim()
                if(buyerName.isNotEmpty()){
                    order.name = buyerName
                    orderViewModel.processOrder(order)
                    view.process_order_in_buyername.error = null
                    toast(resources.getString(R.string.info_wait))
                }else{
                    view.process_order_in_buyername.error = resources.getString(R.string.info_name_cannot_be_blank)
                }
            }else{
                showAlert(resources.getString(R.string.info_no_branch_selected))
            }
        }
    }
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun delete(generatedId: String){
        val branch = arguments?.getInt("branch")
        branch?.let {
            db.localOrderDao().deleteByGeneratedId(generatedId)
            val localOrders : MutableList<LocalOrder> = db.localOrderDao().getAllLocalOrder(it.toString()) as MutableList<LocalOrder>
            val convertedOrder = mutableListOf<OrderCashier>()
            for(lo in localOrders){ convertedOrder.add(Gson().fromJson(lo.orderInJson, OrderCashier::class.java)) }
            orderViewModel.updateOrderValue(convertedOrder)
        }

    }

    private fun toast(message : String) = Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    private fun showAlert(message : String){
        val alertDialog = AlertDialog.Builder(requireActivity()).apply {
            setMessage(message)
            setPositiveButton(resources.getString(R.string.info_understand)) { dialog, _ ->
                dialog.dismiss()
            }
            create()
        }
        alertDialog.show()
    }
}