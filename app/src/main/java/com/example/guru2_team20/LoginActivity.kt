package com.example.guru2_team20

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private var auth: FirebaseAuth? = null
    lateinit var edtId: EditText
    lateinit var edtPwd: EditText
    lateinit var btnLogin: Button
    lateinit var btnJoin: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setTitle("로그인");

        edtId = findViewById(R.id.EdtId)
        edtPwd = findViewById(R.id.EdtPwd)
        btnLogin = findViewById(R.id.btnLogin)
        btnJoin = findViewById(R.id.btnJoin)

        auth = Firebase.auth

        btnJoin.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btnLogin.setOnClickListener {
            signIn(edtId.text.toString(), edtPwd.text.toString())
        }
    }

    // 로그인 유지
   /* public override fun onStart() {
        super.onStart()
        moveMainPage(auth?.currentUser)
   }
   */

    // 로그인
    private fun signIn(email: String, password: String) {

        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth?.signInWithEmailAndPassword(email, password)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            baseContext, "로그인에 성공 하였습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                        moveMainPage(auth?.currentUser)
                    } else {
                        Toast.makeText(
                            baseContext, "로그인에 실패 하였습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    // 유저정보 넘겨주고 메인 액티비티 호출
    fun moveMainPage(user: FirebaseUser?) {
       if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}