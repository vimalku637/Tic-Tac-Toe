package com.sct.tictactoe

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.graphics.drawable.Animatable
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorCompat
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import pl.droidsonroids.gif.GifImageView

class ChooseGamePlayModeActivity : AppCompatActivity(), OnTouchListener {
    var SCREEN_SIZE: Int = 0
    var SET_TRANSLATE: Int = 0
    private val animationStarted = false
    private var settingsGifView: GifImageView? = null
    private var WithAFriendBtn: Button? = null
    private var WithAi: Button? = null

    private var adView: AdView? = null

    /**
     * Checking for app updates
     */
    private var appUpdateManager: AppUpdateManager? = null
    private var installStateUpdatedListener: InstallStateUpdatedListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        )
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_game_play_mode)

        // Initialize the Mobile Ads SDK
        MobileAds.initialize(
            this,
            OnInitializationCompleteListener { initializationStatus: InitializationStatus? -> })

        // Set test device IDs
//        RequestConfiguration configuration = new RequestConfiguration.Builder()
//                .setTestDeviceIds(Arrays.asList("B4C405009710ADFB5DB53F28D42F48A7"))
//                .build();
//        MobileAds.setRequestConfiguration(configuration);

        // Find the AdView and load the ad
        adView = findViewById<AdView?>(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        adView!!.loadAd(adRequest)

        settingsGifView = findViewById<GifImageView>(R.id.settingGifViewOfflineMenu)
        WithAFriendBtn = findViewById<Button>(R.id.btnPlayWithFriendMenu)
        WithAi = findViewById<Button>(R.id.btnPlayWithAiMenu)

        val drawable = settingsGifView!!.getDrawable()
        if (drawable is Animatable) {
            (drawable as Animatable).stop()
        }

        SCREEN_SIZE = getScreenResolution(this)

        if (SCREEN_SIZE > 1500) {
            SET_TRANSLATE = -560
        } else if (SCREEN_SIZE <= 1500) {
            SET_TRANSLATE = -300
        }

        WithAFriendBtn!!.setOnTouchListener(this)
        WithAFriendBtn!!.setOnClickListener(View.OnClickListener { v: View? ->
            val intent =
                Intent(this@ChooseGamePlayModeActivity, PlayWithFriendsActivity::class.java)
            startActivity(intent)
        })

        WithAi!!.setOnTouchListener(this)
        WithAi!!.setOnClickListener(View.OnClickListener { v: View? ->
            val intent = Intent(this@ChooseGamePlayModeActivity, PlayWithAIActivity::class.java)
            startActivity(intent)
        })

        settingsGifView!!.setOnClickListener(View.OnClickListener { v: View? ->
            val drawable1 = settingsGifView!!.getDrawable()
            if (drawable1 is Animatable) {
                (drawable1 as Animatable).start()
            }
            val handler = Handler()
            handler.postDelayed(Runnable {
                val drawable11 = settingsGifView!!.getDrawable()
                if (drawable11 is Animatable) {
                    (drawable11 as Animatable).stop()
                }
                val intent = Intent(this@ChooseGamePlayModeActivity, SettingsActivity::class.java)
                startActivity(intent)
            }, 750)
        })

        /**
         * checking for update aap
         */
        checkingForAppUpdate()
    }

    private fun checkingForAppUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext())

        installStateUpdatedListener = InstallStateUpdatedListener { state: InstallState? ->
            if (state!!.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackBarForCompleteUpdate(this, appUpdateManager)
            } else if (state.installStatus() == InstallStatus.INSTALLED) {
                removeInstallStateUpdateListener()
            } else {
                Log.d("TAG", "InstallStateUpdatedListener: " + state.installStatus())
            }
        }

        appUpdateManager!!.registerListener(installStateUpdatedListener!!)
        checkUpdate()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == FLEXIBLE_APP_UPDATE_REQ_CODE) {
            when (resultCode) {
                RESULT_CANCELED -> Log.d(
                    "TAG",
                    "onActivityResult: Update canceled by user! Result Code: " + resultCode
                )

                RESULT_OK -> Log.d(
                    "TAG",
                    "onActivityResult: Update success! Result Code: " + resultCode
                )

                else -> checkUpdate()
            }
        }
    }

    private fun checkUpdate() {
        val appUpdateInfoTask = appUpdateManager!!.getAppUpdateInfo()

        appUpdateInfoTask.addOnSuccessListener(OnSuccessListener { appUpdateInfo: AppUpdateInfo? ->
            if (appUpdateInfo!!.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                startUpdateFlow(appUpdateInfo)
            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackBarForCompleteUpdate(this, appUpdateManager)
            }
        })
    }

    private fun startUpdateFlow(appUpdateInfo: AppUpdateInfo) {
        try {
            if (appUpdateManager != null) {
                appUpdateManager!!.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.FLEXIBLE,
                    this,
                    FLEXIBLE_APP_UPDATE_REQ_CODE
                )
            }
        } catch (e: SendIntentException) {
            e.printStackTrace()
        }
    }

    private fun removeInstallStateUpdateListener() {
        if (appUpdateManager != null && installStateUpdatedListener != null) {
            appUpdateManager!!.unregisterListener(installStateUpdatedListener!!)
        }
    }

    override fun onStop() {
        super.onStop()
        removeInstallStateUpdateListener()
    }

    private fun popupSnackBarForCompleteUpdate(
        context: Context,
        appUpdateManager: AppUpdateManager?
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("App Update Available")
        builder.setMessage("A new version of the app is ready to install. Would you like to update now?")

        builder.setCancelable(false)

        builder.setPositiveButton(
            "Install Now",
            DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
                if (appUpdateManager != null) {
                    appUpdateManager.completeUpdate()
                }
                dialog!!.dismiss()
            })

        builder.setNegativeButton(
            "Later",
            DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int -> dialog!!.dismiss() })

        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun getScreenResolution(context: Context): Int {
        val wm = context.getSystemService(WINDOW_SERVICE) as WindowManager
        val display = wm.getDefaultDisplay()
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        val width = metrics.widthPixels
        val height = metrics.heightPixels

        return height
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (!hasFocus || animationStarted) {
            return
        }
        animate()
        super.onWindowFocusChanged(hasFocus)
    }

    override fun onPointerCaptureChanged(hasCapture: Boolean) {
    }

    private fun animate() {
        val logoImageView = findViewById<ImageView>(R.id.imgLogoOfflineMenu)
        val container = findViewById<ViewGroup>(R.id.containerOfflineMenu)

        ViewCompat.animate(logoImageView)
            .translationY(SET_TRANSLATE.toFloat())
            .setStartDelay(STARTUP_DELAY.toLong())
            .setDuration(ANIM_ITEM_DURATION.toLong()).setInterpolator(
                DecelerateInterpolator(1.2f)
            ).start()

        for (i in 0..<container.getChildCount()) {
            val v = container.getChildAt(i)
            val viewAnimator: ViewPropertyAnimatorCompat?

            if (v !is Button) {
                viewAnimator = ViewCompat.animate(v)
                    .translationY(50f).alpha(1f)
                    .setStartDelay(((ITEM_DELAY * i) + 500).toLong())
                    .setDuration(1000)
            } else {
                viewAnimator = ViewCompat.animate(v)
                    .scaleY(1f).scaleX(1f)
                    .setStartDelay(((ITEM_DELAY * i) + 500).toLong())
                    .setDuration(500)
            }
            viewAnimator.setInterpolator(DecelerateInterpolator()).start()
        }
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (v === WithAFriendBtn) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.setAlpha(0.5f)
            } else {
                v.setAlpha(1f)
            }
        } else if (v === WithAi) {
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
        finish()
    }

    companion object {
        const val STARTUP_DELAY: Int = 300
        const val ANIM_ITEM_DURATION: Int = 1000
        const val ITEM_DELAY: Int = 300
        private const val FLEXIBLE_APP_UPDATE_REQ_CODE = 123
    }
}
