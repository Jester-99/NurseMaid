package com.example.admin.nursemaid1;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

import static com.example.admin.nursemaid1.SetupFragment.isC;

class SQLite extends SQLiteOpenHelper {                                                                 //溫度曲線圖的資料庫

    final static String DATABASE_NAME = "Nursemaid";
    SQLiteDatabase MyDb;  // 資料庫物件，固定的欄位變數

    final static String USER_NAME = "nursemaid";

    final static String TABLE_NAME_3 = "record_table";		 					//儲存使用者測量記錄的資料表
    final static String FIELD_ID_3 = "id";										//編號(第幾筆)
    final static String FIELD_USER_ID_3 = "user_id";								//使用者ID
    final static String FIELD_TEMPERATURE_3 = "record";
    final static String FIELD_YEAR_3 = "year";
    final static String FIELD_MONTH_3 = "month";
    final static String FIELD_DAY_3 = "day";
    final static String FIELD_HOUR_3 = "hour";
    final static String FIELD_MIN_3 = "min";
    final static String FIELD_SEC_3 = "sec";
    final String ADDITIONAL = "SC_";

    final static String TABLE_NAME_4 = "time_table";							//儲存使用者有量測記錄的小時的資料表
    final static String FIELD_ID_4 = "id";										//編號
    final static String FIELD_DATE_TIME_4 = "date_time";						//時間(年-月-日-時)

    static Context get_Context;

    public SQLite(Context context) {
        super(context, DATABASE_NAME, null, 1);
        get_Context  = context;
        ToNewUserTable(USER_NAME);
        ToNewUserTimeTable(USER_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        MyDb = db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void ToNewUserTable(String name){  //紀錄溫度、時間Table
        String sql = "CREATE TABLE "+name+"("+FIELD_ID_3+" INTEGER primary key autoincrement,"
                +FIELD_USER_ID_3+" text,"
                +FIELD_TEMPERATURE_3+" text,"  //溫度
                +FIELD_YEAR_3+" text,"
                +FIELD_MONTH_3+" text,"
                +FIELD_DAY_3+" text,"
                +FIELD_HOUR_3+" text,"
                +FIELD_MIN_3+" text,"
                +FIELD_SEC_3+" text)";
        SQLiteDatabase dbw = this.getWritableDatabase();
        try{
            dbw.execSQL(sql);
        } catch(Exception e){}

        dbw.close();
//		MyDb.execSQL(sql);
    }

    private void ToNewUserTimeTable(String name){   //時間Table
        name = TABLE_NAME_4+"_"+name;
        String sql = "CREATE TABLE "+name+"("+FIELD_ID_4+" INTEGER primary key autoincrement,"
                +FIELD_DATE_TIME_4+" text)";
        SQLiteDatabase dbw = this.getWritableDatabase();
        try{
            dbw.execSQL(sql);
        }catch(Exception e){}

        dbw.close();
//		MyDb.execSQL(sql);
    }

    public void ToInsertTimeRecord(String data){  //date_time
        String name = TABLE_NAME_4+"_"+USER_NAME;
        ContentValues cv = new ContentValues();
        SQLiteDatabase dbw = this.getWritableDatabase();
        cv.put(FIELD_DATE_TIME_4,data);
        Log.e("ToInsertTimeRecord",data);
        dbw.insert(name, null, cv);
        Log.e("ToInsertTimeRecord_cv", String.valueOf(cv));
        dbw.close();
    }

    public boolean isHaveTimeData(String data){
        String name = TABLE_NAME_4+"_"+USER_NAME;
        data = "'"+data+"'";
        Cursor cursor = null;                                           //從SQLite類別裡抓回來的資料是 Cursor 物件
        SQLiteDatabase dbr = this.getReadableDatabase();
        String where = FIELD_DATE_TIME_4+" = "+data;
        //Log.e("isHaveTimeData_where",where);
        cursor = dbr.query(name, null, where, null, null, null, null);    //query() 查詢指定的資料
        //所帶的參數依序分別是
        // 1.String table
        // 2.String[ ] colums
        // 3.String selection
        // 4.String[ ] selectionsArg
        // 5.Strint groupBy
        // 6.String having
        // 7.String orderBy
        // 8.String limit
        if(cursor.getCount()>0){  //取得資料表列數
            //Log.e("isHaveTimeData_count", String.valueOf(cursor.getCount()));
            return true;
        }
        return false;
    }

    public String ToGetTimeData(int n,String time){
        Log.e("ToGetTimeData","=================================");
        ArrayList<String> al = new ArrayList<String>();
        String name = TABLE_NAME_4+"_"+USER_NAME;

        Cursor cursor = null;
        SQLiteDatabase dbr = this.getReadableDatabase();
        cursor = dbr.query(name, null, null, null, null, null, null);

        for(int i=0;i<cursor.getCount();i++){
            cursor.moveToPosition(i);  //移到指定位置
            al.add(cursor.getString(1));  //add第二個欄位內容
        }
        Log.e("ToGetTimeData_al", String.valueOf(al));
        String data = null;
        switch (n){
            case 1:
                data = al.get(0);
                break;
            case 2:
                for(int s=0;s<al.size();s++){
                    Log.e("time",time);
                    if(time.equals(al.get(s))){
                        Log.e("time_al",time+"_"+al.get(s));
                        if(s==0 || al.size()==1) {
                            data = al.get(s);
                            new AlertDialog.Builder(get_Context)
                                    .setTitle("提示")
                                    .setMessage("前面已經沒有資料了唷！")
                                    .setPositiveButton("確定",new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    }).show();
                        }else{
                            data = al.get(s-1);
                            Log.e("ToGetTimeData_data", data);
                        }
                        break;
                    }
                }
                break;
            case 3:
                for(int i=0;i<al.size();i++){
                    if(time.equals(al.get(i))){
                        Log.e("time_al",time+"_"+al.get(i));
                        if(i==al.size()-1 || al.size()==1) {
                            data = al.get(i);
                            new AlertDialog.Builder(get_Context)
                                    .setTitle("提示")
                                    .setMessage("後面已經沒有資料了唷！")
                                    .setPositiveButton("確定",new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    }).show();
                        }else{
                            data = al.get(i+1);
                            Log.e("ToGetTimeData_data", data);
                        }
                        break;
                    }
                }
                break;
            case 4:
                data = al.get(al.size()-1);
                Log.e("al", String.valueOf(al.size())+"_"+data);
                break;
        }
        return data;
    }

