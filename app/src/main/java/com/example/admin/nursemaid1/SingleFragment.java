package com.example.admin.nursemaid1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.fragment.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.nursemaid1.dbcontrol.DeviceCourseDAO;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

//import com.sinopulsar.nursemaid.R;
import com.sinopulsar.nursemaid1.R;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SingleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SingleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SingleFragment extends Fragment {                                                    //此java為設定單數頁面要顯示的內容
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public Handler checkneartag = new Handler(Looper.myLooper()) {                       //提醒使用者
        @Override
        public void handleMessage(Message msg) {
            Log.e("refind","in");
            Toast.makeText(getActivity(), getResources().getString(R.string.a_scanning_sensors), Toast.LENGTH_LONG).show();
        }
    };

    public Handler checknearhand = new Handler(Looper.myLooper()) {                       //重新掃描附近感測器
        @Override
        public void handleMessage(Message msg) {
            String[] device = new String[MainActivity.deviceRssiList.size()];
            for (int i = 0; i < MainActivity.deviceRssiList.size(); i++) {
                device[i] = MainActivity.deviceRssiList.get(i);
            }
            if (device.length == 0) {
                device = new String[1];
                device[0] = getResources().getString(R.string.nofindscan);
            }
            AlertDialog.Builder DevicesDialog = new AlertDialog.Builder(getActivity());
            final String[] finalDevice = device;
            DevicesDialog.setItems(device, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (!finalDevice[0].equals(getResources().getString(R.string.nofindscan))) {
                        MainActivity.address = MainActivity.deviceRssiList.get(which).split(">>")[1];
                        changeTagScan = true;
                    }
                }
            }).create();
            DevicesDialog.setCancelable(false);
            DevicesDialog.setTitle(R.string.chosemul);
            DevicesDialog.show();
            mAddress.setClickable(true);
            MainActivity.checkDevice = true;

        }
    };

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static boolean isadd = false;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ImageButton mImageButton1, mImageButton2, mImageButton4;
    TextView mAddress, mBattery, mBody, mRoom, mBreath, mTempType, mName, mBpm,mStatus;
    ImageView mKick, mSpit, mWake, mEscape, mwifi;
    EditText ed5, ed6 ;
    static Listitem dataSet = new Listitem();
    static boolean isScan  =false;

    GifImageView  mKickGifView, mSpitGifView, mWakeGifView, mEscapeGifView;
    //   GifAnimationDrawable spitGif, wakeGif, escapeGif, kickGif;
    MainActivity mainActivity = new MainActivity();
    private OnFragmentInteractionListener mListener;
    Timer timer = new Timer();
    Timer timer2 = new Timer();
    Timer timer3 = new Timer();
    Kick classKick = new Kick();
    TimerTask BreathTVtask;
    TimerTask TempTVtask;
    TimerTask BatteryTVtask;
    boolean kickAlert = false;
    boolean change = false;
    boolean change2 = false;
    boolean change3 = false;
    View myView;
    boolean breathTimerRun = false;
    boolean tempTimerRun = false;
    boolean batteryTimerRun = false;

    static DeviceCourseDAO courseDAO;
    Context mContext;
    String macAddress = "";
    GifDrawable kickGif ,splitGif ,wakeGif ,escapeGif;
    int  labaset0=0, labaset1=0, labaset2=0;
    int ringset[] ;
    String[] device ;

    static int kicktimer = 120, Diapertimmer=0 ;
    static boolean iskick = true ;
    static kickthread o = new kickthread() ;

    /**/
    static SharedPreferences sharedPreferences = null;
    String FileName = "";
    SQLite sqLite;
    /**/

    //new
    static BluetoothGatt singlemBluetoothGatt = null;
    static BluetoothGattService singlemBluetoothSelectedService = null;
    static BluetoothGattCharacteristic ch = null;
    UUID singleblueserverlink = UUID.fromString("00001809-0000-1000-8000-00805f9b34fb");
    UUID singlebluereadcode = UUID.fromString("00002a21-0000-1000-8000-00805f9b34fb");
    static BluetoothAdapter singlebtAdapter = BluetoothService.bluetoothAdapter;
    static BluetoothDevice singlemBluetoothDevice = null;
    static Context singlemParent = MainActivity.mParent ;
    byte[] singlerawValue ;
    static int singlereadMeasureInterval;
    boolean firstin = true;
    boolean changeTagScan = false;
    static int push06timer = 0;

    static boolean reconnect = false;
    static SharedPreferences Situation;

    Animation animB,animT,animT1,animB1;
    //
    public SingleFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SingleFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SingleFragment newInstance(String param1, String param2) {
        SingleFragment fragment = new SingleFragment();
        //      Bundle args = new Bundle();
        //      args.putString(ARG_PARAM1, param1);
        //      args.putString(ARG_PARAM2, param2);
        //      fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setAnimationB();
        setAnimationT();
    }
    // always verify the host - dont check for certificate 始終驗證主機 - 不檢查憑證
    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    /**
     * Trust every server - dont check for any certificate 信任每台服務器 - 不檢查任何憑證
     */
    private static void trustAllHosts() {
        // 創建不驗證憑證鏈的信任管理器
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


    private void setAnimationB(){
        animB = new AlphaAnimation(0.1f,1.0f);
        animB.setDuration(400); //You can manage the blinking time with this parameter
        animB.setStartOffset(20);
        animB.setRepeatMode(Animation.REVERSE);
        animB.setRepeatCount(Animation.INFINITE);

    }

    private void setAnimationT(){
        animT = new AlphaAnimation(0.1f,1.0f);
        animT.setDuration(400); //You can manage the blinking time with this parameter
        animT.setStartOffset(20);
        animT.setRepeatMode(Animation.REVERSE);
        animT.setRepeatCount(Animation.INFINITE);

    }

    private void stopAnimationT(){
        animT1 = new AlphaAnimation(1.0f,1.0f);
        mBody.startAnimation(animT1);
        mTempType.startAnimation(animT1);
    }

    private void stopAnimationB(){
        animB1 = new AlphaAnimation(1.0f,1.0f);
        mBreath.startAnimation(animB1);
        mBpm.startAnimation(animB1);
    }

    private void stopAnimationdiaper(){
        animT1 = new AlphaAnimation(0.0f,0.0f);
        mBody.startAnimation(animT1);
        mTempType.startAnimation(animT1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sqLite=new SQLite(getContext());
        // Inflate the layout for this fragment
        LayoutInflater lf = getActivity().getLayoutInflater();
        View view = lf.inflate(R.layout.fragment_single, container, false);
        ringset = new int[4] ;
        myView = view;
        mContext = myView.getContext();
        courseDAO = new DeviceCourseDAO(mContext);
        ed5 = (EditText) view.findViewById(R.id.editText5);
        ed6 = (EditText) view.findViewById(R.id.editText6);
        mAddress = (TextView) view.findViewById(R.id.mAddressTV);
        mBattery = (TextView) view.findViewById(R.id.mBatteryTV);
        mBody = (TextView) view.findViewById(R.id.mBodyTempTV);
        mRoom = (TextView) view.findViewById(R.id.mRoomTempTV);
        mBreath = (TextView) view.findViewById(R.id.mBreathTV);
        mName = (TextView) view.findViewById(R.id.mNameTV);
        mBpm = (TextView) view.findViewById(R.id.mBpmTV);
        mStatus = (TextView) view.findViewById(R.id.mStatusTV);
        mKick = (ImageView) view.findViewById(R.id.kickImg);
        mEscape = (ImageView) view.findViewById(R.id.escapeImg);
        mSpit = (ImageView) view.findViewById(R.id.spitImg);
        mWake = (ImageView) view.findViewById(R.id.wakeImg);
        mwifi = (ImageView) view.findViewById(R.id.imageView4);
        mTempType = (TextView) view.findViewById(R.id.mTempTypeTV);
        mKickGifView = (GifImageView)view.findViewById(R.id.kickgif);
        mSpitGifView = (GifImageView)view.findViewById(R.id.spitgif);
        mEscapeGifView  = (GifImageView)view.findViewById(R.id.escapegif);
        mWakeGifView = (GifImageView)view.findViewById(R.id.wakegif);
        mImageButton1 = (ImageButton)view.findViewById(R.id.imageButton);
        mImageButton2 = (ImageButton)view.findViewById(R.id.imageButton2);
        mImageButton4 = (ImageButton)view.findViewById(R.id.imageButton4);

        //mStatus.setVisibility(View.INVISIBLE);
        mStatus.setText(MainActivity.settings.getString("state","normal").toString());

        if (SetupFragment.isLogin) {                                                                //如果已進入推波可長按tag進行註冊移除的動作
            mAddress.setClickable(true);
            mAddress.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!MainActivity.baddress.equals("null")) {
                        if (dataSet.getisRegist().equals("1")) {
                            registerAlertDialog().show();
                        } else if (dataSet.getisRegist().equals("0")) {
                            disregisterAlertDialog().show();
                        }
                    }
                    else {
                        device = new String[MainActivity.deviceRssiList.size()];
                        for (int i = 0; i < MainActivity.deviceRssiList.size(); i++) {
                            device[i] = MainActivity.deviceRssiList.get(i);
                            System.out.println(device[i]);
                        }
                        if(device.length == 0){
                            device = new String[1];
                            device[0] = getResources().getString(R.string.nofind) ;
                        }

                        AlertDialog.Builder DevicesDialog = new AlertDialog.Builder(
                                getActivity());
                        DevicesDialog.setItems(device,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        if(!device[0].equals(getResources().getString(R.string.nofind))) {

                                            MainActivity.baddress = MainActivity.deviceRssiList.get(
                                                    which).split(">>")[1];
                                            //MainActivity.baddress = MainActivity.address;
                                            dataSet.setisRegist("0");
                                            match(MainActivity.baddress, "true");

                                            System.out.println(MainActivity.address
                                                    + "startLeScan");
                                        }

                                    }
                                }).create();
                        //DevicesDialog.setCancelable(false);
                        DevicesDialog.setTitle(R.string.chosemul);
                        DevicesDialog.show();
                    }
                    return true;
                }
            });
        }

        if(!SetupFragment.isLogin) {                                         //如果是禁端藍芽則可以點及tag名子讓使用者自行修改
            /*mName.setClickable(true);
            mName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setListItemNameDialog(macAddress).show();
                }
            });*/

            mAddress.setClickable(true);                                 //選擇其他的tag
            mAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(MainActivity.checkDevice == true) {
                        Message msg = new Message();
                        checkneartag.sendMessage(msg);
                        Log.e("leeee", String.valueOf(MainActivity.deviceRssiList.size()));
                        mAddress.setClickable(false);
                        MainActivity.deviceRssiList.clear();
                        MainActivity.deviceList.clear();
                        BluetoothService.Blue_deviceList.clear();//0216
                        BluetoothService.Blue_deviceRssiList.clear();
                        MainActivity.checkDevice = false;
                        MainActivity.isTagOffline = false;
                        MainActivity.firstAddTagtime = false;
                        Log.e("isexisttimeronClick", String.valueOf(MainActivity.isTagOffline));
                        neartagthread pp = new neartagthread();
                        pp.start();
                    }
                }
            });
        }

        try {
            splitGif =  new GifDrawable(getResources(), R.raw.spit);
            wakeGif = new GifDrawable(getResources(), R.raw.wake);
            escapeGif = new GifDrawable(getResources(), R.raw.escape);
            kickGif = new GifDrawable(getResources(), R.raw.kick);
            splitGif.start();
            wakeGif.start();
            escapeGif.start();
            kickGif.start();
            mSpitGifView.setImageDrawable(splitGif);
            mEscapeGifView.setImageDrawable(wakeGif);
            mEscapeGifView.setImageDrawable(escapeGif);
            mKickGifView.setImageDrawable(kickGif);
            mSpit.setImageResource(R.drawable.spit);
            mKick.setImageResource(R.drawable.kick);
            mWake.setImageResource(R.drawable.wake);
            mEscape.setImageResource(R.drawable.escape);
        } catch (IOException e) {
            e.printStackTrace();
        }


      /*  try {
            spitGif = new GifAnimationDrawable(getResources().openRawResource(R.raw.spit),view.getContext());
            wakeGif = new GifAnimationDrawable(getResources().openRawResource(R.raw.wake),view.getContext());
            escapeGif = new GifAnimationDrawable(getResources().openRawResource(R.raw.escape),view.getContext());
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        BreathTVtask = new TimerTask() {//讓文字有閃爍效果的tinerTask，當偵測到的值高於或低於設定的警示值
            public void run() {
                mainActivity.runOnUiThread(new Runnable() {
                    public void run() {

                        mBreath.startAnimation(animB);
                        mBpm.startAnimation(animB);

//                        if (change) { // false
//                            change = false;
                        mBreath.setTextColor(Color.rgb(255, 48, 48));
                        mBpm.setTextColor(Color.rgb(255, 48, 48));
//                        } else {
//                            change = true;
//                            mBreath.setTextColor(Color.TRANSPARENT); //这个是透明，=看不到文字
//                            mBpm.setTextColor(Color.TRANSPARENT);
//                        }

                    }
                });
            }
        };

        TempTVtask = new TimerTask() {//讓文字有閃爍效果的tinerTask，當偵測到的值高於或低於設定的警示值
            @Override
            public void run() {
                mainActivity.runOnUiThread(new Runnable() {
                    public void run() {


                        mBody.startAnimation(animT);
                        mTempType.startAnimation(animT);

//                        if (change2) {
//                            change2 = false;
                        mBody.setTextColor(Color.rgb(255, 48, 48));
                        mTempType.setTextColor(Color.rgb(255, 48, 48));
//                        } else {
//                            change2 = true;
//                            mBody.setTextColor(Color.TRANSPARENT); //这个是透明，=看不到文字
//                            mTempType.setTextColor(Color.TRANSPARENT);
//                        }
                        ringset[0] = 1 ;
                    }
                });
            }
        };

        if(isScan==true){//0729
            mBody.setTextColor(Color.rgb(255, 48, 48));
            mTempType.setTextColor(Color.rgb(255, 48, 48));
        }

        BatteryTVtask = new TimerTask() {//讓文字有閃爍效果的tinerTask，當偵測到的值高於或低於設定的警示值
            @Override
            public void run() {
                mainActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        if (change3) {
                            change3 = false;
                            mBattery.setVisibility(View.INVISIBLE);
                        } else {
                            change3 = true;
                            mBattery.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        };

        labaset0 = MainActivity.settings.getInt("labaset0", 0) ;
        labaset1 = MainActivity.settings.getInt("labaset1", 0) ;
        labaset2 = MainActivity.settings.getInt("labaset2", 0) ;

        if(labaset0 == 1){
            mImageButton1.setImageResource(R.drawable.laba2_new);
        }else {
            mImageButton1.setImageResource(R.drawable.laba_new);
        }

        if(labaset1 == 1){
            mImageButton2.setImageResource(R.drawable.laba2_new);
        }else {
            mImageButton2.setImageResource(R.drawable.laba_new);
        }


        if(labaset2 == 1){
            mImageButton4.setImageResource(R.drawable.laba2_new);
        }else {
            mImageButton4.setImageResource(R.drawable.laba_new);
        }

        mImageButton1.setOnClickListener(new View.OnClickListener() {              //可以開關呼吸值的提示聲音
            @Override
            public void onClick(View view) {
                if(labaset0 == 0){
                    mImageButton1.setImageResource(R.drawable.laba2_new);
                    labaset0 = 1 ;
                    MainActivity.settings.edit().putInt("labaset0",labaset0).commit();
                }
                else{
                    mImageButton1.setImageResource(R.drawable.laba_new);
                    labaset0 = 0 ;
                    MainActivity.settings.edit().putInt("labaset0",labaset0).commit();
                }
            }
        });

        mImageButton2.setOnClickListener(new View.OnClickListener() {                  //可以開關體溫值的提示聲音
            @Override
            public void onClick(View view) {
                if(labaset1 == 0){
                    mImageButton2.setImageResource(R.drawable.laba2_new);
                    labaset1 = 1 ;
                    MainActivity.settings.edit().putInt("labaset1",labaset1).commit();
                }
                else{
                    mImageButton2.setImageResource(R.drawable.laba_new);
                    labaset1 = 0 ;
                    MainActivity.settings.edit().putInt("labaset1",labaset1).commit();
                }
            }
        });

        mImageButton4.setOnClickListener(new View.OnClickListener() {                   //可以開關踢被,離床,睡醒值的提示聲音
            @Override
            public void onClick(View view) {
                if(labaset2 == 0){
                    mImageButton4.setImageResource(R.drawable.laba2_new);
                    labaset2 = 1 ;
                    MainActivity.settings.edit().putInt("labaset2",labaset2).commit();
                }
                else{
                    mImageButton4.setImageResource(R.drawable.laba_new);
                    labaset2 = 0 ;
                    MainActivity.settings.edit().putInt("labaset2",labaset2).commit();
                }
            }
        });


     /*   // 連續播放
        spitGif.setOneShot(false);
        // 開始播放
        spitGif.setVisible(true, true);
        wakeGif.setOneShot(false);
        wakeGif.setVisible(true, true);
        escapeGif.setOneShot(false);
        escapeGif.setVisible(true, true);*/
        return view;

    }

    public void setUI(String address, String data,String enable) {//data即31byte資料                       //此函式主要是顯示解出的31bytes資料 如體溫,呼吸值,離床狀態等...
        macAddress = address;
        mAddress.setText(address);
        Log.e("Diapertimmer","laba1"+MainActivity.settings.getInt("labaset1", 0));
        Log.e("111_Single_setui","firstin:"+firstin+"----changeTagScan:"+changeTagScan);
        try{
            if(firstin==true || changeTagScan==true){
                Log.e("setUI_addredd",address);
                if((address!=null || address.equals("") || address.equals("null")) && MainActivity.settings.getBoolean("isScan",true)){
                    singlemBluetoothDevice = singlebtAdapter.getRemoteDevice(address);
                    singlemBluetoothGatt = singlemBluetoothDevice.connectGatt(getContext(), false, singlemBleCallback);
                    Log.e("setUI_gatt","單人連線");
//                    Log.e("connectGatt_getRemote", String.valueOf(singlemBluetoothDevice));

//                    Log.e("connectGatt_getRemote2", String.valueOf(singlemBluetoothDevice));
//                    Log.e("connectGatt", String.valueOf(getContext()));
//                    Log.e("connectGatt", String.valueOf(false));
//                    Log.e("connectGatt", String.valueOf(singlemBleCallback));

//                    Message msg = new Message();
//                    handlerread.sendMessage(msg);
                }
            }
//            else{
//                if(SingleFragment.singlemBluetoothGatt!=null){
//                    SingleFragment.disconnect();
//                    SingleFragment.singlemBluetoothGatt = null;
//                    Log.e("setUI_single重新斷線","結束連線");
//                }
//            }

        }catch (IllegalArgumentException | IllegalStateException e){
            Log.e("errorrr",e.toString());
        }


        if(dataSet.getisRegist().equals("0")&&dataSet.getisState() == 1) {
            mName.setVisibility(View.VISIBLE);

            String isEnable = "1";
            String isRegist = "1";
            String tagName = "";
            String tagNumString = MainActivity.nameNumber + "";
            if (tagNumString.length() == 1) {
                tagNumString = "0" + tagNumString;
            }//tag流水編號，若為 0~9 ，邊號前加個0
            Listitem mListItem = new Listitem("", address, null, isEnable, 0, 0, isRegist,classKick);
            mListItem = customadapter.courseDAO.get(mListItem);//向資料庫取得該tag
            if (mListItem.getName().equals("")) {//如果資料庫中沒有該tag的名稱
                boolean existName = false;
                for (int i = 0; i < MainActivity.tagNameList.size(); i++) {
                    if (MainActivity.tagNameList.size() > 0) {
                        if (MainActivity.tagNameList.get(i).get("macaddress").equals(address)) {
                            tagName = MainActivity.tagNameList.get(i).get("name") + "";//tagNameList位址等於設備位址
                            //   mListItem.setName(name);
                            existName = true;
                            break;
                        }//檢查該tag是否已有流水編號在tagNameList裡
                    }
                }
                if (!existName) {//如果沒有流水編號就給他，並存在tagNameList

                    mListItem.setName("Sinopulsar" + tagNumString);

                    mainActivity.tagNameMap.put("macaddress", address);
                    MainActivity.tagNameMap.put("name", "Sinopulsar" + tagNumString);
                    MainActivity.tagNameList.add(MainActivity.tagNameMap);
                    MainActivity.tagNameMap = new HashMap<>();
                    MainActivity.nameNumber++;
                }
            } else {
                tagName = mListItem.getName(); //tag名稱即為之前更改並在資料庫中儲存的
            }
            mName.setText(tagName);
            Log.e("Data 31 bytes : ", data);
            String pid = data.substring(18, 20);//PID

            String batteryStr = data.substring(56, 58);//電池百分比
            int batteryInt = Integer.parseInt(batteryStr, 16);
            String breathStr = data.substring(30, 32);//呼吸
            int breathInt = Integer.parseInt(breathStr, 16);

            String bodyTemp = transBodyValue(data);     //DEFU 50//寶寶溫度
            String roomTemp = transRoomValue(data);              //室溫

            //kick

            if(pid.equals("05") || pid.equals("03")) {
                if(kicktimer>=120 && MainActivity.settings.getBoolean("KickNoti",false)) {//MainActivity.settings.getString("state","normal").equals("sleep")
                    classKick.kick(Double.parseDouble(bodyTemp), Double.parseDouble(roomTemp));
                    kickAlert = classKick.isKick;
                    kicktimer = 0 ;
                    //Log.e("SingleKick","kick") ;
                }
            }

            if(!MainActivity.settings.getBoolean("KickNoti",false)){
                kicktimer = 0 ;
                //Log.e("SingleKick","notKick") ;
            }


            /**/
//        sharedPreferences = getActivity().getSharedPreferences("Name",0);
//        FileName = sharedPreferences.getString("FileName","output.txt");
//        Log.e("FileName",FileName);

            Time t = new Time();
            t.setToNow();
            String[] now = {String.valueOf(t.year), String.valueOf(t.month + 1), String.valueOf(t.monthDay),
                    String.valueOf(t.hour), String.valueOf(t.minute), String.valueOf(t.second)};
            for (int i = 0; i < now.length; i++) {
                if (Integer.parseInt(now[i]) < 10) {
                    now[i] = "0" + now[i];
                } else {
                    now[i] = now[i];
                }
            }
            String nowTime = now[0] + "/" + now[1] + "/" + now[2] + " " + now[3] + ":" + now[4] + ":" + now[5];
/*        try {
            FileWriter fw = new FileWriter("/sdcard/Nursemaid/BT40_Log/"+FileName, true);
            //Log.e("bodyTemp",bodyTemp);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(nowTime+","+bodyTe);
            bw.newLine();
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        catch (IOException e) {
            e.printStackTrace();
        }*/


            Log.e("ToInsertRecord_BT", bodyTemp);

            sqLite.ToInsertRecord(nowTime, bodyTemp);

            String date_time = Integer.parseInt(now[0]) + "-" + Integer.parseInt(now[1]) + "-" + Integer.parseInt(now[2]) + "-" + Integer.parseInt(now[3]);
            if (!sqLite.isHaveTimeData(date_time)) {
                Log.e("date_time", date_time);
                sqLite.ToInsertTimeRecord(date_time);
            }
            /**/

            if (batteryInt > 10) {
                mBattery.setVisibility(View.VISIBLE);
                mBattery.setBackgroundResource(R.drawable.battery3);
                if (batteryTimerRun) {//如果timer在啟動狀態
                    timer3.cancel();//取消timer
                    BatteryTVtask.cancel();//刪除該task
                    timer3 = new Timer();//重設timer
                    batteryTimerRun = false;
                    BatteryTVtask = new TimerTask() {//讓文字有閃爍效果的tinerTask，當偵測到的值高於或低於設定的警示值
                        @Override
                        public void run() {
                            mainActivity.runOnUiThread(new Runnable() {
                                public void run() {
                                    if (change3) {
                                        change3 = false;
                                        mBattery.setVisibility(View.INVISIBLE);
                                    } else {
                                        change3 = true;
                                        mBattery.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        }
                    };
                    Log.e("Battery","627");
                }
            } else {
                Log.e("Battery","Low Battery "+ batteryTimerRun);
                mBattery.setBackgroundResource(R.drawable.battery4);
                if (!batteryTimerRun) {
                    timer3.schedule(BatteryTVtask, 0, 500);//文字閃爍
                    batteryTimerRun = true; //timer已啟動
                    Log.e("Battery","635");
                }
            }
            mBattery.setText(batteryInt + "%");

            if (SetupFragment.isC) {
                mRoom.setText(roomTemp + " °C");
            } else if (!SetupFragment.isC) {
                float roomTempF = (float) (Float.parseFloat(roomTemp) * 1.8 + 32);//轉華氏
                DecimalFormat df = new DecimalFormat("#.##");//取小數後2位
                String roomTempFstr = df.format(roomTempF);
                mRoom.setText(roomTempFstr + "°F");
            }
            mBreath.setText(breathInt + "");
            mImageButton1.setVisibility(View.VISIBLE);//呼吸提示音鈕可見

            if (breathInt == 255) {
                mBreath.setText("---");
                mImageButton1.setVisibility(View.INVISIBLE);//呼吸提示音鈕不可見
            } else if (breathInt >= SetupFragment.fastBreath && breathInt != 255) {//呼吸急促
//                mBody.setTextColor(Color.rgb(255, 48, 48));
//                mTempType.setTextColor(Color.rgb(255, 48, 48));

//                Animation anim = new AlphaAnimation(0,1);
//                anim.setDuration(400); //You can manage the blinking time with this parameter
//                anim.setStartOffset(20);
//                anim.setRepeatMode(Animation.REVERSE);
//                anim.setRepeatCount(Animation.INFINITE);
//                mBreath.startAnimation(anim);
//                mBpm.startAnimation(anim);

                if (!breathTimerRun) {
                    timer.schedule(BreathTVtask, 0, 500);//文字閃爍
                    breathTimerRun = true; //timer已啟動
                    ringset[2] = 1;
                }
            } else if (breathInt <= SetupFragment.slowBreath) {//呼吸緩慢
//                mBody.setTextColor(Color.rgb(255, 48, 48));
//                mTempType.setTextColor(Color.rgb(255, 48, 48));

//                Animation anim = new AlphaAnimation(0,1);
//                anim.setDuration(400); //You can manage the blinking time with this parameter
//                anim.setStartOffset(20);
//                anim.setRepeatMode(Animation.REVERSE);
//                anim.setRepeatCount(Animation.INFINITE);
//                mBreath.startAnimation(anim);
//                mBpm.startAnimation(anim);

                if (!breathTimerRun) { //false -> true
                    timer.schedule(BreathTVtask, 0, 500);//文字閃爍
                    breathTimerRun = true;
                    ringset[2] = 1;
                }
            } else {//'當值回到正常
                if (breathTimerRun) {//如果timer在啟動狀態
                    timer.cancel();//取消timer
                    ringset[2] = 0;
                    BreathTVtask.cancel();//刪除該task
                    timer = new Timer();//重設timer
                    BreathTVtask = new TimerTask() {//重設timerTask
                        public void run() {
                            mainActivity.runOnUiThread(new Runnable() {
                                public void run() {
//                                    if (change) {
//                                        change = false;

                                    mBreath.startAnimation(animB);
                                    mBpm.startAnimation(animB);

                                    mBreath.setTextColor(Color.rgb(255, 48, 48));
                                    mBpm.setTextColor(Color.rgb(255, 48, 48));

//                                    } else {
//                                        change = true;
//                                         if (!breathTimerRun) { //false -> true
//                    timer.schedule(BreathTVtask, 0, 500);//文字閃爍
//                    breathTimerRun = true;
//                    ringset[2] = 1;
//                }
//                                    }
                                }
                            });
                        }
                    };
                    breathTimerRun = false;//timer未啟動
                }
                stopAnimationB();
                mBreath.setTextColor(Color.BLACK);//黑
                mBpm.setTextColor(Color.BLACK);
            }
            ringset[3] = 0;

            if (ringset[2] == 1) {
                if(ringset[1] == 0 || labaset2 == 1){
                    if (labaset0 == 0 && MainActivity.isring == 0) {//提示音是否開啟
                        Log.e("laba0","ring1");
                        ringset[3] = 1;
                        if (!MainActivity.ringTone.isPlaying()) {
                            MainActivity.ringTone.play();
                        }
                    } else {
                        Log.e("laba0","ring2");
                        if (MainActivity.ringTone.isPlaying()) {
                            MainActivity.ringTone.stop();
                        }
                    }
                }else if(labaset1 == 0 || labaset2 == 0){
                    if (labaset0 == 0 && MainActivity.isring == 0) {
                        Log.e("laba0","ring1");
                        ringset[3] = 1;
                        if (!MainActivity.ringTone.isPlaying()) {
                            MainActivity.ringTone.play();
                        }
                    } else {
                        Log.e("laba0","ring2");
                        if (MainActivity.ringTone.isPlaying()) {
                            MainActivity.ringTone.stop();
                        }
                    }
                }
            }

            String status = data.substring(21, 22);//狀態值
            ringset[1] = 0;
            if(kickAlert) status = "10";
            switch (status) {                                                            //狀態顯示 離床等....  目前只有2跟5有動作 單數模式踢被請加這!!!!!!!!!!!!!!!
                case "0"://踢被
                    mKick.setVisibility(View.VISIBLE);//預設圖無顏色

                    //                   mKick.setVisibility(View.INVISIBLE);//預設圖無顏色

                    mWake.setVisibility(View.VISIBLE);
                    mEscape.setVisibility(View.VISIBLE);

                    mKickGifView.setVisibility(View.INVISIBLE);
//                    mKickGifView.setVisibility(View.VISIBLE);

//                    mSpitGifView.setVisibility(View.INVISIBLE);//GIF圖有顏色並會跳動
                    mWakeGifView.setVisibility(View.INVISIBLE);
                    mEscapeGifView.setVisibility(View.INVISIBLE);
                    break;
                case "1":
                    mKick.setVisibility(View.VISIBLE);
                    mWake.setVisibility(View.VISIBLE);
                    mEscape.setVisibility(View.VISIBLE);
                    mKickGifView.setVisibility(View.INVISIBLE);
//                    mSpitGifView.setVisibility(View.INVISIBLE);
                    mWakeGifView.setVisibility(View.INVISIBLE);
                    mEscapeGifView.setVisibility(View.INVISIBLE);
                    break;
                case "2"://離床狀態
                    mKick.setVisibility(View.VISIBLE);
                    mWake.setVisibility(View.VISIBLE);
                    mEscape.setVisibility(View.INVISIBLE);
                    mKickGifView.setVisibility(View.INVISIBLE);
//                    mSpitGifView.setVisibility(View.INVISIBLE);
                    mWakeGifView.setVisibility(View.INVISIBLE);
                    mEscapeGifView.setVisibility(View.VISIBLE);
                    ringset[1] = 1;
                    if(ringset[1] == 1) {
                        if (labaset2 == 0 && MainActivity.isring == 0) {
                            Log.e("laba2","ring1");
                            if (!MainActivity.ringTone.isPlaying()) {
                                MainActivity.ringTone.play();
                            }
                        } else {
                            Log.e("laba2","ring2");
                            if (MainActivity.ringTone.isPlaying()) {
                                MainActivity.ringTone.stop();
                            }
                        }
                    }
                    break;
                case "3":
                    mKick.setVisibility(View.VISIBLE);
                    mWake.setVisibility(View.VISIBLE);
                    mEscape.setVisibility(View.VISIBLE);
                    mKickGifView.setVisibility(View.INVISIBLE);
//                    mSpitGifView.setVisibility(View.INVISIBLE);
                    mWakeGifView.setVisibility(View.INVISIBLE);
                    mEscapeGifView.setVisibility(View.INVISIBLE);
                    break;
                case "4":
                    mKick.setVisibility(View.VISIBLE);
                    mWake.setVisibility(View.VISIBLE);
                    mEscape.setVisibility(View.VISIBLE);
                    mKickGifView.setVisibility(View.INVISIBLE);
//                    mSpitGifView.setVisibility(View.INVISIBLE);
                    mWakeGifView.setVisibility(View.INVISIBLE);
                    mEscapeGifView.setVisibility(View.INVISIBLE);
                    break;
                case "5"://睡醒狀態
                    mKick.setVisibility(View.VISIBLE);
                    mWake.setVisibility(View.INVISIBLE);
                    mEscape.setVisibility(View.VISIBLE);
                    mKickGifView.setVisibility(View.INVISIBLE);
                    mWakeGifView.setVisibility(View.VISIBLE);
                    mEscapeGifView.setVisibility(View.INVISIBLE);
                    ringset[1] = 1;
                    if(ringset[1] == 1) {
                        if(labaset1 == 1 || labaset0 == 1) {
                            if (labaset2 == 0 && MainActivity.isring == 0) {
                                Log.e("laba2", "ring3");
                                if (!MainActivity.ringTone.isPlaying()) {
                                    MainActivity.ringTone.play();
                                }
                            } else if(labaset2 == 0){
                                Log.e("laba2", "ring4");
                                if (MainActivity.ringTone.isPlaying()) {
                                    MainActivity.ringTone.stop();
                                }
                            }
                        }
                    }
                    break;
                case "6":
                    mKick.setVisibility(View.VISIBLE);
                    mWake.setVisibility(View.VISIBLE);
                    mEscape.setVisibility(View.VISIBLE);
                    mKickGifView.setVisibility(View.INVISIBLE);
//                    mSpitGifView.setVisibility(View.INVISIBLE);
                    mWakeGifView.setVisibility(View.INVISIBLE);
                    mEscapeGifView.setVisibility(View.INVISIBLE);
                    break;
                case "10"://踢被狀態
                    mKick.setVisibility(View.INVISIBLE);
                    mWake.setVisibility(View.VISIBLE);
                    mEscape.setVisibility(View.VISIBLE);
                    mKickGifView.setVisibility(View.VISIBLE);
//                    mSpitGifView.setVisibility(View.INVISIBLE);
                    mWakeGifView.setVisibility(View.INVISIBLE);
                    mEscapeGifView.setVisibility(View.INVISIBLE);
                    ringset[1] = 1;
                    if(ringset[1] == 1) {
                        if (labaset2 == 0 && MainActivity.isring == 0) {
                            Log.e("laba2","ring5");
                            if (!MainActivity.ringTone.isPlaying()) {
                                MainActivity.ringTone.play();
                            }
                        } else {
                            Log.e("laba2","ring6");
                            if (MainActivity.ringTone.isPlaying()) {
                                MainActivity.ringTone.stop();
                            }
                        }
                    }
                    break;
            }

            Activity activity = getActivity();
            if(activity != null) {
                if (pid.equals("06")) {//06偵測尿布值模式
                    mSpit.setVisibility(View.VISIBLE);
                    mBody.setVisibility(View.INVISIBLE);
                    mTempType.setVisibility(View.INVISIBLE);
                    stopAnimationdiaper();
                    mBreath.setTextColor(Color.BLACK);
                    mBpm.setTextColor(Color.BLACK);

                    labaset1 = MainActivity.settings.getInt("labaset1", 0);

                    BreathTVtask.cancel();//刪除該task
                    timer = new Timer();//重設tDiapertimmerimer
                    BreathTVtask = new TimerTask() {//重設timerTask
                        public void run() {
                            mainActivity.runOnUiThread(new Runnable() {
                                public void run() {
                                    if (change) {
                                        change = false;
                                        mBreath.setTextColor(Color.rgb(255, 48, 48));
                                        mBpm.setTextColor(Color.rgb(255, 48, 48));

                                    } else {
                                        change = true;
                                        mBreath.setTextColor(Color.TRANSPARENT); //这个是透明，=看不到文字
                                        mBpm.setTextColor(Color.TRANSPARENT);
                                    }
                                }
                            });
                        }
                    };
                    breathTimerRun = false;
                    Log.e("Diappp",data.substring(20, 21));
                    if (data.substring(20, 21).equals("1")) {//06偵測尿溼狀況
                        if(SetupFragment.isLogin){
                            push06timer += 5;
                            Log.e("Diappp","time:" + push06timer);
                        }else{
                            push06timer = 0 ;
                            Log.e("Diappp","time:" + push06timer);
                        }

                        if (Diapertimmer == 0) {
                            Diapertimmer = 1;
                        } else if (Diapertimmer >= 360 || push06timer >=360) {
                            mSpitGifView.setVisibility(View.VISIBLE);//GIF圖顯示
                            mSpit.setVisibility(View.INVISIBLE);//預設圖隱藏
                            ringset[3] = 0 ;
                            if (ringset[3] == 0) {
                                if(ringset[1] == 0 || labaset2 == 1) {
                                    if (labaset1 == 0 && MainActivity.isring == 0) {
                                        Log.e("laba1", "ring1");
                                        if (!MainActivity.ringTone.isPlaying()) {
                                            MainActivity.ringTone.play();
                                            Log.e("Diapertimmer", "play" + labaset1 + " " + ringset[3] + " " + ringset[1]);
                                        }
                                    } else {
                                        Log.e("laba1", "ring2");
                                        //if (MainActivity.ringTone.isPlaying()) {
                                        MainActivity.ringTone.stop();
                                        Log.e("Diapertimmer", "stop" + labaset1 + " " + ringset[3] + " " + ringset[1]);
                                        //}
                                    }
                                }
                            }else{
                                if(MainActivity.ringTone.isPlaying()){
                                    MainActivity.ringTone.stop();
                                }
                            }
                        } else if(Diapertimmer < 360 || push06timer<360){
                            MainActivity.ringTone.stop();
                        }
                    } else if (data.substring(20, 21).equals("0")) {//0即為正常
                        mSpitGifView.setVisibility(View.INVISIBLE);
                        mSpit.setVisibility(View.VISIBLE);
                        Diapertimmer = 0;
                        push06timer = 0;
                        Log.e("Diappp","time: "+ push06timer);
                        //鈴聲
                        MainActivity.ringTone.stop();
                    }
                    //mStatus.setText(getResources().getText(R.string.Status_value) + status + "\n" + getResources().getText(R.string.Status_value2) + data.substring(20, 21));
                } else if (pid.equals("05")) {//05為偵測吐奶值模式
                    mAddress.setText(MainActivity.address);

                    if (tempTimerRun) {
                        timer2.cancel();
                        TempTVtask.cancel();
                    }
                    stopAnimationdiaper();
                    mBody.setVisibility(View.INVISIBLE);
                    mTempType.setVisibility(View.INVISIBLE);
                    mBody.setVisibility(View.GONE);
                    mTempType.setVisibility(View.GONE);

                    mSpit.setVisibility(View.VISIBLE);
                    if (data.substring(20, 21).equals("1")) {//1代表偵測到吐奶
                        mSpitGifView.setVisibility(View.VISIBLE);//GIF圖顯示
                        mSpit.setVisibility(View.INVISIBLE);//預設圖隱藏
                        if (labaset1 == 0 && MainActivity.isring == 0) {
                            if (!MainActivity.ringTone.isPlaying()) {
                                MainActivity.ringTone.play();
                            }
                        } else {
                            if (MainActivity.ringTone.isPlaying()) {
                                MainActivity.ringTone.stop();
                            }
                        }
                        //   mSpit.setImageResource(R.drawable.spit2);
                        //   mSpit.setImageDrawable(spitGif);
                    } else if (data.substring(20, 21).equals("0")) {//0即為正常
                        mSpitGifView.setVisibility(View.INVISIBLE);
                        mSpit.setVisibility(View.VISIBLE);
                        if (MainActivity.ringTone.isPlaying()) {
                            MainActivity.ringTone.stop();
                        }
                    }
                    //mStatus.setText(getResources().getText(R.string.Status_value) + status + "\n" + getResources().getText(R.string.Status_value2) + data.substring(20, 21));
                } else if (pid.equals("03")) {//03為偵測體溫模式
                    //mStatus.setText(getResources().getString(R.string.Status_value) + status);
                    mAddress.setText(MainActivity.address);
                    mSpit.setVisibility(View.INVISIBLE);
                    mSpitGifView.setVisibility(View.INVISIBLE);
                    mBody.setVisibility(View.VISIBLE);
                    mTempType.setVisibility(View.VISIBLE);
                    mImageButton2.setVisibility(View.VISIBLE);
                    if (SetupFragment.isC) {//若設定為攝氏
                        if (Float.parseFloat(bodyTemp) >= SetupFragment.highTemp) {//預設高溫


//                            Animation anim = new AlphaAnimation(0,1);
//                            anim.setDuration(400); //You can manage the blinking time with this parameter
//                            anim.setStartOffset(20);
//                            anim.setRepeatMode(Animation.REVERSE);
//                            anim.setRepeatCount(Animation.INFINITE);

//                            mBody.startAnimation(anim);
//                            mTempType.startAnimation(anim);

                            if (!tempTimerRun) {
                                timer2.schedule(TempTVtask, 0, 500);
                                tempTimerRun = true;
                                ringset[0] = 1;
                            }
                        } else if (Float.parseFloat(bodyTemp) <= SetupFragment.lowTemp) {//預測低溫


//                            Animation anim = new AlphaAnimation(0,1);
//                            anim.setDuration(400); //You can manage the blinking time with this parameter
//                            anim.setStartOffset(20);
//                            anim.setRepeatMode(Animation.REVERSE);
//                            anim.setRepeatCount(Animation.INFINITE);
//
//                            mBody.startAnimation(anim);
//                            mTempType.startAnimation(anim);

                            if (!tempTimerRun) {
                                timer2.schedule(TempTVtask, 0, 500);
                                tempTimerRun = true;
                                ringset[0] = 1;
                            }
                        } else {
                            if (tempTimerRun) {
                                timer2.cancel();
                                TempTVtask.cancel();
                                timer2 = new Timer();
                                ringset[0] = 0;
                                TempTVtask = new TimerTask() {
                                    @Override
                                    public void run() {
                                        mainActivity.runOnUiThread(new Runnable() {
                                            public void run() {
//                                                if (change2) {
//                                                    change2 = false;
                                                mBody.startAnimation(animT);
                                                mTempType.startAnimation(animT);
                                                mBody.setTextColor(Color.rgb(255, 48, 48));
                                                mTempType.setTextColor(Color.rgb(255, 48, 48));

//                                                } else {
//                                                    change2 = true;
//                                                    mBody.setTextColor(Color.TRANSPARENT); //这个是透明，=看不到文字
//                                                    mTempType.setTextColor(Color.TRANSPARENT);
//                                                }
                                            }
                                        });
                                    }
                                };
                                tempTimerRun = false;
                            }
                            stopAnimationT();
                            mBody.setTextColor(Color.BLACK);//黑
                            mTempType.setTextColor(Color.BLACK);

                        }

                        if (Float.parseFloat(bodyTemp) != 100) {
                            if (bodyTemp.length() == 4) {//只有小數點後一位
                                mBody.setText(bodyTemp + "0");//補個0
                            } else
                                mBody.setText(bodyTemp);
                        } else
                            mBody.setText(bodyTemp + "0");

                        mTempType.setText("°C");
                    } else if (!SetupFragment.isC) {//若設定為華氏

                        float bodyTempF = (float) (Float.parseFloat(bodyTemp) * 1.8 + 32);//轉華氏
                        if (bodyTempF >= SetupFragment.highTemp) {
                            if (!tempTimerRun) {
                                timer2.schedule(TempTVtask, 0, 500);
                                tempTimerRun = true;
                                ringset[0] = 1;

                            }
                        } else if (bodyTempF <= SetupFragment.lowTemp) {
                            if (!tempTimerRun) {
                                timer2.schedule(TempTVtask, 0, 500);
                                tempTimerRun = true;
                                ringset[0] = 1;
                            }
                        } else {
                            if (tempTimerRun) {
                                timer2.cancel();
                                TempTVtask.cancel();
                                ringset[0] = 0;
                                timer2 = new Timer();
                                TempTVtask = new TimerTask() {
                                    @Override
                                    public void run() {
                                        mainActivity.runOnUiThread(new Runnable() {
                                            public void run() {
//                                                if (change2) {
//                                                    change2 = false;
                                                mBody.setTextColor(Color.rgb(255, 48, 48));
                                                mTempType.setTextColor(Color.rgb(255, 48, 48));

//                                                } else {
//                                                    change2 = true;
//                                                    mBody.setTextColor(Color.TRANSPARENT); //这个是透明，=看不到文字
//                                                    mTempType.setTextColor(Color.TRANSPARENT);
//                                                }
                                            }
                                        });
                                    }
                                };
                                tempTimerRun = false;
                            }
                            mBody.setTextColor(Color.BLACK);
                            mTempType.setTextColor(Color.BLACK);
                        }
                        DecimalFormat df = new DecimalFormat("#.##");//取小數後2位

                        String bodyTempFstr = df.format(bodyTempF);

                        if (Float.parseFloat(bodyTempFstr) < 100) {
                            if (bodyTempFstr.length() == 4) {//只有小數點後一位
                                mBody.setText(bodyTempFstr + "0");
                            } else
                                mBody.setText(bodyTempFstr);
                        } else if (Float.parseFloat(bodyTempFstr) >= 100) {
                            if (bodyTempFstr.length() == 5) {//只有小數點後一位
                                mBody.setText(bodyTempFstr + "0");
                            } else
                                mBody.setText(bodyTempFstr);
                        }

                        //  mBody.setText(bodyTempFstr);
                        mTempType.setText("°F");
                    }
                    if (ringset[0] == 1 && ringset[3] == 0) {
                        if(ringset[1] == 0 || labaset2 == 1){
                            if (labaset1 == 0 && MainActivity.isring == 0) {
                                Log.e("laba1","ring5");
                                if (!MainActivity.ringTone.isPlaying()) {
                                    MainActivity.ringTone.play();
                                }
                            } else {
                                Log.e("laba1","ring6");
                                if (MainActivity.ringTone.isPlaying()) {
                                    MainActivity.ringTone.stop();
                                }
                            }
                        }
                    }
                }
            }
        }

        if(!SetupFragment.isLogin) {                                                       //進入推波時才需處理

            if (MainActivity.isstate == 1) {
                mwifi.setImageResource(R.drawable.btsearching);
                if (tempTimerRun) {
                    timer2.cancel();
                    TempTVtask.cancel();
                    timer2 = new Timer();
                    ringset[0] = 0;
                    TempTVtask = new TimerTask() {
                        @Override
                        public void run() {
                            mainActivity.runOnUiThread(new Runnable() {
                                public void run() {
                                    if (change2) {
                                        change2 = false;
                                        mBody.setTextColor(Color.rgb(255, 48, 48));
                                        mTempType.setTextColor(Color.rgb(255, 48, 48));

                                    } else {
                                        change2 = true;
                                        mBody.setTextColor(Color.TRANSPARENT); //这个是透明，=看不到文字
                                        mTempType.setTextColor(Color.TRANSPARENT);
                                    }
                                }
                            });
                        }
                    };
                    tempTimerRun = false;
                }
                mBody.setTextColor(Color.BLACK);
                mTempType.setTextColor(Color.BLACK);
                mBody.setText("00.00");
                if (breathTimerRun) {//如果timer在啟動狀態
                    timer.cancel();//取消timer
                    ringset[2] = 0;
                    BreathTVtask.cancel();//刪除該task
                    timer = new Timer();//重設timer
                    BreathTVtask = new TimerTask() {//重設timerTask
                        public void run() {
                            mainActivity.runOnUiThread(new Runnable() {
                                public void run() {
                                    if (change) {
                                        change = false;
                                        mBreath.setTextColor(Color.rgb(255, 48, 48));
                                        mBpm.setTextColor(Color.rgb(255, 48, 48));

                                    } else {
                                        change = true;
                                        mBreath.setTextColor(Color.TRANSPARENT); //这个是透明，=看不到文字
                                        mBpm.setTextColor(Color.TRANSPARENT);
                                    }
                                }
                            });
                        }
                    };
                    breathTimerRun = false;//timer未啟動
                }
                ringset[3] = 0;

                mBattery.setVisibility(View.VISIBLE);
                mBattery.setBackgroundResource(R.drawable.battery3);
                if (batteryTimerRun) {//如果timer在啟動狀態
                    timer3.cancel();//取消timer
                    BatteryTVtask.cancel();//刪除該task
                    timer3 = new Timer();//重設timer
                    batteryTimerRun = false;
                    BatteryTVtask = new TimerTask() {//讓文字有閃爍效果的tinerTask，當偵測到的值高於或低於設定的警示值
                        @Override
                        public void run() {
                            mainActivity.runOnUiThread(new Runnable() {
                                public void run() {
                                    if (change3) {
                                        change3 = false;
                                        mBattery.setVisibility(View.INVISIBLE);
                                    } else {
                                        change3 = true;
                                        mBattery.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        }
                    };
                    Log.e("Battery","1086");
                }

                mKick.setVisibility(View.VISIBLE);
                mWake.setVisibility(View.VISIBLE);
                mEscape.setVisibility(View.VISIBLE);
                mWakeGifView.setVisibility(View.INVISIBLE);
                mEscapeGifView.setVisibility(View.INVISIBLE);
                mKickGifView.setVisibility(View.INVISIBLE);

                mSpit.setVisibility(View.INVISIBLE);
                mBody.setVisibility(View.VISIBLE);
                mTempType.setVisibility(View.VISIBLE);
                mSpitGifView.setVisibility(View.INVISIBLE);//GIF圖顯示

                mBreath.setTextColor(Color.BLACK);
                mBpm.setTextColor(Color.BLACK);
                mBreath.setText("00");
                mName.setVisibility(View.INVISIBLE);
                //mStatus.setText("----");
                mRoom.setText("----");
                mBattery.setText("");
            }
        }

        if(SetupFragment.isLogin) {                                                       //進入推波時才需處理
            if(dataSet.getisRegist().equals("0")) {
                if (enable.equals("1")) {
                    mwifi.setImageResource(R.drawable.bell_disable);
                    Log.e("dddd", "ddd");
                } else if (enable.equals("0")) {
                    mwifi.setImageResource(R.drawable.bell_enable);
                    Log.e("dddd", "cccccccccccccc");
                }
                if (dataSet.getisState() == 0) {     //getisState 1 是可用
                    if (tempTimerRun) {
                        timer2.cancel();
                        TempTVtask.cancel();
                        timer2 = new Timer();
                        ringset[0] = 0;
                        TempTVtask = new TimerTask() {
                            @Override
                            public void run() {
                                mainActivity.runOnUiThread(new Runnable() {
                                    public void run() {
                                        if (change2) {
                                            change2 = false;
                                            mBody.setTextColor(Color.rgb(255, 48, 48));
                                            mTempType.setTextColor(Color.rgb(255, 48, 48));

                                        } else {
                                            change2 = true;
                                            mBody.setTextColor(Color.TRANSPARENT); //这个是透明，=看不到文字
                                            mTempType.setTextColor(Color.TRANSPARENT);
                                        }
                                    }
                                });
                            }
                        };
                        tempTimerRun = false;
                    }
                    mBody.setTextColor(Color.BLACK);
                    mTempType.setTextColor(Color.BLACK);
                    mBody.setText("00.00");
                    if (breathTimerRun) {//如果timer在啟動狀態
                        timer.cancel();//取消timer
                        ringset[2] = 0 ;
                        BreathTVtask.cancel();//刪除該task
                        timer = new Timer();//重設timer
                        BreathTVtask = new TimerTask() {//重設timerTask
                            public void run() {
                                mainActivity.runOnUiThread(new Runnable() {
                                    public void run() {
                                        if (change) {
                                            change = false;
                                            mBreath.setTextColor(Color.rgb(255, 48, 48));
                                            mBpm.setTextColor(Color.rgb(255, 48, 48));

                                        } else {
                                            change = true;
                                            mBreath.setTextColor(Color.TRANSPARENT); //这个是透明，=看不到文字
                                            mBpm.setTextColor(Color.TRANSPARENT);
                                        }
                                    }
                                });
                            }
                        };
                        breathTimerRun = false;//timer未啟動
                    }
                    ringset[3] = 0 ;

                    mBattery.setVisibility(View.VISIBLE);
                    mBattery.setBackgroundResource(R.drawable.battery3);
                    if (batteryTimerRun) {//如果timer在啟動狀態
                        timer3.cancel();//取消timer
                        BatteryTVtask.cancel();//刪除該task
                        timer3 = new Timer();//重設timer
                        batteryTimerRun = false;
                        BatteryTVtask = new TimerTask() {//讓文字有閃爍效果的tinerTask，當偵測到的值高於或低於設定的警示值
                            @Override
                            public void run() {
                                mainActivity.runOnUiThread(new Runnable() {
                                    public void run() {
                                        if (change3) {
                                            change3 = false;
                                            mBattery.setVisibility(View.INVISIBLE);
                                        } else {
                                            change3 = true;
                                            mBattery.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });
                            }
                        };
                        Log.e("Battery","1198");
                    }

                    mKick.setVisibility(View.VISIBLE);
                    mWake.setVisibility(View.VISIBLE);
                    mEscape.setVisibility(View.VISIBLE);
                    mWakeGifView.setVisibility(View.INVISIBLE);
                    mEscapeGifView.setVisibility(View.INVISIBLE);
                    mKickGifView.setVisibility(View.INVISIBLE);

                    mSpit.setVisibility(View.INVISIBLE);
                    mBody.setVisibility(View.VISIBLE);
                    mTempType.setVisibility(View.VISIBLE);
                    mSpitGifView.setVisibility(View.INVISIBLE);//GIF圖顯示

                    mBreath.setTextColor(Color.BLACK);
                    mBpm.setTextColor(Color.BLACK);
                    mBreath.setText("00");
                    mName.setVisibility(View.INVISIBLE);
                    //mStatus.setText("----");
                    mRoom.setText("----");
                    mBattery.setText("");
                }
            }else{                    //Regist是1是沒註冊
                mwifi.setImageResource(R.drawable.btsearching);
                if (tempTimerRun) {
                    timer2.cancel();
                    TempTVtask.cancel();
                    timer2 = new Timer();
                    ringset[0] = 0;
                    TempTVtask = new TimerTask() {
                        @Override
                        public void run() {
                            mainActivity.runOnUiThread(new Runnable() {
                                public void run() {
                                    if (change2) {
                                        change2 = false;
                                        mBody.setTextColor(Color.rgb(255, 48, 48));
                                        mTempType.setTextColor(Color.rgb(255, 48, 48));

                                    } else {
                                        change2 = true;
                                        mBody.setTextColor(Color.TRANSPARENT); //这个是透明，=看不到文字
                                        mTempType.setTextColor(Color.TRANSPARENT);
                                    }
                                }
                            });
                        }
                    };
                    tempTimerRun = false;
                }
                mBody.setTextColor(Color.BLACK);
                mTempType.setTextColor(Color.BLACK);
                mBody.setText("00.00");
                if (breathTimerRun) {//如果timer在啟動狀態
                    timer.cancel();//取消timer
                    ringset[2] = 0 ;
                    BreathTVtask.cancel();//刪除該task
                    timer = new Timer();//重設timer
                    BreathTVtask = new TimerTask() {//重設timerTask
                        public void run() {
                            mainActivity.runOnUiThread(new Runnable() {
                                public void run() {
                                    if (change) {
                                        change = false;
                                        mBreath.setTextColor(Color.rgb(255, 48, 48));
                                        mBpm.setTextColor(Color.rgb(255, 48, 48));

                                    } else {
                                        change = true;
                                        mBreath.setTextColor(Color.TRANSPARENT); //这个是透明，=看不到文字
                                        mBpm.setTextColor(Color.TRANSPARENT);
                                    }
                                }
                            });
                        }
                    };
                    breathTimerRun = false;//timer未啟動
                }
                ringset[3] = 0 ;

                mBattery.setVisibility(View.VISIBLE);
                mBattery.setBackgroundResource(R.drawable.battery3);
                if (batteryTimerRun) {//如果timer在啟動狀態
                    timer3.cancel();//取消timer
                    BatteryTVtask.cancel();//刪除該task
                    timer3 = new Timer();//重設timer
                    batteryTimerRun = false;
                    BatteryTVtask = new TimerTask() {//讓文字有閃爍效果的tinerTask，當偵測到的值高於或低於設定的警示值
                        @Override
                        public void run() {
                            mainActivity.runOnUiThread(new Runnable() {
                                public void run() {
                                    if (change3) {
                                        change3 = false;
                                        mBattery.setVisibility(View.INVISIBLE);
                                    } else {
                                        change3 = true;
                                        mBattery.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        }
                    };
                    Log.e("Battery","1300");
                }

                mKick.setVisibility(View.VISIBLE);
                mWake.setVisibility(View.VISIBLE);
                mEscape.setVisibility(View.VISIBLE);
                mWakeGifView.setVisibility(View.INVISIBLE);
                mEscapeGifView.setVisibility(View.INVISIBLE);
                mKickGifView.setVisibility(View.INVISIBLE);

                mSpit.setVisibility(View.INVISIBLE);
                mBody.setVisibility(View.VISIBLE);
                mTempType.setVisibility(View.VISIBLE);
                mSpitGifView.setVisibility(View.INVISIBLE);//GIF圖顯示

                mBreath.setTextColor(Color.BLACK);
                mBpm.setTextColor(Color.BLACK);
                mBreath.setText("00");
                mName.setVisibility(View.INVISIBLE);
                //mStatus.setText("----");
                mRoom.setText("----");
                mBattery.setText("");
            }
        }
    }



    public AlertDialog setListItemNameDialog(final String address) {                                          //讓使用者設定tag名稱
        final View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_setlistitemname, null);
        //  final Listitem tmpitem = getItemAddress(position);
        AlertDialog.Builder tmpDialog = new AlertDialog.Builder(mContext);
        tmpDialog.setTitle(R.string.dialog_title_setListItemName);
        tmpDialog.setView(dialogView);
        tmpDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                EditText editText = (EditText) dialogView.findViewById(R.id.editText);
                String tmpString = editText.getText().toString();
                if (tmpString != null) {
                    if (!tmpString.trim().equals("")) {
                        //  tmpitem.setName(tmpString);
                        setItemName(address , tmpString);
                    } else {
                        Toast.makeText(mContext, R.string.dialog_Message_NoEnter, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, R.string.dialog_Message_NoEnter, Toast.LENGTH_SHORT).show();
                }
            }
        });
        return tmpDialog.create();
    }

    public void setItemName(String address , String name) {
        //Log.d(TAG, "address=" + address);
        // mNames.set(position, name);
        //  int position = dataSet.indexOf(listitem);
        if (courseDAO.update(address,name)) {
            Toast.makeText(mContext, "Edit Name Success", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "error", Toast.LENGTH_SHORT).show();
        }
    }


    public String transBodyValue(String ID1) {                                       //解體溫

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
    // transform data

    public String transRoomValue(String ID1) {                                       //解室溫

        byte data1 = (byte) (Integer.parseInt(ID1.substring(22, 24), 16) & 0xff);
        //Byte.parseByte(ID1.substring(24, 26) );
        byte data2 = (byte) (Integer.parseInt(ID1.substring(24, 26), 16) & 0xff);
        byte data3 = (byte) (Integer.parseInt(ID1.substring(26, 28), 16) & 0xff);
        byte data4 = (byte) (Integer.parseInt(ID1.substring(28, 30), 16) & 0xff);
        /*Log.e("data1"+ "::::",data1 +"");
        Log.e("data2"+ "::::",data2+"");
		Log.e("data3"+ "::::",data3+"");
		Log.e("data4"+ "::::",data4+"");*/
        float tem = bytesToFloat(data1, data2, data3, data4);//decodeTempLevel(data,0);

        return tem + "";
    }

    /**
     * Convert signed bytes to a 32-bit short float value.
     */
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
//        mAddress.setText("8787");
    // context.mAddress.setText(Address);
    // mAddress.setText("654");
   /* public String getAddress(String address){
        mAddress.setText(address);
        return address;
    }*/

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
/*
    public void setText(String name){
        mAddress.setText(name);
    }*/

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //     isadd = true;
   /*     if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private AlertDialog registerAlertDialog() {                                                                   //詢問tag是否要註冊
        final View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_setregist, null);
        AlertDialog.Builder tmpDialog = new AlertDialog.Builder(getActivity());
        tmpDialog.setTitle(R.string.tag_regist_ask_title);
        tmpDialog.setMessage(R.string.tag_regist_ask_content);
        tmpDialog.setView(dialogView);
        tmpDialog.setPositiveButton(R.string.register_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                if(MainActivity.settings.getBoolean("isEnable",true)){
                    match(MainActivity.baddress, "true");
                }else if(!MainActivity.settings.getBoolean("isEnable",true)){
                    match(MainActivity.baddress, "false");
                }
            }
        });
        return tmpDialog.create();
    }

    private AlertDialog disregisterAlertDialog() {                                                            //詢問tag是否要解除註冊
        final View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_setregist, null);
        AlertDialog.Builder tmpDialog = new AlertDialog.Builder(getActivity());
        tmpDialog.setTitle(R.string.chang_tag_status_title);
        tmpDialog.setView(dialogView);
        tmpDialog.setPositiveButton(R.string.unregister_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                removematch(MainActivity.baddress);
            }
        });
        return tmpDialog.create();
    }

    private void match(final String mac, final String ableBoolean) {                                            //tag註冊php
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
                        dataSet.setisRegist("0") ;
                        Log.e("RetMsg", RetMsg);
                    } else if (ret.equals("1")) {
                        //token不正確
                        Message msg = new Message();
                        msg.what = 1;
                        Log.e("RetMsg", RetMsg);
                    } else if (ret.equals("2")) {
                        //tag已和別的手機配對
                        Message msg = new Message();
                        msg.what = 1;
                        Log.e("RetMsg", RetMsg);
                    } else if (ret.equals("3")) {
                        //tag已和別的手機配對
                        Message msg = new Message();
                        msg.what = 1;
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

    private void removematch(final String mac) {                                                        //tag移除php
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                String postParameter = "token=" + MainActivity.token + "&device=" + mac;
                Log.e("string=", postParameter);
                URL url = null;

                try {
                    url = new URL("https://"+MainActivity.ip+"/jpushex/device_remove.php?" + postParameter);

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
                    String RetMsg = jObject.getString("RetMsg");
                    //          String address = buffer.toString().substring(2);

                    if (ret.equals("0")) {
                        //配對成功

                        for (int i = 0; i < customadapter.dataSet.size(); i++) {
                            if (customadapter.dataSet.get(i).macaddress.equals(mac)) {
                                customadapter.dataSet.get(i).setisRegist("1");
                                customadapter.dataSet.get(i).setIsenable("1");

                                //  mRegistListAdapter.notifyDataSetChanged();
                            }
                        }
                        dataSet.setisRegist("1") ;
                        MainActivity.baddress = "null" ;
                        MainActivity.address = "null" ;

                        Message msg2 = new Message();
                        msg2.what = 2;
                    } else if (ret.equals("1")) {
                        //token不正確
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = getResources().getText(R.string.token_not_correct);
                    } else if (ret.equals("2")) {
                        //tag已和別的手機配對
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = getResources().getText(R.string.tag_not_exist_in_database);
                    } else if (ret.equals("3")) {
                        //tag已和別的手機配對
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = "原本就沒配對";
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

    static class kickthread extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    while (iskick) {
                        kicktimer++;
                        if(Diapertimmer!=0) {
                            Diapertimmer++;
                        }
                        sleep(1000);
                        Log.e("kick", String.valueOf(kicktimer));
                        Log.e("Diapertimmer", String.valueOf(Diapertimmer));
                    }
                    sleep(1000);
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class neartagthread extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(5000);
                Message msg = new Message();
                checknearhand.sendMessage(msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    //new
    final BluetoothGattCallback singlemBleCallback = new BluetoothGattCallback() {                    //以下為藍芽連線
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.e("setUI_連線_連線狀態改變","onConnectionStateChange");
//            try{
//                Log.e("Gatt_gatt", String.valueOf(gatt));
//            Log.e("Gatt_status", String.valueOf(status));
            Log.e("setUI_Gatt_newState", String.valueOf(newState));
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                singlemBluetoothGatt.discoverServices();   //開始搜尋服務
//                Toast.makeText(MainActivity.mParent, getResources().getString(R.string.chinsice11), Toast.LENGTH_SHORT).show();
                Log.e("setUI_single_連線成功值", String.valueOf(newState));
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.e("setUI_single_連線失敗值", String.valueOf(newState));
                singlemBluetoothGatt.close();
                singlemBluetoothGatt.disconnect();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.e("setUI_連線_搜尋到服務","onServicesDiscovered");
            Log.e("setUI_disc",String.valueOf(status)); //0成功
            if (status == BluetoothGatt.GATT_SUCCESS) {
                singlemBluetoothSelectedService = singlemBluetoothGatt.getService(singleblueserverlink);
                ch = singlemBluetoothSelectedService.getCharacteristic(singlebluereadcode);
                singlemBluetoothGatt.readCharacteristic(ch) ;
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic){
            Log.e("setUI_連線_數值有變動","onCharacteristicChanged");
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.e("setUI_連線_讀取參數","onCharacteristicRead");
            Log.e("single_read123",String.valueOf(status));     //0成功
            if (status == BluetoothGatt.GATT_SUCCESS) {
                getWetParameter(characteristic) ;
            }
            else {
                Toast.makeText(MainActivity.mParent, getResources().getString(R.string.chinsice17), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status){
            Log.e("setUI_連線_寫入並改變特爭值","onCharacteristicWrite");
        }
    };

    public void getWetParameter(BluetoothGattCharacteristic characteristic) {
        Log.e("Single連線_singlebtAdapter", String.valueOf(singlebtAdapter));
        Log.e("Single連線_singlemBluetoothGatt", String.valueOf(singlemBluetoothGatt));
        Log.e("Single連線_characteristic", String.valueOf(characteristic));
        if (singlebtAdapter == null || singlemBluetoothGatt == null || characteristic == null) return;
        singlerawValue = characteristic.getValue();
        Log.e("setUI_mdata_singlerawValue", String.valueOf(singlerawValue));
        Message msg = new Message();
        handlerread.sendMessage(msg);
    }

    static public void disconnect() {
        singlemBluetoothGatt.disconnect();
        singlemBluetoothGatt.close();
        Log.e("setUI_連線_disconnect:", "結束連線");
    }

    Handler handlerread = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if(singlerawValue.length>5) {
                singlereadMeasureInterval = (int) (((singlerawValue[1] & 0xFF) << 8) + (singlerawValue[0] & 0xFF));
                String state = "";
                //----
//                String state = MainActivity.settings.getString("state", "");
//                Log.e("state",state);
                //---
                if (singlereadMeasureInterval == 4) {
                    state = "quick";
                } else if (singlereadMeasureInterval == 120) {
                    state = "sleep";
                } else {
                    state = "normal";
                }
                MainActivity.settings.edit().putString("state",state).commit();
                //Toast.makeText(MainActivity.mParent,getResources().getString(R.string.chinsice16), Toast.LENGTH_SHORT).show();
                mStatus.setText(state);
                firstin = false;
                changeTagScan = false;
                disconnect();

                reconnect = true;
                Log.e("5555555", String.valueOf(reconnect));
            }
            else{
                Toast.makeText(getActivity(), R.string.support_temperature, Toast.LENGTH_SHORT).show();
            }
        }
    };
}
