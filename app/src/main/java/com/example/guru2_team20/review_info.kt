package com.example.guru2_team20

import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView

class review_info : AppCompatActivity() {

    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase
    lateinit var tvName: TextView
    lateinit var tvOrnot: TextView
    lateinit var tvContent: TextView

    /*별점값을 textview로 보이기*/
    lateinit var tvRatingBar: TextView

    lateinit var str_name: String
    lateinit var str_orNot: String
    lateinit var str_content: String
    lateinit var str_ratingBar: String
    var bar: Int =0

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_info)

        setTitle("DETAIL_INFO")

        tvName = findViewById(R.id.storeEdit)
        tvOrnot = findViewById(R.id.orNot)
        tvContent = findViewById(R.id.contentEdit)
        tvRatingBar = findViewById(R.id.ratingBar)

        val intent = intent
        str_name = intent.getStringExtra("intent_name").toString()

        dbManager = DBManager(this, "togedog", null, 1)
        sqlitedb = dbManager.readableDatabase

        var cursor: Cursor
        cursor = sqlitedb.rawQuery("SELECT * FROM togedog WHERE storeName = '"+str_name+"';", null)

        if(cursor.moveToNext()) {
            str_orNot = cursor.getString(cursor.getColumnIndex("orNot")).toString()
            str_content = cursor.getString(cursor.getColumnIndex("content")).toString()
            str_ratingBar = cursor.getString(cursor.getColumnIndex("ratingBar")).toString()

        }

        cursor.close()
        sqlitedb.close()
        dbManager.close()

        tvName.text = str_name
        tvOrnot.text = str_orNot
        tvRatingBar.text = str_ratingBar
        tvContent.text = str_content + "\n"
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