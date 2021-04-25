package com.example.databasetest

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import java.lang.NullPointerException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val dbHelper = MyDatabaseHelper(this, "BookStore.db", 2)
        createDatabase.setOnClickListener {
            dbHelper.writableDatabase
        }

        addData.setOnClickListener {
            val db = dbHelper.writableDatabase
            val values1 = ContentValues().apply {
                //开始组装第一条数据
                put("name", "The Da Vinci Code")
                put("author", "Dan Brown")
                put("pages", 453)
                put("price", 18.23)
            }
            db.insert("Book", null, values1)    //出入第一条数据
            val values2 = ContentValues().apply {
                //开始组装第2条数据
                put("name", "The Lost Symbol")
                put("author", "Dan Brown")
                put("pages", 510)
                put("price", 32.4)
            }
            db.insert("Book", null, values2)
        }

        updateData.setOnClickListener {
            val db = dbHelper.writableDatabase
            val values = ContentValues()
            values.put("price", 10.11)
            db.update("Book", values, "name = ?", arrayOf("The Da Vinci Code"))     //第三个参数where
        }

        deleteData.setOnClickListener {
            val db = dbHelper.writableDatabase
            db.delete("Book", "pages > ?", arrayOf("500"))
        }

        queryData.setOnClickListener {
            val db = dbHelper.writableDatabase
            //查询Book所有的数据
            val cursor = db.query("Book", null, null, null, null, null, null)
            if (cursor.moveToFirst()) {
                do {
                    //遍历Cursor对象，取出数据并打印
                    val name = cursor.getString(cursor.getColumnIndex("name"))
                    val author = cursor.getString(cursor.getColumnIndex("author"))
                    val pages = cursor.getInt(cursor.getColumnIndex("pages"))
                    val price = cursor.getDouble(cursor.getColumnIndex("price"))
                    Log.d("MainActivity", "book name is $name")
                    Log.d("MainActivity", "book author is $author")
                    Log.d("MainActivity", "book pages is $pages")
                    Log.d("MainActivity", "book price is $price")
                } while (cursor.moveToNext())
            }
            cursor.close()
        }

        //android中事务的标准用法，首先beginTransaction开启事务，然后在一个异常捕获的代码块中执行具体的数据库操作
        //然后用setTransactionSuccessful表示成功，然后在finally中用endTransaction结束事务
        replaceData.setOnClickListener {
            val db = dbHelper.writableDatabase
            db.beginTransaction()       //开启事务
            try {
                db.delete("Book", null, null)
//                if (true) {
                    //手动抛出一个异常，让事务失败
//                    throw NullPointerException()
//                }
                val values = ContentValues().apply {
                    put("name", "Game of Thrones")
                    put("author", "George Martin")
                    put("pages", "720")
                    put("price", "20.85")
                }
                db.insert("Book", null, values)
                db.setTransactionSuccessful()       //事务已经成功
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                db.endTransaction()     //结束事务
            }
        }
    }
}