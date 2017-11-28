package yuni.dispbbs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

//把存取DB的操作包裝成類別
public class BoardHistoryDB {
    private SQLiteDatabase db;
    final String TABLE_NAME = "boardHistory"; //資料表名稱
    final int MAX_LENGTH = 20; //限制最多幾筆資料

    //建構子
    public BoardHistoryDB(Context context){
        DBHelper dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    //關閉資料庫
    public void close(){
        db.close();
    }

    //取得資料的筆數
    public int getCount(){
        int count = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(bi) FROM " + TABLE_NAME, null);
        if(cursor.moveToNext()){//取得後 idx 會在 -1, 必須 moveToNext 移動到第 0 筆, 接著在 get idx = 0 的參數
            count = cursor.getInt(0);
        }
        cursor.close();
        return  count;
    }

    //新增一個瀏覽過的看板
    public void add(int bi, String name, String title){
        String where = "bi = " + bi;
        Cursor cursor = db.query(TABLE_NAME, null, where, null, null, null, null);
        if(cursor.moveToFirst()){//若已有存過這個看板
            db.delete(TABLE_NAME, where, null);
        }
        cursor.close();

        if(getCount() >= MAX_LENGTH){ //記錄的看板到達上限
            String orderBy = "timestamp ASC";
            cursor = db.query(TABLE_NAME, null, null, null, null, null, orderBy);
            if(cursor.moveToFirst()){
                int first_bi = cursor.getInt(0);
                db.delete(TABLE_NAME, "bi = " + first_bi, null); //刪掉第一筆記錄
            }
        }

        ContentValues values = new ContentValues();
        values.put("bi",bi);
        values.put("name",name);
        values.put("title",title);
        db.insert(TABLE_NAME,null,values);
    }

    //輸出瀏覽過的看板列表，依時間排序，由新到舊
    public ArrayList<String> getArrayList(){
        ArrayList<String> result = new ArrayList<>();
        String orderBy = "timestamp DESC";
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, orderBy);
        while (cursor.moveToNext()){
            result.add(cursor.getString(1) + " " + cursor.getString(2));
        }
        cursor.close();
        return result;
    }

    //清除所有資料
    public  void clear(){
        db.delete(TABLE_NAME, null, null);
    }


}
