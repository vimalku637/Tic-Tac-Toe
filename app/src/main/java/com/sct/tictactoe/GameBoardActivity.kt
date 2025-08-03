package com.sct.tictactoe

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Animatable
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import com.mikhaellopez.circularimageview.CircularImageView
import com.mikhaellopez.circularimageview.CircularImageView.GradientDirection
import pl.droidsonroids.gif.GifImageView
import java.util.Objects

class GameBoardActivity : AppCompatActivity(), View.OnClickListener {
    // Initialize the boxes
    private var Box_1: ImageView? = null
    private var Box_2: ImageView? = null
    private var Box_3: ImageView? = null
    private var Box_4: ImageView? = null
    private var Box_5: ImageView? = null
    private var Box_6: ImageView? = null
    private var Box_7: ImageView? = null
    private var Box_8: ImageView? = null
    private var Box_9: ImageView? = null
    private var backBtn: ImageView? = null

    private var settingsGifView: GifImageView? = null
    private var playerOneImg: CircularImageView? = null
    private var playerTwoImg: CircularImageView? = null

    private var playerOneWins: TextView? = null
    private var playerTwoWins: TextView? = null
    private var playerOneName: TextView? = null
    private var playerTwoName: TextView? = null
    var vibrator: Vibrator? = null

    var dialog: Dialog? = null
    var drawdialog: Dialog? = null
    var quitdialog: Dialog? = null

    var playerOneWinCount: Int = 0
    var playerTwoWinCount: Int = 0

    var PICK_SIDE: Int = 0
    private var playerOne: String? = null
    private var playerTwo: String? = null

    // Initialize the player X and O with 0 and 1 respectively
    var Player_X: Int = 0
    var Player_0: Int = 1

    var storeActivePlayer: Int = 0
    var ActivePlayer: Int = 0

    // No player wins the game the isGameActive is true when the player X or O wins it will be false
    var isGameActive: Boolean = true

