package com.example.admin.nursemaid1;

import android.app.Activity;
import android.content.SharedPreferences;

import com.example.admin.nursemaid1.MainActivity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

class CAction {                                                                //寫檔，目前看不出用途
    SharedPreferences settings;
    Activity activity;

    String FileName="";

    public CAction(MainActivity activity) {
        this.activity = activity;

        File file=new File("/sdcard/BT40_Log/");
        if(!file.exists()){      //如果資料夾不存在
            file.mkdir();        //建立資料夾
        }

        Calendar c= Calendar.getInstance();
        int[] nowtime=new int[]{c.get(Calendar.MONTH)+1,c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE)};
        FileName="Log_";
        for(int i=0;i<nowtime.length;i++){
            if(nowtime[i]<10){
                FileName+="0"+ String.valueOf(nowtime[i]);
            }else {
                FileName+= String.valueOf(nowtime[i]);
            }
            if(i==1){
                FileName+="_";
            }
        }
        FileName+=".txt";
        //Log.e("TESTLOG",FileName);

        FileWriter fw=null;
        try {
            fw=new FileWriter("/sdcard/BT40_Log/"+FileName,false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        settings = activity.getSharedPreferences("Name",0);
        settings.edit().putString("FileName",FileName).commit();
    }
}

