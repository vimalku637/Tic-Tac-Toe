package com.sct.tictactoe

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import java.util.Objects

class PlayWithAIActivity : AppCompatActivity(), OnTouchListener {
    private var playerName: String? = null
    private var playerNameTxt: EditText? = null
    private var playerButton: Button? = null

    //    private ImageView BackBtn;
    private var adView: AdView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE) //will hide the title
        Objects.requireNonNull<ActionBar?>(getSupportActionBar()).hide() // hide the title bar
        this.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        ) //enable full screen

        setContentView(R.layout.activity_play_with_ai)

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

        //        BackBtn = findViewById(R.id.ai_player_names_back_btn);
        playerNameTxt = findViewById<EditText>(R.id.aiPlayerNameEditText)
        playerButton = findViewById<Button>(R.id.aiPlayerNameBtn)

        playerButton!!.setOnTouchListener(this)
        playerButton!!.setOnClickListener(View.OnClickListener { v: View? ->
            if (TextUtils.isEmpty(playerNameTxt!!.getText().toString())) {
                Toast.makeText(getBaseContext(), "Enter Name", Toast.LENGTH_LONG).show()
            } else {
                playerName = playerNameTxt!!.getText().toString()
                val intent =
                    Intent(this@PlayWithAIActivity, AiPlayerChooseSymbolActivity::class.java)
                intent.putExtra("p1", playerName)
                startActivity(intent)
            }
        })

        //        BackBtn.setOnClickListener(v -> onBackPressed());
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (v === playerButton) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.setAlpha(0.5f)
            } else {
                v.setAlpha(1f)
            }
        }
        return false
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
