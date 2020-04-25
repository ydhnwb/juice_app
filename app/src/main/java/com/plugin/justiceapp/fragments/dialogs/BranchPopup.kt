package com.plugin.justiceapp.fragments.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.plugin.justiceapp.R
import com.plugin.justiceapp.viewmodels.BranchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class BranchPopup : DialogFragment() {
//    private val branchViewModel : BranchViewModel by viewModel()
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.popup_branch,container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        activity?.let { act ->
//            branchViewModel.listenToUIState().observe(viewLifecycleOwner, Observer {
//                when(it){
//                    is SettingState.IsLoading -> {
//                        if(it.state){
//                            view.rv_branch.visibility = View.GONE
//                            view.loading.visibility = View.VISIBLE
//                        }else{
//                            view.rv_branch.visibility = View.VISIBLE
//                            view.loading.visibility = View.GONE
//                        }
//                    }
//                    is SettingState.ShowToast -> Toast.makeText(act, it.message, Toast.LENGTH_LONG).show()
//                }
//            })
//            settingViewModel.listenToBranches().observe(viewLifecycleOwner, Observer {
//                view.rv_branch.apply {
//                    layoutManager = LinearLayoutManager(act)
//                    adapter = BranchAdapter(it as MutableList<Branch>, act, this@BranchPopup, settingViewModel )
//                }
//            })
//            settingViewModel.fetchBranch()
//        }
//    }
//
//    override fun onStart() {
//        super.onStart()
//        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
//    }
}