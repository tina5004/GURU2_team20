package com.example.guru2_team20

import android.annotation.SuppressLint
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class review_info : AppCompatActivity() {

    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase
    lateinit var tvName: TextView
    lateinit var tvReview: TextView
    lateinit var tvPossible: TextView

    lateinit var str_name: String
    lateinit var str_possible: String
    var bar: Int =0
    lateinit var str_review: String

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_info)

        tvName = findViewById(R.id.edtName)
        tvReview = findViewById(R.id.edtReview)
        tvPossible = findViewById(R.id.possible)

        val intent = intent
        str_name = intent.getStringExtra("intent_name").toString()

        dbManager = DBManager(this, "togedog", null, 1)
        sqlitedb = dbManager.readableDatabase

        var cursor: Cursor
        cursor = sqlitedb.rawQuery("SELECT * FROM togedog WHERE name = '"+str_name+"';", null)

        if(cursor.moveToNext()) {
            str_possible = cursor.getString(cursor.getColumnIndex("possible")).toString()
            str_review = cursor.getInt(cursor.getColumnIndex("review")).toString()
        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()

        tvName.text = str_name
        tvPossible.text = str_possible
        tvReview.text = str_review + "\n"
    }
}