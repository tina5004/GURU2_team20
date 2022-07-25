package com.example.guru2_team20

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView

class review_list : AppCompatActivity() {

    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase
    lateinit var layout: LinearLayout

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_list)

        setTitle("내가 쓴 후기")

        dbManager = DBManager(this, "togedog", null, 1)
        sqlitedb = dbManager.readableDatabase

        layout = findViewById(R.id.reviewList)
        var cursor: Cursor
        cursor = sqlitedb.rawQuery("SELECT * FROM togedog;", null)


        var num: Int = 0
        while(cursor.moveToNext()) {
            var str_name = cursor.getString(cursor.getColumnIndex("name")).toString()
            var str_possible = cursor.getString(cursor.getColumnIndex("possible")).toString()
            var bar = cursor.getString(cursor.getColumnIndex("bar"))
            var str_review = cursor.getString(cursor.getColumnIndex("review")).toString()

            var layout_item: LinearLayout = LinearLayout(this)
            layout_item.orientation = LinearLayout.VERTICAL
            layout_item.setPadding(20, 10, 20, 10)
            layout_item.id = num
            layout_item.setTag(str_name)

            var tvName: TextView = TextView(this)
            tvName.text = str_name
            tvName.textSize = 30F
            tvName.setBackgroundColor(Color.LTGRAY)
            layout_item.addView(tvName)

            var tvPossible: TextView = TextView(this)
            tvPossible.text = str_possible
            layout_item.addView(tvPossible)

            var tvBar: TextView = TextView(this)
            tvBar.text = bar.toString()
            layout_item.addView(tvBar)

            var tvReview: TextView = TextView(this)
            tvReview.text = str_review
            layout_item.addView(tvReview)

            layout_item.setOnClickListener {
                val intent = Intent(this, review_info::class.java)
                intent.putExtra("intent_name", str_name)
                startActivity(intent)
            }

            layout.addView(layout_item)
            num++

        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()

    }
}