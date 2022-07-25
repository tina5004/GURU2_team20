package com.example.guru2_team20

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RatingBar
import android.widget.TextView

class review : AppCompatActivity() {


    lateinit var bar: TextView
    lateinit var ratingBar: RatingBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        bar = findViewById(R.id.bar)
        ratingBar = findViewById(R.id.ratingBar)


        setTitle("리뷰 작성하기")


    }
}