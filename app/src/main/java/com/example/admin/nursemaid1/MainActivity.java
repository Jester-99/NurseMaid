package com.example.admin.nursemaid1;

import static android.content.pm.PackageManager.PERMISSION_DENIED;

import static com.example.admin.nursemaid1.SetupFragment.btAdapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

//import com.sinopulsar.nursemaid.R;
import com.sinopulsar.nursemaid1.R;

import cn.jpush.android.api.JPushInterface;

import static java.lang.Thread.sleep;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity {
    public ExecutorService logThreadPool = Executors.newSingleThreadExecutor();
    public static ExecutorService executorService = Executors.newCachedThreadPool();
    public static Future<?> debugLogFuture;

    public static boolean isForeground = false;
    public static boolean isTagOffline = false;

    int addressBlue_deviceList = 0;

    String CheckTagExistAddress = "";

    public static boolean firstAddTagtime = false;

    public final int PERMISSION_REQUEST = 101;

    private final int REQUEST_PERMISSIONS_CODE = 1;

    DrawerLayout drawerLayout;
    static NavigationView navigationView;
    Button drawerbutton;
    ChartFragment chartFragment;
    SetupFragment setupFragment;
    SingleFragment singleFragment;
    MultipleFragment multipleFragment;
    InformationFragment informationFragment;
    //    static BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    static BluetoothManager btManager;
    static Context mParent;
    static scanthread ww;
    String Uname = "";
    String calibrationcode = "";
    String prevtime = "";

    String re = "", bl = "", ipprocet = "", ipsee = "";
    int red = 0, blue = 0, value = 0;
    byte[] dataToWrite;
    static FragmentTransaction transaction;

    Handler h = new Handler();
    static ArrayList<String> deviceRssiList = new ArrayList<String>();
    static ArrayList<String> deviceList = new ArrayList<String>();
    static boolean checkDevice = false;
    static boolean systemrun = true;
    static String address = "", baddress = "null", rid = "", ip, username, password, token;
    static String multi_address = "", multi_data = "";  //多人
    static SharedPreferences settings;
    static boolean tryapp = false;
    Context par = MainActivity.this;
    static ArrayList<HashMap> tagNameList = new ArrayList<>();
    static HashMap<String, String> tagNameMap = new HashMap<>();
    static int nameNumber = 0, set = 0, rusume = 0, btexist = 0, isring = 0, datathreadset = 0, isfrist = 0;
    static Ringtone ringTone;
    static Uri notification;
    private final int PERMISSION_REQUEST_COARSE_LOCATION = 0xb01;
    static SharedPreferences preferences;
    String SingleEable = "";
    int[] checkrssi = new int[3];
    //    int numRssi = 0 ;
    int rssitimer = 0;
    static int isstate = 0;
    int scanwindow = 5;

    int isexisttimer = 0;
    int Multi_existtimer = 0;
    String mac = "";
    String bluef = "0";
    rssithread oo = new rssithread();
    isexistThread iu = new isexistThread();
    Boolean isexist = true;
    Boolean ooo = true;

    //家豪
    String language;
    String exlanguage;
    lan_thread lan_threadstart;
    //


    /**/
    CAction action;
    String user_name = "nursemaid";
    String StartTime;
    static String todayfileName = "";
    /**/

    //new
    boolean checkbool = false;
    //BluetoothAdapter checkmBluetoothAdapter;
    int openble = 0;
    //BluetoothCloseThread bct = new BluetoothCloseThread();
    int firstopenapp = 0;
    String registdata = "";
    String log_filename = "";
    static String KickLog_fileDateTime = "";
    //static String nursemaidlogtime = "";
    Boolean checkpermissionbool = false;
    String multi_tosingle = "";
    //
    static boolean isService = false;
    Intent intent;
    Timer timer = new Timer();
    Timer ConnectTimer = new Timer();
    Timer JudgeService = new Timer();
    boolean judgu_service = false;
    static DeviceListAdapter mDevicesListAdapter = null;
//    String Blue_multi_tosingle = "";
//    SingleFragment singleFragment;

    static boolean isSetup = false;
    static boolean isSingle = false;
    static ArrayList<String> Tagtime = new ArrayList<String>();
    //    static String Tagtime;
    static int times = 0;
    public Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    //處理少量資訊或UI
                    Toast.makeText(MainActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case 2:                                             //多數介面更新
                    Message msg2 = new Message();
                    MultipleFragment.updateHandler.sendMessage(msg2);

                    break;
                case 3:
                    Toast.makeText(MainActivity.this, R.string.check_internet, Toast.LENGTH_LONG).show();
                    break;
                case 4:
                    Message msg3 = new Message();
                    MultipleFragment.updateHandler2.sendMessage(msg3);
                    //Log.e("up","12") ;
            }
        }
    };

    private Handler singleui = new Handler(Looper.myLooper()) {                       //gatdata用來顯示於單數頁面
        @Override
        public void handleMessage(Message msg) {
            singleFragment.setUI(baddress, String.valueOf(msg.obj), SingleEable);
        }
    };

    private Handler rssihandle = new Handler(Looper.myLooper()) {                       //gatdata用來顯示於單數頁面
        @Override
        public void handleMessage(Message msg) {
            //rssiDialog().show();

            final AlertDialog dlg = rssiDialog();
            dlg.show();
//            final Timer t = new Timer();
//            t.schedule(new TimerTask() {
//                public void run() {
//                    //10秒自動消失
//                    dlg.dismiss();
//                    t.cancel();
//                }
//            }, 10000);
        }
    };

    //BindService
    private BluetoothService bluetoothService;
    private ServiceConnection serviceConnection = new ServiceConnection() {     //建立繫結服務
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            BluetoothService.BluetoothBinder binder = (BluetoothService.BluetoothBinder) iBinder;
            bluetoothService = binder.getService();
            isService = true;
            Log.e("blue_onServiceConnected", "有觸發");
        }

        @SuppressLint("LongLogTag")
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.e("blue_onServiceDisconnected", "解除綁定");
            isService = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        checkpermissionbool = CheckPermission();

        /**/
        //action = new CAction(MainActivity.this);
        /**/

        Log.e("onCreate", "onCreate");

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//概略權限
            if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }

        Resources res = getResources();
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());

//        Calendar calendar = Calendar.getInstance();
//        int[] nowtime = new int[]{calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)};
//        todayfileName = "Log_";
//        String dirdelday = "";
//        for (int i = 0; i < nowtime.length; i++) {
//            if (nowtime[i] < 10) {
//                todayfileName += "0" + String.valueOf(nowtime[i]);
//                dirdelday += "0" + String.valueOf(nowtime[i]);
//            } else {
//                todayfileName += String.valueOf(nowtime[i]);
//                dirdelday += String.valueOf(nowtime[i]);
//            }
//        }
//        Log.e("dateCreate", todayfileName);

        MultipleFragment.checken = 1;
        isring = 0;
        systemrun = true;

        JPushInterface.setDebugMode(true);    // 设置开启日志,发布时请关闭日志
        JPushInterface.init(getApplicationContext());            // 初始化 JPush
        rid = JPushInterface.getRegistrationID(getApplicationContext());
        Log.e("rid", "rid=" + rid);

        preferences = getSharedPreferences("UserAccount", 0);
        username = preferences.getString("account", "");
        password = preferences.getString("password", "");
        token = preferences.getString("token", "");
        settings = getSharedPreferences("APPSETTING", 0);
        exlanguage = preferences.getString("language", "");

        /**/
        settings.edit().putString("user_name", user_name).commit();
        /**/

        language = (String) getResources().getText(R.string.language);
        preferences.edit().putString("language", language).commit();

        if (!exlanguage.equals("") && !exlanguage.equals(language)) {
            lan_threadstart = new lan_thread();
            lan_threadstart.start();
            Log.e("rid", "in");
            try {
                lan_threadstart.join();


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            // 殺掉進程
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);

        } else {
            Log.e("rid", "else");
        }

        if (isfrist == 0) {
//            DebugLogRunnable wttt = new DebugLogRunnable();
//            wttt.start();
//            debugLogFuture = executorService.submit(new DebugLogRunnable());//Log檔
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter.isEnabled()) {      //紀錄手機藍芽是否開啟中
                settings.edit().putInt("isblueexit", 1).commit();
            } else {
                settings.edit().putInt("isblueexit", 0).commit();
            }
            isfrist = 1;
        }

        String title = null;
        String content = null;

        bluef = settings.getString("login", "0");

        if (bluef.equals("1")) {
            title = getResources().getString(R.string.chinsice0);
            content = getResources().getString(R.string.chinsice1_2);                         //如果是登入 提示內容改長壓tag
            baddress = settings.getString("baddress", "null");
            Log.e("content", "1");
        } else {
            title = getResources().getString(R.string.chinsice0);
            content = getResources().getString(R.string.chinsice1);                           //如果是未登入 提示內容改插入元件
            baddress = "null";
            Log.e("content", "2");
        }
//0912
        new AlertDialog.Builder(MainActivity.this)     //程式開始前會先跳出提示窗，使用者按下確定之後才會執行以下程式
                .setTitle(title)//設定視窗標題
                .setIcon(R.mipmap.ic_launcher)//設定對話視窗圖示
                .setMessage(content)//設定顯示的文字
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.chinsice2), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mParent = getApplicationContext();
                        isSetup = false;
                        isSingle = false;
                        String ad;
                        ad = Locale.getDefault().toString();
                        Log.e("cc", ad);
                        ip = settings.getString("ip", "120.105.161.186");

                        notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);                    //單數模式提示音
                        ringTone = RingtoneManager.getRingtone(getApplicationContext(), notification);
                        ww = new scanthread();

                        // 藍芽初始化
//                        btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//                        btAdapter = BluetoothAdapter.getDefaultAdapter() ;

//                        if(!settings.getBoolean("isScan",true)) {
//                            unbindService(serviceConnection);
//                            BluetoothService.bluetoothAdapter.disable();
//                            Log.e("555", String.valueOf(555));
//                        }

                        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                        navigationView = (NavigationView) findViewById(R.id.navigation);
                        drawerbutton = (Button) findViewById(R.id.drawerbutton);

                        FragmentManager fragmentManager = getSupportFragmentManager();
                        //singlefragment = fragmentManager.findFragmentById(R.id.firstfragment);
                        chartFragment = new ChartFragment();
                        setupFragment = new SetupFragment();
                        singleFragment = new SingleFragment();
                        multipleFragment = new MultipleFragment();
                        informationFragment = new InformationFragment();

                        //ConnectTimer.schedule(new ConnectTask(), 1000, 5000);
