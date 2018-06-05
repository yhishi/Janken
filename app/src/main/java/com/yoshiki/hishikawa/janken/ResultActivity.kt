package com.yoshiki.hishikawa.janken

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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

        // コンピュータの手をランダム表示
        val comHand = (Math.random() * 3).toInt()
        when(comHand) {
            gu -> comHandImage.setImageResource(R.drawable.com_gu)
            choki -> comHandImage.setImageResource(R.drawable.com_choki)
            pa -> comHandImage.setImageResource(R.drawable.com_pa)
        }

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
    }
}
