package com.example.guru2_team20
/*앱 실행 시 3초간 팀 로고가 그려진 화면 출력*/
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class introActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        var handler = Handler()
        handler.postDelayed( {
            var intent = Intent( this, LoginActivity::class.java)
            startActivity(intent)
        }, 3000)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}