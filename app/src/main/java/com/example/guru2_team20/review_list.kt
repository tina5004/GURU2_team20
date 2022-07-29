package com.example.guru2_team20
/*작성한 감상문 목록을 출력하는 화면과 관련 코드*/
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
import android.widget.Toast

class review_list : AppCompatActivity() {

    lateinit var dbManager: DBManager
    lateinit var sqlitedb: SQLiteDatabase
    lateinit var layout: LinearLayout

    //lateinit var str_name: String

    @SuppressLint("Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_review_list)

        setTitle("LIST")

        /*DB 내 데이터와 연동*/
        dbManager = DBManager(this, "togedog", null, 1)
        sqlitedb = dbManager.readableDatabase

        layout = findViewById(R.id.reviewList)

        /*togedog 테이블에 있는 데이터 불러오기*/
        var cursor: Cursor
        cursor = sqlitedb.rawQuery("SELECT * FROM togedog;", null)

        /*텍스트뷰로 등록했던 기록들 출력하기*/
        var num: Int = 0
        while (cursor.moveToNext()) {
            var str_name = cursor.getString(cursor.getColumnIndex("storeName")).toString()
            var str_orNot = cursor.getString(cursor.getColumnIndex("orNot")).toString()
            var str_ratingBar = cursor.getString(cursor.getColumnIndex("ratingBar")).toString()
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

            var tvRatingBar: TextView = TextView(this)
            tvRatingBar.text = str_ratingBar
            layout_item.addView(tvRatingBar)

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

    /*감상문 목록보기 : 등록 메뉴*/
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_review_info, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.action_reg -> { //새로운 글 등록 시 사용
                val intent = Intent(this, review::class.java)
                Toast.makeText(applicationContext, "글 추가하러 이동", Toast.LENGTH_SHORT).show()
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}