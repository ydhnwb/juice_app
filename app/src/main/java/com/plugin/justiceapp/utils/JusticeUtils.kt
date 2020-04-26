package com.plugin.justiceapp.utils

import android.content.Context
import java.text.NumberFormat
import java.util.*
import kotlin.random.Random

class JusticeUtils {
    companion object {
        const val PUSHER_KEY = "af150b12c7f8cf0e920f"
        const val CLUSTER_NAME = "ap1"
        const val CHANNEL_NAME = "product-order"
        const val EVENT_NAME = "App\\Events\\Order"
        var API_ENDPOINT = "http://juice-apps.herokuapp.com/"

        fun getToken(c: Context): String? {
            val s = c.getSharedPreferences("USER", Context.MODE_PRIVATE)
            return s?.getString("TOKEN", null)
        }

        fun setToken(context: Context, token: String) {
            val pref = context.getSharedPreferences("USER", Context.MODE_PRIVATE)
            pref.edit().apply {
                putString("TOKEN", token)
                apply()
            }
        }

        fun clearToken(context: Context) {
            val pref = context.getSharedPreferences("USER", Context.MODE_PRIVATE)
            pref.edit().clear().apply()
        }

        fun isFirstTime(context: Context): Boolean {
            val pref = context.getSharedPreferences("UTILS", Context.MODE_PRIVATE)
            return pref.getBoolean("FIRST_TIME", true)
        }

        fun setFirstTime(context: Context, value: Boolean) {
            val pref = context.getSharedPreferences("UTILS", Context.MODE_PRIVATE)
            pref.edit().apply {
                putBoolean("FIRST_TIME", value)
                apply()
            }
        }

        fun setBranch(context: Context, branchId: Int) {
            val pref = context.getSharedPreferences("UTILS", Context.MODE_PRIVATE)
            pref.edit().apply() {
                putInt("BRANCH", branchId)
                apply()
            }
        }

        fun setBranchName(context: Context, branchName: String?) {
            val pref = context.getSharedPreferences("UTILS", Context.MODE_PRIVATE)
            pref.edit().apply() {
                putString("BRANCH_NAME", branchName)
                apply()
            }
        }

        fun getCurrentBranch(context: Context): Int {
            val pref = context.getSharedPreferences("UTILS", Context.MODE_PRIVATE)
            return pref.getInt("BRANCH", 0)
        }

        fun getCurrentBranchName(context: Context): String? {
            val pref = context.getSharedPreferences("UTILS", Context.MODE_PRIVATE)
            return pref.getString("BRANCH_NAME", null)
        }

        fun setDefaultPin(context: Context, pin : String) {
            val pref = context.getSharedPreferences("UTILS", Context.MODE_PRIVATE)
            pref.edit().apply {
                putString("PIN", pin)
                apply()
            }
        }

        fun getPin(context: Context): String? {
            val pref = context.getSharedPreferences("UTILS", Context.MODE_PRIVATE)
            return pref.getString("PIN", null)
        }

        fun setToIDR(num: Int): String {
            val localeID = Locale("in", "ID")
            val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeID)
            return formatRupiah.format(num)
        }

        fun setDeviceId(id: String, context: Context) {
            val pref = context.getSharedPreferences("UTILS", Context.MODE_PRIVATE)
            pref.edit().apply {
                putString("DEVICE_ID", id)
                apply()
            }
        }

        fun getDeviceId(context: Context): String? {
            val pref = context.getSharedPreferences("UTILS", Context.MODE_PRIVATE)
            return pref.getString("DEVICE_ID", null)
        }

        fun getRandomMillis() : String {
            val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
            return (1..3).map { _ -> Random.nextInt(0, charPool.size) }
                .map(charPool::get)
                .joinToString("")
        }
    }
}