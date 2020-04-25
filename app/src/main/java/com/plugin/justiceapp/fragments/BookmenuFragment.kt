package com.plugin.justiceapp.fragments.dialogs

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.plugin.justiceapp.R
import com.plugin.justiceapp.adapters.ProductAdapter
import com.plugin.justiceapp.models.Category
import com.plugin.justiceapp.models.Product
import com.plugin.justiceapp.viewmodels.ProductViewModel
import kotlinx.android.synthetic.main.etc_empty_view.view.*
import kotlinx.android.synthetic.main.fragment_bookmenu.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class BookmenuFragment : Fragment(R.layout.fragment_bookmenu){
    private val productViewModel : ProductViewModel by sharedViewModel()

    companion object {
        fun instance(category: Category?) : BookmenuFragment {
            return if(category == null){
                BookmenuFragment()
            }else{
                val args = Bundle()
                args.putParcelable("category", category)
                BookmenuFragment().apply {
                    arguments = args
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            view.rv_product.apply {
                layoutManager = if(this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT){
                    GridLayoutManager(it, 2)
                }else{
                    GridLayoutManager(it, 4)
                }
                adapter = ProductAdapter(mutableListOf(), it, productViewModel)
            }
            if(arguments == null ){
                view.etc_empty_view.visibility = View.VISIBLE
                productViewModel.listenSearchResultProduct().observe(viewLifecycleOwner, Observer {
                    view.rv_product.adapter?.let { adapter ->
                        if(adapter is ProductAdapter){
                            if(it.isEmpty()){
                                view.etc_empty_view.visibility = View.VISIBLE
                            }else{
                                view.etc_empty_view.visibility = View.GONE
                            }
                            adapter.updateList(it)
                        }
                    }
                })
            }else{
                productViewModel.listenAllProduct().observe(viewLifecycleOwner, Observer {updatedProducts ->
                    arguments?.let { arg ->
                        view.etc_empty_view.visibility = View.GONE
                        val category : Category? = arg.getParcelable("category")
                        category?.let {cat ->
                            CoroutineScope(Dispatchers.Default).launch {
                                filterProductsByCategory(cat, updatedProducts)
                            }
                        }
                    }
                })
            }
        }
    }

    private suspend fun filterProductsByCategory(categoryAttached : Category, products : List<Product>) {
        val filtered =  products.filter { product -> product.category.equals(categoryAttached.name) }
        attachToRecycler(filtered as MutableList<Product>)
    }

    private suspend fun attachToRecycler(filteredProducts : MutableList<Product>){
        withContext(Main){
            view?.rv_product?.adapter?.let {
                if(it is ProductAdapter){
                    it.updateList(filteredProducts)
                }
            }
        }
    }
}