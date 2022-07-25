package com.example.guru2_team20

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*

class review : AppCompatActivity() {

    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase
    lateinit var btnRegist: Button

    lateinit var edtName: EditText
    lateinit var edtReview: EditText

    lateinit var rg_possible: RadioGroup
    lateinit var rb_possible_y: RadioButton
    lateinit var rb_possible_n: RadioButton

    lateinit var bar: TextView
    lateinit var ratingBar: RatingBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        setTitle("리뷰 작성하기")

        btnRegist = findViewById(R.id.btnRegister)
        edtName = findViewById(R.id.edtName)
        edtReview = findViewById(R.id.edtReview)
        rg_possible = findViewById(R.id.possible)
        rb_possible_y = findViewById(R.id.yes)
        rb_possible_n = findViewById(R.id.no)
        bar = findViewById(R.id.bar)
        ratingBar = findViewById(R.id.ratingBar)

        dbManager = DBManager(this, "togedog", null, 1)

        btnRegist.setOnClickListener {
            var str_name: String = edtName.text.toString()
            var str_review: String = edtReview.text.toString()
            var str_possible: String = ""

            if (rg_possible.checkedRadioButtonId == R.id.yes) {
                str_possible = rb_possible_y.text.toString()
            }
            if (rg_possible.checkedRadioButtonId == R.id.no) {
                str_possible = rb_possible_n.text.toString()
            }

            sqlitedb = dbManager.writableDatabase
            sqlitedb.execSQL("INSERT INTO togedog VALUES ('"+ str_name +"', '"+ str_possible +"', '"+ str_review +"');")
            sqlitedb.close()

            val intent = Intent(this, review_info::class.java)
            intent.putExtra("intent_name", str_name)
            startActivity(intent)
        }
    }
}