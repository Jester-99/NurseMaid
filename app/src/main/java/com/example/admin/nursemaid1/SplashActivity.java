package com.example.admin.nursemaid1;

import static android.app.Service.START_NOT_STICKY;
import static android.content.pm.PackageManager.PERMISSION_DENIED;

import android.Manifest;
import android.app.Notification;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ServiceCompat;

import com.sinopulsar.nursemaid1.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SplashActivity extends AppCompatActivity {

    public static ExecutorService executorService = Executors.newCachedThreadPool();
    public static Future<?> debugLogFuture;
    static String KickLog_fileDateTime = "";
    static String todayfileName = "";
    public final String TAG = "SplashActivity";
    private final int REQUEST_PERMISSIONS_CODE = 1;
    public ExecutorService logThreadPool = Executors.newSingleThreadExecutor();
    // 定义前台服务类型常量
    private static final int FOREGROUND_SERVICE_TYPE_LOCATION = ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //隱藏狀態欄
        getSupportActionBar().hide();

        Log.e(TAG, "onCreate");

        Calendar calendar = Calendar.getInstance();
        int[] nowtime = new int[]{calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)};
        todayfileName = "Log_";
        String dirdelday = "";
        for (int i = 0; i < nowtime.length; i++) {
            if (nowtime[i] < 10) {
                todayfileName += "0" + nowtime[i];
                dirdelday += "0" + nowtime[i];
            } else {
                todayfileName += String.valueOf(nowtime[i]);
                dirdelday += String.valueOf(nowtime[i]);
            }
        }
        Log.e("dateCreate", todayfileName);


        ArrayList<String> requestPermissions = new ArrayList<String>() {{
            add(Manifest.permission.READ_PHONE_STATE);
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            add(Manifest.permission.READ_EXTERNAL_STORAGE);
            add(Manifest.permission.ACCESS_FINE_LOCATION);
            add(Manifest.permission.BLUETOOTH);
            add(Manifest.permission.BLUETOOTH_ADMIN);
            add(Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        }};


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissions.add(Manifest.permission.BLUETOOTH_SCAN);
            requestPermissions.add(Manifest.permission.BLUETOOTH_CONNECT);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){//API=33移除讀寫增加通知權限
            requestPermissions.remove(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            requestPermissions.remove(Manifest.permission.READ_EXTERNAL_STORAGE);
            requestPermissions.add(Manifest.permission.POST_NOTIFICATIONS);
        }
        if(Build.VERSION.SDK_INT >= 34){//API=34
            requestPermissions.add(Manifest.permission.FOREGROUND_SERVICE);
        }
//        debugLogFuture = executorService.submit(new DebugLogRunnable());

        //提示要求權限
        if (requestPermissions.stream().anyMatch(i -> ActivityCompat.checkSelfPermission(this, i) == PERMISSION_DENIED)) {
            Log.e(TAG, "onCreate: GoGo 92");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Intent intent = new Intent();

                PowerManager pm = (PowerManager) SplashActivity.this.getSystemService(POWER_SERVICE);
                Log.e("pm", String.valueOf(pm));
                if (!pm.isIgnoringBatteryOptimizations("com.sinopulsar.nursemaid1")) {
                    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + "com.sinopulsar.nursemaid1"));
                    SplashActivity.this.startActivity(intent);

                }

                new AlertDialog.Builder(this)
                        .setTitle("位置權限")
                        .setMessage("為了能夠正確找尋到Tag，請允許Nursemaid可以取用\"精確\"位置！")
                        .setPositiveButton("確定", ((dialog, which) -> {
                            dialog.dismiss();
                            ActivityCompat.requestPermissions(this, requestPermissions.toArray(new String[0]), REQUEST_PERMISSIONS_CODE);
                        }))
                        .create()
                        .show();


            } else {
                ActivityCompat.requestPermissions(this, requestPermissions.toArray(new String[0]), REQUEST_PERMISSIONS_CODE);
            }
        } else {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Log.e("許可權限", "對方已經許可");
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                SplashActivity.this.finish();
            }, 1000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, String.valueOf(logThreadPool.isShutdown()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            if (grantResults.length > 0 && Arrays.stream(grantResults).noneMatch(i -> i == PERMISSION_DENIED)) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Log.e(TAG, "權限已經許可");
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }, 1000);
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("無法使用Nursemaid")
                        .setMessage("請至設定允許所有權限")
                        .setPositiveButton("確定", ((dialog, which) -> {
                            dialog.dismiss();
                            new Handler().postDelayed(this::finish, 1000);
                        }))
                        .create()
                        .show();
            }
        }
    }

    // 在 Service 中启动前台服务
