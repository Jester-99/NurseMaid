package com.example.admin.nursemaid1;

import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.BaseAdapter;

import com.sinopulsar.nursemaid1.R;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class BluetoothService extends Service {
    static SharedPreferences settings;
//    protected ArrayList<String> Mac = new ArrayList<>();
    static BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    static BluetoothLeScanner btScanner = bluetoothAdapter.getBluetoothLeScanner();
    static ArrayList<String> Blue_deviceRssiList = new ArrayList<String>();
    static ArrayList<String> Blue_deviceList = new ArrayList<String>();
    static ArrayList<String> Blue_data = new ArrayList<String>();
//    static ArrayList<String> Blue_rssi = new ArrayList<String>();
    static String Blue_baddress= "null";
    static int Blue_rssitimer = 0 ;
    static int Blue_isexisttimer = 0 ;
    static int Blue_numRssi = 0 ;
    static String Blue_multi_address = "null";
    String Blue_multi_tosingle = "";
    SingleFragment Blue_singleFragment;
    static String Blue_mData = "";
    static String Blue_enable;
    private final IBinder mBinder = new BluetoothBinder();
    static String multi_data = "";
    static String Tagtime = "1";
    static int counts =0,count = 0;//1221
    public int time=0;//1221
    static List<String> arraycount = new ArrayList<String>();
    static float tempValue;
    public class BluetoothBinder extends Binder {
        BluetoothService getService(){
            Log.e("BS","getService");
            bluetoothAdapter.enable();
            if(bluetoothAdapter.isEnabled()) {
                btScanner.startScan(null,createScanSetting(),ScanCallback);
                Log.e("blue","藍芽搜尋");
            }else{
                bluetoothAdapter.enable();
                btScanner.startScan(null,createScanSetting(),ScanCallback);
                Log.e("blue","藍芽開啟並搜尋");
            }
            return  BluetoothService.this;
        }
    }

    public ScanSettings createScanSetting() {
        ScanSettings.Builder builder = new ScanSettings.Builder();
        //builder.setScanMode(ScanSettings.SCAN_MODE_LOW_POWER);//耗電最少，掃描時間間隔最長
        //builder.setScanMode(ScanSettings.SCAN_MODE_BALANCED);//平衡模式，耗電適中，掃描時間間隔一般，我使用這種模式來更新裝置狀態
        builder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);//最耗電，掃描延遲時間短，開啟掃描需要立馬返回結果可以使用
        //builder.setReportDelay(100);//設定延遲返回時間
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//9
            builder.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES);
        }
        return builder.build();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    public void onCreate() {
        super.onCreate();
        Log.e("service","開啟服務");
        settings = getSharedPreferences("APPSETTING", 0);
        setNotification();
    }

    public android.bluetooth.le.ScanCallback ScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    byte[] record = result.getScanRecord().getBytes();
//                    String deviceAddress = result.getDevice().toString();
                    String deviceAddress = result.getDevice().getAddress();

                    int deviceRssi = result.getRssi();
                    ArrayList<String> intValueArray = new ArrayList<>();
                    String data = "";
                    for (int i = 0; i < record.length; i++) {
                        String hexString = Integer.toHexString(Byte.valueOf(record[i]).intValue() & 0xff);
                        if (hexString.length() == 1) {
                            hexString = "0" + hexString;
                        }
                        intValueArray.add(hexString);
                        data += intValueArray.get(i);
                    }
