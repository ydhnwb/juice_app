package com.plugin.justiceapp.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.beautycoder.pflockscreen.security.PFFingerprintPinCodeHelper
import com.github.paolorotolo.appintro.AppIntro2
import com.github.paolorotolo.appintro.AppIntroFragment
import com.github.paolorotolo.appintro.model.SliderPage
import com.plugin.justiceapp.R
import com.plugin.justiceapp.utils.JusticeUtils
import java.util.*

class IntroActivity : AppIntro2() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        val sliderPage = SliderPage().apply {
            description = resources.getString(R.string.info_intro_1)
            descColor = ContextCompat.getColor(this@IntroActivity, R.color.colorPrimaryDark)
            imageDrawable = R.drawable.ic_doodle_app_development
            bgColor = ContextCompat.getColor(this@IntroActivity, R.color.colorWhite)
        }

        val sliderPage2 = SliderPage().apply {
            description = resources.getString(R.string.info_intro_2)
            descColor = ContextCompat.getColor(this@IntroActivity, R.color.colorPrimaryDark)
            imageDrawable = R.drawable.ic_doodle_payment_processed
            bgColor = ContextCompat.getColor(this@IntroActivity, R.color.colorWhite)
        }

        val sliderPage3 = SliderPage().apply {
            description = resources.getString(R.string.info_intro_3)
            descColor = ContextCompat.getColor(this@IntroActivity, R.color.colorPrimaryDark)
            imageDrawable = R.drawable.ic_doodle_ecommerce
            bgColor = ContextCompat.getColor(this@IntroActivity, R.color.colorWhite)
        }

        addSlide(AppIntroFragment.newInstance(sliderPage))
        addSlide(AppIntroFragment.newInstance(sliderPage2))
        addSlide(AppIntroFragment.newInstance(sliderPage3))
        setFadeAnimation()
        isSkipButtonEnabled = false
        isVibrateOn = true
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        JusticeUtils.setFirstTime(this@IntroActivity, false).also {
            val generatedId = UUID.randomUUID().toString()
            println("Device id -> $generatedId")
            JusticeUtils.setDeviceId(generatedId, this)
            PFFingerprintPinCodeHelper.getInstance().encodePin(this, "1234") { res ->
                JusticeUtils.setDefaultPin(this@IntroActivity, res.result)
            }
            JusticeUtils.setBranch(this, 0)
            JusticeUtils.setBranchName(this, null)
            startActivity(Intent(this@IntroActivity, MainActivity::class.java))
            finish()
        }
    }
}