package com.example.admin.nursemaid1;

import android.text.format.Time;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.acl.LastOwnerException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Kick {                                                                                         //踢被演算法
    String kickData="";
    int valueRS_CNT = 0;
    int valueRCAL_CNT = 0;
    int time_hr_min_sec;
    int times = 0;
    int count = 0;
    final int totalTime = 300;
    int timesOneHour = 0;
    int maxRCAL = 0;
    int minRCAL = Integer.MAX_VALUE;
    int minStanupCNT = Integer.MAX_VALUE;
    int oneFourDelta = 0;
    int delta = 0;
    boolean isKick = false;
    //isStandUp->是否正在踢被  isAlert->是否正在警示
    boolean isStandUp = false, isAlert = false, isTimeSet = false;
    //用來記錄"上一次"的CNT
    String recordCNT = "";
    ArrayList<Integer> historyCNT = new ArrayList<>();
    //delta的歷史紀錄
    ArrayList<Integer> CNTdata = new ArrayList<>();

    static String errorMsg = "";

    String NowKickLogTime = "";

    public Kick(){

        String state = MainActivity.settings.getString("state","normal");
//        switch (state) {
//            case "sleep":
//                time_hr_min_sec = 120;
//                break;
//            case "quick":
//                time_hr_min_sec = 4;
//                break;
//            case "normal":
//                time_hr_min_sec = 300;
//                break;
//        }
        time_hr_min_sec = 120;

        if (3600 % time_hr_min_sec > 0)
            timesOneHour = (3600 / time_hr_min_sec) + 1;
        else
            timesOneHour = 3600 / time_hr_min_sec;
        Log.e("CNT_state", String.valueOf(time_hr_min_sec)+state);
        Log.e("CNT_first count", String.valueOf(timesOneHour));


//        String[] KickDate = SplashActivity.KickLog_fileDateTime.split("_");
//        String[] KickTime = KickDate[2].split(":");
//        NowKickLogTime = KickDate[1] + "_" + KickTime[0] + KickTime[1];
//        Log.e("KickLogTime",NowKickLogTime);

    }
    public void kick(final double temperature, final double roomtemperature) {
        //minRCAL 預設 整數的最大值->為了讓收到的數字肯定比minRCAL小  ，minStanupCNT 同上
//      Log.e("CNT", valueRS_CNT+","+valueRCAL_CNT);
        final int temp_cal = (int)(roomtemperature * 150);     //室溫
        final int body_cal = (int) (temperature * 150); //體溫
        Log.e("CNT", body_cal+","+temp_cal);
//      Log.e("0617test1",getTime()+"kick");
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        final String t = decimalFormat.format(temperature);
        final String r = decimalFormat.format(roomtemperature);

        //計算在時間區間裡一共需要幾次
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileWriter fileWriter;

                Log.e("Kick setting",String.valueOf(MainActivity.settings.getBoolean("KickNoti",false)));

                if(!isTimeSet){
//                  Log.e("CNT0616test","INisTimeSet"+time_hr_min_sec);
                    if (totalTime % time_hr_min_sec > 0)
                        times = (totalTime / time_hr_min_sec) + 1;  //次數 = 時間區間/一次的秒數 ，除不盡則多加一次
                    else
                        times = totalTime / time_hr_min_sec;
                    //為了讓後面判斷可以在"次數額滿"的情況下就執行
                    times--;
                    //計算在一小時內一共需要幾次
                    count = times;
                }
                Log.e("0616test","times:"+times+"count:"+count);
                try {
                    //用來記錄"這一次"的CNT
                    String strNum;
                    int record = 0;
                    strNum = body_cal +","+temp_cal;
                    if(MainActivity.settings.getBoolean("KickNoti",false)){
                        doCNTcRecord(t+","+r+","+strNum);
                    }
                    //假裝收到一筆一筆的溫度(其實是讀一行一行)，以後要換成接收到CNT的部分
                    if (strNum != null) {
                        //切出"模組CNT"值，以後要換成拿到的CNT值
                        record = Integer.parseInt(strNum.split(",")[1]);
                        //第一次進入while迴圈抓取"最低點"，以後要換成第一次收到CNT抓取"最低點"
                        Log.e("0617test","recoed"+record+"strNum"+strNum);
                        if (timesOneHour * time_hr_min_sec >= 3600)
                            if(record > minRCAL){
                                minRCAL = minRCAL;
                            }else {
                                minRCAL = record;
                                Log.e("time_hr_min_sec0620:",time_hr_min_sec+"");
                            }
                        Log.e("0617test","minRCAL"+minRCAL+" Time:"+getTime());


                        //minRCAL = (record > minRCAL) ? minRCAL : record;  // 變數 = (boolean)?變數A:變數B  語法意思是如果boolean為true 變數 = 變數A ，如果boolean為false 變數 = 變數B
                        //如果未滿一小時
                        if (timesOneHour > 0) {
                            timesOneHour--;
                            isTimeSet = false;
                            //如果一小時到了
                        } else if (timesOneHour == 0) {
                            Log.e("CNT","In2");
                            //一小時後抓取"最高點"
                            timesOneHour--;
                            isTimeSet = true;
                            int RS_CNT = Integer.parseInt(strNum.split(",")[0]);
                            int RCAL_CNT = Integer.parseInt(strNum.split(",")[1]);
                            Log.e("CNT158",String.valueOf(RS_CNT));
                            if(RS_CNT > RCAL_CNT){
                                maxRCAL = RS_CNT;
                            }else {
                                maxRCAL = RCAL_CNT;
                            }

                            Log.e("CNTTTT",String.valueOf(maxRCAL));

                            //maxRCAL = (RS_CNT > RCAL_CNT) ? RS_CNT : RCAL_CNT;
                            System.out.println(maxRCAL);

                            //初始化delta為(最高點-最低點)/4
                            oneFourDelta = ((maxRCAL - minRCAL)/4);

                            delta = oneFourDelta;
                            historyCNT.add(delta);
                            System.out.println(oneFourDelta);
                            Log.e("0617test","oneFourDelta"+oneFourDelta);
                        }
                        if (count > 0) {
                            Log.e("CNT","In3");
                            count--;
                            CNTdata.add(record);
                        } else {
                            Log.e("CNT","In4");
                            if (CNTdata.size() > times)
                                CNTdata.remove(0);
                            CNTdata.add(record);
                            //5分鐘125門檻，判斷是否有踢被現象
                            if (getPeakDifferent(CNTdata, 0) > 80){
                                isStandUp = true;
                            }

                            //如果有踢被現象
                            if (isStandUp) {

                                if (Integer.parseInt(recordCNT.split(",")[1]) - Integer.parseInt(strNum.split(",")[1]) >= 0) {

                                } else if (oneFourDelta < (maxRCAL - Integer.parseInt(recordCNT.split(",")[1])) && delta < (maxRCAL - Integer.parseInt(recordCNT.split(",")[1]))) {
                                    if(MainActivity.settings.getBoolean("KickNoti",false)){
                                        doRecordKick("已關閉" );
                                    }
                                    isKick = false;
                                    historyCNT.add(delta);
                                    // delta = maxRCAL-Integer.parseInt(recordCNT.split(", ")[1].split(",")[2]);
                                    delta = maxRCAL-Integer.parseInt(recordCNT.split(",")[1]);
                                    //minStanupCNT = (minStanupCNT>Integer.parseInt(recordCNT.split(", ")[1].split(",")[2]))?Integer.parseInt(recordCNT.split(", ")[1].split(",")[2]):minStanupCNT;
                                    minStanupCNT = (minStanupCNT>Integer.parseInt(recordCNT.split(",")[1]))?Integer.parseInt(recordCNT.split(",")[1]):minStanupCNT;
                                    isStandUp = false;
                                }
                                //防止在"同一次踢被"溫度持續下降的情況下重複出現警告
                                if (isStandUp && (maxRCAL - record) >= delta) {
                                    if (!isKick) {
                                        //這個System.out.println要換成警示
                                        if(MainActivity.settings.getBoolean("KickNoti",false)){
                                            doRecordKick(kickData);
                                        }
                                        isKick = true;
                                    }else {
                                        if(MainActivity.settings.getBoolean("KickNoti",false)){
                                            doRecordKick("已通知" + kickData);
                                        }
                                    }
                                }
                                //如果沒有踢被現象，而且delta的歷史紀錄>0筆
                            } else if (historyCNT.size() > 1) {
                                if ((maxRCAL - record) <= delta) {
                                    //讓"目前的"delata跟"上一次"的delta取平均
                                    delta = ((maxRCAL-record) + historyCNT.get(historyCNT.size()-1))/3;
                                    historyCNT.remove(historyCNT.size()- historyCNT.size());
                                    System.out.println("!!!"+strNum+","+delta);
                                    historyCNT.add(delta);
                                }
                            }
                            if ((maxRCAL - record) < 0)
                                maxRCAL = record;
                        }

                        if(MainActivity.settings.getBoolean("KickNoti",false)){
                            doRecorddelta(delta+" ");
                        }

                        recordCNT = strNum;

                        if(MainActivity.settings.getBoolean("KickNoti",false)){
                            Log.e("CNTcount", String.valueOf(times));
                            Log.e("CNTrecord", String.valueOf(record));
                            Log.e("CNTtimesOneHour", String.valueOf(timesOneHour));
                            Log.e("CNToneFourDelta", String.valueOf(oneFourDelta));
                            Log.e("CNTDelta", String.valueOf(delta));
                            Log.e("CNTdata", String.valueOf(CNTdata.size()));
                            for(int i : CNTdata){
                                Log.e("CNTdata", String.valueOf(i));
                            }
                            Log.e("CNT最高點", String.valueOf(maxRCAL));
                            Log.e("CNT最低點", String.valueOf(minRCAL));
                            Log.e("CNT踢被最低CNT值", String.valueOf(minStanupCNT));
                        }
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

            }

        }).start();

    }

    private int getPeakDifferent(ArrayList<Integer> data, int mode) {
        int result = -1, different = 0, heightPosition = -1, heightPeak = Integer.MIN_VALUE, lowPosiotion = -1, lowPeak = Integer.MAX_VALUE;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i) > heightPeak) {
                heightPeak = data.get(i);
                heightPosition = i;
            }
            if (data.get(i) < lowPeak) {
                lowPeak = data.get(i);
                lowPosiotion = i;
            }
        }
        if (mode == 0) {
            //以最大值與最小值在陣列的位置去區分是"溫度上升"還是"溫度下降"，溫度下降為正整數
            result = (heightPosition - lowPosiotion > 0) ? (heightPeak - lowPeak) * -1 : (heightPeak - lowPeak);
        } else if (mode == 1) {
            //首項減去尾項
            result = data.get(0) - data.get(data.size() - 1);
        }
        Log.e("CNTresult ", String.valueOf(result));
        return result;

    }

    public static String getTime() {
        String time = "";
        Time t = new Time();

        t.setToNow();

        time = String.valueOf(t.hour) + ":";
        if (t.minute < 10)
            time += "0";
        time += String.valueOf(t.minute) + ":";
        if (t.second < 10)
            time += "0";
        time += String.valueOf(t.second);

        return time;

    }

    public void doCNTcRecord(String thing) {
        //輸出Log記錄
        Log.e("write","Kick_doCNTcRecord inla");
        FileWriter file = null;
        BufferedWriter bw;
//		String location="/sdcard/Smart Clothes BT Log/"user_name+".txt";
        try {
            file = new FileWriter(("/sdcard/Nursemaid/"+SplashActivity.todayfileName+"/KickLog/" + "Log_CNTchange_"+NowKickLogTime+".txt"), true);

        } catch (IOException e) {
        }

        bw = new BufferedWriter(file);

        String str = getTime() + ", " + thing;


        try {
            bw.write(str + "\r\n");
        } catch (IOException e) {
        }

        try {
            bw.close();
//			Toast.makeText(heartbluetooth.this, "匯出記錄", Toast.LENGTH_SHORT).show();
        } catch (IOException e1) {
        }
    }
    void doRecordKick(String thing) {
        //輸出Log記錄
        Log.e("write","Kick_doRecordKick inla");
        FileWriter file = null;
        BufferedWriter bw;

        //家時間
        try {
            file = new FileWriter(("/sdcard/Nursemaid/"+SplashActivity.todayfileName+"/KickLog/" + "Log_KICK_"+ NowKickLogTime +".txt"), true);
        } catch (IOException e) {
        }

        bw = new BufferedWriter(file);

        String str = getTime() + ", " + thing;

        try {
            bw.write(str + "\r\n");
        } catch (IOException e) {
        }

        try {
            bw.close();
//			Toast.makeText(heartbluetooth.this, "匯出記錄", Toast.LENGTH_SHORT).show();
        } catch (IOException e1) {
        }

    }
    void doRecorddelta(String thing) {
        //輸出Log記錄
        Log.e("write","Kick_doRecorddelta inla");
        FileWriter file = null;
        BufferedWriter bw;
//		String location="/sdcard/Smart Clothes BT Log/"user_name+".txt";

        try {
            ///Log.e("87878787","logFileName:"+logFileName);
            file = new FileWriter(("/sdcard/Nursemaid/"+SplashActivity.todayfileName+"/KickLog/" + "Log_Dalta_" + NowKickLogTime + ".txt"), true);
        } catch (IOException e) {
        }

        bw = new BufferedWriter(file);

        String str = getTime() + ", " + thing;


        try {
            bw.write(str + "\r\n");
        } catch (IOException e) {
        }


        try {
            bw.close();
//			Toast.makeText(heartbluetooth.this, "匯出記錄", Toast.LENGTH_SHORT).show();
        } catch (IOException e1) {
        }

    }
}
