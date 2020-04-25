package com.plugin.justiceapp.activities

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.plugin.justiceapp.R
import com.plugin.justiceapp.adapters.DashboardItemAdapter
import com.plugin.justiceapp.models.DashboardItem
import com.plugin.justiceapp.utils.JusticeUtils

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.hide()
        Thread(Runnable {
            if (JusticeUtils.isFirstTime(this@MainActivity)) {
                runOnUiThread { startActivity(Intent(this@MainActivity, IntroActivity::class.java).also {
                    finish()
                })}
            }
        }).start()
        setupDashboard()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupDashboard(){
        rv_dashboard.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = DashboardItemAdapter(
                mutableListOf<DashboardItem>(
                    DashboardItem(resources.getString(R.string.common_bookmenu), R.drawable.ic_doodle_mail, ContextCompat.getColor(this@MainActivity, R.color.colorPrimaryDark)),
                    DashboardItem(resources.getString(R.string.common_cashier), R.drawable.ic_doodle_mail, ContextCompat.getColor(this@MainActivity, R.color.colorPrimaryDark))
            ), this@MainActivity)
        }
    }
}