//                        JudgeService.schedule(new JudgeTask(),5000,5000);

                        if (settings.getBoolean("isScan", true)) {               //先決定是否用推波    掃描true   推波false
                            Log.e("scan", "1");
                            Log.e("openble", "132" + isfrist);

                            if (!isServiceRunning(MainActivity.this, "com.example.admin.nursemaid1.BluetoothService")) {
                                intent = new Intent(MainActivity.this, BluetoothService.class);    //執行Service服務
                                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
                                Log.e("Main初始_service", "初始執行Service");
                            }
//                            unbindService(serviceConnection);
//                            Log.e("選單_初始判斷", String.valueOf(isServiceRunning(MainActivity.this,"com.example.admin.nursemaid1.BluetoothService")));

                            //每10分鐘開啟關閉
                            /*if(!bct.isAlive()){
                                Log.e("openble","first");
                                openble = 0;
                                bct.start();
                                firstopenapp =1;
                                Log.e("openble",bct.getState().toString());
                            }*/
                            if (!BluetoothService.bluetoothAdapter.isEnabled()) {
                                Toast.makeText(MainActivity.this, "Enabling Bluetooth, please wait …", Toast.LENGTH_LONG).show();
//                                BluetoothService.bluetoothAdapter.enable();
                                Log.e("2", String.valueOf(2));
                            }
                            if (datathreadset == 1) {
                                datathreadset = 0;
                            }
                            checkDevice = false;
                            deviceRssiList.clear();
                            deviceList.clear();
//
                            timer.schedule(new timerTask(), 1000, 2000);
                            Log.e("掃描模式", String.valueOf(1));
                            bluetoothScan();
                            BluetoothCloseThread();  //藍芽重啟1205
                            rusume = 1;
                        } else if (!settings.getBoolean("isScan", true)) {
                            ooo = false;
                            //清除任務
//                            timer.cancel();
//                            timer.purge();
//                            timer = new Timer();

//                            if (BluetoothService.bluetoothAdapter.isEnabled()) {
//                                BluetoothService.bluetoothAdapter.disable();
//                            }
                            if (!iu.isAlive()) {
                                iu.start();
                            }
                            if (datathreadset == 0) {
                                datathreadset = 1;
                            }
                        }

                        transaction = getSupportFragmentManager().beginTransaction();              //單或多數模式
                        if (settings.getBoolean("isSingle", true)) {
                            isSingle = true;
                            if (MainActivity.settings.getBoolean("isScan", true)) {
                                if (!SingleFragment.o.isAlive()) {
                                    SingleFragment.o = new SingleFragment.kickthread();
                                    SingleFragment.o.start();
                                    Log.e("kick", "start");
                                }
                                SingleFragment.iskick = true;
                            } else {
                                SingleFragment.iskick = false;
                            }
                            transaction.add(R.id.fragmentcontainer, singleFragment).add(R.id.fragmentcontainer, multipleFragment).add(R.id.fragmentcontainer, chartFragment)
                                    .add(R.id.fragmentcontainer, setupFragment).add(R.id.fragmentcontainer, informationFragment)
                                    .hide(chartFragment).hide(setupFragment).hide(informationFragment).hide(multipleFragment).commitAllowingStateLoss();
                        } else if (!settings.getBoolean("isSingle", true)) {
                            SingleFragment.iskick = false;
                            isSingle = false;
                            transaction.add(R.id.fragmentcontainer, multipleFragment).add(R.id.fragmentcontainer, singleFragment).add(R.id.fragmentcontainer, chartFragment)
                                    .add(R.id.fragmentcontainer, setupFragment).add(R.id.fragmentcontainer, informationFragment)
                                    .hide(chartFragment).hide(setupFragment).hide(informationFragment).hide(singleFragment).commitAllowingStateLoss();
                        }

                        drawerbutton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                drawerLayout.openDrawer(Gravity.END);
                            }
                        });

                        SetupFragment.mBluetoothGatt = null;

                        final Boolean lastisScan = settings.getBoolean("isScan", true);
                        //右邊選單的程式碼
                        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                            @Override
                            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                                int id = item.getItemId();
                                transaction = getSupportFragmentManager().beginTransaction();
                                Log.e("MainActivity_右選單", String.valueOf(SetupFragment.mBluetoothGatt));
                                if (SetupFragment.mBluetoothGatt != null) {
                                    SetupFragment.disconnect();
                                    set = 0;
                                    Log.e("MainActivity_右選單", "結束連線");
//                                    Toast.makeText(MainActivity.this, "結束連線", Toast.LENGTH_SHORT).show();
//                                    Log.e("MainActivity_右選單", String.valueOf(SetupFragment.mBluetoothGatt));

//                                    Log.e("MainActivity_右選單", String.valueOf(SetupFragment.mBluetoothGatt));

                                }
                                if (id == R.id.single) {
                                    //bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
                                    isSetup = false;
                                    isSingle = true;
                                    Log.e("選單事件", "單人模式");
                                    Log.e("選單_單人", String.valueOf(isServiceRunning(MainActivity.this, "com.example.admin.nursemaid1.BluetoothService")));
                                    //Log.e("single","in");
                                    isexist = true;
                                    SingleFragment.iskick = true;
                                    isring = 0;
//                                    BluetoothService.bluetoothAdapter.disable();

                                    if (settings.getBoolean("isScan", true)) {
                                        if (!isServiceRunning(MainActivity.this, "com.example.admin.nursemaid1.BluetoothService")) {
                                            intent = new Intent(MainActivity.this, BluetoothService.class);    //執行Service服務
                                            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
                                            Log.e("選單_service", "執行Service");
                                        }
                                        //清除任務
                                        timer.cancel();
                                        timer.purge();
                                        timer = new Timer();

                                        timer.schedule(new timerTask(), 1000, 2000);
                                        Log.e("選單_單人_掃描", "掃描模式");

                                        ooo = true;
                                        Multi_existtimer = 0;
                                        if (!BluetoothService.bluetoothAdapter.isEnabled()) {
                                            Toast.makeText(MainActivity.this, "Enabling Bluetooth, please wait …", Toast.LENGTH_LONG).show();
//                                            BluetoothService.bluetoothAdapter.enable();
                                        }
                                        if (datathreadset == 1) {
                                            datathreadset = 0;
                                        }
                                        bluetoothScan();
//                                        BluetoothCloseThread();
                                        singleFragment.mStatus.setText(MainActivity.settings.getString("state", "normal").toString());
                                        rusume = 1;
                                    } else if (!settings.getBoolean("isScan", true)) {
                                        Log.e("選單事件", "推撥模式");
                                        //清除任務
                                        timer.cancel();
                                        timer.purge();
                                        timer = new Timer();
//                                        Log.e("選單_單人_推撥", String.valueOf(isServiceRunning(MainActivity.this,"com.example.admin.nursemaid1.BluetoothService")));
                                        if (isServiceRunning(MainActivity.this, "com.example.admin.nursemaid1.BluetoothService")) {
                                            unbindService(serviceConnection);
                                            Log.e("選單service", "解除綁定Service");
                                        }
//                                        Log.e("選單_推撥", String.valueOf(isServiceRunning(MainActivity.this,"com.example.admin.nursemaid1.BluetoothService")));
//                                        if (BluetoothService.bluetoothAdapter.isEnabled()) {
//                                            BluetoothService.bluetoothAdapter.disable();
//                                        }

                                        if (datathreadset == 0) {
                                            datathreadset = 1;
                                        }
                                    }
                                    Log.e("datathreadset", String.valueOf(datathreadset));
                                    transaction.hide(chartFragment).hide(setupFragment).hide(informationFragment).hide(multipleFragment).show(singleFragment).commitAllowingStateLoss();
                                } else if (id == R.id.TemperatureChart) {
                                    isSetup = false;
                                    isSingle = false;
                                    Log.e("選單事件", "溫度圖");
                                    SingleFragment.iskick = false;
                                    ooo = false;
                                    isexist = false;
                                    rssitimer = 0;
                                    isexisttimer = 0;
                                    Multi_existtimer = 0;

                                    if (datathreadset == 1) {
                                        datathreadset = 0;
                                    }
                                    isring = 1;
                                    /**/
                                    SQLite sqLite = new SQLite(MainActivity.this);
                                    //
                                    if (sqLite.IfData()) {
                                        //sqLite.toDeleteAllOldData();//1216
                                        Time getCurrnetTime = new Time();
                                        getCurrnetTime.setToNow();
                                        StartTime = String.valueOf(getCurrnetTime.year) + "-" + String.valueOf(getCurrnetTime.month + 1)
                                                + "-" + String.valueOf(getCurrnetTime.monthDay) + "-" + String.valueOf(getCurrnetTime.hour) + "-" + "00" + "-" + "00";
                                        chartFragment.toChartFragment(StartTime);
                                        transaction.hide(singleFragment).hide(setupFragment).hide(informationFragment).hide(multipleFragment).show(chartFragment).commitAllowingStateLoss();
                                    } else {
                                        new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this)
                                                .setTitle(R.string.chinsice0)
                                                .setMessage(R.string.check_temperature)
                                                .setPositiveButton(R.string.confirm_button, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.cancel();
                                                    }
                                                }).show();
                                    }
                                    /**/
                                    //transaction.hide(singleFragment).hide(setupFragment).hide(informationFragment).hide(multipleFragment).show(chartFragment).commitAllowingStateLoss();
                                } else if (id == R.id.setup) {

                                    //1203-logtest1
                                    Log.e("選單事件-SETTING", "enter設定模式");
                                    // 獲取手機的外部存儲路徑（適用於 Android 10 以上，需使用 scoped storage）
                                    File logDir = new File(getExternalFilesDir(null), "Logs"); // 應用專用目錄
                                    if (!logDir.exists()) {
                                        logDir.mkdir(); // 創建目錄
                                    }

                                    File logFile = new File(logDir, "app_logTTT.txt"); // 日誌文件名

                                    try (FileWriter writer = new FileWriter(logFile, true)) { // 以附加模式打開文件
                                        writer.append("選單事件-SETTING: enter設定模式\n"); // 記錄日誌內容
                                    } catch (IOException e) {
                                        Log.e("FileWriteError", "Error writing log to file", e);
                                    }
                                    //1203-logtest1

                                    //1203-logtest2
                                    ContentValues values = new ContentValues();
                                    values.put(MediaStore.MediaColumns.DISPLAY_NAME, "app_log.txt");
                                    values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
                                    values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                                    Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

                                    if (uri != null) {
                                        try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
                                            if (outputStream != null) {
                                                String logContent = "選單事件-SETTING: 進入設定模式";
                                                outputStream.write(logContent.getBytes());
                                                outputStream.close();
                                            }
                                        } catch (IOException e) {
                                            Log.e("LogFileError", "Error writing to log file", e);
                                        }
                                    }
                                    //1203-logtest2

                                    // 如果服務綁定，則解除綁定
                                    if (isService) {
                                        unbindService(serviceConnection);
                                        isService = false;
                                    }

                                    // 狀態設置
                                    isSetup = true;
                                    isSingle = false;
                                    Log.e("選單事件", "設定模式");
                                    SingleFragment.iskick = false;
                                    ooo = false;
                                    isexist = false;

                                    if (datathreadset == 1) {
                                        datathreadset = 0;
                                    }
                                    isring = 1;

                                    // 初始化或顯示設定頁
                                    if (setupFragment == null) {
                                        setupFragment = new SetupFragment();

                                        // 傳遞藍牙資料到 setupFragment
                                        Bundle bundle = new Bundle();
                                        Log.e("BluetoothADD",address);
                                        Log.e("BluetoothADDB",baddress);
                                        bundle.putString("bluetooth_address", baddress);
                                        setupFragment.setArguments(bundle);

                                        transaction.add(R.id.fragmentcontainer, setupFragment);
                                    }
