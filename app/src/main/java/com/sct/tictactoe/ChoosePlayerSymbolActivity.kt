package com.sct.tictactoe

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import java.util.Objects

class ChoosePlayerSymbolActivity : AppCompatActivity(), OnTouchListener {
    //    private ImageView BackBtn;
    private var CrossImg: ImageView? = null
    private var CrossRadioImg: ImageView? = null
    private var CircleImg: ImageView? = null
    private var CircleRadioImg: ImageView? = null
    private var ContinueBtn: Button? = null

    var PICK_SIDE: Int = 0
    private var playerOne: String? = null
    private var playerTwo: String? = null

    private var adView: AdView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE) //will hide the title
        Objects.requireNonNull<ActionBar?>(getSupportActionBar()).hide() // hide the title bar
        this.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        ) //enable full screen
        setContentView(R.layout.activity_choose_player_symbol)

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

        playerOne = getIntent().getStringExtra("p1")
        playerTwo = getIntent().getStringExtra("p2")

        //        BackBtn = findViewById(R.id.pick_side_back_btn);
        CrossImg = findViewById<ImageView>(R.id.pickSideCrossImg)
        CircleImg = findViewById<ImageView>(R.id.pickSideCircleImg)
        CrossRadioImg = findViewById<ImageView>(R.id.pickSideCrossRadio)
        CircleRadioImg = findViewById<ImageView>(R.id.pickSideCircleRadio)

        ContinueBtn = findViewById<Button>(R.id.pickSideContinueBtn)

        // CrossRadioImg.setOnTouchListener(this);
        CrossRadioImg!!.setOnClickListener(View.OnClickListener { v: View? ->
            PICK_SIDE = 0
            CrossRadioImg!!.setImageResource(R.drawable.ic_radio_button_checked)
            CircleRadioImg!!.setImageResource(R.drawable.ic_radio_button_unchecked)
            CircleImg!!.setAlpha(0.3f)
            CrossImg!!.setAlpha(1.0f)
        })

        // CircleRadioImg.setOnTouchListener(this);
        CircleRadioImg!!.setOnClickListener(View.OnClickListener { v: View? ->
            PICK_SIDE = 1
            CircleRadioImg!!.setImageResource(R.drawable.ic_radio_button_checked)
            CrossRadioImg!!.setImageResource(R.drawable.ic_radio_button_unchecked)
            CrossImg!!.setAlpha(0.3f)
            CircleImg!!.setAlpha(1.0f)
        })

        //        BackBtn.setOnClickListener(v -> onBackPressed());
        ContinueBtn!!.setOnTouchListener(this)
        ContinueBtn!!.setOnClickListener(View.OnClickListener { v: View? ->
            val intent = Intent(this@ChoosePlayerSymbolActivity, GameBoardActivity::class.java)
            intent.putExtra("p1", playerOne)
            intent.putExtra("p2", playerTwo)
            intent.putExtra("ps", PICK_SIDE)
            startActivity(intent)
        })
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (v === ContinueBtn) {
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