//    @Override
//    public void onStartCommand(Intent intent, int flags, int startId) {
////        Notification notification = createNotification();
//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
//            // API 33 及以下
//            ServiceCompat.startForeground(this, NOTIFICATION_ID, notification);
//        } else {
//            // API 34 及以上
//            ServiceCompat.startForeground(this, notification, FOREGROUND_SERVICE_TYPE_LOCATION);
//        }
//
//        return START_NOT_STICKY;
//    }

//    public class DebugLogRunnable implements Runnable {
//
//        String todayFileName = "";
//        String logFileName = "";
//
//        //Log File Root Path = /storage/emulated/0/Child_AP_Log/;
////        String defaultRootPath = Environment.getExternalStorageDirectory().getPath() + "/Child_AP_Log/";
//
//        //Log File Root Path = /storage/emulated/0/Android/data/org.easydarwin.nursemaidgetupsetting_ap/files/Download/Child_AP_Log/;
//        String defaultRootPath = getExternalFilesDir(null).getPath() + "/Nursemaid_Log/";
//        String defaultRootPath_kick = getExternalFilesDir(null).getPath() + "/Kick_Log/";
//
//        @Override
//        public void run() {
//            try {
//                Log.e("TTAA", getExternalFilesDir(null).getPath());
//                Log.e("T", "");
//                java.lang.Process p = Runtime.getRuntime().exec("logcat");
//                final InputStream is = p.getInputStream();
//                final DateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
//                final String childLogTime = df.format(new Date());
//                final String nursemaidlogtime = df.format(new Date());
//                String logDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
//
////                log_filename = "/sdcard/Nursemaid/" + todayfileName + "/nursemaidlog_" + nursemaidlogtime + ".txt";
////                KickLog_fileDateTime = todayfileName + "_" +  nursemaidlogtime;
////                File logfiledirrr = new File("/sdcard/Nursemaid/" + todayfileName + "/");
//
//                File todayDirChild = new File(defaultRootPath);
//
//                // 如果資料夾不存在
//                if (!todayDirChild.exists()) {
//                    // 建立資料夾
//                    todayDirChild.mkdir();
//                }
//
//                todayFileName = "Log_" + getTodayFileDate();
//                logFileName = defaultRootPath + logDate + "/" + todayFileName + "_" + childLogTime + ".txt";
//                Log.e("Logfile Name", logFileName);
//                KickLog_fileDateTime = todayfileName + "_" + nursemaidlogtime;
//
//                File logFileDir = new File(defaultRootPath + logDate + "/");
//
//                while (!logFileDir.exists()) {
//                    try {
//                        logFileCreateAndDelete();
//                        Log.e("logfile", "in1");
//                        Thread.sleep(3000);
//                        if (logFileDir.exists()) {
//                            break;
//                        }
//                    } catch (Exception ignored) {
//                    }
//                }
//
//                //刪除過舊的Log
//                deleteOldLog();
//
//                logThreadPool.submit(() -> {
//                    FileOutputStream os = null;
//                    try {
//                        Log.e("logfile", "MainActivity_DebugThread LogFile:" + logFileName);
//                        os = new FileOutputStream(logFileName);
//                        int len;
//                        byte[] buf = new byte[1024];
//
//                        while (-1 != (len = is.read(buf))) {
//                            os.write(buf, 0, len);
//                            os.flush();
//                            File file123 = new File(logFileName);
//                            if (file123.exists()) {
//                                int fileSize = (int) file123.length();
//                                if ((fileSize / 10000) / 1000 >= 1) {
//                                    logFileCreateAndDelete();
//                                    logFileName = defaultRootPath + logDate + "/" + todayFileName + "_" + df.format(new Date()) + ".txt";
//                                    Log.e("File", "newLogFile");
//                                    break;
//                                }
//                            } else {
//                                Log.e("check", "?????");
//                                logFileCreateAndDelete();
//                                break;
//                            }
//                        }
//                        Thread.sleep(1000);
//                        os.close();
//                        run();
//                        Log.e("log", "re");
//                    } catch (Exception e) {
//                        Log.e("writelog", "read logcat process failed. message: " + e.getMessage());
//                        try {
//                            Thread.sleep(1500);
//                        } catch (InterruptedException ex) {
//                            ex.printStackTrace();
//                        }
//                        run();
//                    } finally {
//                        if (null != os) {
//                            try {
//                                os.close();
//                            } catch (IOException ignored) {
//                            }
//                        }
//                    }
//                });
//            } catch (Exception e) {
//                Log.d("writelog", "open logcat process failed. message: " + e.getMessage());
//            }
//        }
//
//        private String getTodayFileDate() {
//            Calendar calendar = Calendar.getInstance();
//            int[] nowTime = new int[]{calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)};
//            StringBuilder dirDelDay = new StringBuilder();
//
//            for (int item : nowTime) {
//                if (item < 10) {
//                    dirDelDay.append("0").append(item);
//                } else {
//                    dirDelDay.append(item);
//                }
//            }
//            return dirDelDay.toString();
//        }
//
//        private void deleteOldLog() {
//            String dirDelDay = getTodayFileDate();
//            File fileDel = new File(defaultRootPath);
//            Log.e("TAG_", fileDel.getAbsolutePath());
//            File[] files = fileDel.listFiles();
//            if (files != null) {
//                Log.e("listFiles", Arrays.toString(files));
//                for (File file : files) {
//                    int dirMonth = Integer.parseInt(dirDelDay.substring(0, 2));
//                    int dirDay = Integer.parseInt(dirDelDay.substring(2, 4));
//
//                    String[] alreadyExistFileDay = file.getName().split("-");
//                    String checkIsMath = alreadyExistFileDay[1].substring(0, 1);
//
//                    if (checkIsMath.equals("0") || checkIsMath.equals("1")) {
//                        int existFileYear = Integer.parseInt(alreadyExistFileDay[0]);
//                        int existFileMonth = Integer.parseInt(alreadyExistFileDay[1]);
//                        int existFileDay = Integer.parseInt(alreadyExistFileDay[2]);
//
//                        if (Calendar.getInstance().get(Calendar.YEAR) > existFileYear) {
//                            File nowDeleteDir = new File(file.getAbsolutePath());
//                            Log.e("delete", nowDeleteDir.getAbsolutePath());
//                            File[] nowDeleteAllFile = nowDeleteDir.listFiles();
//                            for (File value : nowDeleteAllFile) {
//                                Log.e("delete value", value.toString());
//                                value.delete();
//                            }
//                            nowDeleteDir.delete();
//                        } else if (dirMonth == existFileMonth) {
//                            if (dirDay - existFileDay > 7) {
//                                File nowDeleteDir = new File(file.getAbsolutePath());
//                                Log.e("delete", file.getAbsolutePath());
//                                File[] nowDeleteAllFile = nowDeleteDir.listFiles();
//                                for (File value : nowDeleteAllFile) {
//                                    Log.e("delete value", value.toString());
//                                    value.delete();
//                                }
//                                nowDeleteDir.delete();
//                            }
//                        } else if (dirMonth > existFileMonth || dirMonth - existFileMonth < 0) {
//                            dirDay = 30 + dirDay;
//                            if (dirDay - existFileDay > 7) {
//                                File nowDeleteDir = new File(file.getAbsolutePath());
//                                Log.e("delete", file.getAbsolutePath());
//                                File[] nowDeleteAllFile = nowDeleteDir.listFiles();
//                                for (int all = 0; all < Objects.requireNonNull(nowDeleteAllFile).length; all++) {
//                                    nowDeleteAllFile[all].delete();
//                                }
//                                nowDeleteDir.delete();
//                            }
//                        }
//                    } else {
//                        File notChildFileDir = new File(file.getAbsolutePath());
//                        File[] notChildAllFile = notChildFileDir.listFiles();
//                        for (File value : notChildAllFile) {
//                            value.delete();
//                        }
//                        file.delete();
//                    }
//                }
//            }
//        }
//
//        //debug
//        private void logFileCreateAndDelete() {
//            String logDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
//            String dirDelDay;
//            File ChildLogDir = new File(defaultRootPath + logDate + "/");
//            Log.e("Logcat", "Open");
//
//            // 如果資料夾不存在
//            if (!ChildLogDir.exists()) {
//                // 建立資料夾
//                ChildLogDir.mkdir();
//            }
//
//            dirDelDay = getTodayFileDate();
//            todayFileName = "Log_" + dirDelDay;
//
//            File todayDirChild = new File(defaultRootPath);
//            Log.e("dateLog123", "todayDirChild");
//            // 如果資料夾不存在
//            if (!todayDirChild.exists()) {
//                // 建立資料夾
//                todayDirChild.mkdir();
//            }
//
//            File todayDirLogcat = new File(defaultRootPath);
//            Log.e("dateLog123", "todayDirLogcat");
//            // 如果資料夾不存在
//            if (!todayDirLogcat.exists()) {
//                // 建立資料夾
//                todayDirLogcat.mkdir();
//            }
//
//            File todayDirDate = new File(defaultRootPath + logDate + "/");
//            Log.e("dateLog123", "todayDirLogcat");
//            // 如果資料夾不存在
//            if (!todayDirDate.exists()) {
//                // 建立資料夾
//                todayDirDate.mkdir();
//            }
//
//            File todayDirLogcat_kick = new File(defaultRootPath_kick);
//            Log.e("dateLog123", "todayDirLogcat");
//            // 如果資料夾不存在
//            if (!todayDirLogcat_kick.exists()) {
//                // 建立資料夾
//                todayDirLogcat_kick.mkdir();
//            }
//
//            File todayDirDate_kick = new File(defaultRootPath_kick + logDate + "/");
//            Log.e("dateLog123", "todayDirLogcat");
//            // 如果資料夾不存在
//            if (!todayDirDate_kick.exists()) {
//                // 建立資料夾
//                todayDirDate_kick.mkdir();
//            }
//
//            deleteOldLog();
//        }
//    }
}


