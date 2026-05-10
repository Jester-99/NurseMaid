package com.example.admin.nursemaid1;

import android.provider.BaseColumns;

/**
 * Created by ASUS on 2017/5/25.
 */
public class DBcontract {                                                                              //rid資料庫
    /* Inner class that defines the table contents */
    public static abstract class DBcol implements BaseColumns {
        public static final String TABLE_NAME = "rid";
        public static final String COLUMN_Rrgister_ID = "reid";
        public static final String COLUMN_Token = "reid";
    }
}
