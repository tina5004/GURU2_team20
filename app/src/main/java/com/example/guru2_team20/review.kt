package com.example.guru2_team20

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*

class review : AppCompatActivity() {

    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase

    lateinit var storeEdit: EditText
    lateinit var contentEdit: EditText

    //lateinit var ratingBar: RatingBar

    lateinit var orNot: RadioGroup
    lateinit var possible: RadioButton
    lateinit var impossible: RadioButton

    lateinit var regButton: Button

    lateinit var str_name: String
    lateinit var str_orNot: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review)

        setTitle("리뷰 작성하기")

        storeEdit = findViewById(R.id.storeEdit)
        contentEdit = findViewById(R.id.contentEdit)
        orNot = findViewById(R.id.orNot)
        possible = findViewById(R.id.possible)
        impossible = findViewById(R.id.impossible)
        //ratingBar = findViewById(R.id.ratingBar)
        regButton = findViewById(R.id.regButton)

        dbManager = DBManager(this, "togedog", null, 1)

        regButton.setOnClickListener {
            var str_name: String = storeEdit.text.toString()
            var str_content: String = contentEdit.text.toString()
            var str_orNot: String = ""

            if(orNot.checkedRadioButtonId == R.id.possible) {
                str_orNot = possible.text.toString()
            }
            if(orNot.checkedRadioButtonId == R.id.impossible) {
                str_orNot = impossible.text.toString()
            }

            sqlitedb = dbManager.writableDatabase
            sqlitedb.execSQL("INSERT INTO togedog VALUES ('"+ str_name +"', '"+ str_orNot +"', '"+ str_content +"');")
            sqlitedb.close()

            val intent = Intent(this, review_info::class.java)
            intent.putExtra("intent_name", str_name)
            startActivity(intent)
        }
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