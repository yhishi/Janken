package com.yoshiki.hishikawa.janken

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {

    val gu = 0
    val choki = 1
    val pa = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        // じゃんけん画面でタップした手のIDを取得
        val id = intent.getIntExtra("MY_HAND", 0)

        val myHand: Int

        // プレイヤーの手を画像表示し、数字で変数に格納
        myHand = when(id) {
            R.id.gu -> {
                myHandimage.setImageResource(R.drawable.gu)
                gu
            }
            R.id.choki -> {
                myHandimage.setImageResource(R.drawable.choki)
                choki
            }
            R.id.pa -> {
                myHandimage.setImageResource(R.drawable.pa)
                pa
            }
            else -> gu
        }

        // コンピュータの手を過去の結果から決定
        val comHand = getComHand()

        // コンピュータの手をランダム表示
//        val comHand = (Math.random() * 3).toInt()
//        when(comHand) {
//            gu -> comHandImage.setImageResource(R.drawable.com_gu)
//            choki -> comHandImage.setImageResource(R.drawable.com_choki)
//            pa -> comHandImage.setImageResource(R.drawable.com_pa)
//        }

        // 勝敗判定
        // ロジック （コンピュータの手 - プレイヤーの手 + 3）% 3 = 0:引き分け 1:勝ち 2:負け
        val gameResult = (comHand - myHand + 3) % 3
        when (gameResult) {
            0 -> resultLabel.setText(R.string.result_draw)
            1 -> resultLabel.setText(R.string.result_win)
            2 -> resultLabel.setText(R.string.result_lose)
        }

        // 戻るボタンをタップした時はじゃんけん画面に戻る
        backButton.setOnClickListener { finish() }

        // 結果を保存
        saveJankenData(myHand, comHand, gameResult)
    }

    /**
     * saveJankenData
     *
     * １回ごとのじゃんけんごとの手と結果を保存する
     */
    private fun saveJankenData(myHand: Int, comHand: Int, gameResult: Int) {

        // 保存されている結果データ（共有プリファレンス）を取得

        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val gameCount = pref.getInt("GAME_COUNT", 0)
        val winningStreakCount = pref.getInt("WINNING_STREAK_COUNT", 0)
        val lastComHand = pref.getInt("LAST_COM_HAND", 0)
        val lastGameresult = pref.getInt("GAME_RESULT", -1)

        // 結果の書き込み
        val editor = pref.edit()
        editor.putInt("GAME_COUNT", gameCount + 1)
                .putInt("WINNING_STREAK_COUNT",
                        if(lastGameresult == 2 && gameResult == 2)
                            winningStreakCount + 1
                        else
                            0)
                .putInt("LAST_MY_HAND", myHand)
                .putInt("LAST_COM_HAND", comHand)
                .putInt("BEFORE_LAST_HAND", lastComHand)
                .putInt("GAME_RESULT", gameResult)
                .apply()
    }

    /**
     * getComHand
     *
     * 過去のじゃんけんの結果からコンピュータの手を決定する
     */
    private fun getComHand(): Int {
        var hand = (Math.random() * 3).toInt()
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val gameCount = pref.getInt("GAME_COUNT", 0)
        val winningStreakCount = pref.getInt("WINNING_STREAK_COUNT", 0)
        val lastMyHand = pref.getInt("LAST_MY_HAND", 0)
        val lastComHand = pref.getInt("LAST_COM_HAND", 0)
        val beforeLastComHand = pref.getInt("BEFORE_LAST_COM_HAND", 0)
        val gameResult = pref.getInt("GAME_RESULT", -1)

        if (gameCount == 1) {
            if (gameResult == 2) {
                // 前回の勝負が1回目で、コンピュータが勝った場合、
                // コンピュータは次に出す手を変える
                while (lastComHand == hand) {
                    hand = (Math.random() * 3).toInt()
                }
            } else if (gameResult == 1) {
                // 前回の勝負が1回目で、コンピュータが負けた場合
                // 相手の出した手に勝つ手を出す
                hand = (lastMyHand - 1 + 3) % 3
            }
        } else if (winningStreakCount > 0) {
            if (beforeLastComHand == lastComHand) {
                // 同じ手で連勝した場合は手を変える
                while (lastComHand == hand) {
                    hand = (Math.random() * 3).toInt()
                }
            }
        }
        return hand
    }
}
