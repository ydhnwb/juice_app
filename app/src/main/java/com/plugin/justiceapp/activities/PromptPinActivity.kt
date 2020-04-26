package com.plugin.justiceapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.beautycoder.pflockscreen.PFFLockScreenConfiguration
import com.beautycoder.pflockscreen.fragments.PFLockScreenFragment
import com.beautycoder.pflockscreen.fragments.PFLockScreenFragment.OnPFLockScreenCodeCreateListener
import com.beautycoder.pflockscreen.fragments.PFLockScreenFragment.OnPFLockScreenLoginListener
import com.beautycoder.pflockscreen.security.PFFingerprintPinCodeHelper
import com.beautycoder.pflockscreen.security.PFResult
import com.beautycoder.pflockscreen.security.PFSecurityManager
import com.beautycoder.pflockscreen.security.callbacks.PFPinCodeHelperCallback
import com.beautycoder.pflockscreen.viewmodels.PFPinCodeViewModel
import com.plugin.justiceapp.R
import com.plugin.justiceapp.utils.JusticeUtils
import com.plugin.justiceapp.utils.PFPinCodeHelperImpl
import kotlinx.android.synthetic.main.activity_prompt_pin.*


class PromptPinActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prompt_pin)
        setSupportActionBar(toolbar)
        supportActionBar?.hide()
        showLockScreenFragment(true)
    }

    private val mCodeCreateListener: OnPFLockScreenCodeCreateListener = object : OnPFLockScreenCodeCreateListener {
            override fun onCodeCreated(encodedCode: String) { println("Code created") }
            override fun onNewCodeValidationFailed() { println("Code error") }
    }

    private fun toast(e: String) = Toast.makeText(this, e, Toast.LENGTH_LONG).show()

    private val mLoginListener: OnPFLockScreenLoginListener = object : OnPFLockScreenLoginListener {
        override fun onCodeInputSuccessful() {
            startActivity(Intent(this@PromptPinActivity, BranchActivity::class.java).apply {
                putExtra("is_cashier", intent.getBooleanExtra("is_cashier", false))
            })
            finish()
        }

        override fun onFingerprintSuccessful() { toast("Success Finger") }

        override fun onPinLoginFailed() {
            AlertDialog.Builder(this@PromptPinActivity).apply {
                setMessage(resources.getString(R.string.info_wrong_pin))
                setPositiveButton(resources.getString(R.string.common_understand)){o, _ -> o.dismiss()}
            }.show()
        }

        override fun onFingerprintLoginFailed() { toast("Failed finger") }
    }

    private fun showLockScreenFragment() {
        PFPinCodeViewModel().isPinCodeEncryptionKeyExist.observe(this@PromptPinActivity,
            Observer<PFResult<Boolean?>?> {result ->
                if (result == null) {
                    return@Observer
                }
                if (result.error != null) {
                    Toast.makeText(this@PromptPinActivity, "Can not get pin code info", Toast.LENGTH_SHORT).show()
                    return@Observer
                }
                showLockScreenFragment(result.result!!);

            }
        )
    }

    private fun showLockScreenFragment(isPinExist: Boolean) {
        val builder = PFFLockScreenConfiguration.Builder(this).apply {
            setTitle(if (isPinExist) "Masukkan PIN Anda" else "Create Code")
            setCodeLength(4)
            setNewCodeValidation(true)
            setNewCodeValidationTitle("Please input code again")
            setUseFingerprint(false)
        }

        val fragment = PFLockScreenFragment()
        builder.setMode(if (isPinExist) PFFLockScreenConfiguration.MODE_AUTH else PFFLockScreenConfiguration.MODE_CREATE)

        if (isPinExist) {
            fragment.setEncodedPinCode(JusticeUtils.getPin(this@PromptPinActivity))
            fragment.setLoginListener(mLoginListener)
        }
        fragment.setConfiguration(builder.build())
        fragment.setCodeCreateListener(mCodeCreateListener)
        supportFragmentManager.beginTransaction().replace(R.id.container_view, fragment).commit()
    }
}
