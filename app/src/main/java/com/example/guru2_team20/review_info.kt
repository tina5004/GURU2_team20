package com.example.guru2_team20
/*작성한 감상문의 상세내용 화면과 관련 코드*/
import android.annotation.SuppressLint
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast

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
    //var bar: Int =0

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

        /*DB 내 데이터와 연동*/
        dbManager = DBManager(this, "togedog", null, 1)
        sqlitedb = dbManager.readableDatabase

        /*togedog 테이블에 있는 데이터 불러오기*/
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

    /*감상문 상세보기 : 등록, 삭제, 목록이동 메뉴*/
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_review_list, menu)
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
            R.id.action_list -> { //등록한 글 목록으로 이동 시 사용
                val intent = Intent(this, review_list::class.java)
                Toast.makeText(applicationContext, "목록으로 이동", Toast.LENGTH_SHORT).show()
                startActivity(intent)
                return true
            }
            R.id.action_remove -> { //해당 글 삭제 시 사용
                dbManager = DBManager(this, "togedog", null, 1)
                sqlitedb = dbManager.readableDatabase
                sqlitedb.execSQL("DELETE FROM togedog WHERE storeName = '"+ str_name +"';")
                sqlitedb.close()
                dbManager.close()

                val intent = Intent(this, review_list::class.java)
                Toast.makeText(applicationContext, "삭제 완료", Toast.LENGTH_SHORT).show()
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}