    public ArrayList<ArrayList<String>> ToGetUserTT(String [] time) {
        Log.e("ToGetUserTT","=================================");
        Log.e("ToGetUserTT", String.valueOf(time));
        ArrayList<ArrayList<String>> UserTimeTemp = new ArrayList<ArrayList<String>>();
        ArrayList<String> UserTemp = new ArrayList<String>();
        ArrayList<String> UserTime = new ArrayList<String>();

        String UTime = "";
        String UTemp = "";
        String name = USER_NAME;
        Cursor cursor = null;
        SQLiteDatabase dbr = this.getReadableDatabase();
        String where = FIELD_YEAR_3 +"=" + Integer.parseInt(time[0]) +" and "+ FIELD_MONTH_3 +"="+ Integer.parseInt(time[1])+ " and "
                + FIELD_DAY_3 + "=" +Integer.parseInt(time[2]) + " and " + FIELD_HOUR_3 + "=" + Integer.parseInt(time[3]);
        Log.e("ToGetUserTT_where",where);
        cursor = dbr.query(name, null, where, null, null, null, null);
        int count = cursor.getCount();
        Log.e("ToGetUserTT_count", String.valueOf(count));
        while (cursor.moveToNext()){
            UTemp = cursor.getString(2);
            if(!isC){
                UTemp=Float.toString((float) (Float.parseFloat(UTemp) * (9.0 / 5.0) + 32.0));
                try{
                    UTemp=UTemp.substring(0, 5);
                }catch(Exception e){

                }
            }
            //Log.e("UTemp",UTemp);
            UserTemp.add(UTemp);
            UTime = cursor.getString(3) + "-" +  cursor.getString(4) + "-" +  cursor.getString(5)
                    + " " + cursor.getString(6) + ":" + cursor.getString(7) + ":" + cursor.getString(8);
            UserTime.add(UTime);
            //Log.e("UTime",UTime);
        }

        UserTimeTemp.add(UserTime);
        UserTimeTemp.add(UserTemp);
        //Log.e("ToGetUserTT_UTT", String.valueOf(UserTimeTemp));

        cursor.close();
        dbr.close();
        return UserTimeTemp;
    }