//                                    Log.e("BluetoothADD",address);
//                                    Log.e("BluetoothADDB",baddress);
                                    transaction.hide(singleFragment)
                                            .hide(chartFragment)
                                            .hide(informationFragment)
                                            .hide(multipleFragment)
                                            .show(setupFragment)
                                            .commitAllowingStateLoss();

                                    // 確保在設定頁中進行藍牙連線並顯示資料

                                    if (!address.equals("null")) {
                                        address = BluetoothService.Blue_baddress;
                                        Log.e("Bluetooth", "連線到：" + address);
//                                        setupFragment.mBluetoothDevice = btAdapter.getRemoteDevice(address);
//                                        setupFragment.mBluetoothGatt = setupFragment.mBluetoothDevice.connectGatt(mParent, false,setupFragment.mBleCallback);
                                    }
                                    set = 1;
                                }
                                else if (id == R.id.Information) {
                                    isSetup = false;
                                    isSingle = false;
                                    SingleFragment.iskick = false;
                                    ooo = false;
                                    isexist = false;
                                    rssitimer = 0;
                                    isexisttimer = 0;
                                    Multi_existtimer = 0;

                                    if (datathreadset == 1) {
                                        datathreadset = 0;
                                    }
                                    isring = 1;
                                    transaction.hide(singleFragment).hide(chartFragment).hide(setupFragment).hide(multipleFragment).show(informationFragment).commitAllowingStateLoss();
//                                } else if (id == R.id.NotificationServer) {
//                                    isSetup = false;
//                                    isSingle = false;
//                                    SingleFragment.iskick = false;
//                                    ooo = false;
//                                    isexist = false;
//                                    rssitimer = 0;
//                                    isexisttimer = 0;
//                                    Multi_existtimer = 0;
//
//                                    if (datathreadset == 1) {
//                                        datathreadset = 0;
//                                    }
//                                    isring = 1;
//                                    transaction.hide(singleFragment).hide(setupFragment).hide(informationFragment).hide(multipleFragment).show(chartFragment).commitAllowingStateLoss();
//                                    setConnectDialog().show();
                                } else if (id == R.id.multiple) {
                                    isSetup = false;
                                    isSingle = false;
                                    Log.e("選單事件", "多人模式");
                                    SingleFragment.iskick = false;
                                    isexist = true;
                                    if (settings.getBoolean("isScan", true)) {
                                        Log.e("選單", "掃描模式");
                                        if (!isServiceRunning(MainActivity.this, "com.example.admin.nursemaid1.BluetoothService")) {
                                            intent = new Intent(MainActivity.this, BluetoothService.class);    //執行Service服務
                                            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
                                        }


                                        //清除任務
                                        timer.cancel();
                                        timer.purge();
                                        timer = new Timer();
                                        timer.schedule(new timerTask(), 1000, 2000);

                                        ooo = false;
                                        rssitimer = 0;
                                        isexisttimer = 0;
                                        if (!BluetoothService.bluetoothAdapter.isEnabled()) {
                                            Toast.makeText(MainActivity.this, "Enabling Bluetooth, please wait …", Toast.LENGTH_LONG).show();
//                                            BluetoothService.bluetoothAdapter.enable();
                                        }
                                        if (datathreadset == 1) {
                                            datathreadset = 0;
                                        }
                                        bluetoothScan();
                                        rusume = 1;
                                    } else if (!settings.getBoolean("isScan", true)) {
                                        Log.e("選單", "推撥模式");

                                        //清除任務
                                        timer.cancel();
                                        timer.purge();
                                        timer = new Timer();
                                        if (isServiceRunning(MainActivity.this, "com.example.admin.nursemaid1.BluetoothService")) {
                                            unbindService(serviceConnection);
                                            Log.e("選單service", "解除綁定Service");
                                            Log.e("判斷_service", "選單解除Service");
                                        }

                                        Log.e("scan", "123");
                                        if (BluetoothService.bluetoothAdapter.isEnabled()) {
//                                            BluetoothService.bluetoothAdapter.disable();
                                        }
                                        if (datathreadset == 0) {
                                            datathreadset = 1;
                                        }
                                    }
                                    transaction.hide(chartFragment).hide(setupFragment).hide(informationFragment).hide(singleFragment).show(multipleFragment).commitAllowingStateLoss();
                                }
                                drawerLayout.closeDrawer(Gravity.END);
                                return false;
                            }
                        });

                        if (MainActivity.settings.getBoolean("isScan", true)) {
                            Log.e("logim88", String.valueOf("88"));
                        } else {
                            if (!sec5_thread.isAlive()) {
                                sec5_thread.start();
                                Log.e("threadstart", "123");
                            }
                        }
//                        checkpermissionbool = CheckPermission();
//                        if (checkpermissionbool == true) {
////                            LogFileCreateAndDelete();
//                            Log.e("logfile", "inla");
//                        }
                    }
                })//設定結束的子視窗
                .show();//呈現對話視窗

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (settings.getBoolean("isScan", true)) {
                        if (isexisttimer > 120 || Multi_existtimer > 120) {
                            if (registdata.equals("0201060CFF968759800306810C00FE0007160918930C00FE06160F1857C522") || DeviceListAdapter.dataSet.size() == 0) {
                                Log.e("blemiss", "restartScan");
//                                stopScanDevice();
                                Log.e("blemiss", "stopScan");
                                Thread.sleep(500);

                                BluetoothService.bluetoothAdapter.disable();
                                Log.e("blemiss", "disable bt");
                                Thread.sleep(1000);

                                BluetoothService.bluetoothAdapter.enable();
                                Log.e("blemiss", "bt enable");
                                Thread.sleep(1000);

//                                startScanDevice();
                                Log.e("blemiss", "startScan");
                                Thread.sleep(1000);

                                isexisttimer = 0;
                                Multi_existtimer = 0;
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("MainonCreate", "error" + e.toString());
                }
            }
        }).start();

    }




    @Override
    protected void onDestroy() {                                      //仇視意外關閉時，會檢查之前使用者藍芽是否開啟
        super.onDestroy();
        if (settings.getInt("isblueexit", 0) == 1) {
            BluetoothService.bluetoothAdapter.enable();
        } else {
            BluetoothService.bluetoothAdapter.disable();
//            btAdapter.disable();
        }
        MultipleFragment.checken = 0;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_CODE) {
            if (grantResults.length > 0 && Arrays.stream(grantResults).noneMatch(i -> i == PERMISSION_DENIED)) {
                Log.e("TAG", "權限已經許可");
            } else {
                new androidx.appcompat.app.AlertDialog.Builder(this)
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        menu.findItem(R.id.action_drawbar).setVisible(true);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {                       //如果有輸入溫度校正碼會進行藍芽連線寫入
        switch (item.getItemId()) {
            case R.id.action_drawbar:
                if (set == 1) {
                    re = String.valueOf(SetupFragment.ed5.getText());
                    bl = String.valueOf(SetupFragment.ed6.getText());
                    Uname = String.valueOf(SetupFragment.userEditText.getText());
                    final String nowState = SetupFragment.selectnowState;
                    if (!re.equals("") && !bl.equals("") && !Uname.equals("")) {
                        red = Integer.valueOf(re);
                        blue = Integer.valueOf(bl);
                        if (SetupFragment.isKickNotifyOn) {//new
                            //SetupFragment.value[0] = 120;
                            Log.e("write", "on in");
                            value = 120;
                        } else if (!SetupFragment.isKickNotifyOn) {
                            Log.e("write", "off in");
                            if (nowState.equals("quick")) {
                                value = 4;
                            } else if (nowState.equals("sleep")) {
                                value = 120;
                            } else if (nowState.equals("normal")) {
                                value = 300;
                            }
                        }//new end
                        //value = SetupFragment.value[1];
                        Log.e("value", String.valueOf(value) + " " + SetupFragment.value[0] + " " + nowState);
                        if (red != SetupFragment.Sensorred || blue != SetupFragment.Sensorblue || value != SetupFragment.value[0] || !Uname.equals(SetupFragment.Uname)) {
                            new AlertDialog.Builder(MainActivity.this)
                                    .setMessage(getResources().getString(R.string.chinsice3))
                                    .setNeutralButton(getResources().getString(R.string.chinsice4), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.chinsice10), Toast.LENGTH_SHORT).show();
                                            set = 1;
                                        }
                                    })
                                    .setPositiveButton(getResources().getString(R.string.chinsice5), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Log.e("1122","111111111");
                                            dataToWrite = new byte[6];
                                            Log.e("wwwww", String.valueOf(value));
                                            dataToWrite[0] = (byte) (value & 0xFF);
                                            dataToWrite[1] = (byte) ((value >> 8) & 0xFF);
                                            dataToWrite[2] = (byte) (red & 0xFF);
                                            dataToWrite[3] = (byte) ((red >> 8) & 0xFF);
                                            dataToWrite[4] = (byte) (blue & 0xFF);
                                            dataToWrite[5] = (byte) ((blue >> 8) & 0xFF);
                                            SetupFragment.writeDataToCharacteristic(SetupFragment.ch, dataToWrite);
                                            Log.e("writeCH", Arrays.toString(dataToWrite));
                                            Log.e("writeCHA", String.valueOf(SetupFragment.ch));
                                            //Toast.makeText(getApplicationContext(), "設定成功", Toast.LENGTH_SHORT).show();
                                            set = 0;
                                            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
                                            String phonetime2 = sDateFormat.format(new java.util.Date());
                                            SetupFragment.userSQLite.ToInsert(MainActivity.address, Uname, re, bl, String.valueOf(value), phonetime2);
                                            setItemName(MainActivity.address, Uname);

                                            if (value == 4) {
                                                MainActivity.settings.edit().putString("state", "quick").commit();
                                            } else if (value == 120) {
                                                MainActivity.settings.edit().putString("state", "sleep").commit();
                                            } else {
                                                MainActivity.settings.edit().putString("state", "normal").commit();
                                            }
                                            singleFragment.mStatus.setText(settings.getString("state", "normal").toString());
                                            SetupFragment.disconnect();
                                        }
                                    })
                                    .show();
                        }
                    }
                }
                drawerLayout.openDrawer(Gravity.END);
                break;
        }
        invalidateOptionsMenu();
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {                          //如果按返回建會檢查之前使用者藍芽是否開啟
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(getResources().getString(R.string.chinsice0))
                    .setMessage(getResources().getString(R.string.exitmsg))
                    .setIcon(R.drawable.exitrobot)
                    .setPositiveButton(getResources().getString(R.string.confirm_button),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (settings.getInt("isblueexit", 0) == 1) {
                                        BluetoothService.bluetoothAdapter.enable();
                                    } else {
                                        BluetoothService.bluetoothAdapter.disable();
                                    }
                                    MultipleFragment.checken = 0;
                                    Log.e("aaa", "1451");
                                    finish();
                                    android.os.Process.killProcess(android.os.Process.myPid());
                                }
                            })
                    .setNegativeButton(R.string.cancel_button,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                }
                            }).show();
        }
//        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) { //
//            AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//            mAudioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM,AudioManager.ADJUST_RAISE, AudioManager.FX_FOCUS_NAVIGATION_UP);
//        }
//
//        else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) { // 攔截返回鍵
//            AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//            mAudioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM,AudioManager.ADJUST_LOWER, AudioManager.FX_FOCUS_NAVIGATION_UP);
//        }

        return true;
    }


    @Override
    protected void onResume() {                       //程式重啟時執行getdata
        super.onResume();
        Log.e("onResume", "onResume");

        //家豪
        language = (String) getResources().getText(R.string.language);
        //exlanguage = preferences.getString("language","");
        Log.e("languageex", language + "@@" + exlanguage);
        if (!language.equals(exlanguage) && !exlanguage.equals("")) {
            Log.e("languageex2", language + "@@" + exlanguage);
            lan_threadstart = new lan_thread();
            lan_threadstart.start();
            try {
                lan_threadstart.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //
        if (settings.getBoolean("isScan", true)) {
            systemrun = true;
        } else if (!settings.getBoolean("isScan", true)) {
            MultipleFragment.sec5_while = true;
            if (!sec5_thread.isAlive()) {
                sec5_thread.start();
            }
        }
    }

    @Override
    protected void onStop() {                       //程式重啟時執行getdata
        super.onStop();
        Log.e("onStop", "onStop");
        tryapp = true;
    }

    @Override
    protected void onPause() {                       //程式重啟時執行getdata
        super.onPause();
        Log.e("onPause", "onPause");
        systemrun = false;

//        finish() ;
    }

    void bluetoothScan() {                                                         //藍芽掃描
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("bluetoothScan", "開始");

                scanfunction(0, 3000);
                rusume = 1;
            }
        });

