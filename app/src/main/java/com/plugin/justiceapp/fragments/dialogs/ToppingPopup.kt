package com.plugin.justiceapp.fragments.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.plugin.justiceapp.R
import com.plugin.justiceapp.adapters.ToppingAdapter
import com.plugin.justiceapp.models.Product
import com.plugin.justiceapp.models.Topping
import com.plugin.justiceapp.viewmodels.ProductViewModel
import com.plugin.justiceapp.viewmodels.ToppingPopupState
import kotlinx.android.synthetic.main.popup_topping.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ToppingPopup : DialogFragment(){
    private val productViewModel: ProductViewModel by sharedViewModel()
    private var availableTopping = mutableListOf<Topping>()

    companion object {
        fun instance(product: Product) : ToppingPopup{
            val args = Bundle()
            args.putParcelable("product", product)
            return ToppingPopup().apply {
                arguments = args
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(
        R.layout.popup_topping, container)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val product : Product = arguments?.getParcelable("product")!!
        val p = Product().apply {
            id = product.id
            name = product.name
            category = product.category
            image = product.image
            price = product.price
            description = product.description
        }
        view.rv_popup_topping.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = ToppingAdapter(mutableListOf(), requireActivity())
        }
        productViewModel.listenAllTopping().observe(viewLifecycleOwner, Observer {
            CoroutineScope(Dispatchers.Default).launch{
                filterTopping(it)
            }
        })
        productViewModel.listenToppingPopupState().observe(viewLifecycleOwner, Observer {
            when(it){
                is ToppingPopupState.IsLoading -> {
                    if(it.state){
                        view.loading_popup.visibility = View.VISIBLE
                        view.rv_popup_topping.visibility = View.GONE
                        view.btn_submit_popup_topping.isEnabled = false
                    }else{
                        view.rv_popup_topping.visibility = View.VISIBLE
                        view.loading_popup.visibility = View.GONE
                        view.btn_submit_popup_topping.isEnabled = true
                    }
                }
                is ToppingPopupState.ShowToast -> Toast.makeText(activity, it.message, Toast.LENGTH_LONG).show()
            }
        })
        productViewModel.fetchAllTopping()
        view.btn_submit_popup_topping.setOnClickListener {
            val selectedTopping = availableTopping.filter { topping -> topping.isChecked }
            p.selectedToppings = selectedTopping.toMutableList()
            productViewModel.betaAddSelectedProduct(p)
            this.dismiss()
        }

        view.no_topping.setOnClickListener {
            productViewModel.betaAddSelectedProduct(p)
            this.dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private suspend fun filterTopping(it : List<Topping>){
        val product : Product = arguments?.getParcelable("product")!!
        availableTopping = it.filter { topping ->
            topping.category.equals(product.category)
        }.toMutableList()
        attachToRecycler(availableTopping)
    }

    private suspend fun attachToRecycler(filteredToppings : MutableList<Topping>){
        withContext(Dispatchers.Main){
            requireView().rv_popup_topping.adapter?.let { adapter ->
                if(adapter is ToppingAdapter){
                    adapter.changeList(filteredToppings)
                }
            }
        }
    }
}