package com.sct.tictactoe

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.Window
import android.view.WindowManager
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import java.util.Objects

class PlayWithFriendsActivity : AppCompatActivity(), OnTouchListener {
    private var playerOne: String? = null
    private var playerTwo: String? = null

    private var playerOneName: EditText? = null
    private var playerTwoName: EditText? = null
    private var playerOneButton: Button? = null
    private var playerTwoButton: Button? = null

    //    private ImageView BackBtn;
    private var playerOneLayout: LinearLayout? = null
    private var playerTwoLayout: LinearLayout? = null
    var isLayout: Boolean = true

    private var adView: AdView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE) //will hide the title
        Objects.requireNonNull<ActionBar?>(getSupportActionBar()).hide() // hide the title bar
        this.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        ) //enable full screen

        setContentView(R.layout.activity_play_with_friends)

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

        //        BackBtn = findViewById(R.id.player_names_back_btn);
        playerOneName = findViewById<EditText>(R.id.playerOneNameEditText)
        playerTwoName = findViewById<EditText>(R.id.playerTwoNameEditText)
        playerOneButton = findViewById<Button>(R.id.playerOneBtn)
        playerTwoButton = findViewById<Button>(R.id.playerTwoBtn)
        playerOneLayout = findViewById<LinearLayout>(R.id.playerOneLayout)
        playerTwoLayout = findViewById<LinearLayout>(R.id.playerTwoLayout)

        playerOneButton!!.setOnTouchListener(this)
        playerOneButton!!.setOnClickListener(View.OnClickListener { v: View? ->
            if (TextUtils.isEmpty(
                    playerOneName!!.getText().toString()
                )
            ) {
                Toast.makeText(getBaseContext(), "Enter Name", Toast.LENGTH_LONG).show()
            } else {
                isLayout = false
                playerOneLayout!!.setVisibility(View.GONE)
                playerTwoLayout!!.setVisibility(View.VISIBLE)
                slideUp(playerTwoLayout!!)
                playerOne = playerOneName!!.getText().toString()
            }
        })

        //        BackBtn.setOnClickListener(v -> onBackPressed());
        playerTwoButton!!.setOnTouchListener(this)
        playerTwoButton!!.setOnClickListener(View.OnClickListener { v: View? ->
            if (TextUtils.isEmpty(
                    playerTwoName!!.getText().toString()
                )
            ) {
                Toast.makeText(getBaseContext(), "Enter Name", Toast.LENGTH_LONG).show()
            } else {
                playerTwo = playerTwoName!!.getText().toString()
                val intent =
                    Intent(this@PlayWithFriendsActivity, ChoosePlayerSymbolActivity::class.java)
                intent.putExtra("p1", playerOne)
                intent.putExtra("p2", playerTwo)
                startActivity(intent)
            }
        })
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (isLayout) {
            if (v === playerOneButton) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setAlpha(0.5f)
                } else {
                    v.setAlpha(1f)
                }
            }
        } else if (!isLayout) {
            if (v === playerTwoButton) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setAlpha(0.5f)
                } else {
                    v.setAlpha(1f)
                }
            }
        }
        return false
    }

    // slide the view from below itself to the current position
    fun slideUp(view: View) {
        view.setVisibility(View.VISIBLE)
        val animate = TranslateAnimation(
            0f,  // fromXDelta
            0f,  // toXDelta
            view.getHeight().toFloat(),  // fromYDelta
            0f
        ) // toYDelta
        animate.setDuration(500)
        animate.setFillAfter(true)
        view.startAnimation(animate)
    }

    // slide the view from its current position to below itself
    fun slideDown(view: View) {
        val animate = TranslateAnimation(
            0f,  // fromXDelta
            0f,  // toXDelta
            0f,  // fromYDelta
            view.getHeight().toFloat()
        ) // toYDelta
        animate.setDuration(500)
        animate.setFillAfter(true)
        view.startAnimation(animate)
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