//        intent = new Intent(MainActivity.this,BluetoothService.class);    //執行Service服務
//        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
//        Log.e("service", "啟動");

        if (settings.getBoolean("isSingle", true)) { //單數

            if (!checkDevice) {
                // mLeftNumber.setVisibility(View.INVISIBLE);
                // mRightNumber.setVisibility(View.INVISIBLE);
                thread.start();
            }
        } else if (!settings.getBoolean("isSingle", true)) { //多數
            thread.interrupt();
            thread = null;
            if (!ww.isAlive()) {  //執行緒已經啟動但還沒有正常終止或者中止，則為true ，否則為 false。
                ww.start();
            }
            if (datathreadset == 1) {
                datathreadset = 0;
            }
        }
    }

    public void scanfunction(int num, int mmSec) {
        if (datathreadset == 0) {
            if (settings.getBoolean("isSingle", true)) { //單人
                System.out.println("MainScan");
                try {
                    // 先掃三秒確定附近有幾台設備
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            if (systemrun)
                                Toast.makeText(MainActivity.this,
                                        "Looking for nearby device...",
                                        Toast.LENGTH_SHORT).show();
                        }
                    });
//                    startScanDevice();
                    Thread.sleep(mmSec);
//                    stopScanDevice();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (deviceRssiList.size() == 0) {
                    // 沒有設備開啟
                    if (num < 3) {
                        h.post(new Runnable() {
                            @Override
                            public void run() {
                                if (systemrun)
                                    Toast.makeText(MainActivity.this,
                                                    "Not Found. Try again!", Toast.LENGTH_SHORT)
                                            .show();
                            }
                        });
                        scanfunction(++num, 5000);
                    } else {
                        h.post(new Runnable() {
                            @Override
                            public void run() {
                                if (systemrun) {
                                    Toast.makeText(MainActivity.this,
                                            "Not Found. Rest 5 seconds.",
                                            Toast.LENGTH_SHORT).show();
                                    Log.e("TEST", "Not Found. Rest 5 seconds.");
                                }
                            }
                        });
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        scanfunction(0, 3000);
                    }
                } else if (deviceRssiList.size() == 1) {
                    // 一台直接繼續Scan
                    MainActivity.address = deviceRssiList.get(0).split(">>")[1];
                    System.out.println(MainActivity.address + "/startLeScan");
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Found one.",
                                    Toast.LENGTH_SHORT).show();
                            Toast.makeText(MainActivity.this, "Start to get value.",
                                    Toast.LENGTH_SHORT).show();
                            Log.e("Main Tag Address", MainActivity.address);
                            if (!ww.isAlive()) {
                                ww.start();
                            }
                            checkDevice = true;
                        }
                    });
                } else {
                    // 兩台以上跳出讓使用者選
                    // 照Rssi排序
                    //   Collections.sort(deviceRssiList, new RSSIComparator());
                    final String[] device = new String[deviceRssiList.size()];
                    for (int i = 0; i < deviceRssiList.size(); i++) {
                        device[i] = deviceRssiList.get(i);
                        System.out.println(device[i]);
                    }
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            if (!MainActivity.this.isFinishing()) {
                                AlertDialog.Builder DevicesDialog = new AlertDialog.Builder(MainActivity.this);
                                DevicesDialog.setItems(device,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                MainActivity.address = deviceRssiList.get(
                                                        which).split(">>")[1];
                                                if (!ww.isAlive()) {
                                                    ww.start();
                                                }
                                                if (!oo.isAlive()) {
                                                    oo.start();
                                                }
                                                System.out.println(MainActivity.address + "startLeScan");
                                                checkDevice = true;
                                            }
                                        }).create();
                                DevicesDialog.setCancelable(false);
                                DevicesDialog.setTitle(R.string.chosemul);
                                DevicesDialog.show();
                            }
                        }
                    });
                }
            }
        }
    }

    private class timerTask extends TimerTask {  //取得Service的tag內容/每隔1秒取一次
        @Override
        public void run() {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            Log.e("Main","timerTask");
            Message msg = new Message();
            MacHandler.sendMessage(msg);
        }
    }



