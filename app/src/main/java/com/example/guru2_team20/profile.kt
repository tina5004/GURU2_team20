package com.example.guru2_team20

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class profile : AppCompatActivity() {
    lateinit var profileImageView: ImageView
    lateinit var registButton: Button
    //lateinit var visitButton: Button
    lateinit var reviewButton: Button
    lateinit var logOutButton: Button

    private var auth : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = Firebase.auth

        profileImageView = findViewById(R.id.profileImageView)
        registButton = findViewById(R.id.registButton)
        //visitButton = findViewById(R.id.visitButton)
        reviewButton = findViewById(R.id.reviewButton)
        logOutButton = findViewById(R.id.logOutButton)

        setTitle("마이 페이지")


        // 로그아웃
        logOutButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            auth?.signOut()
        }




    }









}