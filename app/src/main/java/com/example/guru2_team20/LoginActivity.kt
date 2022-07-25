package com.example.guru2_team20

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    lateinit var EdtId : EditText
    lateinit var EdtPwd : EditText
    lateinit var btnLogin : Button
    lateinit var btnJoin : Button

    lateinit var myDBHelper: SQLiteOpenHelper
    lateinit var sqlDB : SQLiteDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setTitle("로그인");

        EdtId = findViewById(R.id.EdtId)
        EdtPwd = findViewById(R.id.EdtPwd)
        btnJoin = findViewById(R.id.btnJoin)
        btnLogin = findViewById(R.id.btnLogin)




    }
}