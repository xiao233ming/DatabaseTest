package com.example.mingming.databasetest;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new MyDatabaseHelper(this, "BookStore.db", null, 2);
        Button createDatabase = (Button) findViewById(R.id.create_database);
        Button addData = (Button) findViewById(R.id.add_data);
        Button updateData = (Button) findViewById(R.id.update_data);
        Button deleteButton = (Button) findViewById(R.id.delete_data);
        Button queryButton = (Button) findViewById(R.id.query_data);
        Button replaceData = (Button) findViewById(R.id.replace_data);

        replaceData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.beginTransaction();//开启事务
                try{
                    /*
                    我们在删除旧数据的操作完成后
                    手动抛出了一个 NullPointerException，
                    这样添加新数据的代码就执行不到了。不过由于事务的存
在，中途出现异常    会导致事务的失败，此时旧数据应该是删除不掉的。
                     */
                    db.delete("Book",null,null);
                    if (true){
                        //在这里手动抛出一个异常，让事务失败
                        throw new NullPointerException();
                    }
                    ContentValues values = new ContentValues();
                    values.put("name","Game of Thrones");
                    values.put("author","George Martin");
                    values.put("pages",720);
                    values.put("price",20.85);
                    db.insert("Boook",null,values);
                    db.setTransactionSuccessful();//事务已经执行成功

                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    db.endTransaction();//结束事务
                }
            }
        });

        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                //查询Book表中所有的数据
                Cursor cursor = db.query("Book",null,null,null,null,null,null);
                if(cursor.moveToFirst()){
                    do{
                        //遍历Cursor对象，取出数据并打印
                        String name = cursor.getString(cursor.getColumnIndex("name"));
                        String author = cursor.getString(cursor.getColumnIndex("author"));
                        int pages  = cursor.getInt(cursor.getColumnIndex("pages"));
                        double price = cursor.getDouble(cursor.getColumnIndex("price"));

                        Log.d(TAG,"book name is " + name);
                        Log.d(TAG,"book author is" + author);
                        Log.d(TAG,"book pages is" + pages);
                        Log.d(TAG,"book price is" + pages);

                    }while (cursor.moveToNext());
                }
                cursor.close();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.delete("Book", "pages > ?", new String[]{"500"});
            }
        });

        updateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("price", 10.99);
                db.update("Book", values, "name=?", new String[]{"" +
                        "The Da Vinci Code"});

            }
        });

        createDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHelper.getWritableDatabase();
            }
        });

        addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                //开始组装第一条数据
                values.put("name", "The Da Vinci Code");
                values.put("author", "Dan Brown");
                values.put("pages", 454);
                values.put("price", 16.96);
                db.insert("Book", null, values);//插入第一条数据
                values.clear();

                //开始组装第二条数据
                values.put("name", "The Lost Symbol");
                values.put("author", "Dan Brown");
                values.put("pages", 510);
                values.put("price", 16.95);
                db.insert("Book", null, values);//插入第二条数据
            }
        });

    }
}
