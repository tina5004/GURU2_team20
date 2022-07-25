package com.example.guru2_team20

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class LoginActivity : AppCompatActivity() {

    lateinit var idEdit: EditText
    lateinit var pwEdit: EditText
    lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setTitle("로그인");

        idEdit = findViewById(R.id.idEdit)
        pwEdit = findViewById(R.id.pwEdit)
        loginButton = findViewById(R.id.loginButton)

    }
}