    // Initialize array with -1 when Player X or O fill click on the box it turn 0 and 1 respectively
    var filledPos: IntArray = intArrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1)

    private var adView: AdView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE) //will hide the title
        Objects.requireNonNull<ActionBar?>(getSupportActionBar()).hide() // hide the title bar
        this.getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        ) //enable full screen

        setContentView(R.layout.activity_game_board)

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

        dialog = Dialog(this)
        drawdialog = Dialog(this)
        quitdialog = Dialog(this)

        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator

        // link all the Boxes with Design (boxes in the activity_game.Xml  has the id so link with each Box)
        Box_1 = findViewById<ImageView>(R.id.img_1)
        Box_2 = findViewById<ImageView>(R.id.img_2)
        Box_3 = findViewById<ImageView>(R.id.img_3)
        Box_4 = findViewById<ImageView>(R.id.img_4)
        Box_5 = findViewById<ImageView>(R.id.img_5)
        Box_6 = findViewById<ImageView>(R.id.img_6)
        Box_7 = findViewById<ImageView>(R.id.img_7)
        Box_8 = findViewById<ImageView>(R.id.img_8)
        Box_9 = findViewById<ImageView>(R.id.img_9)

        backBtn = findViewById<ImageView>(R.id.offlineGameBackBtn)
        settingsGifView = findViewById<GifImageView>(R.id.offlineGameSettingGifView)

        playerOneImg = findViewById<CircularImageView>(R.id.playerOneImg)
        playerTwoImg = findViewById<CircularImageView>(R.id.playerTwoImg)

        playerOneName = findViewById<TextView>(R.id.playerOneNameText)
        playerTwoName = findViewById<TextView>(R.id.playerTwoNameText)
        playerOneWins = findViewById<TextView>(R.id.playerOneWinCountText)
        playerTwoWins = findViewById<TextView>(R.id.playerTwoWonText)

        // if user click on particular Box the tag basically value of box (Box_1 has vlaue 1,Box_2 has vlaue 2 ,... ) send to the onClick function
        Box_1!!.setOnClickListener(this)
        Box_2!!.setOnClickListener(this)
        Box_3!!.setOnClickListener(this)
        Box_4!!.setOnClickListener(this)
        Box_5!!.setOnClickListener(this)
        Box_6!!.setOnClickListener(this)
        Box_7!!.setOnClickListener(this)
        Box_8!!.setOnClickListener(this)
        Box_9!!.setOnClickListener(this)

        playerOneWins!!.setText(playerOneWinCount.toString())
        playerTwoWins!!.setText(playerTwoWinCount.toString())

        playerOne = getIntent().getStringExtra("p1")
        playerTwo = getIntent().getStringExtra("p2")
        PICK_SIDE = getIntent().getIntExtra("ps", 0)
        playerOneName!!.setText(playerOne)
        playerTwoName!!.setText(playerTwo)
        ActivePlayer = PICK_SIDE
        storeActivePlayer = PICK_SIDE

        val drawable = settingsGifView!!.getDrawable()
        if (drawable is Animatable) {
            (drawable as Animatable).stop()
        }

        if (PICK_SIDE == 0) {
            playerOneImg!!.borderWidth = 10f
            playerOneImg?.borderColorStart = Color.parseColor("#EB469A")
            playerOneImg?.borderColorEnd = Color.parseColor("#7251DF")

            playerOneImg!!.borderColorDirection = GradientDirection.TOP_TO_BOTTOM

            // Set Border
            playerTwoImg!!.borderWidth = 10f
            playerTwoImg?.borderColorStart = Color.parseColor("#F7A27B")
            playerTwoImg?.borderColorEnd = Color.parseColor("#FF3D00")
            playerTwoImg!!.borderColorDirection = GradientDirection.TOP_TO_BOTTOM

            playerTwoImg!!.setAlpha(0.6f)

            storeActivePlayer = 0
            ActivePlayer = 0
        } else if (PICK_SIDE == 1) {
            // Set Border
            playerOneImg!!.borderWidth = 10f
            playerOneImg?.borderColorStart = Color.parseColor("#F7A27B")
            playerOneImg?.borderColorEnd = Color.parseColor("#FF3D00")
            playerOneImg!!.borderColorDirection = GradientDirection.TOP_TO_BOTTOM

            playerTwoImg!!.borderWidth = 10f
            playerTwoImg?.borderColorStart = Color.parseColor("#EB469A")
            playerTwoImg?.borderColorEnd = Color.parseColor("#7251DF")

            playerOneImg!!.borderColorDirection = GradientDirection.TOP_TO_BOTTOM

            playerTwoImg!!.setAlpha(0.6f)
            storeActivePlayer = 1
            ActivePlayer = 1
        }

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
                val intent = Intent(this@GameBoardActivity, SettingsActivity::class.java)
                startActivity(intent)
            }, 750)
        })
        backBtn!!.setOnClickListener(View.OnClickListener { v: View? -> quitDialogFunction() })
    }

    override fun onClick(view: View) {
        // if isGameActive is false when the user click on button nothing can do and program exit from function
        if (!isGameActive) return

        val clickImg = findViewById<ImageView>(view.getId())
        // get the tag of button which user click
        val gettingTag = view.getTag().toString().toInt()

        // check the Active player  and checked whether it already with X or O
        // if Active player is X than set the text to X , set its color to red and filled position to 0
        // and change the Active player O
        if (ActivePlayer == Player_X && filledPos[gettingTag - 1] == -1) {
            if (MyServices.SOUND_CHECK) {
                val mp = MediaPlayer.create(this, R.raw.x)
                mp.start()
            }
            if (MyServices.VIBRATION_CHECK) {
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator!!.vibrate(
                        VibrationEffect.createOneShot(
                            200,
                            VibrationEffect.DEFAULT_AMPLITUDE
                        )
                    )
                } else {
                    vibrator!!.vibrate(200)
                }
            }

            if (PICK_SIDE == 0) {
                playerOneImg!!.setAlpha(0.6f)
                playerTwoImg!!.setAlpha(1.0f)
            } else if (PICK_SIDE == 1) {
                playerTwoImg!!.setAlpha(0.6f)
                playerOneImg!!.setAlpha(1.0f)
            }
            clickImg.setImageResource(R.drawable.ic_cross)

            storeActivePlayer = ActivePlayer
            ActivePlayer = Player_0
            val value = gettingTag - 1
            filledPos[value] = Player_X
        } else if (ActivePlayer == Player_0 && filledPos[gettingTag - 1] == -1) {
            if (MyServices.SOUND_CHECK) {
                val mp = MediaPlayer.create(this, R.raw.o)
                mp.start()
            }

            if (MyServices.VIBRATION_CHECK) {
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator!!.vibrate(
                        VibrationEffect.createOneShot(
                            200,
                            VibrationEffect.DEFAULT_AMPLITUDE
                        )
                    )
                } else {
                    vibrator!!.vibrate(200)
                }
            }

            if (PICK_SIDE == 0) {
                playerTwoImg!!.setAlpha(0.6f)
                playerOneImg!!.setAlpha(1.0f)
            } else if (PICK_SIDE == 1) {
                playerOneImg!!.setAlpha(0.6f)
                playerTwoImg!!.setAlpha(1.0f)
            }
            clickImg.setImageResource(R.drawable.ic_circle)

            storeActivePlayer = ActivePlayer
            ActivePlayer = Player_X
            val value = gettingTag - 1
            filledPos[value] = Player_0
        }

        // check the win condition
        checkForWin()

        if (isGameActive) {
            checkDraw()
        }
    }

    private fun checkForWin() {
        // Store all the Winning conditions in 2D array
        val winningPos = arrayOf<IntArray?>(
            intArrayOf(1, 2, 3),
            intArrayOf(4, 5, 6),
            intArrayOf(7, 8, 9),
            intArrayOf(1, 4, 7),
            intArrayOf(2, 5, 8),
            intArrayOf(3, 6, 9),
            intArrayOf(1, 5, 9),
            intArrayOf(3, 5, 7)
        )

        for (i in 0..7) {
            val val0 = winningPos[i]!![0]
            val val1 = winningPos[i]!![1]
            val val2 = winningPos[i]!![2]

            if (filledPos[val0 - 1] == filledPos[val1 - 1] && filledPos[val1 - 1] == filledPos[val2 - 1]) {
                if (filledPos[val0 - 1] != -1) {
                    //winner declare

                    if (storeActivePlayer == Player_X) {
                        if (PICK_SIDE == 0) {
                            playerOneWinCount = playerOneWinCount + 1
                            playerOneWins!!.setText(playerOneWinCount.toString())
                        }
                        if (PICK_SIDE == 1) {
                            playerTwoWinCount = playerTwoWinCount + 1
                            playerTwoWins!!.setText(playerTwoWinCount.toString())
                        }

                        if (val0 == 1 && val1 == 2 && val2 == 3) {
                            Box_1!!.setBackgroundResource(R.drawable.cross_background)
                            Box_2!!.setBackgroundResource(R.drawable.cross_background)
                            Box_3!!.setBackgroundResource(R.drawable.cross_background)
                        } else if (val0 == 4 && val1 == 5 && val2 == 6) {
                            Box_4!!.setBackgroundResource(R.drawable.cross_background)
                            Box_5!!.setBackgroundResource(R.drawable.cross_background)
                            Box_6!!.setBackgroundResource(R.drawable.cross_background)
                        } else if (val0 == 7 && val1 == 8 && val2 == 9) {
                            Box_7!!.setBackgroundResource(R.drawable.cross_background)
                            Box_8!!.setBackgroundResource(R.drawable.cross_background)
                            Box_9!!.setBackgroundResource(R.drawable.cross_background)
                        } else if (val0 == 1 && val1 == 4 && val2 == 7) {
                            Box_1!!.setBackgroundResource(R.drawable.cross_background)
                            Box_4!!.setBackgroundResource(R.drawable.cross_background)
                            Box_7!!.setBackgroundResource(R.drawable.cross_background)
                        } else if (val0 == 2 && val1 == 5 && val2 == 8) {
                            Box_2!!.setBackgroundResource(R.drawable.cross_background)
                            Box_5!!.setBackgroundResource(R.drawable.cross_background)
                            Box_8!!.setBackgroundResource(R.drawable.cross_background)
                        } else if (val0 == 3 && val1 == 6 && val2 == 9) {
                            Box_3!!.setBackgroundResource(R.drawable.cross_background)
                            Box_6!!.setBackgroundResource(R.drawable.cross_background)
                            Box_9!!.setBackgroundResource(R.drawable.cross_background)
                        } else if (val0 == 1 && val1 == 5 && val2 == 9) {
                            Box_1!!.setBackgroundResource(R.drawable.cross_background)
                            Box_5!!.setBackgroundResource(R.drawable.cross_background)
                            Box_9!!.setBackgroundResource(R.drawable.cross_background)
                        } else if (val0 == 3 && val1 == 5 && val2 == 7) {
                            Box_3!!.setBackgroundResource(R.drawable.cross_background)
                            Box_5!!.setBackgroundResource(R.drawable.cross_background)
                            Box_7!!.setBackgroundResource(R.drawable.cross_background)
                        }

                        val handler = Handler()
                        if (MyServices.SOUND_CHECK) {
                            val mp = MediaPlayer.create(this, R.raw.click)
                            mp.start()
                        }
                        handler.postDelayed(Runnable { celebrateDialog(0) }, 750)
                    } else if (storeActivePlayer == Player_0) {
                        if (PICK_SIDE == 0) {
                            playerTwoWinCount = playerTwoWinCount + 1
                            playerTwoWins!!.setText(playerTwoWinCount.toString())
                        }
                        if (PICK_SIDE == 1) {
                            playerOneWinCount = playerOneWinCount + 1
                            playerOneWins!!.setText(playerOneWinCount.toString())
                        }

                        if (val0 == 1 && val1 == 2 && val2 == 3) {
                            Box_1!!.setBackgroundResource(R.drawable.circle_background)
                            Box_2!!.setBackgroundResource(R.drawable.circle_background)
                            Box_3!!.setBackgroundResource(R.drawable.circle_background)
                        } else if (val0 == 4 && val1 == 5 && val2 == 6) {
                            Box_4!!.setBackgroundResource(R.drawable.circle_background)
                            Box_5!!.setBackgroundResource(R.drawable.circle_background)
                            Box_6!!.setBackgroundResource(R.drawable.circle_background)
                        } else if (val0 == 7 && val1 == 8 && val2 == 9) {
                            Box_7!!.setBackgroundResource(R.drawable.circle_background)
                            Box_8!!.setBackgroundResource(R.drawable.circle_background)
                            Box_9!!.setBackgroundResource(R.drawable.circle_background)
                        } else if (val0 == 1 && val1 == 4 && val2 == 7) {
                            Box_1!!.setBackgroundResource(R.drawable.circle_background)
                            Box_4!!.setBackgroundResource(R.drawable.circle_background)
                            Box_7!!.setBackgroundResource(R.drawable.circle_background)
                        } else if (val0 == 2 && val1 == 5 && val2 == 8) {
                            Box_2!!.setBackgroundResource(R.drawable.circle_background)
                            Box_5!!.setBackgroundResource(R.drawable.circle_background)
                            Box_8!!.setBackgroundResource(R.drawable.circle_background)
                        } else if (val0 == 3 && val1 == 6 && val2 == 9) {
                            Box_3!!.setBackgroundResource(R.drawable.circle_background)
                            Box_6!!.setBackgroundResource(R.drawable.circle_background)
                            Box_9!!.setBackgroundResource(R.drawable.circle_background)
                        } else if (val0 == 1 && val1 == 5 && val2 == 9) {
                            Box_1!!.setBackgroundResource(R.drawable.circle_background)
                            Box_5!!.setBackgroundResource(R.drawable.circle_background)
                            Box_9!!.setBackgroundResource(R.drawable.circle_background)
                        } else if (val0 == 3 && val1 == 5 && val2 == 7) {
                            Box_3!!.setBackgroundResource(R.drawable.circle_background)
                            Box_5!!.setBackgroundResource(R.drawable.circle_background)
                            Box_7!!.setBackgroundResource(R.drawable.circle_background)
                        }

                        val handler = Handler()
                        if (MyServices.SOUND_CHECK) {
                            val mp = MediaPlayer.create(this, R.raw.click)
                            mp.start()
                        }
                        handler.postDelayed(object : Runnable {
                            override fun run() {
                                celebrateDialog(1)
                            }
                        }, 750)
                    }
                    isGameActive = false
                }
            }
        }
    }

    fun checkDraw() {
        var check = true
        for (i in 0..8) {
            if (filledPos[i] == -1) {
                check = false
            }
        }
        if (check) {
            isGameActive = false
            if (MyServices.SOUND_CHECK) {
                val mp = MediaPlayer.create(this, R.raw.click)
                mp.start()
            }
            DrawDialogFunction()
        }
    }

    private fun celebrateDialog(player_check: Int) {
        dialog!!.setContentView(R.layout.celebrate_dialog)
        Objects.requireNonNull<Window?>(dialog!!.getWindow()).setBackgroundDrawable(
            ColorDrawable(
                Color.TRANSPARENT
            )
        )
        dialog!!.setCanceledOnTouchOutside(false)

        val animationView = dialog!!.findViewById<LottieAnimationView>(R.id.celebrate_animationView)
        val linearLayout = dialog!!.findViewById<LinearLayout>(R.id.container_1)
        val quitBtn = dialog!!.findViewById<Button>(R.id.offline_game_quit_btn)
        val continueBtn = dialog!!.findViewById<Button>(R.id.offline_game_continue_btn)
        val playerImg = dialog!!.findViewById<ImageView>(R.id.offline_game_player_img)

        val handler = Handler()
        handler.postDelayed(Runnable {
            animationView.setVisibility(View.GONE)
            linearLayout.setVisibility(View.VISIBLE)
            if (player_check == 0) {
                playerImg.setImageResource(R.drawable.ic_cross)
            } else if (player_check == 1) {
                playerImg.setImageResource(R.drawable.ic_circle)
            }
        }, 2300)

        quitBtn.setOnClickListener(View.OnClickListener { v: View? ->
            dialog!!.dismiss()
            val intent = Intent(this@GameBoardActivity, ChooseGamePlayModeActivity::class.java)
            startActivity(intent)
        })

        continueBtn.setOnClickListener(View.OnClickListener { v: View? ->
            dialog!!.dismiss()
            Restart()
        })
        dialog!!.show()
    }

    private fun DrawDialogFunction() {
        drawdialog!!.setContentView(R.layout.draw_dialog)
        drawdialog!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.setCanceledOnTouchOutside(false)

        val quitBtn = drawdialog!!.findViewById<Button>(R.id.offline_game_draw_quit_btn)
        val continueBtn = drawdialog!!.findViewById<Button>(R.id.offline_game_draw_continue_btn)

        quitBtn.setOnClickListener(View.OnClickListener { v: View? ->
            drawdialog!!.dismiss()
            val intent = Intent(this@GameBoardActivity, ChooseGamePlayModeActivity::class.java)
            startActivity(intent)
        })

        continueBtn.setOnClickListener(View.OnClickListener { v: View? ->
            drawdialog!!.dismiss()
            Restart()
        })
        drawdialog!!.show()
    }

    private fun Restart() {
        for (i in 0..8) {
            filledPos[i] = -1
        }

        Box_1!!.setBackgroundResource(0)
        Box_2!!.setBackgroundResource(0)
        Box_3!!.setBackgroundResource(0)
        Box_4!!.setBackgroundResource(0)
        Box_5!!.setBackgroundResource(0)
        Box_6!!.setBackgroundResource(0)
        Box_7!!.setBackgroundResource(0)
        Box_8!!.setBackgroundResource(0)
        Box_9!!.setBackgroundResource(0)

        Box_1!!.setImageResource(0)
        Box_2!!.setImageResource(0)
        Box_3!!.setImageResource(0)
        Box_4!!.setImageResource(0)
        Box_5!!.setImageResource(0)
        Box_6!!.setImageResource(0)
        Box_7!!.setImageResource(0)
        Box_8!!.setImageResource(0)
        Box_9!!.setImageResource(0)

        isGameActive = true
    }

    private fun quitDialogFunction() {
        quitdialog!!.setContentView(R.layout.quit_dialog)
        Objects.requireNonNull<Window?>(quitdialog!!.getWindow()).setBackgroundDrawable(
            ColorDrawable(
                Color.TRANSPARENT
            )
        )
        quitdialog!!.setCanceledOnTouchOutside(false)

        val quitBtn = quitdialog!!.findViewById<Button>(R.id.quit_btn)
        val continueBtn = quitdialog!!.findViewById<Button>(R.id.continue_btn)

        quitBtn.setOnClickListener(View.OnClickListener { v: View? ->
            quitdialog!!.dismiss()
            val intent = Intent(this@GameBoardActivity, ChooseGamePlayModeActivity::class.java)
            startActivity(intent)
        })

        continueBtn.setOnClickListener(View.OnClickListener { v: View? -> quitdialog!!.dismiss() })
        quitdialog!!.show()
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
}