//                    Log.e("isexisttimerdata",data);
                    if (data.substring(10, 18).equals("96875980")) {
                        if(data.substring(18, 20).equals("03")||data.substring(18, 20).equals("05")) {
                            final DateFormat df = new SimpleDateFormat("ss");
                            final String tag = df.format(new Date());

                            if (settings.getBoolean("isSingle", true)) {//若為單數模式
                                if (!MainActivity.checkDevice) {
                                    if (record[5] == (byte) 0x96 && record[6] == (byte) 0x87 && record[7] == (byte) 0x59 && record[8] == (byte) 0x80) {
                                        if (!Blue_deviceList.contains(deviceAddress)) {
                                            Blue_deviceRssiList.add("Rssi: " + deviceRssi + " dBm\n>>" + deviceAddress);
                                            Blue_deviceList.add(deviceAddress);
                                            Log.e("Blue_deviceList",Blue_deviceList.get(0));
                                            Log.e("Blue_deviceRssiList",Blue_deviceRssiList.get(0));
                                        }
                                    }
                                }
                                Tagtime = tag;
                                Blue_baddress = deviceAddress;//0216

                                Log.e("ble_time","Service---"+Tagtime);
                                if (MainActivity.checkDevice) {
                                    if (MainActivity.address.equals(deviceAddress)) {
                                        Blue_baddress = deviceAddress;
                                        Blue_rssitimer = 0 ;
                                        Blue_isexisttimer = 0;
                                        Log.e("rssi",String.valueOf(deviceRssi));
//                                        if(rssi<=-90) {
//                                            if(Blue_numRssi!=5) {
//                                                //checkrssi[numRssi] = rssi;
//                                                Blue_numRssi++;
//                                            }
//                                        }else {
//                                            Blue_numRssi = 0 ;
//                                        }

                                        /*if(numRssi==3){
                                            Message msg = new Message();
                                            rssihandle.sendMessage(msg);
                                            numRssi = 5 ;
                                        }*/
                                        settings.edit().putString("baddress",Blue_baddress).commit();
                                        Blue_mData = data.toUpperCase().substring(0, 62);  //將data全部轉為大寫
//                                        Log.e("Blue_mData",Blue_mData);
                                        Blue_enable = "0" ;
//                                        SingleFragment.dataSet.setisRegist("0");
//                                        Blue_multi_tosingle = device.getAddress();
//                                        SingleFragment.setUI(Blue_baddress, Blue_mData,Blue_enable);
//                                        SingleFragment.setUI(device.getAddress(), mData,enable);
                                    }
                                    //singleFragment.getAddress(device.getAddress());

//                                    debug
                                    tempValue = Float.parseFloat(transBodyValue(data));
                                    Log.e("Service_溫度", String.valueOf(tempValue));
                                }


                            } else if (!settings.getBoolean("isSingle", true)) { //若為多數模式
                                Blue_multi_address = deviceAddress;
                                multi_data = data;
                                Log.e("multi tag quantity: ","Address: "+Blue_multi_address + "31byte:" + data);
//                                MultipleFragment.mDevicesListAdapter.additem(device.getAddress(), data);

//                                mHandler.sendMessage(msg);
//                                Multi_existtimer = 0;
                                Log.e("multi", "timer = 0");
                            }
                        }
                    }
                }
            }).start();;
        }
    };

    @SuppressLint("UnspecifiedImmutableFlag")
    private void setNotification() {
        //初始化 NotificationManager，取得 Notification 服務(顯示通知訊息)
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //設定當執行這個通知之後，所要執行的 activity
        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pi;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        } else {
            pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        Notification notification = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {   //Android 8.0 後使用方式， "必須" 建立一個 NotificationChannel 並使用 createNotificationChannel() 方法指定給 NotificationManager

            // 設定系統預設音效
            Uri mUri = Settings.System.DEFAULT_NOTIFICATION_URI;

            //NotificationChannel是具有唯一通道ID，"SmartDiaper"是在此應用程式通知可設定是否顯示通知的那個名稱
            NotificationChannel mChannel = new NotificationChannel("CHANNEL_ONE_ID", "Nursemaid", NotificationManager.IMPORTANCE_DEFAULT);

            mChannel.setDescription("description");

            mChannel.setSound(mUri, Notification.AUDIO_ATTRIBUTES_DEFAULT);

            manager.createNotificationChannel(mChannel);

//            notification = new Notification.Builder(this)
            notification = new Notification.Builder(this, "CHANNEL_ONE_ID")
//                    .setChannelId(CHANNEL_ONE_ID)
                    .setSmallIcon(R.drawable.icon2)  // 設置狀態列裡面的圖示
                    .setContentTitle(getString(R.string.app_name))  //通知欄標題
                    .setContentText("應用程式正在執行中")   // 設置上下文內容
//                    .setContentIntent(pi)   //按下通知欄的通知時，可以跳轉到某一個 Activity
//                    .setAutoCancel(true)//用户点击就自动消失
                    .build();
        } else {
            // 提升應用許可權
            notification = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.icon)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText("藍芽搜尋中")
                    .setContentIntent(pi)
                    .build();
        }
        notification.flags = Notification.FLAG_ONGOING_EVENT;    //表示該通知通知放置在正在執行,不能被手動清除
