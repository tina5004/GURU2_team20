package com.example.guru2_team20

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView

class review_list : AppCompatActivity() {

    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase
    lateinit var layout: LinearLayout

    lateinit var str_name: String

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_list)

        setTitle("작성한 리뷰 목록")

        dbManager = DBManager(this, "togedog", null, 1)
        sqlitedb = dbManager.readableDatabase

        layout = findViewById(R.id.reviewList)

        var cursor: Cursor
        cursor = sqlitedb.rawQuery("SELECT * FROM togedog;", null)

        var num: Int = 0
        while (cursor.moveToNext()) {
            var str_name = cursor.getString(cursor.getColumnIndex("storeName")).toString()
            var str_orNot = cursor.getString(cursor.getColumnIndex("orNot")).toString()
            var str_content = cursor.getString(cursor.getColumnIndex("content")).toString()

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

            var tvOrnot: TextView = TextView(this)
            tvOrnot.text = str_orNot
            layout_item.addView(tvOrnot)


            var tvContent: TextView = TextView(this)
            tvContent.text = str_content
            layout_item.addView(tvContent)

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_review_list, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.action_reg -> {
                val intent = Intent(this, review::class.java)
                startActivity(intent)
                return true
            }
            R.id.action_list -> {
                val intent = Intent(this, review_list::class.java)
                startActivity(intent)
                return true
            }
            R.id.action_remove -> {
                dbManager = DBManager(this, "togedog", null, 1)
                sqlitedb = dbManager.readableDatabase
                sqlitedb.execSQL("DELETE FROM togedog WHERE storeName = '"+ str_name +"';")
                sqlitedb.close()
                dbManager.close()

                val intent = Intent(this, review_list::class.java)
                startActivity(intent)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}