/*public class SplashActivity extends AppCompatActivity {

    public final String TAG = "SplashActivity";
    public static ExecutorService executorService = Executors.newCachedThreadPool();
    public ExecutorService logThreadPool = Executors.newSingleThreadExecutor();
    public static Future<?> debugLogFuture;

    private final int REQUEST_PERMISSIONS_CODE = 1;

    static String KickLog_fileDateTime = "";
    static String todayfileName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Log.e(TAG, "onCreate");

        Calendar calendar = Calendar.getInstance();
        int[] nowtime = new int[]{calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)};
        todayfileName = "Log_";
        String dirdelday = "";
        for (int i = 0; i < nowtime.length; i++) {
            if (nowtime[i] < 10) {
                todayfileName += "0" + String.valueOf(nowtime[i]);
                dirdelday += "0" + String.valueOf(nowtime[i]);
            } else {
                todayfileName += String.valueOf(nowtime[i]);
                dirdelday += String.valueOf(nowtime[i]);
            }
        }
        Log.e("dateCreate", todayfileName);



        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //隱藏狀態欄

        ArrayList<String> requestPermissions = new ArrayList<String>() {{
            add(Manifest.permission.CAMERA);
            add(Manifest.permission.RECORD_AUDIO);
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            add(Manifest.permission.READ_EXTERNAL_STORAGE);
            add(Manifest.permission.ACCESS_FINE_LOCATION);
        }};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermissions.add(Manifest.permission.BLUETOOTH_SCAN);
            requestPermissions.add(Manifest.permission.BLUETOOTH_CONNECT);
        }

        debugLogFuture = executorService.submit(new DebugLogRunnable());

        //提示要求權限
        if (requestPermissions.stream().anyMatch(i -> ActivityCompat.checkSelfPermission(this, i) == PERMISSION_DENIED)) {
            // Arrays.copyOf(requestPermissions.toArray(), requestPermissions.size(), String[].class)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                new AlertDialog.Builder(this)
                        .setTitle("位置權限")
                        .setMessage("為了能夠正確找尋到Wifi與Tag，請允許BabyGetUp可以取用\"精確\"位置！")
                        .setPositiveButton("確定", ((dialog, which) -> {
                            dialog.dismiss();
                            ActivityCompat.requestPermissions(this, requestPermissions.toArray(new String[0]), REQUEST_PERMISSIONS_CODE);
                        }))
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, requestPermissions.toArray(new String[0]), REQUEST_PERMISSIONS_CODE);
            }
        } else {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Log.e("許可權限", "對方已經許可");
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                SplashActivity.this.finish();
            }, 1000);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, String.valueOf(logThreadPool.isShutdown()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            if (grantResults.length > 0 && Arrays.stream(grantResults).noneMatch(i -> i == PERMISSION_DENIED)) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    Log.e(TAG, "權限已經許可");
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }, 1000);
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("無法使用Nursemaid")
                        .setMessage("請至設定允許所有權限")
                        .setPositiveButton("確定", ((dialog, which) -> {
                            dialog.dismiss();
                            new Handler().postDelayed(this::finish, 1000);
                        }))
                        .create()
                        .show();
            }
        }
    }

    public class DebugLogRunnable implements Runnable {

        String todayFileName = "";
        String logFileName = "";

        //Log File Root Path = /storage/emulated/0/Child_AP_Log/;
//        String defaultRootPath = Environment.getExternalStorageDirectory().getPath() + "/Child_AP_Log/";

        //Log File Root Path = /storage/emulated/0/Android/data/org.easydarwin.nursemaidgetupsetting_ap/files/Download/Child_AP_Log/;
        String defaultRootPath = getExternalFilesDir(null).getPath() + "/Nursemaid_Log/";
        String defaultRootPath_kick = getExternalFilesDir(null).getPath() + "/Kick_Log/";

        @Override
        public void run() {
            try {
                Log.e("TTAA", getExternalFilesDir(null).getPath());
                Log.e("T", "");
                java.lang.Process p = Runtime.getRuntime().exec("logcat");
                final InputStream is = p.getInputStream();
                final DateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                final String childLogTime = df.format(new Date());
                final String nursemaidlogtime = df.format(new Date());
                String logDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

//                log_filename = "/sdcard/Nursemaid/" + todayfileName + "/nursemaidlog_" + nursemaidlogtime + ".txt";
//                KickLog_fileDateTime = todayfileName + "_" +  nursemaidlogtime;
//                File logfiledirrr = new File("/sdcard/Nursemaid/" + todayfileName + "/");

                File todayDirChild = new File(defaultRootPath);

                // 如果資料夾不存在
                if (!todayDirChild.exists()) {
                    // 建立資料夾
                    todayDirChild.mkdir();
                }

                todayFileName = "Log_" + getTodayFileDate();
                logFileName = defaultRootPath + logDate + "/" + todayFileName + "_" + childLogTime + ".txt";
                Log.e("Logfile Name", logFileName);
                KickLog_fileDateTime = todayfileName + "_" +  nursemaidlogtime;

                File logFileDir = new File(defaultRootPath + logDate + "/");

                while (!logFileDir.exists()) {
                    try {
                        logFileCreateAndDelete();
                        Log.e("logfile", "in1");
                        Thread.sleep(3000);
                        if (logFileDir.exists()) {
                            break;
                        }
                    } catch (Exception ignored) {
                    }
                }

                //刪除過舊的Log
                deleteOldLog();

                logThreadPool.submit(() -> {
                    FileOutputStream os = null;
                    try {
                        Log.e("logfile", "MainActivity_DebugThread LogFile:" + logFileName);
                        os = new FileOutputStream(logFileName);
                        int len;
                        byte[] buf = new byte[1024];

                        while (-1 != (len = is.read(buf))) {
                            os.write(buf, 0, len);
                            os.flush();
                            File file123 = new File(logFileName);
                            if (file123.exists()) {
                                int fileSize = (int) file123.length();
                                if ((fileSize / 10000) / 1000 >= 1) {
                                    logFileCreateAndDelete();
                                    logFileName = defaultRootPath + logDate + "/" + todayFileName + "_" + df.format(new Date()) + ".txt";
                                    Log.e("File", "newLogFile");
                                    break;
                                }
                            } else {
                                Log.e("check", "?????");
                                logFileCreateAndDelete();
                                break;
                            }
                        }
                        Thread.sleep(1000);
                        os.close();
                        run();
                        Log.e("log", "re");
                    } catch (Exception e) {
                        Log.e("writelog", "read logcat process failed. message: " + e.getMessage());
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        run();
                    } finally {
                        if (null != os) {
                            try {
                                os.close();
                            } catch (IOException ignored) {
                            }
                        }
                    }
                });
            } catch (Exception e) {
                Log.d("writelog", "open logcat process failed. message: " + e.getMessage());
            }
        }

        private String getTodayFileDate() {
            Calendar calendar = Calendar.getInstance();
            int[] nowTime = new int[]{calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)};
            StringBuilder dirDelDay = new StringBuilder();

            for (int item : nowTime) {
                if (item < 10) {
                    dirDelDay.append("0").append(item);
                } else {
                    dirDelDay.append(item);
                }
            }
            return dirDelDay.toString();
        }

        private void deleteOldLog() {
            String dirDelDay = getTodayFileDate();
            File fileDel = new File(defaultRootPath);
            Log.e("TAG_", fileDel.getAbsolutePath());
            File[] files = fileDel.listFiles();
            if (files != null) {
                Log.e("listFiles", Arrays.toString(files));
                for (File file : files) {
                    int dirMonth = Integer.parseInt(dirDelDay.substring(0, 2));
                    int dirDay = Integer.parseInt(dirDelDay.substring(2, 4));

                    String[] alreadyExistFileDay = file.getName().split("-");
                    String checkIsMath = alreadyExistFileDay[1].substring(0, 1);

                    if (checkIsMath.equals("0") || checkIsMath.equals("1")) {
                        int existFileYear = Integer.parseInt(alreadyExistFileDay[0]);
                        int existFileMonth = Integer.parseInt(alreadyExistFileDay[1]);
                        int existFileDay = Integer.parseInt(alreadyExistFileDay[2]);

                        if (Calendar.getInstance().get(Calendar.YEAR) > existFileYear) {
                            File nowDeleteDir = new File(file.getAbsolutePath());
                            Log.e("delete", nowDeleteDir.getAbsolutePath());
                            File[] nowDeleteAllFile = nowDeleteDir.listFiles();
                            for (File value : nowDeleteAllFile) {
                                Log.e("delete value", value.toString());
                                value.delete();
                            }
                            nowDeleteDir.delete();
                        } else if (dirMonth == existFileMonth) {
                            if (dirDay - existFileDay > 7) {
                                File nowDeleteDir = new File(file.getAbsolutePath());
                                Log.e("delete", file.getAbsolutePath());
                                File[] nowDeleteAllFile = nowDeleteDir.listFiles();
                                for (File value : nowDeleteAllFile) {
                                    Log.e("delete value", value.toString());
                                    value.delete();
                                }
                                nowDeleteDir.delete();
                            }
                        } else if (dirMonth > existFileMonth || dirMonth - existFileMonth < 0) {
                            dirDay = 30 + dirDay;
                            if (dirDay - existFileDay > 7) {
                                File nowDeleteDir = new File(file.getAbsolutePath());
                                Log.e("delete", file.getAbsolutePath());
                                File[] nowDeleteAllFile = nowDeleteDir.listFiles();
                                for (int all = 0; all < Objects.requireNonNull(nowDeleteAllFile).length; all++) {
                                    nowDeleteAllFile[all].delete();
                                }
                                nowDeleteDir.delete();
                            }
                        }
                    } else {
                        File notChildFileDir = new File(file.getAbsolutePath());
                        File[] notChildAllFile = notChildFileDir.listFiles();
                        for (File value : notChildAllFile) {
                            value.delete();
                        }
                        file.delete();
                    }
                }
            }
        }

        //debug
        private void logFileCreateAndDelete() {
            String logDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            String dirDelDay;
            File ChildLogDir = new File(defaultRootPath + logDate + "/");
            Log.e("Logcat", "Open");

            // 如果資料夾不存在
            if (!ChildLogDir.exists()) {
                // 建立資料夾
                ChildLogDir.mkdir();
            }

            dirDelDay = getTodayFileDate();
            todayFileName = "Log_" + dirDelDay;

            File todayDirChild = new File(defaultRootPath);
            Log.e("dateLog123", "todayDirChild");
            // 如果資料夾不存在
            if (!todayDirChild.exists()) {
                // 建立資料夾
                todayDirChild.mkdir();
            }

            File todayDirLogcat = new File(defaultRootPath);
            Log.e("dateLog123", "todayDirLogcat");
            // 如果資料夾不存在
            if (!todayDirLogcat.exists()) {
                // 建立資料夾
                todayDirLogcat.mkdir();
            }

            File todayDirDate = new File(defaultRootPath + logDate + "/");
            Log.e("dateLog123", "todayDirLogcat");
            // 如果資料夾不存在
            if (!todayDirDate.exists()) {
                // 建立資料夾
                todayDirDate.mkdir();
            }

            File todayDirLogcat_kick = new File(defaultRootPath_kick);
            Log.e("dateLog123", "todayDirLogcat");
            // 如果資料夾不存在
            if (!todayDirLogcat_kick.exists()) {
                // 建立資料夾
                todayDirLogcat_kick.mkdir();
            }

            File todayDirDate_kick = new File(defaultRootPath_kick + logDate + "/");
            Log.e("dateLog123", "todayDirLogcat");
            // 如果資料夾不存在
            if (!todayDirDate_kick.exists()) {
                // 建立資料夾
                todayDirDate_kick.mkdir();
            }

            deleteOldLog();
        }
    }
}*/