//        notification.flags = Notification.FLAG_AUTO_CANCEL;    //表示該通知能被狀態列的清除按鈕給清除掉
        notification.flags |= Notification.FLAG_NO_CLEAR;        //FLAG_NO_CLEAR 表示該通知不能被狀態列的清除按鈕給清除掉,也不能被手動清除,但能通過 cancel() 方法清除
        notification.flags |= Notification.FLAG_FOREGROUND_SERVICE;  //表示正在執行的服務

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            startForeground(10000, notification);
        } else {
            startForeground(10000, notification,
                    FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK);
        }
//        startForeground(10000, notification);
    }

//    private boolean CheckDevice(BluetoothDevice device,String Update){
//        for(int i = 0; i < Mac.size(); i++){
//            if(String.valueOf(device.getAddress()).equals(String.valueOf(Mac.get(i).split(",")[0]))){
//                Mac.set(i,Update);
//                Log.e("BS","CheckDevice_false");
////                Log.e("BS", "Mac:" + Mac.get(i));
//
//                return false;
//            }
//        }
//        Log.e("BS","CheckDevice_true");
//        return true;
//    }


    public String transBodyValue(String ID1) {
        byte data1 = (byte) (Integer.parseInt(ID1.substring(40, 42), 16) & 0xff);
        //Byte.parseByte(ID1.substring(24, 26) );
        //Log.e("data1"+ "::::",String.valueOf(data1) +"");
        byte data2 = (byte) (Integer.parseInt(ID1.substring(42, 44), 16) & 0xff);
        byte data3 = (byte) (Integer.parseInt(ID1.substring(44, 46), 16) & 0xff);
        byte data4 = (byte) (Integer.parseInt(ID1.substring(46, 48), 16) & 0xff);
        /*Log.e("data1"+ "::::",data1 +"");
        Log.e("data2"+ "::::",data2+"");
		Log.e("data3"+ "::::",data3+"");
		Log.e("data4"+ "::::",data4+"");*/
        float tem = bytesToFloat(data1, data2, data3, data4);//decodeTempLevel(data,0);

        return tem + "";
    }

    float bytesToFloat(byte b0, byte b1, byte b2, byte b3) {
        int mantissa = unsignedToSigned(unsignedByteToInt(b0) + (unsignedByteToInt(b1) << 8) + (unsignedByteToInt(b2) << 16), 24);
        return (float) (mantissa * Math.pow(10, b3));
    }

    /**
     * Convert a signed byte to an unsigned int.
     */
    int unsignedByteToInt(byte b) {
        return b & 0xFF;
    }

    /**
     * Convert an unsigned integer value to a two's-complement encoded signed value.
     */
    int unsignedToSigned(int unsigned, int size) {
        if ((unsigned & (1 << size - 1)) != 0) {
            unsigned = -1 * ((1 << size - 1) - (unsigned & ((1 << size - 1) - 1)));
        }
        return unsigned;
    }
    
}
