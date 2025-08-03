package com.sct.tictactoe

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import java.util.Objects

class SettingsActivity : AppCompatActivity() {
    var vibrationSwitch: Switch? = null
    var soundSwitch: Switch? = null

    private var rateUs: LinearLayout? = null
    private var feedback: LinearLayout? = null
    private var backBtn: ImageView? = null

    private var adView: AdView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE) //will hide the title
        Objects.requireNonNull<ActionBar?>(getSupportActionBar()).hide() // hide the title bar
        this.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        ) //enable full screen
        setContentView(R.layout.activity_settings)

        // Initialize the Mobile Ads SDK
        MobileAds.initialize(
            this,
            OnInitializationCompleteListener { initializationStatus: InitializationStatus? -> })

        // Set test device IDs
//                RequestConfiguration configuration = new RequestConfiguration.Builder()
//                .setTestDeviceIds(Arrays.asList("B4C405009710ADFB5DB53F28D42F48A7"))
//                .build();
//        MobileAds.setRequestConfiguration(configuration);

        // Find the AdView and load the ad
        adView = findViewById<AdView?>(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        adView!!.loadAd(adRequest)

        vibrationSwitch = findViewById<Switch>(R.id.vibrationSwitch)
        soundSwitch = findViewById<Switch>(R.id.soundSwitch)

        backBtn = findViewById<ImageView>(R.id.settingsBackBtn)
        rateUs = findViewById<LinearLayout>(R.id.rateUsLayout)
        feedback = findViewById<LinearLayout>(R.id.feedbackLayout)

        if (MyServices.VIBRATION_CHECK) {
            vibrationSwitch!!.setChecked(true)
        } else if (!MyServices.VIBRATION_CHECK) {
            vibrationSwitch!!.setChecked(false)
        }

        vibrationSwitch!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                MyServices.VIBRATION_CHECK = true
            } else {
                MyServices.VIBRATION_CHECK = false
            }
        })

        if (MyServices.SOUND_CHECK) {
            soundSwitch!!.setChecked(true)
        } else if (!MyServices.SOUND_CHECK) {
            soundSwitch!!.setChecked(false)
        }

        soundSwitch!!.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                MyServices.SOUND_CHECK = true
            } else {
                MyServices.SOUND_CHECK = false
            }
        })

        backBtn!!.setOnClickListener(View.OnClickListener { v: View? -> onBackPressed() })

        rateUs!!.setOnClickListener(View.OnClickListener { v: View? -> askRatings() })

        feedback!!.setOnClickListener(View.OnClickListener { v: View? -> composeEmail("Tic Tac Toe Feedback") })
    }

    fun askRatings() {
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + this.getPackageName())
                )
            )
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + this.getPackageName())
                )
            )
        }
    }

    fun composeEmail(subject: String?) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.setData(Uri.parse("mailto:vimalku637@gmail.com")) // only email apps should handle this
            intent.putExtra(Intent.EXTRA_SUBJECT, subject)
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(Intent.createChooser(intent, "Send feedback"))
            }
        } catch (e: ActivityNotFoundException) {
            //TODO smth
        }
    }

    override fun onPause() {
        if (adView != null) {
            adView!!.pause()
        }
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (adView != null) {
            adView!!.resume()
        }
    }

    override fun onDestroy() {
        if (adView != null) {
            adView!!.destroy()
        }
        super.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}