    public boolean IfData(){
        String name = TABLE_NAME_4+"_"+USER_NAME;
        Cursor cursor = null;
        SQLiteDatabase dbr = this.getReadableDatabase();
        cursor = dbr.query(name, null, null, null, null, null, null);
        Log.e("IfData", String.valueOf(cursor.getCount()));
        if(cursor.getCount()>0) {
            cursor.close();
            dbr.close();
            return true;
        }
        cursor.close();
        dbr.close();
        return false;
    }

    public void ToInsertRecord(String time,String temp) {
        String name = USER_NAME;
        String[] ymd_hms = time.split(" ");
        String[] y_m_d = ymd_hms[0].split("/");
        String[] h_m_s = ymd_hms[1].split(":");

        ContentValues cv = new ContentValues();
        SQLiteDatabase dbw = this.getWritableDatabase();

        cv.put(FIELD_USER_ID_3,name);
        cv.put(FIELD_TEMPERATURE_3,temp);
        cv.put(FIELD_YEAR_3,Integer.valueOf(y_m_d[0]));
        cv.put(FIELD_MONTH_3,Integer.valueOf(y_m_d[1]));
        cv.put(FIELD_DAY_3,Integer.valueOf(y_m_d[2]));
        cv.put(FIELD_HOUR_3,Integer.valueOf(h_m_s[0]));
        cv.put(FIELD_MIN_3,Integer.valueOf(h_m_s[1]));
        cv.put(FIELD_SEC_3,Integer.valueOf(h_m_s[2]));

        Log.e("cv", String.valueOf(cv));
        dbw.insert(name, null, cv);
        dbw.close();
    }

    public void toDeleteAllOldData(){
        String name = TABLE_NAME_4+"_"+USER_NAME;
        long aDayInMilliSecond = 60 * 60 * 24 * 1000 ;
        Calendar cal;
        Calendar cal2;
        SQLiteDatabase dbw=this.getWritableDatabase();
        ArrayList<String> tmpTime=new ArrayList<String>();

        tmpTime.clear();

        tmpTime = ToGetTimeALLData(5);
        for(int j=0;j<tmpTime.size();j++){
            String[] strTime=tmpTime.get(j).split("-");
            int tmpYear=Integer.parseInt(strTime[0]);
            int tmpMonth=Integer.parseInt(strTime[1]);
            int tmpDay=Integer.parseInt(strTime[2]);
            int tmpHour=Integer.parseInt(strTime[3]);
            Log.e("tmpTime",tmpYear+"/"+tmpMonth+"/"+tmpDay+"/"+tmpHour);
            cal2=Calendar.getInstance();
            cal2.set(tmpYear, tmpMonth-1, tmpDay, tmpHour, 0);
            cal=Calendar.getInstance();
            cal2.add(Calendar.MONTH, 2);
            long x=cal2.getTimeInMillis()-cal.getTimeInMillis();
            x=x/aDayInMilliSecond;

            if(x<0){
                dbw.delete(name,
                        FIELD_DAY_3+"="+Integer.toString(tmpDay)+" and "+FIELD_MONTH_3+"="+Integer.toString(tmpMonth)+
                                " and "+FIELD_YEAR_3+"="+Integer.toString(tmpYear), null);
//					int y=dbw.delete(tableName, FIELD_DATE_TIME_4+"="+tmpTime.get(j)+"", null);
                dbw.execSQL("delete from "+name+" where "+FIELD_DATE_TIME_4+"=?",
                        new Object[]{tmpTime.get(j)});
//					Log.e("deleteSize", y+", "+tmpTime.get(j));
            }else{
                break;
            }
        }
        dbw.close();
    }


    public ArrayList<String> ToGetTimeALLData(int n){
        Log.e("ToToGetTimeALLData","=================================");
        ArrayList<String> al = new ArrayList<String>();
        String name = TABLE_NAME_4+"_"+USER_NAME;

        Cursor cursor = null;
        SQLiteDatabase dbr = this.getReadableDatabase();
        cursor = dbr.query(name, null, null, null, null, null, null);

        for(int i=0;i<cursor.getCount();i++){
            cursor.moveToPosition(i);  //移到指定位置
            al.add(cursor.getString(1));  //add第二個欄位內容
        }
        Log.e("ToToGetTimeALLData_al", String.valueOf(al));
        ArrayList<String> data = null;
        switch (n){
            case 5:
                for(int i=0;i<al.size();i++){
                    data.add(al.get(i));
                }
                break;
        }
        return data;
    }
}

