package com.example.admin.nursemaid1.dbcontrol;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2018/1/4.
 */

public class MyDBHelper extends SQLiteOpenHelper {                                                      //建立手機資料庫
    // 資料庫名稱
    public static final String DATABASE_NAME = "mydata.db";
    // 資料庫版本，資料結構改變的時候要更改這個數字，通常是加一
    public static final int VERSION = 1;
    // 資料庫物件，固定的欄位變數
    private static SQLiteDatabase database;

    public MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static SQLiteDatabase getDatabase(Context context) {
        if (database == null || !database.isOpen()) {
            database = new MyDBHelper(context, DATABASE_NAME,
                    null, VERSION).getWritableDatabase();
        }

        return database;
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DeviceCourseDAO.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // 刪除原有的表格
        // 待會再回來完成它
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS" + DeviceCourseDAO.TABLE_NAME );
        // 呼叫onCreate建立新版的表格
        onCreate(sqLiteDatabase);
    }
}
