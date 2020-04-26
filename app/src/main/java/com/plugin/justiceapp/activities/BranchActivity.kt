package com.plugin.justiceapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.plugin.justiceapp.R
import com.plugin.justiceapp.adapters.BranchAdapter
import com.plugin.justiceapp.models.Branch
import com.plugin.justiceapp.viewmodels.BranchState
import com.plugin.justiceapp.viewmodels.BranchViewModel
import kotlinx.android.synthetic.main.activity_branch.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class BranchActivity : AppCompatActivity() {
    private val branchViewModel : BranchViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_branch)
        supportActionBar?.hide()
        setupUI()
        branchViewModel.fetchBranch()
        branchViewModel.listenToUIState().observer(this, Observer { handleUIState(it) })
        branchViewModel.listenToBranches().observe(this, Observer { handleState(it) })
    }

    private fun setupUI(){
        rv_branch.apply {
            layoutManager = LinearLayoutManager(this@BranchActivity)
            adapter = BranchAdapter(mutableListOf(), this@BranchActivity)
        }
    }

    private fun handleUIState(it: BranchState){
        when(it){
            is BranchState.IsLoading -> {
                if(it.state){ loading.visibility = View.VISIBLE} else loading.visibility = View.GONE
            }
            is BranchState.ShowToast -> toast(it.message)
        }
    }

    private fun handleState(it: List<Branch>) = rv_branch.adapter?.let { x -> if(x is BranchAdapter){ x.updateList(it) } }
    private fun toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    private fun isCashier() : Boolean = intent.getBooleanExtra("is_cashier", false)
}
