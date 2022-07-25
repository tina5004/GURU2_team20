package com.example.guru2_team20

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView

class profile : AppCompatActivity() {
    lateinit var profileImageView: ImageView
    lateinit var registButton: Button
    lateinit var visitButton: Button
    lateinit var reviewButton: Button
    lateinit var logInOutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        profileImageView = findViewById(R.id.profileImageView)
        registButton = findViewById(R.id.registButton)
        visitButton = findViewById(R.id.visitButton)
        reviewButton = findViewById(R.id.reviewButton)
        logInOutButton = findViewById(R.id.logInOutButton)

        setTitle("나의 프로필")
    }
}