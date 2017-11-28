package yuni.dispbbs;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "mydata.db"; // 資料庫檔案名稱
    private static final int DB_VERSION = 1; // 資料庫版本，資料結構改變的時候要加1

    //建構子
    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String sql = "CREATE TABLE IF NOT EXISTS boardHistory "
                + "(bi INTEGER PRIMARY KEY, name VARCHAR(20) NOT NULL, title VARCHAR(50),"
                + " timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP )";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS boardHistory");
        onCreate(db);
    }
}