//    private class JudgeTask extends TimerTask{ //判斷連線
//        @Override
//        public void run() {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    Log.e("connect_Judge", String.valueOf(judgu_service));
//                    if (judgu_service = true) {
//                        if(settings.getBoolean("isScan", true)){
//                            if(!isServiceRunning(MainActivity.this,"com.example.admin.nursemaid1.BluetoothService")){  //萬一途中綁定斷掉，則重新綁定
//                                if (bluetoothService.bluetoothAdapter.isEnabled()) { //如果目前是開啟狀態
//                                    bluetoothService.bluetoothAdapter.disable();   //關閉藍芽
//                                    Log.e("判斷service中途未連接 ", "重新關閉藍芽(1次)");
//                                }
//                                while (bluetoothService.bluetoothAdapter.isEnabled()) {
//                                    bluetoothService.bluetoothAdapter.disable();
//                                    Log.e("判斷service中途未連接 ", "重新關閉藍芽(多次)");
//                                }
//
//                                intent = new Intent(MainActivity.this,BluetoothService.class);    //執行Service服務
//                                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
//                                Log.e("判斷service中途未連接","service重綁");
//
//                                if (!bluetoothService.bluetoothAdapter.isEnabled()) { //如果藍芽已關閉
//                                    bluetoothService.bluetoothAdapter.enable(); //開啟藍芽
//                                    Log.e("判斷service中途未連接", "重新開啟藍芽(1次)");
//                                }
//
//                                while (!bluetoothService.bluetoothAdapter.isEnabled()) {
//                                    bluetoothService.bluetoothAdapter.enable();
//                                    Log.e("判斷service中途未連接", "重新開啟藍芽(多次)");
//                                }
//                            }
//                        }
//                        if(isServiceRunning(MainActivity.this,"com.example.admin.nursemaid1.BluetoothService")){
//                            if(ConnectTimer!=null) {
//                                ConnectTimer.cancel();
//                                ConnectTimer = null;
//                            }
//                        }
//                    }
//
//                }
//
//            }).start();
//
//        }
//    }

    //接收service資料
    Handler MacHandler = new Handler(Looper.myLooper()){     //主要是把Service收到的藍芽資料搬過來而已
        @Override
        public void handleMessage(@NonNull Message msg) {
//            Log.e("blue_Mac.size_if外", String.valueOf(bluetoothService.Mac.size()));
            String temp;
            if (settings.getBoolean("isSingle", true)){
                Log.e("timer","單人模式");
                if (!checkDevice){
                    for(int i = 0;i < BluetoothService.Blue_deviceList.size();i++){
                        if(deviceList.size() == 0){
                            deviceRssiList.add(BluetoothService.Blue_deviceRssiList.get(i));
                            deviceList.add(BluetoothService.Blue_deviceList.get(i));
                            Log.e("timer_deviceList.size()","0");

                            address = BluetoothService.Blue_baddress;
//                            Log.e("isexisttimeraddress",address);
                        }else{
                            temp = BluetoothService.Blue_deviceList.get(i);
                            if (!deviceList.contains(temp)) {
                                deviceRssiList.add(BluetoothService.Blue_deviceRssiList.get(i));
                                deviceList.add(BluetoothService.Blue_deviceList.get(i));

                                address = BluetoothService.Blue_baddress;
//                                Log.e("isexisttimeraddress",address);
                            }
                        }
                    }
                }
//                if(!BluetoothService.Tagtime.equals(null)){
//                    Tagtime.add(BluetoothService.Tagtime);
//                }
//
//                Log.e("ble_time","Main---"+Tagtime);

                if (checkDevice) {
                    if(!firstAddTagtime){
                        for(int i = 0;i < BluetoothService.Blue_deviceList.size();i++) {
                            if (address.equals(BluetoothService.Blue_deviceList.get(i))) {
                                if (!BluetoothService.Tagtime.equals(null)) {
                                    addressBlue_deviceList = i;
                                    Tagtime.add(BluetoothService.Tagtime);
                                    firstAddTagtime=true;
                                    CheckTagExistAddress = address;
                                }
                            }
                        }
                    }

                    if(firstAddTagtime){
                        if(CheckTagExistAddress.equals(BluetoothService.Blue_baddress)){
                            if(!BluetoothService.Tagtime.equals(null)){
                                Tagtime.add(BluetoothService.Tagtime);
                                for(int u=0; u < Tagtime.size();u++){
                                    Log.e("isexisttimerTagtime", Tagtime.get(u));
                                }
//                                Log.e("isexisttimerTagtime.size", String.valueOf(Tagtime.size()));
                            }

                            Log.e("ble_time","Main---"+Tagtime);
                            baddress = BluetoothService.Blue_baddress;
                            rssitimer = BluetoothService.Blue_rssitimer;
                            //isexisttimer = BluetoothService.Blue_isexisttimer;//0216
                            runOnUiThread(new Runnable(){
                                @Override
                                public void run() {
                                    if(!oo.isAlive()) {
                                        oo = new rssithread() ;
                                        oo.start();
                                        Log.e("123","ssssss");
                                    }
                                }
                            });


                            final String mData = BluetoothService.Blue_mData;
                            Log.e("mData",mData);
                            if(!mData.equals("")){
                                final String enable = BluetoothService.Blue_enable ;
                                SingleFragment.dataSet.setisRegist("0");
//                            multi_tosingle = baddress;
                                Log.e("debug_enable",enable);
                                Log.e("debug_baddress",baddress);
                                Log.e("debug_mData",mData);
                                Log.e("debug_mData", String.valueOf(BluetoothService.tempValue));
                                runOnUiThread(new Runnable(){
                                    @Override
                                    public void run() {
                                        singleFragment.setUI(baddress, mData,enable);
                                    }
                                });
                            }
                            try {
                                if(Tagtime.size()==5 && !isTagOffline){//0216
                                    CheckTagExist(BluetoothService.Tagtime);
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }




//                    for(int i = 0;i < BluetoothService.Blue_deviceList.size();i++){
//                        if (address.equals(BluetoothService.Blue_deviceList.get(i))){
//                            if(!BluetoothService.Tagtime.equals(null)){
//                                Tagtime.add(BluetoothService.Tagtime);
//                            }
//
//                            Log.e("ble_time","Main---"+Tagtime);
//                            baddress = BluetoothService.Blue_baddress;
//                            rssitimer = BluetoothService.Blue_rssitimer;
//                            //isexisttimer = BluetoothService.Blue_isexisttimer;
//                            runOnUiThread(new Runnable(){
//                                @Override
//                                public void run() {
//                                    if(!oo.isAlive()) {
//                                        oo = new rssithread() ;
//                                        oo.start();
//                                        Log.e("123","ssssss");
//                                    }
//                                }
//                            });
//
//
//                            final String mData = BluetoothService.Blue_mData;
//                            Log.e("mData",mData);
//                            if(!mData.equals("")){
//                                final String enable = BluetoothService.Blue_enable ;
//                                SingleFragment.dataSet.setisRegist("0");
////                            multi_tosingle = baddress;
//                                Log.e("debug_enable",enable);
//                                Log.e("debug_baddress",baddress);
//                                Log.e("debug_mData",mData);
//                                Log.e("debug_mData", String.valueOf(BluetoothService.tempValue));
//                                runOnUiThread(new Runnable(){
//                                    @Override
//                                    public void run() {
//                                        singleFragment.setUI(baddress, mData,enable);
//                                    }
//                                });
//                            }
//                            try {
//                                if(Tagtime.size()==5 && !isTagOffline){//0216
//                                    CheckTagExist(BluetoothService.Tagtime);
//                                    Log.e("isexisttimerCheckTagExistaddress1",BluetoothService.Blue_baddress);
//                                }
//
//                                if(isTagOffline){//0216
//                                    Log.e("isTagOfflineFalsesize", String.valueOf(Tagtime.size()));
//                                    if(Tagtime.size() == 5){
//                                        isexisttimer++;
//                                        Log.e("isexisttimer++", String.valueOf(isexisttimer));
//
//                                        for(int k = 0; k < 5 ; k++){
//                                            if(Tagtime.get(k).equals(BluetoothService.Tagtime)){
//                                                isTagOffline = true;//離線
//                                                Log.e("isexisttimerisTagOfflineFalse23", String.valueOf(isTagOffline));
//                                            }else{
//                                                isTagOffline = false;//連線
//                                                Log.e("isexisttimerisTagOfflineFalse24", String.valueOf(isTagOffline));
//                                                CheckTagExist(BluetoothService.Tagtime);
//                                                break;
//                                            }
//                                        }
//                                        Tagtime.clear();
//                                        times = 0;
//                                    }
//
//                                }
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }

                }
                if(isTagOffline){//0216
                    isexisttimer++;
                    Log.e("isexisttimer++", String.valueOf(isexisttimer));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(Tagtime.size() == 5){
                        for(int k = 0; k < 5 ; k++){
                            if(Tagtime.get(k).equals(BluetoothService.Tagtime)){
                                isTagOffline = true;//離線
                            }else{
                                isTagOffline = false;//連線
                                try {
                                    CheckTagExist(BluetoothService.Tagtime);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                break;
                            }
                        }
                        Tagtime.clear();
                        times = 0;
                    }
                }

            }else if(!settings.getBoolean("isSingle", true)){
                multi_address = BluetoothService.Blue_multi_address;
                multi_data = BluetoothService.multi_data;
//                Log.e("MainActivity_multi_data","有沒有東西"+multi_data);
                Log.e("timer","多人模式");
                if(!multi_address.isEmpty() && !multi_data.isEmpty()){
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            MultipleFragment.mDevicesListAdapter.additem(multi_address, multi_data);
                            Message msg4 = new Message() ;
                            msg4.what = 4;
                            mHandler.sendMessage(msg4);
                        }
                    });
                    Multi_existtimer = 0;
                    Log.e("multi", "timer = 0");
                }
//                try {
//                    if(Tagtime.size()==5){
//                        CheckTagExist(BluetoothService.Tagtime);
//                    }
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }

        }
    };

    private void CheckTagExist(String ble_time) throws InterruptedException {
//        Log.e("isexisttimerTagtime.size", String.valueOf(Tagtime.size()));
        CheckTagExistAddress = address;
        Log.e("isexisttimer", String.valueOf(isexisttimer));
        if(Tagtime.size() == 5){
            for(int i = 0; i < 5 ; i++){
                if(Tagtime.get(i).equals(ble_time)){
                    times++;
                }else{
                    isTagOffline = false;//0216
                    isexisttimer = 0;//0216
                }
            }
            Tagtime.clear();
        }
        Log.e("Tagtime.times", String.valueOf(times));
        if(times == 5){
//            Tagtime.clear();//0216
//            isTagOffline = true;//tag離線

            SingleFragment.isScan = true;//0729

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                startScanDevice();
            }else{
                if(isServiceRunning(MainActivity.this,"com.example.admin.nursemaid1.BluetoothService")){
                    unbindService(serviceConnection);

                    Log.e("CheckTagExist_service","service解綁");
                }

                if(bluetoothService.bluetoothAdapter.isEnabled()) {
                    bluetoothService.bluetoothAdapter.disable();
                    Log.e("CheckTagExist_close ", "藍牙關閉(1次)");
                    sleep(3000);
                }

                while (bluetoothService.bluetoothAdapter.isEnabled()) {
                    bluetoothService.bluetoothAdapter.disable();
                    Log.e("CheckTagExist_close ", "藍牙關閉(多次)");
                    sleep(3000);
                }

                if(!bluetoothService.bluetoothAdapter.isEnabled()) {
                    bluetoothService.bluetoothAdapter.enable();
                    Log.e("CheckTagExist_open", "藍芽開啟(1次)");
                    sleep(3000);
                }

                while (!bluetoothService.bluetoothAdapter.isEnabled()) {
                    bluetoothService.bluetoothAdapter.enable();
                    Log.e("CheckTagExist_open", "藍芽開啟(多次)");
                    sleep(3000);
                }

                if(!isServiceRunning(MainActivity.this,"com.example.admin.nursemaid1.BluetoothService")){
                    intent = new Intent(MainActivity.this,BluetoothService.class);    //執行Service服務
                    bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
                    Log.e("CheckTagExist__service","service重綁");
                    SingleFragment.isScan = false;//0729
                }
            }



            isTagOffline = true;//tag離線
        }
        times = 0;
    }
    public static boolean isServiceRunning(Context context, String serviceName) {
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningServiceInfo> runningServiceInfos = am.getRunningServices(255);
            if (runningServiceInfos.size() <= 0) {
                return false;
            }
            for (ActivityManager.RunningServiceInfo serviceInfo : runningServiceInfos) {
                if (serviceInfo.service.getClassName().equals(serviceName)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    //掃描到藍芽封包回到此函示callback
//    private BluetoothAdapter.LeScanCallback leScanDeviceCallback = new BluetoothAdapter.LeScanCallback() {
//        @Override
//        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] record) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    ArrayList<String> intValueArray = new ArrayList<>();
//                    String data = "";
//                    for (int i = 0; i < record.length; i++) {
//                        String hexString = Integer.toHexString(Byte.valueOf(record[i]).intValue() & 0xff);
//                        if (hexString.length() == 1) {
//                            hexString = "0" + hexString;
//                        }
//                        intValueArray.add(hexString);
//                        data += intValueArray.get(i);
//                    }
//                    if (data.substring(10, 14).equals("9687")) {
//                        if(data.substring(18, 20).equals("05")||data.substring(18, 20).equals("03")||data.substring(18, 20).equals("06")) {
//                            if (settings.getBoolean("isSingle", true)) {//若為單數模式
//                                if (!checkDevice) {
//                                    if (record[5] == (byte) 0x96 && record[6] == (byte) 0x87 && record[7] == (byte) 0x59 && record[8] == (byte) 0x80) {
//                                        if (!deviceList.contains(device.getAddress())) {
//                                            deviceRssiList.add("Rssi: " + rssi + " dBm\n>>" + device.getAddress());
//                                            deviceList.add(device.getAddress());
//                                        }
//                                    }
//                                }
//                                if (checkDevice) {
//                                    if (MainActivity.address.equals(device.getAddress())) {
//                                        baddress = device.getAddress();
//                                        rssitimer = 0 ;
//                                        isexisttimer = 0;
//                                        if(!oo.isAlive()) {
//                                            oo = new rssithread() ;
//                                            oo.start();
//                                            Log.e("123","ssssss");
//                                        }
//                                        Log.e("rssi",String.valueOf(rssi));
//                                        if(rssi<=-90) {
//                                            if(numRssi!=5) {
//                                                //checkrssi[numRssi] = rssi;
//                                                numRssi++;
//                                            }
//                                        }else {
//                                            numRssi = 0 ;
//                                        }
//
//                                        /*if(numRssi==3){
//                                            Message msg = new Message();
//                                            rssihandle.sendMessage(msg);
//                                            numRssi = 5 ;
//                                        }*/
//                                        settings.edit().putString("baddress",baddress).commit();
//                                        String mData = data.toUpperCase().substring(0, 62);
//                                        String enable = "0" ;
//                                        SingleFragment.dataSet.setisRegist("0");
//                                        multi_tosingle = device.getAddress();
//                                        singleFragment.setUI(device.getAddress(), mData,enable);
//                                    }
//                                    //singleFragment.getAddress(device.getAddress());
//                                }
//
//                            } else if (!settings.getBoolean("isSingle", true)) {//若為多數模式
//
////                                logToDisplay(device.getAddress());
//                                Log.e("multi tag quantity: ","Address: "+device.getAddress() + "31byte:" + data);
//                                MultipleFragment.mDevicesListAdapter.additem(device.getAddress(), data);
//                                Message msg = new Message() ;
//                                msg.what = 4;
//                                mHandler.sendMessage(msg);
//                                Multi_existtimer = 0;
//                                Log.e("multi", "timer = 0");
//                            }
//                        }
//                    }
//                }
//            });
//        }
//    };

//    public void startScanDevice() {
//        BluetoothService.leScanDeviceCallback le;
//        BluetoothAdapter.LeScanCallback leScanDeviceCallback;
//        if(btAdapter.isEnabled()){
//            BluetoothService.bluetoothAdapter.startLeScan(leScanDeviceCallback);
//        }else{
//            try {
//                btAdapter.enable();
//                Log.e("startScanDeviceElse","inla");
//                Thread.sleep(1500);
//                MainActivity.btAdapter.startLeScan(leScanDeviceCallback);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//        //Log.e("btstart","now"+btAdapter.getState());
//    }
//
//    public void stopScanDevice() {
//        MainActivity.btAdapter.stopLeScan(leScanDeviceCallback);
//        //Log.e("btstart","before"+btAdapter.getState());
//    }

    ///-------------------------------------------------------------------------------改
//    public void startScanDevice() {  //開始掃描
//        intent = new Intent(MainActivity.this,BluetoothService.class);    //執行Service服務
//        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
//        Log.e("service", "啟動");
//    }
//
//    public void stopScanDevice() {
//        Thread close = new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                unbindService(serviceConnection);
//                while (BluetoothService.bluetoothAdapter.isEnabled()) {
//                    BluetoothService.bluetoothAdapter.disable();
//                    Log.e("Bluetooth is not close ", "藍芽關閉");
//                }
//            }
//        };
//        close.start();
//    }
    public void startScanDevice() {
        if(bluetoothService.bluetoothAdapter.isEnabled()){  //判斷目前藍芽是否開啟
            bluetoothService.btScanner.startScan(null,bluetoothService.createScanSetting(),bluetoothService.ScanCallback);  //掃描藍芽
        }else{
            try {
                bluetoothService.bluetoothAdapter.enable();
                Log.e("startScanDeviceElse","inla");
                sleep(1500);
                bluetoothService.btScanner.startScan(null,bluetoothService.createScanSetting(),bluetoothService.ScanCallback);  //掃描藍芽
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //Log.e("btstart","now"+btAdapter.getState());
    }
    ///-------------------------------------------------------------------------------

    class scanthread extends Thread { //Scanwindow當藍芽三秒沒掃到裝置時延長五秒，最多延長到十五秒
        @Override
        public void run() {
            try {
                if(!iu.isAlive()){
                    iu.start();
                }
                while (datathreadset==0) {
                    if(isexisttimer>scanwindow&&scanwindow<15&&scanwindow==3){
                        scanwindow = 5 ;
                    }
                    else if(isexisttimer>scanwindow&&scanwindow<15){
                        scanwindow = scanwindow + 5 ;
                    }else if(scanwindow>5&&isexisttimer<5){
                        scanwindow = scanwindow - 5 ;
                    }else if(scanwindow==5&&isexisttimer<5){
                        scanwindow = 3 ;
                    }
//                    MainActivity.btAdapter.startLeScan(leScanDeviceCallback);
                    Log.e("scanwindow",String.valueOf(scanwindow)) ;
                    sleep(scanwindow*1000);
//                    MainActivity.btAdapter.stopLeScan(leScanDeviceCallback);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class rssithread extends Thread {  //當tag 90秒沒收到資料時不顯示資料
        @Override
        public void run() {
            try {
                while (ooo) {
                    if(rssitimer>=90){
                        Message msg = new Message();
                        rssihandle.sendMessage(msg);
                        break;
                    }
                    rssitimer++ ;
                    sleep(1000);
                    if(isexisttimer>90){//tag離線90秒跳通知0216

                        isexisttimer=0;
                        Log.e("isexisttimer123", String.valueOf(isexisttimer));
                        Message msg = new Message();
                        rssihandle.sendMessage(msg);

                        break;
                    }
                    sleep(1000);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class isexistThread extends Thread { //iu
        @Override
        public void run() {
            try {
                while (true){
                    if (isexist==true) {
                        if(settings.getBoolean("isScan",true)) {
                            if (MainActivity.settings.getBoolean("isSingle", true)) { //單人
                                Log.e("single",String.valueOf(isexisttimer)) ;
                                if (isexisttimer >= 90) {
                                    isstate = 1;
                                    singleFragment.dataSet.setisState(0);
                                    SingleEable = "1";
                                    Message msg = new Message();
                                    msg.obj = "0201060CFF968759800306810C00FE0007160918930C00FE06160F1857C522";
                                    //test
                                    registdata = "0201060CFF968759800306810C00FE0007160918930C00FE06160F1857C522";
                                    singleui.sendMessage(msg);
                                } else {
                                    isstate = 0;
                                    singleFragment.dataSet.setisState(1);
                                }
                                if(multi_tosingle.equals("")){
                                    Log.e("single","m to s");
                                }else{
                                    isexisttimer++;
                                }
                            }else {  //多人
                                Log.e("multi",String.valueOf(Multi_existtimer));
                                for(int j=0;j<DeviceListAdapter.dataSet.size();j++){
                                    int p = 0, p2 = 0 ;
                                    p2 = DeviceListAdapter.dataSet.get(j).getspittimmer() ;
                                    p2++ ;
                                    DeviceListAdapter.dataSet.get(j).setspittimmer(p2);

                                    p = DeviceListAdapter.dataSet.get(j).getTimmer() ;
                                    p++ ;
                                    DeviceListAdapter.dataSet.get(j).setTimmer(p);

                                    if(DeviceListAdapter.dataSet.get(j).getTimmer() >= 90){
                                        DeviceListAdapter.dataSet.remove(j);
                                        j-- ;
                                        //Log.e("bletest","1186");
                                    }
                                }
                                Log.e("multi tag quantity:T ",String.valueOf(DeviceListAdapter.dataSet.size()));
                                Message msg = new Message() ;
                                msg.what = 4;
                                mHandler.sendMessage(msg);
                                Multi_existtimer++;
                            }
                        }else {
                            if(customadapter.dataSet!=null) {
                                for (int j = 0; j < customadapter.dataSet.size(); j++) {
                                    int p2 = 0;
                                    p2 = customadapter.dataSet.get(j).getspittimmer();
                                    if(p2!=0) {
                                        p2++;
                                        customadapter.dataSet.get(j).setspittimmer(p2);
                                    }
                                }
                            }
                        }
                        sleep(1000);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.sinopulsar.nursemaid1.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                    String messge = intent.getStringExtra(KEY_MESSAGE);
                    String extras = intent.getStringExtra(KEY_EXTRAS);
                    StringBuilder showMsg = new StringBuilder();
                    showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                    if (!ExampleUtil.isEmpty(extras)) {
                        showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                    }
                }
            } catch (Exception e){
            }
        }
    }

    private AlertDialog setConnectDialog() {                                      //要修改ip時會執行，米號是為了安全而不顯示
        final View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_setconnect, null);
        //final BleDevice tmpitem = mDevicesListAdapter.getItemAddress(position);
        AlertDialog.Builder tmpDialog = new AlertDialog.Builder(MainActivity.this);
        tmpDialog.setTitle(getResources().getString(R.string.setip));
        tmpDialog.setView(dialogView);
        final EditText editText3 = (EditText) dialogView
                .findViewById(R.id.editText3);
        ip = settings.getString("ip", "120.105.161.186") ;
        ipprocet="" ; ipsee="" ;
        if(ip.length()>12){
            ipsee = ip.substring(ip.length()-3,ip.length());
            for(int i=1;i<ip.length()-2;i++){
                if(i%4!=0) {
                    ipprocet = ipprocet + "*";
                }
                else {
                    ipprocet = ipprocet + ".";
                }
            }
        }
        editText3.setText(ipprocet + ipsee);
        tmpDialog.setNeutralButton(getResources().getString(R.string.chinsice10), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.chinsice10), Toast.LENGTH_SHORT).show();
            }
        });
        tmpDialog.setPositiveButton(getResources().getString(R.string.chinsice2), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                final String ipset = editText3.getText().toString();
                if(!ipset.equals("")) {
                    if(ipset.length()>12) {
                        if (ipset.equals(ipprocet + ipsee)) {
                            Log.e("ip", ip);
                        } else {
                            ip = ipset;
                            settings.edit().putString("ip", ip).commit();
                            Log.e("ip2", ip);
                        }
                    }
                }
            }
        });
        return tmpDialog.create();
    }

    // always verify the host - dont check for certificate
    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    /**
     * Trust every server - dont check for any certificate
     */
    private static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }

            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
        } };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getdata() {
        final Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                TorkenDBhelper mdbhelper = new TorkenDBhelper(MainActivity.this);
                SQLiteDatabase db;

                db = mdbhelper.getReadableDatabase();
                Cursor c = db.rawQuery("SELECT * FROM " + DBcontract.DBcol.TABLE_NAME, null);

                c.moveToFirst();
//                String itemrId = c.getString(
//                        c.getColumnIndexOrThrow(DBcontract.DBcol.COLUMN_Rrgister_ID)
//                );

//                Log.e("sql:", itemrId);

//                MainActivity.token = itemrId;
                db.close();
                String postParameter = "token=" + MainActivity.token;
                Log.e("string=", postParameter);
                URL url = null;

                try {
                    SimpleDateFormat sDateFormat1 = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
                    String checkin = sDateFormat1.format(new java.util.Date());

                    //Log.e("checktime","intime=" + checkin);

                    url = new URL("https://"+MainActivity.ip+"/jpushex/getdata.php?" + postParameter);
                    //Log.e("checktime",url.toString());

                    HttpURLConnection http = null;
                    if (url.getProtocol().toLowerCase().equals("https")) {
                        trustAllHosts();
                        HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
                        https.setHostnameVerifier(DO_NOT_VERIFY);
                        http = https;
                    } else {
                        http = (HttpURLConnection) url.openConnection();
                    }

//                    HttpURLConnection urlConnection = (HttpURLConnection) url
//                            .openConnection();
                    http.setRequestMethod("GET");
                    http.setRequestProperty("Accept",
                            "application/json");
                    http.setRequestProperty("Content-Type",
                            "application/x-www-form-urlencoded");// setting your headers its a json in my case set your appropriate header

                    http.setDoOutput(true);
                    http.connect();// setting your connection

                    StringBuffer buffer = new StringBuffer();
                    InputStream is = http.getInputStream();
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(is));
                    String line = null;
                    while ((line = br.readLine()) != null)
                        buffer.append(line);
                    // reading your response

                    is.close();
                    http.disconnect();// close your connection

                    String res = buffer.toString();
                    Log.e("res", res);
                    JSONObject jObject = new JSONObject(res);
                    //     JSONObject data = jObject.getJSONObject("data"); // get data object
                    String ret = jObject.getString("RetCode"); // get the name from data.

                    Log.e("ret", ret);
                    Log.e("res", res);

                    int yyy = 0 ;

                    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
                    String phonetime = sDateFormat.format(new java.util.Date());

                    if (ret.equals("0")) {
                        JSONArray jsonArr = jObject.getJSONArray("data");
                        for (int i = 0; i < jsonArr.length(); i++) {  // **line 2**
                            JSONObject childJSONObject = jsonArr.getJSONObject(i);
                            mac = childJSONObject.getString("device");
                            String data = childJSONObject.getString("dataFormated");
                            String rssi = childJSONObject.getString("rssi");
                            String enableOrDisable = childJSONObject.getString("enable");//0是ENABLE，1是DISABLE
                            //  int rssivalue = Integer.parseInt(rssi,16);
                            String state = childJSONObject.getString("state");
                            String time = childJSONObject.getString("time");//0是ENABLE，1是DISABLE

                            int checktime = 0 ;

                            int www = 0 ;

                            for(int j=0;j<deviceList.size();j++){
                                if(mac.equals(deviceList.get(j))){
                                    www = 1 ;
                                    break;
                                }
                            }
                            if(www == 0 ){
                                deviceRssiList.add("Rssi: " + rssi + " dBm\n>>" + mac);
                                deviceList.add(mac);
                            }

                            if (data.length() > 60) {
                                char sos_char = data.charAt(61);
                                String sos_string = String.valueOf(sos_char);
                            }
                            if (data.length() > 50) {
                                if (time.substring(0, 14).equals(phonetime.substring(0, 14))) {
                                    if (time.substring(14, 15).equals(phonetime.substring(14, 15))) {
                                        int sec1 = Integer.valueOf(time.substring(16, 18));
                                        int sec2 = Integer.valueOf(phonetime.substring(16, 18));
                                        if (sec2 - sec1 < 60) {
                                            Log.e("checktime",String.valueOf(sec2-sec1)+" time "+ time +" phonetime " + phonetime);
                                            checktime = 1;                                      //1是可用
                                        }
                                    } else {//改過的
                                        //Log.e("checktime","in");
                                        int min1 = Integer.valueOf(time.substring(14, 15));//資料分鐘
                                        int min2 = Integer.valueOf(phonetime.substring(14, 15));//手機分鐘

                                        int sec1 = Integer.valueOf(time.substring(16, 18));//資料秒數
                                        int sec2 = Integer.valueOf(phonetime.substring(16, 18));//手機秒數
                                        if(min2 != 0){
                                            if (min2 - min1 == 1) {
                                                //int sec1 = Integer.valueOf(time.substring(16, 18));
                                                //int sec2 = Integer.valueOf(phonetime.substring(16, 18));
                                                if (sec2 + 60 - sec1 < 60) {
                                                    checktime = 1;                                      //1是可用
                                                    Log.e("checktime","!=0 "+String.valueOf(sec2-sec1)+" time "+ time +" phonetime " + phonetime);
                                                }
                                            }
                                        }

                                    }
                                }else{
                                    Log.e("checktime","inbao");
                                    int min2 = Integer.valueOf(phonetime.substring(14, 15));//手機分鐘

                                    int sec1 = Integer.valueOf(time.substring(16, 18));//資料秒數
                                    int sec2 = Integer.valueOf(phonetime.substring(16, 18));//手機秒數
                                    if(min2==0){
                                        if (sec2 + 60 - sec1 < 60) {
                                            checktime = 1;                                      //1是可用
                                            Log.e("checktime","==0 "+String.valueOf(sec2-sec1)+" time "+ time +" phonetime " + phonetime);
                                        }
                                    }
                                }

                                if (settings.getBoolean("isSingle", true)) {                                //單數模式推波
                                    if(mac.equals(baddress)) {
                                        SingleEable = enableOrDisable;

                                        singleFragment.dataSet.setisRegist("0");
                                        if (checktime == 1) {
                                            singleFragment.dataSet.setisState(1);
                                            yyy = 1;
                                            if (settings.getBoolean("isEnable", true)) {
                                                match(baddress, "true");
                                            } else if (!settings.getBoolean("isEnable", true)) {
                                                match(baddress, "false");
                                            }
                                            if (singleFragment != null) {
                                                Message msg = new Message();
                                                msg.obj = data.toString();
                                                //test
                                                registdata = data.toString();
                                                singleui.sendMessage(msg);
                                            } else {
                                                Log.e("singleFragment", String.valueOf(data.length()));
                                            }
                                        } else {                                                       //單數模式沒找到已註冊tag會執行
                                            yyy = 1 ;
                                            singleFragment.dataSet.setisState(0);       //沒找到
//                                            SingleEable = "0";
                                            Log.e("checktime","Phonetime= " + phonetime +" time= "+ time);
                                            if (singleFragment != null) {
                                                Message msg = new Message();
                                                msg.obj = "0201060CFF968759800306810C00FE0007160918930C00FE06160F1857C522";
                                                //test
                                                registdata = "0201060CFF968759800306810C00FE0007160918930C00FE06160F1857C522";
                                                singleui.sendMessage(msg);
                                            } else {
//                                singleFragment = new SingleFragment();131417
                                            }
                                        }
                                    }
                                }else if (!settings.getBoolean("isSingle", true)) {                       //多數模式推波
                                    if(MultipleFragment.customadapter!=null) {
                                        Log.e("mac"+i,mac);
                                        MultipleFragment.customadapter.additem(mac, data, enableOrDisable, checktime);
                                        yyy = 1 ;
                                        Message msg2 = new Message();
                                        msg2.what = 2;
                                        mHandler.sendMessage(msg2);
                                    }
                                }
                            }

                        }
                        if(yyy==0){                                                       //單數模式沒找到已註冊tag會執行
                            singleFragment.dataSet.setisRegist("1");
                            SingleEable = "1";
                            baddress = "null" ;
                            address = "null" ;
                            if (singleFragment!=null) {
                                Message msg = new Message();
                                msg.obj = "0201060CFF968759800306810C00FE0007160918930C00FE06160F1857C522" ;
                                //test
                                registdata = "0201060CFF968759800306810C00FE0007160918930C00FE06160F1857C522";
                                singleui.sendMessage(msg);
                            }else {
//                                singleFragment = new SingleFragment();131417
                            }
                        }
                    } else if (ret.equals("1")) {
                        //token不正確
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        thread.start();
    }

    Thread sec5_thread = new Thread() {
        @Override
        public void run() {
            int count = 4;
            super.run();
            while (MultipleFragment.sec5_while) {
                try {
                    sleep(1000);
                    count += 1;
                    if (count == 5) {
                        getdata();
                        count = 0 ;
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void match(final String mac, final String ableBoolean) {                            //註冊tag的php
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                String postParameter = "token=" + MainActivity.token + "&device=" + mac + "&enable=" + ableBoolean;
                Log.e("string=", postParameter);
                URL url = null;

                try {
                    url = new URL("https://"+MainActivity.ip+"/jpushex/device_register.php?" + postParameter);

                    HttpURLConnection http = null;
                    if (url.getProtocol().toLowerCase().equals("https")) {
                        trustAllHosts();
                        HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
                        https.setHostnameVerifier(DO_NOT_VERIFY);
                        http = https;
                    } else {
                        http = (HttpURLConnection) url.openConnection();
                    }

//                    HttpURLConnection urlConnection = (HttpURLConnection) url
//                            .openConnection();
                    http.setRequestMethod("GET");
                    http.setRequestProperty("Accept",
                            "application/json");
                    http.setRequestProperty("Content-Type",
                            "application/x-www-form-urlencoded");// setting your headers its a json in my case set your appropriate header

                    http.setDoOutput(true);
                    http.connect();// setting your connection

                    Log.e("response:", http.getResponseCode() + "");
                    StringBuffer buffer = new StringBuffer();
                    InputStream is = http.getInputStream();

                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(is));
                    String line = null;
                    while ((line = br.readLine()) != null)
                        buffer.append(line);
                    // reading your response

                    is.close();
                    http.disconnect();// close your connection
                    String res = buffer.toString();
                    Log.e("res", res);
                    JSONObject jObject = new JSONObject(res);
                    String ret = jObject.getString("RetCode"); // get the name from data.
                    Log.e("RET", ret);
                    String RetMsg = jObject.getString("RetMsg");
                    //          String address = buffer.toString().substring(2);

                    String rqwe = "" ;
                    if(ableBoolean.equals("true")){
                        rqwe = "0" ;
                    }else {
                        rqwe = "1" ;
                    }

                    if (ret.equals("0")) {
                        //配對成功
                        // timer.schedule(new Sendtask(username), 0);
                        for (int i = 0; i < customadapter.dataSet.size(); i++) {
                            if (customadapter.dataSet.get(i).macaddress.equals(mac)) {
                                customadapter.dataSet.get(i).setisRegist("0");
                                customadapter.dataSet.get(i).setIsenable(rqwe);

                                //  mRegistListAdapter.notifyDataSetChanged();
                            }
                        }

                        Log.e("RetMsg", RetMsg);
                    } else if (ret.equals("1")) {
                        //token不正確
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = getResources().getText(R.string.token_not_correct);
                        mHandler.sendMessage(msg);
                        Log.e("RetMsg", RetMsg);
                    } else if (ret.equals("2")) {
                        //tag已和別的手機配對
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = getResources().getText(R.string.tag_not_exist_in_database);
                        mHandler.sendMessage(msg);
                        Log.e("RetMsg", RetMsg);
                    } else if (ret.equals("3")) {
                        //tag已和別的手機配對
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = "tag已和其他手機配對";
                        mHandler.sendMessage(msg);
                        Log.e("RetMsg", RetMsg);
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    private AlertDialog rssiDialog() {
        AlertDialog.Builder tmpDialog = new AlertDialog.Builder(this);
        tmpDialog.setTitle(getResources().getString(R.string.chinsice0));
        tmpDialog.setMessage(getResources().getString(R.string.tagrssi));
        tmpDialog.setPositiveButton(getResources().getString(R.string.confirm_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });

        return tmpDialog.create();
    }

    //家豪
    class lan_thread extends Thread {                     //此thread於APP更換語系時會將語系寫入資料庫中，讓推波時能依手機語系發送對應通知。
        @Override
        public void run() {
            language = (String) getResources().getText(R.string.language);
            Log.e("languageTHREAD:",language);
            String rid = JPushInterface.getRegistrationID(getApplicationContext());
            String postParameter = "username=" + username + "&password=" + password + "&registrationid=" + rid + "&language="+language+"&app=1-android";
            URL url = null;
            try{

                url = new URL("https://"+ip+"/jpushex/login.php?" + postParameter);

                HttpURLConnection http = null;
                if (url.getProtocol().toLowerCase().equals("https")) {
                    trustAllHosts();
                    HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
                    https.setHostnameVerifier(DO_NOT_VERIFY);
                    http = https;
                } else {
                    http = (HttpURLConnection) url.openConnection();
                }

//                HttpURLConnection urlConnection = (HttpURLConnection) url
//                        .openConnection();
                http.setRequestMethod("GET");
                http.setRequestProperty("Accept",
                        "application/json");
                http.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");// setting your headers its a json in my case set your appropriate header

                http.setDoOutput(true);
                http.connect();// setting your connection

                StringBuffer buffer = new StringBuffer();
                InputStream is = http.getInputStream();
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(is));
                String line = null;
                while ((line = br.readLine()) != null) {
                    buffer.append(line);
                    Log.e("line",line);
                }
                // reading your response

                is.close();
                http.disconnect();// close your connection

                String res = buffer.toString();
                Log.e("res1", res);
                JSONObject jObject = new JSONObject(res);
                token = jObject.getString("Token");
                Log.e("token",token);
                preferences.edit()
                        .putString("account", username)
                        .putString("password", password)
                        .putString("rid", rid)
                        .putString("token", token)
                        .putString("language", language).commit();
                TorkenDBhelper mdbhelper = new TorkenDBhelper(getApplicationContext());
                SQLiteDatabase db = mdbhelper.getWritableDatabase();
                String count = "SELECT * FROM rid";
                Cursor mcursor = db.rawQuery(count, null);
                mcursor.moveToFirst();
                int icount = mcursor.getCount();
                if (icount > 0) {
                    String id = "1"; //修改id為1的資料
                    ContentValues values = new ContentValues();
                    values.put(DBcontract.DBcol.COLUMN_Rrgister_ID, token);
                    db.update(DBcontract.DBcol.TABLE_NAME, values, DBcontract.DBcol._ID + "=" + id, null);
                    Log.e("preference", preferences.getString("token", ""));
                }
                Log.e("6969595929","9969698888888888");

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch(JSONException e){
                e.printStackTrace();
            }
        }
    }
    //



    public void setItemName(String address , String name) {
        //Log.d(TAG, "address=" + address);
        // mNames.set(position, name);
        //  int position = dataSet.indexOf(listitem);
        if (SingleFragment.courseDAO.update(address,name)) {
            Toast.makeText(MainActivity.mParent, "Edit Name Success", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.mParent, "error", Toast.LENGTH_SHORT).show();
        }
    }

    public Boolean CheckPermission() {

        ArrayList<String> requestPermission = new ArrayList<String>() {{
            add(Manifest.permission.READ_PHONE_STATE);
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            add(Manifest.permission.ACCESS_FINE_LOCATION);
            add(Manifest.permission.BLUETOOTH);
            add(Manifest.permission.BLUETOOTH_ADMIN);
        }};

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestPermission.add(Manifest.permission.BLUETOOTH_SCAN);
            requestPermission.add(Manifest.permission.BLUETOOTH_CONNECT);
        }
//0711直接給定精準定位
        if (requestPermission.stream().anyMatch(i -> ActivityCompat.checkSelfPermission(this, i) == PERMISSION_DENIED)) {
            // Arrays.copyOf(requestPermissions.toArray(), requestPermissions.size(), String[].class)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("位置權限")
                        .setMessage("為了能夠正確找尋到Tag，請允許Nursemaid可以取用\"精確\"位置！")
                        .setPositiveButton("確定", ((dialog, which) -> {
                            dialog.dismiss();
                            ActivityCompat.requestPermissions(this, requestPermission.toArray(new String[0]), REQUEST_PERMISSIONS_CODE);
                        }))
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, requestPermission.toArray(new String[0]), REQUEST_PERMISSIONS_CODE);
            }
        }

        requestPermissions(requestPermission.toArray(new String[0]), PERMISSION_REQUEST);

        for (String a : requestPermission) {
            if (ContextCompat.checkSelfPermission(this, a) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "尚未取得權限", Toast.LENGTH_SHORT).show();

                return false;
            }
        }
        return true;
    }

//    public Boolean CheckPermission() {
//
//        //檢查是否取得權限
//        int permissionCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
//        int permissionStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        int permissionLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
//
//        if(permissionCamera != PackageManager.PERMISSION_GRANTED || permissionStorage != PackageManager.PERMISSION_GRANTED || permissionLocation != PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(MainActivity.this,
//                    new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION},1);
//        }
//
//        if(permissionCamera != PackageManager.PERMISSION_GRANTED || permissionStorage != PackageManager.PERMISSION_GRANTED || permissionLocation != PackageManager.PERMISSION_GRANTED){
//            return false;
//        }else {
//            //LogFileCreateAndDelete();
//            Log.e("dateCheckP","???");
//            return true;
//        }
//    }


    void BluetoothCloseThread(){    //10分鐘斷線重連
        Thread restartthread = new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (true) {
                    startScanDevice();
                    if (MainActivity.settings.getBoolean("isScan", true)) {
                        try {
                            sleep(4900);
                            openble += 5;     //紀錄藍牙重開時間600秒
                            Log.e("Bluetooth_openble", this.getName()+"  "+String.valueOf(openble));

                            if (openble >= 600 ) {                                       //每10分鐘藍芽重開
                                // Log.e("openble", String.valueOf(openble));
                            /*先關閉一次，再迴圈檢查是否已經關閉，若沒關就在休息一秒，再關閉一次
                               關閉之後，確認關閉後才打開，打開未成功，休息一秒，再打開一次*/

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                                    if (bluetoothService.bluetoothAdapter.isEnabled()) {  //判斷目前藍芽是否開啟
                                        bluetoothService.btScanner.stopScan(bluetoothService.ScanCallback);  //掃描藍芽
                                    }

                                    sleep(3000);
                                    if (bluetoothService.bluetoothAdapter.isEnabled()) {  //判斷目前藍芽是否開啟
                                        bluetoothService.btScanner.startScan(null,bluetoothService.createScanSetting(),bluetoothService.ScanCallback);  //掃描藍芽
                                    } else {
                                        try {
                                            bluetoothService.bluetoothAdapter.enable();
                                            Log.e("startScanDeviceElse", "inla");
                                            sleep(1500);
                                            bluetoothService.btScanner.startScan(null,bluetoothService.createScanSetting(),bluetoothService.ScanCallback);  //掃描藍芽
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }else{
                                    if(isServiceRunning(MainActivity.this,"com.example.admin.nursemaid1.BluetoothService")){
                                        unbindService(serviceConnection);
                                        Log.e("Bluetooth_service","service解綁");
                                    }
                                    if (bluetoothService.bluetoothAdapter.isEnabled()) { //如果目前是開啟狀態
                                        bluetoothService.bluetoothAdapter.disable();   //關閉藍芽
                                        sleep(2000);
                                    }

                                    while (bluetoothService.bluetoothAdapter.isEnabled()) {
                                        bluetoothService.bluetoothAdapter.disable();
                                        Log.e("Bluetooth is not close ", "藍芽10分鐘還沒關閉");
                                        sleep(2000);
                                    }

//                                Log.e("Bluetooth is close ", "藍芽10分鐘已關閉");//0:該關閉已關閉
                                    sleep(1000);
                                    Log.e("Bluetooth is close ", "藍芽10分鐘已關閉");//0:該關閉已關閉
                                    if (!bluetoothService.bluetoothAdapter.isEnabled()) { //如果藍芽已關閉
                                        bluetoothService.bluetoothAdapter.enable(); //開啟藍芽
                                        Log.e("Bluetooth is not close ", "藍芽一次就開啟");
                                        sleep(2000);
                                    }

                                    sleep(1000);
                                    while (!bluetoothService.bluetoothAdapter.isEnabled()) {
                                        bluetoothService.bluetoothAdapter.enable();
                                        Log.e("Bluetooth is not open", "藍芽10分鐘後重新打開失敗");
                                        sleep(2000);
                                    }

                                    if(!isServiceRunning(MainActivity.this,"com.example.admin.nursemaid1.BluetoothService")){
                                        intent = new Intent(MainActivity.this,BluetoothService.class);    //執行Service服務
                                        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
                                        Log.e("Bluetooth_service","service重綁");
                                    }
                                }


                                Log.e("Bluetooth is open", "藍芽10分鐘後重新打開");
                                openble = 0;
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }else{
//                        if(BluetoothService.bluetoothAdapter.enable()){
////                            BluetoothService.bluetoothAdapter.disable();
//                            Log.e("openble","Notify bt close");
//                        }
//                        if(isServiceRunning(MainActivity.this,"com.example.admin.nursemaid1.BluetoothService")){
//                            unbindService(serviceConnection);
//                            Log.e("Bluetooth_service","service解綁");
//                        }
                        break;
                    }

                }
            }
        };

        if (settings.getBoolean("isScan", true)) {
//            if(!restartthread.isAlive()){

//            }
            restartthread.start();
            Log.e("openble","if ");
        } else if (!settings.getBoolean("isScan", true)) {
            restartthread.interrupt();
            Log.e("openble","else if "+restartthread.getState().toString());
            restartthread = null;
        }
    }

    //用來Debug的程式碼將所有Logcat的資訊寫入手機文字檔中，方便debug，此文字檔路徑為/storage/emulated/0/Nursemaid_Log/
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
            Log.e("dirDelDay", String.valueOf(dirDelDay));
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




//    class Debugeth extends Thread {                                 //用來Debug的程式碼將所有Logcat的資訊寫入手機文字檔中，方便debug，此文字檔路徑為/sdcard/nursemaidlog.txt
//        @Override
//        public void run() {
//            try {
//                java.lang.Process p = Runtime.getRuntime().exec("logcat");
//                final InputStream is = p.getInputStream();
//                final DateFormat df = new SimpleDateFormat("HH:mm:ss");
//                final String nursemaidlogtime = df.format(new Date());
//
//                log_filename = "/sdcard/Nursemaid/" + todayfileName + "/nursemaidlog_" + nursemaidlogtime + ".txt";
//                KickLog_fileDateTime = todayfileName + "_" +  nursemaidlogtime;
//                File logfiledirrr = new File("/sdcard/Nursemaid/" + todayfileName + "/");
//
//                while(!logfiledirrr.exists()){
//                    try {
//                        LogFileCreateAndDelete();
//                        Log.e("logfile","in1");
//                        Thread.sleep(3000);
//                        if(logfiledirrr.exists()){
//                            break;
//                        }
//                    }catch (Exception e){
//                    }
//                }
//                Log.e("??","???");
//                new Thread() {
//                    @Override
//                    public void run() {
//                        FileOutputStream os = null;
//                        try {
//                            Log.e("logfile", "MainActivity_Debugeth LogFile:" + log_filename);
//                            os = new FileOutputStream(log_filename);
//                            int len = 0;
//                            byte[] buf = new byte[1024];
//
//                            while (-1 != (len = is.read(buf))) {
//                                //Log.e("logfile","in2");
//                                os.write(buf, 0, len);
//                                os.flush();
//                                File file123 = new File(log_filename);
//                                if (file123.exists()) {
//                                    int filesize = (int) file123.length();// /1000
//                                    //Log.e("logfile","size:"+((filesize/1000)/1000) +filesize);
//                                    if ((filesize / 1000) / 1000 >= 1) {
//                                        //os = new FileOutputStream("/sdcard/Nursemaid/"+todayfileName+"/nursemaidlog_"+df.format(new Date())+".txt");
//                                        LogFileCreateAndDelete();
//                                        log_filename = "/sdcard/Nursemaid/" + todayfileName + "/nursemaidlog_" + df.format(new Date()) + ".txt";
//                                        Log.e("File", "newlogfile");
//                                        break;
//                                    }
//                                } else {
//                                    Log.e("check", "?????");
//                                    LogFileCreateAndDelete();
//                                    break;
//                                }
//                            }
//                            Thread.sleep(1000);
//                            run();
//                            Log.e("log","re");
//                        } catch (Exception e) {
//                            Log.e("writelog", "read logcat process failed. message: " + e.getMessage());
//                            try {
//                                Thread.sleep(1500);
//                            } catch (InterruptedException ex) {
//                                ex.printStackTrace();
//                            }
//                            run();
//                        } finally {
//                            if (null != os) {
//                                try {
//                                    os.close();
//                                    os = null;
//                                } catch (IOException e) {
//                                    // Do nothing
//                                }
//                            }
//                        }
//                    }
//                }.start();
//            } catch(Exception e){
//                Log.d("writelog", "open logcat process failed. message: " + e.getMessage());
//            }
//        }
//    }
//
//    void LogFileCreateAndDelete(){
//        File NursemaidLogDir = new File("/sdcard/Nursemaid/");
//        Log.e("NursemaidDir","Open");
//
//        if (!NursemaidLogDir.exists()) {// 如果資料夾不存在
//            NursemaidLogDir.mkdir();// 建立資料夾
//        }
//
//        Calendar calendar = Calendar.getInstance();
//        int[] nowtime = new int[]{calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)};
//        todayfileName = "Log_";
//        String dirdelday = "";
//        for (int i = 0; i < nowtime.length; i++) {
//            if (nowtime[i] < 10) {
//                todayfileName += "0" + String.valueOf(nowtime[i]);
//                dirdelday +="0"+ String.valueOf(nowtime[i]);
//            }else{
//                todayfileName += String.valueOf(nowtime[i]);
//                dirdelday += String.valueOf(nowtime[i]);
//            }
//        }
//        Log.e("dateLog",todayfileName);
//
//        File todaydirLog = new File("/sdcard/Nursemaid/"+todayfileName+"/");
//        Log.e("dateLog123",todayfileName);
//
//        if (!todaydirLog.exists()) {// 如果資料夾不存在
//            todaydirLog.mkdir();// 建立資料夾
//        }
//
//        File dirLogFile = new File("/sdcard/Nursemaid/"+todayfileName+"/KickLog/");
//        if (!dirLogFile.exists()) {// 如果資料夾不存在
//            dirLogFile.mkdir();// 建立資料夾
//        }
//
//        if(checkpermissionbool == true){
//
//            File filedel = new File("/sdcard/Nursemaid/");
//            File[] files = filedel.listFiles();
//            if(files!=null){
//                for(int i=0; i<files.length; i++) {
//                    //Log.e("filecheck",files[i].getName());
//                    //Log.e("logdate",dirdelday);
//                    int dirmonth = Integer.valueOf(dirdelday.substring(0, 2));
//                    int dirday = Integer.valueOf(dirdelday.substring(2, 4));
//                    //Log.e("del","month"+dirmonth+"day"+dirday);
//                    //Log.e("delete", "len" + files[i].getName().length());
//                    if (files[i].getName().length() == 8) {
//                        if (files[i].getName().substring(0, 4).equals("Log_")) {
//                            String[] alreadyexistfileday = files[i].getName().split("_");
//                            String checkismath = alreadyexistfileday[1].substring(0, 1);
//                            if (checkismath.equals("1") || checkismath.equals("0")) {
//                                int existfilemonth = Integer.valueOf(alreadyexistfileday[1].substring(0, 2));
//                                int existfileday = Integer.valueOf(alreadyexistfileday[1].substring(2, 4));
//
//                                if (dirmonth == existfilemonth) {
//                                    if (dirday - existfileday > 7) {
//                                        //Log.e("delete",dirday+" "+existfileday+" "+(dirday-existfileday));
//                                        File nowdeletedir = new File(files[i].getAbsolutePath());
//                                        Log.e("delete", files[i].getAbsolutePath());
//                                        File[] nowdeleteAllfile = nowdeletedir.listFiles();
//
//                                        for (int all = 0; all < nowdeleteAllfile.length; all++) {
//                                            if (nowdeleteAllfile[all].getName().equals("KickLog")) {
//                                                File nowdeleteKickLogdir = new File(nowdeleteAllfile[all].getAbsolutePath());
//                                                File[] nowdeleteKickLog = nowdeleteKickLogdir.listFiles();
//                                                for (int kickall = 0; kickall < nowdeleteKickLog.length; kickall++) {
//                                                    nowdeleteKickLog[kickall].delete();
//                                                }
//                                                nowdeleteKickLogdir.delete();
//                                            }
//                                            nowdeleteAllfile[all].delete();
//                                        }
//                                        nowdeletedir.delete();
//                                        //files[i].delete();
//                                    }
//                                } else if (dirmonth > existfilemonth || dirmonth - existfilemonth < 0) {
//                                    dirday = 30 + dirday;
//                                    if (dirday - existfileday > 7) {
//                                        File nowdeletedir = new File(files[i].getAbsolutePath());
//                                        Log.e("delete", files[i].getAbsolutePath());
//                                        File[] nowdeleteAllfile = nowdeletedir.listFiles();
//                                        for (int all = 0; all < nowdeleteAllfile.length; all++) {
//                                            if (nowdeleteAllfile[all].getName().equals("KickLog")) {
//                                                File nowdeleteKickLogdir = new File(nowdeleteAllfile[all].getAbsolutePath());
//                                                File[] nowdeleteKickLog = nowdeleteKickLogdir.listFiles();
//                                                for (int kickall = 0; kickall < nowdeleteKickLog.length; kickall++) {
//                                                    nowdeleteKickLog[kickall].delete();
//                                                }
//                                                nowdeleteKickLogdir.delete();
//                                            }
//                                            nowdeleteAllfile[all].delete();
//                                        }
//                                        nowdeletedir.delete();
//                                    }
//                                }
//                            } else {
//                                File notNursemaidFiledir = new File(files[i].getAbsolutePath());
//                                File[] notNursemaidAllFile = notNursemaidFiledir.listFiles();
//                                for (int filesquantity = 0; filesquantity < notNursemaidAllFile.length; filesquantity++) {
//                                    notNursemaidAllFile[filesquantity].delete();
//                                }
//                                files[i].delete();
//                            }
//                        } else {
//                            File notNursemaidFiledir = new File(files[i].getAbsolutePath());
//                            File[] notNursemaidAllFile = notNursemaidFiledir.listFiles();
//                            if (notNursemaidAllFile != null) {
//                                for (int filesquantity = 0; filesquantity < notNursemaidAllFile.length; filesquantity++) {
//                                    notNursemaidAllFile[filesquantity].delete();
//                                }
//                            }
//                            files[i].delete();
//                        }
//                    }else{
//                        files[i].delete();
//                        Log.e("delete","inla");
//                    }
//                }
//            }
//        }
//
//    }
//
//    public MainActivity() {
//        mainActivity = this;
//    }
//
//    public static MainActivity getMainActivity() {
//        return mainActivity;
//    }



}
