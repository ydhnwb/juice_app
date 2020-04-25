package com.plugin.justiceapp.webservices

import com.google.gson.annotations.SerializedName
import com.plugin.justiceapp.models.*
import com.plugin.justiceapp.utils.JusticeUtils
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

class JustApi {
    companion object {
        private var retrofit: Retrofit? = null
        private var okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        private fun getClient() : Retrofit {
            return if (retrofit == null){
                retrofit = Retrofit.Builder().baseUrl(JusticeUtils.API_ENDPOINT).client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create()).build()
                retrofit!!
            }else{
                retrofit!!
            }
        }

        fun instance(): JusticeAPIService = getClient().create(JusticeAPIService::class.java)
    }
}

interface JusticeAPIService{
    @GET("api/category")
    fun getAllCategory() : Call<WrappedListResponse<Category>>

    @GET("api/product")
    fun getAllProduct() : Call<WrappedListResponse<Product>>

    @GET("api/toping")
    fun getAllTopping() : Call<WrappedListResponse<Topping>>

    @GET("api/product/search/{query}")
    fun searchProduct(@Path("query") query : String) : Call<WrappedListResponse<Product>>

    @GET("api/branch")
    fun getAllBranch() : Call<WrappedListResponse<Branch>>

    @Headers("Content-Type: application/json")
    @POST("api/order")
    fun createOrder(@Body order : RequestBody) : Call<WrappedResponse<Order>>
}

data class WrappedResponse<T>(
    @SerializedName("message") var message : String?,
    @SerializedName("status") var status : Boolean,
    @SerializedName("data") var data : T?
){
    constructor(): this(null, false, null )
}

data class WrappedListResponse<T>(
    @SerializedName("message") var message: String?,
    @SerializedName("status") var status: Boolean?,
    @SerializedName("data") var data: List<T>?
){
    constructor(): this(null, null, null )
}