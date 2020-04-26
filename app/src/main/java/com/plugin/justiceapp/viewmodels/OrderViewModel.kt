package com.plugin.justiceapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.plugin.justiceapp.models.OrderCashier
import com.plugin.justiceapp.utils.JusticeUtils
import com.plugin.justiceapp.utils.SingleLiveEvent
import com.plugin.justiceapp.webservices.JusticeAPIService
import com.plugin.justiceapp.webservices.WrappedResponse
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.Channel
import com.pusher.client.channel.SubscriptionEventListener
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderViewModel(private val api : JusticeAPIService) : ViewModel(){
    private val pusher : Pusher = Pusher(JusticeUtils.PUSHER_KEY, PusherOptions().apply {
        setCluster(JusticeUtils.CLUSTER_NAME)
    })
    private var channel: Channel? = null
    private var eventListener: SubscriptionEventListener
    private var state : SingleLiveEvent<OrderState> = SingleLiveEvent()
    private var orders = MutableLiveData<List<OrderCashier>>()
    private var hasSubscribed = MutableLiveData<Boolean>().apply { value = false }
    private var pivotOrder = MutableLiveData<OrderCashier>()

    init {
        pusher.connect(object : ConnectionEventListener {
            override fun onConnectionStateChange(change: ConnectionStateChange?) { println("State changed from " + change?.getPreviousState() + " to " + change?.getCurrentState()); }
            override fun onError(message: String?, code: String?, e: Exception?) { println("Pusher error: $message with code $code because of ${e?.message}") }
        }, ConnectionState.ALL)

        eventListener = SubscriptionEventListener {
            val jsonObj = JSONObject(it.data)
            val order = Gson().fromJson(jsonObj.get("message").toString(), OrderCashier::class.java)
            order.generatedId = JusticeUtils.getRandomMillis()
            pivotOrder.postValue(order)
        }
    }


    fun bindPusher(branchId : String, forceResubscribe : Boolean){
        if(channel != null){
            pusher.unsubscribe("${JusticeUtils.CHANNEL_NAME}-$branchId")
            if(channel!!.isSubscribed){
                unbindPusher()
            }
            channel = pusher.subscribe("${JusticeUtils.CHANNEL_NAME}-$branchId")
        }else{
            channel = pusher.subscribe("${JusticeUtils.CHANNEL_NAME}-$branchId")
        }

        hasSubscribed.value = true
        channel?.bind(JusticeUtils.EVENT_NAME, eventListener)
    }


    fun processOrder(order: OrderCashier){
        try{
            state.value = OrderState.IsLoading(true)
            val gson = Gson().toJson(order)
            val body: RequestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), gson)
            api.confirmOrder(body).enqueue(object: Callback<WrappedResponse<OrderCashier>> {
                override fun onFailure(call: Call<WrappedResponse<OrderCashier>>, t: Throwable) {
                    println("onFailure createOrder -> ${t.message}")
                    state.value = OrderState.IsLoading(false)
                    state.value = OrderState.ShowToast("Gagal saat mengkonfirmasi pesanan")
                    state.value = OrderState.FailedCreateOrder
                }

                override fun onResponse(call: Call<WrappedResponse<OrderCashier>>, response: Response<WrappedResponse<OrderCashier>>) {
                    if(response.isSuccessful){
                        val b = response.body() as WrappedResponse
                        if(b.status){
                            state.value = OrderState.SuccessCreateOrder(order.generatedId.toString())
                        }else{
                            state.value = OrderState.FailedCreateOrder
                            println(response.body())
                            state.value = OrderState.ShowToast("Gagal membuat pesanan")
                        }
                    }else{
                        println(response.code())
                        println(response.message())
                        println(response.errorBody())
                        state.value = OrderState.FailedCreateOrder
                        state.value = OrderState.ShowToast("Tidak dapat mengonfirmasi pesanan")
                    }
                    state.value = OrderState.IsLoading(false)
                }

            })
        }catch (e: Exception){
            println(e.message)
            state.value = OrderState.IsLoading(false)
            state.value = OrderState.ShowToast("Gagal saat mengkonfirmasi pesanan")
            state.value = OrderState.FailedCreateOrder
        }
    }

    fun updateOrderValue(ors : List<OrderCashier>){ orders.postValue(ors) }
    fun unbindPusher(){
        channel?.unbind(JusticeUtils.EVENT_NAME, eventListener)
    }
    fun listenState() = state
    fun listenToOrders() = orders
    fun listenToPivotOrder() = pivotOrder
}

sealed class OrderState {
    data class SuccessCreateOrder(var generatedId : String) : OrderState()
    object FailedCreateOrder : OrderState()
    data class IsLoading(var state: Boolean) : OrderState()
    data class AttachToRecycler(var e : String) : OrderState()
    data class ShowToast(var message : String) :OrderState()
    object ClearLocalDatabase : OrderState()
}