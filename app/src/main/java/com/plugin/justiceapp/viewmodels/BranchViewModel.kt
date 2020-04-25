package com.plugin.justiceapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.plugin.justiceapp.models.Branch
import com.plugin.justiceapp.utils.SingleLiveEvent
import com.plugin.justiceapp.webservices.JusticeAPIService
import com.plugin.justiceapp.webservices.WrappedListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BranchViewModel(private val api : JusticeAPIService) : ViewModel(){
    private var state : SingleLiveEvent<BranchState> = SingleLiveEvent()
    private var branches = MutableLiveData<List<Branch>>()

    fun fetchBranch(){
        state.value = BranchState.IsLoading(true)
        api.getAllBranch().enqueue(object : Callback<WrappedListResponse<Branch>> {
            override fun onFailure(call: Call<WrappedListResponse<Branch>>, t: Throwable) {
                println(t.message)
                state.value = BranchState.IsLoading(false)
                state.value = BranchState.ShowToast(t.message.toString())
            }

            override fun onResponse(call: Call<WrappedListResponse<Branch>>, response: Response<WrappedListResponse<Branch>>) {
                if(response.isSuccessful){
                    val body = response.body() as WrappedListResponse<Branch>
                    if(body.status!!){
                        branches.postValue(body.data)
                    }else{
                        state.value = BranchState.ShowToast("Tidak dapat mengambil data cabang")
                    }
                }else{
                    state.value = BranchState.ShowToast("Kesalahan saat mengambil data cabang")
                }
                state.value = BranchState.IsLoading(false)
            }
        })
    }

    fun updateBranchName(branchName: String) { state.value = BranchState.SelectedBranch(branchName) }
    fun listenToUIState() = state
    fun listenToBranches() = branches
}

sealed class BranchState{
    data class IsLoading(var state : Boolean) : BranchState()
    data class ShowToast(var message : String) : BranchState()
    data class SelectedBranch(var branchName : String) : BranchState()
    object Reset : BranchState()
}