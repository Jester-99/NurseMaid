package com.example.admin.nursemaid1;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListPopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

//import com.sinopulsar.nursemaid.R ;
import com.sinopulsar.nursemaid1.R ;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static android.widget.Toast.LENGTH_SHORT;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SetupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SetupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SetupFragment extends Fragment implements View.OnClickListener {                   //設定頁面，包含藍芽連線
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static boolean isadd = false;
    public ProgressDialog progress_dialog;
    ArrayList<String> Alluser = new ArrayList<String>();
    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
    static boolean isC = false; //判斷為攝氏或華氏
    static String Uname = "";
    static float highTemp,lowTemp,fastBreath,slowBreath;
    EditText userEdit;
    ImageButton nameButton ;
    static EditText ed5, ed6 ;
    static BluetoothGatt mBluetoothGatt = null;
    static BluetoothGattService mBluetoothSelectedService = null;
    static BluetoothGattCharacteristic ch = null;
    UUID blueserverlink = UUID.fromString("00001809-0000-1000-8000-00805f9b34fb"); //"Health Thermometer"
    UUID bluereadcode = UUID.fromString("00002a21-0000-1000-8000-00805f9b34fb");  //Measurement Interval
    static BluetoothAdapter btAdapter = BluetoothService.bluetoothAdapter;
    static BluetoothDevice mBluetoothDevice = null;
    static Context mParent = MainActivity.mParent ;
    String token = "";

    private String[] highItems,lowItems,fastItems,slowItems;
    ArrayList<String> fastlist = new ArrayList<>();
    private Button singleB;
    private Button multiB;
    private static Button scanB;
    private static Button notifB;
    private Button enableB;
    private Button disableB;
    private Button cB;
    private Button fB;
    private Button norB;
    private Button sleepB;
    private Button sickB;
    private Button quickB;
    private Button longB;
    private Button KickNotienable;
    private Button KickNotidisable;
    private Spinner highspinner,lowspinner,fastspinner,slowspinner;
    private TextView ctext1,ctext2,textView22;
    private ArrayAdapter<String> highcList,highfList,lowcList,lowfList,fastList,slowList;
    private OnFragmentInteractionListener mListener;
    private static final String[] highc_group={"40","39.5","39","38.5","38","37.5","37"};//攝氏高溫
    private static final String[] lowc_group={"36","35.5","35","34.5","34","33.5","33"};//攝氏低溫
    private static final String[] highf_group={"104","103","102","101","100","99","98"};//華氏高溫
    private static final String[] lowf_group={"97","96","95","94","93","92","91"};//華氏低溫
    private static final String[] fast_group={"70","65","60","55","50","45"};//急促
    private static final String[] slow_group={"35","30","25","20","15","10"};//緩慢
    static int readMeasureInterval, pushset ;
    static int value[] ;
    static int Sensorblue = 0 ;
    static int Sensorred = 0 ;
    static EditText userEditText;
    static boolean bl = false;
    static boolean isLogin = false;
    String address="" , enable = "0" ;
    byte[] rawValue ;
    int check = 0 ;
    static UserSQLite userSQLite;
    SingleFragment singleFragment;
    //kicknotify
    static Boolean isKickNotifyOn = false;
    static String selectnowState = "normal";
    static boolean reconnect = false;
    public Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    //處理少量資訊或UI
                    //textView.setText(msg.obj.toString());
                    Toast.makeText(getActivity(), msg.obj.toString(), LENGTH_SHORT).show();
                    break;
                case 2:
                    //mRegistListAdapter.notifyDataSetChanged();
                    break;
                case 3:
                    Toast.makeText(getActivity(), R.string.check_internet, Toast.LENGTH_LONG).show();
                    break;
                case 4:
                    //sosDialog().show();
                    //streamID = soundPool.play(alertId, 1, 1, 0, -1, 1);
            }
        }
    };

    public Handler handlertest = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Toast.makeText(getContext(), getResources().getString(R.string.chinsice11), LENGTH_SHORT).show();
                    break;
                case 2:
                    if(check!=3) {
                        mBluetoothDevice = btAdapter.getRemoteDevice(address);
                        mBluetoothGatt = mBluetoothDevice.connectGatt(mParent, false, mBleCallback);
                    }
                    break;
                case 3:
                    Toast.makeText(getContext(), "結束連線", LENGTH_SHORT).show();
                    break;
                case 4:
                    Toast.makeText(getContext(), getResources().getString(R.string.chinsice14), LENGTH_SHORT).show();
                    break;
                case 5:
                    Toast.makeText(getContext(), getResources().getString(R.string.chinsice15), LENGTH_SHORT).show();
                    break;
                case 6:
                    Toast.makeText(getContext(), getResources().getString(R.string.chinsice17), LENGTH_SHORT).show();
                    break;
            }
        }
    };

    Handler handlerread = new Handler(Looper.myLooper()) {
        @SuppressLint("LongLogTag")
        @Override
        public void handleMessage(Message msg) {
            if(rawValue.length>5) {
                Log.e("setup_rawValue", String.valueOf(rawValue));
                readMeasureInterval = (int) (((rawValue[1] & 0xFF) << 8) + (rawValue[0] & 0xFF));
                Log.e("setup_readMeasureInterval", String.valueOf(readMeasureInterval));
                Sensorred = (int) (((rawValue[3] & 0xFF) << 8) + (rawValue[2] & 0xFF));
                Sensorblue = (int) (((rawValue[5] & 0xFF) << 8) + (rawValue[4] & 0xFF));
                String state = "";
                if (readMeasureInterval == 4) {
                    state = "quick";
                } else if (readMeasureInterval == 120) {
                    state = "sleep";
                } else {
                    state = "normal";
                }

                selectnowState = state;

                Log.e("srrrrr",state) ;
                MainActivity.settings.edit().putString("state",state).commit();
                buttonSetState(state);

                DecimalFormat format = new DecimalFormat("000");
                String w0 = format.format(Sensorred);
                String w1 = format.format(Sensorblue);
                ed5.setText(w0);
                ed6.setText(w1);
                Log.e("w0",w0);
                Log.e("w1",w1);
                Toast.makeText(getContext(),getResources().getString(R.string.chinsice16), LENGTH_SHORT).show();
                reconnect = true;
            }
            else{
                Toast.makeText(getActivity(), R.string.support_temperature, LENGTH_SHORT).show();
            }
        }
    };

    public SetupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SetupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SetupFragment newInstance(String param1, String param2) {
        SetupFragment fragment = new SetupFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
   //         mParam1 = getArguments().getString(ARG_PARAM1);
    //        mParam2 = getArguments().getString(ARG_PARAM2);
            MainActivity.isSetup = true;  //

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.fragment_setup, container, false);
        // Inflate the layout for this fragment
        singleFragment = new SingleFragment();

        userSQLite=new UserSQLite(getContext());
        userEdit = (EditText) myView.findViewById(R.id.username);
        nameButton = (ImageButton)myView.findViewById(R.id.imageButton3) ;

        nameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showListPopulWindow();
            }
        });

        userEditText = (EditText) myView.findViewById(R.id.username);
        ed5 = (EditText) myView.findViewById(R.id.editText5);
        ed6 = (EditText)myView.findViewById(R.id.editText6) ;
        textView22 = (TextView)myView.findViewById(R.id.textView22) ;

        value = new int[2] ;
        value[0] = 0 ;
        value[1] = 0 ;

        ctext1 = (TextView)myView.findViewById(R.id.ctext1);
        ctext2 = (TextView)myView.findViewById(R.id.ctext2);

        highspinner = (Spinner)myView.findViewById(R.id.highspinner);
        lowspinner = (Spinner)myView.findViewById(R.id.lowspinner);
        fastspinner = (Spinner)myView.findViewById(R.id.fastspinner);
        slowspinner = (Spinner)myView.findViewById(R.id.slowspinner);

        singleB = (Button)myView.findViewById(R.id.singleB);
        multiB = (Button)myView.findViewById(R.id.multiB);
        scanB = (Button)myView.findViewById(R.id.scanB);
        notifB = (Button)myView.findViewById(R.id.notifB);
        enableB = (Button)myView.findViewById(R.id.enableB);
        disableB = (Button)myView.findViewById(R.id.disableB);
        cB = (Button)myView.findViewById(R.id.cB);
        fB = (Button)myView.findViewById(R.id.fB);
        norB = (Button)myView.findViewById(R.id.norB);
        sleepB = (Button)myView.findViewById(R.id.sleepB);
//        longB = (Button)myView.findViewById(R.id.longB);
      //  sickB = (Button)myView.findViewById(R.id.sickB);
        quickB = (Button)myView.findViewById(R.id.quickB);
        KickNotienable = (Button)myView.findViewById(R.id.buttonKickOn);
        KickNotidisable = (Button)myView.findViewById(R.id.buttonKickOff);

        singleB.setOnClickListener(this);
        multiB.setOnClickListener(this);
        scanB.setOnClickListener(this);
        notifB.setOnClickListener(this);
        enableB.setOnClickListener(this);
        disableB.setOnClickListener(this);
        cB.setOnClickListener(this);
        fB.setOnClickListener(this);
        norB.setOnClickListener(this);
        sleepB.setOnClickListener(this);
   //     longB.setOnClickListener(this);
  //      sickB.setOnClickListener(this);
        quickB.setOnClickListener(this);
        KickNotienable.setOnClickListener(this);
        KickNotidisable.setOnClickListener(this);



        if(MainActivity.settings.getBoolean("isScan",true)){
            buttonSetScan();
            isLogin = false;
        }else if(!MainActivity.settings.getBoolean("isScan",true)){
            buttonSetPush();
            isLogin = true ;
        }
        if(MainActivity.settings.getBoolean("isEnable",true)){
            buttonSetEnable();
        }else if(!MainActivity.settings.getBoolean("isEnable",true)){
            buttonSetDisable();
        }
        if(MainActivity.settings.getBoolean("isC",true)){
            buttonSetC();
        }else if(!MainActivity.settings.getBoolean("isC",true)){
            buttonSetF();
        }
        if(!MainActivity.settings.getBoolean("KickNoti",false)){
            buttonSetKickNotiOff();
            //Log.e("kick button",String.valueOf(MainActivity.settings.getBoolean("KickNoti",false)));
        }else if(MainActivity.settings.getBoolean("KickNoti",false)){
            buttonSetKickNotiOn();
            //Log.e("kick button",String.valueOf(MainActivity.settings.getBoolean("KickNoti",false)));
        }
//        buttonSetState(MainActivity.settings.getString("state","normal"));
        highcList = new ArrayAdapter<String>(myView.getContext(),R.layout.myspinner,android.R.id.text1,highc_group);
        highfList = new ArrayAdapter<String>(myView.getContext(),R.layout.myspinner,android.R.id.text1,highf_group);
        lowcList = new ArrayAdapter<String>(myView.getContext(),R.layout.myspinner,android.R.id.text1,lowc_group);
        lowfList = new ArrayAdapter<String>(myView.getContext(),R.layout.myspinner,android.R.id.text1,lowf_group);
        fastList = new ArrayAdapter<String>(myView.getContext(),R.layout.myspinner,android.R.id.text1,fast_group);
        slowList = new ArrayAdapter<String>(myView.getContext(),R.layout.myspinner,android.R.id.text1,slow_group);

   /*     highcList.setDropDownViewResource(R.layout.myspinner);
        highfList.setDropDownViewResource(R.layout.myspinner);
        lowcList.setDropDownViewResource(R.layout.myspinner);
        lowfList.setDropDownViewResource(R.layout.myspinner);
        fastList.setDropDownViewResource(R.layout.myspinner);
        slowList.setDropDownViewResource(R.layout.myspinner);*/
/*
        highfList = ArrayAdapter.createFromResource(myView.getContext(),
                R.array.highf,
                R.layout.myspinner);

        lowcList = ArrayAdapter.createFromResource(myView.getContext(),
                R.array.lowc,
                R.layout.myspinner);

        lowfList = ArrayAdapter.createFromResource(myView.getContext(),
                R.array.lowf,
                R.layout.myspinner);

        fastList = ArrayAdapter.createFromResource(myView.getContext(),
                R.array.fast,
                R.layout.myspinner);

        slowList = ArrayAdapter.createFromResource(myView.getContext(),
                R.array.slow,
                R.layout.myspinner);
*/


        //高溫警示spinner
        if(isC){
            highspinner.setAdapter(highcList);
        }else if(!isC){
            highspinner.setAdapter(highfList);
        }
        highspinner.setSelection(MainActivity.settings.getInt("highListPostion",4));//取得上次設定的高溫選項位置
        highspinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(isC) {
                    highItems = getResources().getStringArray(R.array.highc);
                   // MainActivity.settings.edit().putString("highC",highItems[position]).commit();
                }else {
                    highItems = getResources().getStringArray(R.array.highf);
                   // MainActivity.settings.edit().putString("highF", highItems[position]).commit();
                }

                MainActivity.settings.edit().putInt("highListPostion", position).commit();//儲存設定的高溫選項位置
                //拿到選取的數字
                float selectvalue = Float.parseFloat(highItems[MainActivity.settings.getInt("highListPostion", 4)]);
                highTemp = selectvalue;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //低溫警示spinner
        if(isC){
            lowspinner.setAdapter(lowcList);
        }else if(!isC){
            lowspinner.setAdapter(lowfList);
        }
        lowspinner.setSelection(MainActivity.settings.getInt("lowListPostion",2));
        lowspinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(isC) {
                    lowItems = getResources().getStringArray(R.array.lowc);
                   // MainActivity.settings.edit().putString("lowC",lowItems[position]).commit();

                }else if(!isC) {
                    lowItems = getResources().getStringArray(R.array.lowf);
                 //   MainActivity.settings.edit().putString("lowF", lowItems[position]).commit();
                }
                //記錄選取的位置
                MainActivity.settings.edit().putInt("lowListPostion", position).commit();
                //拿到選取的數字
                float selectvalue = Float.parseFloat(lowItems[MainActivity.settings.getInt("lowListPostion", 2)]);
                lowTemp = selectvalue;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //急促警示spinner
        fastspinner.setAdapter(fastList);
        fastspinner.setSelection(MainActivity.settings.getInt("fastListPostion",4));
        fastItems = getResources().getStringArray(R.array.fast);
        fastBreath = Float.parseFloat(fastItems[MainActivity.settings.getInt("fastListPostion",4)]);
        fastspinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fastItems = getResources().getStringArray(R.array.fast);
                MainActivity.settings.edit().putInt("fastListPostion", position).commit();
                //拿到選取的數字
                int selectvalue = Integer.parseInt(fastItems[position]);
                fastBreath = Float.parseFloat(fastItems[MainActivity.settings.getInt("fastListPostion",4)]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //緩慢警示spinner
        slowspinner.setAdapter(slowList);
        slowspinner.setSelection(MainActivity.settings.getInt("slowListPostion",2));
        slowItems = getResources().getStringArray(R.array.slow);
        slowBreath = Float.parseFloat(slowItems[MainActivity.settings.getInt("slowListPostion",2)]);
        slowspinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                slowItems = getResources().getStringArray(R.array.slow);
                MainActivity.settings.edit().putInt("slowListPostion", position).commit();
                //拿到選取的數字
                int selectvalue = Integer.parseInt(slowItems[position]);
                slowBreath = Float.parseFloat(slowItems[MainActivity.settings.getInt("slowListPostion",2)]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (MainActivity.settings.getBoolean("isScan", true)) {
            if (MainActivity.settings.getBoolean("isSingle", true)) {
                address = MainActivity.baddress ;
                if(!address.equals("null")) {
                    Log.e("address87", address);
                    mBluetoothDevice = btAdapter.getRemoteDevice(address);
                    Log.e("address_SETTING", "開始設定的連線");
                    mBluetoothGatt = mBluetoothDevice.connectGatt(mParent, false, mBleCallback);
                }
                userEdit.setEnabled(true);
                nameButton.setEnabled(true);
                if(userSQLite.isHaveData(MainActivity.address)) {
                    userEdit.setText(userSQLite.ToGetNowUser(MainActivity.address));
                    Uname = userSQLite.ToGetNowUser(MainActivity.address) ;
                }else {
                    userEdit.setText("Sinopulsar");
                }
                buttonSetSingle();
                ed5.setEnabled(true);
                ed6.setEnabled(true);
                norB.setEnabled(true);
                sleepB.setEnabled(true);
                quickB.setEnabled(true);
                enableB.setEnabled(false);
                disableB.setEnabled(false);
                enableB.setBackgroundColor(Color.parseColor("#d6d7d7"));
                disableB.setBackgroundColor(Color.parseColor("#d6d7d7"));
                enableB.setTextColor(Color.parseColor("#888888"));
                disableB.setTextColor(Color.parseColor("#888888"));
            } else if (!MainActivity.settings.getBoolean("isSingle", true)) {
                userEdit.setEnabled(false);
                nameButton.setEnabled(false);
                buttonSetMulti();
                ed5.setEnabled(false);
                ed6.setEnabled(false);
                norB.setEnabled(false);
                sleepB.setEnabled(false);
                quickB.setEnabled(false);
                enableB.setEnabled(false);
                disableB.setEnabled(false);
                norB.setBackgroundColor(Color.parseColor("#d6d7d7"));
                sleepB.setBackgroundColor(Color.parseColor("#d6d7d7"));
                quickB.setBackgroundColor(Color.parseColor("#d6d7d7"));
                enableB.setBackgroundColor(Color.parseColor("#d6d7d7"));
                disableB.setBackgroundColor(Color.parseColor("#d6d7d7"));
                norB.setTextColor(Color.parseColor("#888888"));
                sleepB.setTextColor(Color.parseColor("#888888"));
                quickB.setTextColor(Color.parseColor("#888888"));
                enableB.setTextColor(Color.parseColor("#888888"));
                disableB.setTextColor(Color.parseColor("#888888"));
                //踢被 (不確定
                KickNotienable.setEnabled(false);
                KickNotienable.setBackgroundColor(Color.parseColor("#d6d7d7"));
                KickNotienable.setTextColor(Color.parseColor("#888888"));
                KickNotidisable.setEnabled(false);
                KickNotidisable.setBackgroundColor(Color.parseColor("#d6d7d7"));
                KickNotidisable.setTextColor(Color.parseColor("#888888"));
            }
        } else if (SetupFragment.isLogin) {
            ed5.setEnabled(false);
            ed6.setEnabled(false);
            norB.setEnabled(false);
            sleepB.setEnabled(false);
            quickB.setEnabled(false);
            norB.setTextColor(Color.parseColor("#888888"));
            sleepB.setTextColor(Color.parseColor("#888888"));
            quickB.setTextColor(Color.parseColor("#888888"));
            norB.setBackgroundColor(Color.parseColor("#d6d7d7"));
            sleepB.setBackgroundColor(Color.parseColor("#d6d7d7"));
            quickB.setBackgroundColor(Color.parseColor("#d6d7d7"));
            //踢被 (不確定
            KickNotienable.setEnabled(false);
            KickNotienable.setBackgroundColor(Color.parseColor("#d6d7d7"));
            KickNotienable.setTextColor(Color.parseColor("#888888"));
            KickNotidisable.setEnabled(false);
            KickNotidisable.setBackgroundColor(Color.parseColor("#d6d7d7"));
            KickNotidisable.setTextColor(Color.parseColor("#888888"));

            if(MainActivity.settings.getBoolean("isSingle",true)){
                buttonSetSingle();
                enableB.setEnabled(true);
                disableB.setEnabled(true);

            }else if(!MainActivity.settings.getBoolean("isSingle",true)){
                buttonSetMulti();
                enableB.setEnabled(false);
                disableB.setEnabled(false);
                enableB.setTextColor(Color.parseColor("#888888"));
                disableB.setTextColor(Color.parseColor("#888888"));
                enableB.setBackgroundColor(Color.parseColor("#d6d7d7"));
                disableB.setBackgroundColor(Color.parseColor("#d6d7d7"));
            }
        }
        return myView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    //    isadd = true;
    /*    if (context instanceof OnFragmentInteractionListener) {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //按下單數
            case R.id.singleB:
                MainActivity.settings.edit().putBoolean("isSingle",true).commit();
                buttonSetSingle();
                break;

            //按下多數
            case R.id.multiB:
                MainActivity.settings.edit().putBoolean("isSingle",false).commit();
                buttonSetMulti();
                break;

            //按下掃描
            case R.id.scanB:
//                MainActivity.settings.edit().putBoolean("isScan",true).commit();
                buttonSetScan();
                if(isLogin==true){
                    logoutDialog().show();
                }
                break;

            //按下推播
            case R.id.notifB:
//                MainActivity.settings.edit().putBoolean("isScan",false).commit();
                buttonSetPush();
                if(isLogin==false) {
                    loginDialog().show();//呈現對話視窗
                }
                break;

            //按下啟用
            case R.id.enableB:
                MainActivity.settings.edit().putBoolean("isEnable",true).commit();
                enable = "0" ;
                setenablethread() ;
                buttonSetEnable();
                break;

            //按下禁用
            case R.id.disableB:
                MainActivity.settings.edit().putBoolean("isEnable",false).commit();
                enable = "1" ;
                setenablethread() ;
                buttonSetDisable();
                break;

            case R.id.buttonKickOn:
                MainActivity.settings.edit().putBoolean("KickNoti",true).commit();
                buttonSetKickNotiOn();
                //Log.e("kick button",String.valueOf(MainActivity.settings.getBoolean("KickNoti",false)));
                break;

            case R.id.buttonKickOff:
                MainActivity.settings.edit().putBoolean("KickNoti",false).commit();
                buttonSetKickNotiOff();
                //Log.e("kick button",String.valueOf(MainActivity.settings.getBoolean("KickNoti",false)));
                break;

            //按下C
            case R.id.cB:
                MainActivity.settings.edit().putBoolean("isC",true).commit();
                highspinner.setAdapter(highcList);
                lowspinner.setAdapter(lowcList);
                highspinner.setSelection(MainActivity.settings.getInt("highListPostion",4));
                lowspinner.setSelection(MainActivity.settings.getInt("lowListPostion",2));
                buttonSetC();
                break;

            //按下F
            case R.id.fB:
                MainActivity.settings.edit().putBoolean("isC",false).commit();
                highspinner.setAdapter(highfList);
                lowspinner.setAdapter(lowfList);
                highspinner.setSelection(MainActivity.settings.getInt("highListPostion",4));
                lowspinner.setSelection(MainActivity.settings.getInt("lowListPostion",2));
                buttonSetF();
                break;

            //按下睡眠
            case R.id.norB:
                buttonSetState("normal");
                value[1] = 300 ;
                break;

            //按下迅速
            case R.id.sleepB:
                buttonSetState("sleep");
                value[1] = 120 ;
                break;

            //按下快速
            case R.id.quickB:
                buttonSetState("quick");
                value[1] = 4 ;
                break;

            default:
                break;
        }
    }

    private void buttonSetSingle(){
        MainActivity.navigationView.getMenu().findItem(R.id.single).setVisible(true);
        MainActivity.navigationView.getMenu().findItem(R.id.TemperatureChart).setVisible(true);
        MainActivity.navigationView.getMenu().findItem(R.id.multiple).setVisible(false);
        singleB.setBackgroundColor(Color.parseColor("#93d051"));
        multiB.setBackgroundColor(Color.parseColor("#d6d7d7"));
    }
    private void buttonSetMulti(){
        MainActivity.navigationView.getMenu().findItem(R.id.single).setVisible(false);
        MainActivity.navigationView.getMenu().findItem(R.id.TemperatureChart).setVisible(false);
        MainActivity.navigationView.getMenu().findItem(R.id.multiple).setVisible(true);
        singleB.setBackgroundColor(Color.parseColor("#d6d7d7"));
        multiB.setBackgroundColor(Color.parseColor("#93d051"));
    }
    public static void buttonSetScan(){
        scanB.setBackgroundColor(Color.parseColor("#93d051"));
        notifB.setBackgroundColor(Color.parseColor("#d6d7d7"));
        pushset = 0 ;
    }
    private static void buttonSetPush(){
        scanB.setBackgroundColor(Color.parseColor("#d6d7d7"));
        notifB.setBackgroundColor(Color.parseColor("#93d051"));
        pushset = 1 ;
    }
    private void buttonSetEnable(){
        enableB.setBackgroundColor(Color.parseColor("#93d051"));
        disableB.setBackgroundColor(Color.parseColor("#d6d7d7"));
    }
    private void buttonSetDisable(){
        enableB.setBackgroundColor(Color.parseColor("#d6d7d7"));
        disableB.setBackgroundColor(Color.parseColor("#93d051"));
    }
    private void buttonSetC(){
        isC = true;
        cB.setBackgroundColor(Color.parseColor("#93d051"));
        fB.setBackgroundColor(Color.parseColor("#d6d7d7"));
        ctext1.setText("℃ ");
        ctext2.setText("℃ ");
        highItems = getResources().getStringArray(R.array.highc);
        lowItems = getResources().getStringArray(R.array.lowc);
        highTemp = Float.parseFloat(highItems[MainActivity.settings.getInt("highListPostion", 4)]);
        lowTemp = Float.parseFloat(lowItems[MainActivity.settings.getInt("lowListPostion", 2)]);
    }

    private void buttonSetF(){
        isC = false;
        cB.setBackgroundColor(Color.parseColor("#d6d7d7"));
        fB.setBackgroundColor(Color.parseColor("#93d051"));
        ctext1.setText("℉");
        ctext2.setText("℉");
        highItems = getResources().getStringArray(R.array.highf);
        lowItems = getResources().getStringArray(R.array.lowf);
        highTemp = Float.parseFloat(highItems[MainActivity.settings.getInt("highListPostion", 4)]);
        lowTemp = Float.parseFloat(lowItems[MainActivity.settings.getInt("lowListPostion", 2)]);

    }
    //new
    private void buttonSetKickNotiOn(){
        KickNotienable.setBackgroundColor(Color.parseColor("#93d051"));
        KickNotidisable.setBackgroundColor(Color.parseColor("#d6d7d7"));
        isKickNotifyOn = true;
    }

    private void buttonSetKickNotiOff(){
        KickNotienable.setBackgroundColor(Color.parseColor("#d6d7d7"));
        KickNotidisable.setBackgroundColor(Color.parseColor("#93d051"));
        isKickNotifyOn = false;
    }

    private void buttonSetState(String state){
        selectnowState = state;
        switch (state){
            case "normal":
                norB.setBackgroundColor(Color.parseColor("#93d051"));
                sleepB.setBackgroundColor(Color.parseColor("#d6d7d7"));
                quickB.setBackgroundColor(Color.parseColor("#d6d7d7"));
                if(value[0] == 0) {
                    value[0] = 300;
                    value[1] = 300 ;
                }
               // quickB.setBackgroundColor(Color.parseColor("#d6d7d7"));
                break;
            case "sleep":
                norB.setBackgroundColor(Color.parseColor("#d6d7d7"));
                sleepB.setBackgroundColor(Color.parseColor("#93d051"));
                quickB.setBackgroundColor(Color.parseColor("#d6d7d7"));
                if(value[0] == 0) {
                    value[0] = 120;
                    value[1] = 120 ;
                }
               // quickB.setBackgroundColor(Color.parseColor("#d6d7d7"));
                break;
            case "quick":
                norB.setBackgroundColor(Color.parseColor("#d6d7d7"));
                sleepB.setBackgroundColor(Color.parseColor("#d6d7d7"));
                quickB.setBackgroundColor(Color.parseColor("#93d051"));
                if(value[0] == 0) {
                    value[0] = 4;
                    value[1] = 4 ;
                }
              //  quickB.setBackgroundColor(Color.parseColor("#d6d7d7"));
                break;
         /*   case "quick":
                norB.setBackgroundColor(Color.parseColor("#d6d7d7"));
                sleepB.setBackgroundColor(Color.parseColor("#d6d7d7"));
                sickB.setBackgroundColor(Color.parseColor("#d6d7d7"));
                quickB.setBackgroundColor(Color.parseColor("#93d051"));
                break;*/
            default:
                break;
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    final BluetoothGattCallback mBleCallback = new BluetoothGattCallback() {                    //以下為藍芽連線
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.e("連線_連線狀態改變","onConnectionStateChange");

            Message msg = new Message();
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mBluetoothGatt.discoverServices();   //開始搜尋服務
                Log.e("writeDataToCharacteristic1", String.valueOf(mBluetoothGatt));
                Log.e("連線_mBluetoothGatt", String.valueOf(mBluetoothGatt));
                msg.what = 1 ;
                handlertest.sendMessage(msg);
                Log.e("連線成功值", String.valueOf(newState));
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
//                msg.what = 2 ;
//                handlertest.sendMessage(msg);
//                mBluetoothGatt.close();
                mBluetoothDevice = btAdapter.getRemoteDevice(address);
                Log.e("連線失敗值", String.valueOf(newState));
                Log.e("連線_mBluetoothGatt", String.valueOf(mBluetoothGatt));
                Log.e("address_SETTING", "開始設定的連線(失敗過)");
                mBluetoothGatt = mBluetoothDevice.connectGatt(mParent, false, mBleCallback);
//                check++;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.e("連線_搜尋到服務","onServicesDiscovered");
            Log.e("disc",String.valueOf(status)); //0成功
            if (status == BluetoothGatt.GATT_SUCCESS) {
                mBluetoothSelectedService = mBluetoothGatt.getService(blueserverlink);
                ch = mBluetoothSelectedService.getCharacteristic(bluereadcode);
                Log.e("write", String.valueOf(ch));
                Log.e("write", String.valueOf(bluereadcode));
                mBluetoothGatt.readCharacteristic(ch);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic){
            Log.e("連線_數值有變動","onCharacteristicChanged");
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
        {
            Log.e("連線_讀取參數","onCharacteristicRead");
            Log.e("read123",String.valueOf(status));     //0成功
            if (status == BluetoothGatt.GATT_SUCCESS) {
                getWetParameter(characteristic) ;
            }
            else {
                Message msg = new Message();
                msg.what = 6 ;
                handlertest.sendMessage(msg);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status){
            Log.e("連線_寫入並改變特爭值","onCharacteristicWrite");
            Message msg = new Message();
            Log.e("status", String.valueOf(status));
            if(status==0){
                msg.what = 4 ;
                handlertest.sendMessage(msg);
            }
            else {
                msg.what = 5 ;
                handlertest.sendMessage(msg);
            }
            disconnect();
        }
    };

    static public void writeDataToCharacteristic(final BluetoothGattCharacteristic ch, final byte[] dataToWrite) {
        if (btAdapter == null || SetupFragment.mBluetoothGatt == null || ch == null) return;
        Log.e("writeDataToCharacteristic1", String.valueOf(ch));
        Log.e("writeDataToCharacteristic1", String.valueOf(mBluetoothGatt));
        ch.setValue(dataToWrite);
        Log.e("writeDataToCharacteristic2", String.valueOf(dataToWrite));
        bl = mBluetoothGatt.writeCharacteristic(ch);
        Log.e("writeDataToCharacteristic4", String.valueOf(bl));

        if(bl){
            Toast.makeText(mParent.getApplicationContext(), "寫入成功", LENGTH_SHORT).show();
            //0927新增
//            disconnect();
        }else {
            Toast.makeText(mParent.getApplicationContext(), "寫入失敗", LENGTH_SHORT).show();
        }
    }


    public void getWetParameter(BluetoothGattCharacteristic characteristic) {
        if (btAdapter == null || mBluetoothGatt == null || characteristic == null) return;
        rawValue = characteristic.getValue();
        Message msg = new Message();
        handlerread.sendMessage(msg);
        for(int i = 0;i < 6;i++){
            Log.e("setup_raw["+i+"]", String.valueOf(rawValue[i]));
//            Log.e("setup_raw["+i+"]", String.valueOf(rawValue[0]));
        }
    }

    static public void disconnect() {
        mBluetoothGatt.disconnect();
        mBluetoothGatt.close();
        Log.e("Setup連線", "結束連線");
    }

    public AlertDialog loginDialog() {                                           //詢問是否進入推波
        final View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_setregist, null);
        // final BleDevice tmpitem =
        // mDevicesListAdapter.getItemAddress(position);
        AlertDialog.Builder tmpDialog = new AlertDialog.Builder(getActivity());
        tmpDialog.setTitle(R.string.login_ask_title);
        tmpDialog.setMessage(R.string.login_ask_content);
        tmpDialog.setView(dialogView);
        tmpDialog.setPositiveButton(R.string.confirm_button, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                if(mBluetoothGatt!=null) {
                    disconnect();
                }
                Intent intent = new Intent() ;
                intent.setClass(getActivity(), Login_dialog.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);//设置不要刷新将要跳到的界面
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//它可以关掉所要到的界面中间的activity
                startActivity(intent);
                Log.e("login","???");

                //isLogin = true ;
            }
        });
        return tmpDialog.create();
    }

    private AlertDialog logoutDialog() {
        final View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_setregist, null);
        // final BleDevice tmpitem =
        // mDevicesListAdapter.getItemAddress(position);
        AlertDialog.Builder tmpDialog = new AlertDialog.Builder(getActivity());
        tmpDialog.setTitle(R.string.logout_ask_title);
        tmpDialog.setMessage(R.string.logout_ask_content);
        tmpDialog.setView(dialogView);
        tmpDialog.setPositiveButton(R.string.confirm_button, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                //if (haveInternet()) {
                    progress_dialog = new ProgressDialog(getActivity());
                    progress_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progress_dialog.setMessage(getResources().getText(R.string.logout_process).toString());
                    progress_dialog.setIndeterminate(false);
                    progress_dialog.setCancelable(false);
                    progress_dialog.show();
                    logout();
//                MainActivity.settings.edit().putString("login", "0").commit();
//                MainActivity.settings.edit().putBoolean("isScan",true).commit();

                final AlertDialog dlg = progress_dialog;
                dlg.show();

                final Timer t = new Timer();
                t.schedule(new TimerTask() {
                    public void run() {
                        //10秒自動消失
                        Log.e("dialogout","now "+dlg.isShowing());
                        if(dlg.isShowing()){
                            dlg.dismiss();
                            t.cancel();
                            Message msg = new Message();
                            logouthandler.sendMessage(msg);
                        }
                    }
                }, 30000);
                //isLogin = false ;
                //MultipleFragment.sec5_while = false ;

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

    private void logout() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();

                URL url = null;

                try {
                    TorkenDBhelper mdbhelper = new TorkenDBhelper(getActivity());
                    SQLiteDatabase db;

                    db = mdbhelper.getReadableDatabase();
                    Cursor c = db.rawQuery("SELECT * FROM " + DBcontract.DBcol.TABLE_NAME, null);

                    c.moveToFirst();
                    String itemrId = c.getString(
                            c.getColumnIndexOrThrow(DBcontract.DBcol.COLUMN_Rrgister_ID)
                    );

                    Log.e("sql:", itemrId);

                    token = itemrId;
                    Log.e("LOGOUT_token", token);
                    db.close();
                    String postParameter = "token=" + token;
                    Log.e("string=", postParameter);

                    url = new URL("https://"+MainActivity.ip+"/jpushex/logout.php?" + postParameter);

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
                    String RetMsg = jObject.getString("RetMsg");

                    Log.e("ret", ret);
                    Log.e("RetMsg", RetMsg);


                    if (ret.equals("0")) {
                        Log.e("LOGOUT_RET", "0");
                        MainActivity.preferences.edit()
                                .putString("account", null)
                                .putString("password", null)
                                .putString("rid", null)
                                .putString("token", null).commit();
                        Message msg = new Message();
                        msg.what = 1;
                        progress_dialog.dismiss();
                        msg.obj = getResources().getText(R.string.logout_success);
                        mHandler.sendMessage(msg);

                        MainActivity.settings.edit().putString("login", "0").commit();
                        MainActivity.settings.edit().putBoolean("isScan",true).commit();
                        isLogin = false;
                        MultipleFragment.sec5_while = false;

                        Intent intent = new Intent() ;
                        intent.setClass(getActivity(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);//设置不要刷新将要跳到的界面
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//它可以关掉所要到的界面中间的activity
                        startActivity(intent);

                    } else if (ret.equals("1")) {
                        Log.e("LOGOUT_RET", "1");
                        Message msg = new Message();
                        msg.what = 1;
                        progress_dialog.dismiss();
                        msg.obj = getResources().getText(R.string.logout_fail);
                        //.sendMessage(msg);
                    } else if (ret.equals("2")) {
                        Log.e("LOGOUT_RET", "2");
                        Message msg = new Message();
                        msg.what = 1;
                        progress_dialog.dismiss();
                        msg.obj = getResources().getText(R.string.logout_fail);
                        mHandler.sendMessage(msg);
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

    private void setenablethread() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();

                URL url = null;

                try {
                    String postParameter = "token=" + MainActivity.token + "&enabled=" + enable + "&username=" + Login_dialog.username + "&mac=" + MainActivity.baddress;
                    Log.e("string=", postParameter);

                    url = new URL("https://"+MainActivity.ip+"/jpushex/pushenabled.php?" + postParameter);

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

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
        thread.start();
    }

    private void showListPopulWindow() {
        if(userSQLite.isHaveData(MainActivity.address)){
            Alluser = userSQLite.ToGetAllUser(MainActivity.address) ;
            final ListPopupWindow listPopupWindow;
            listPopupWindow = new ListPopupWindow(getActivity());
            listPopupWindow.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1, Alluser));//用android内置布局，或设计自己的样式
            listPopupWindow.setAnchorView(userEdit);//以哪个控件为基准，在该处以logId为基准
            listPopupWindow.setModal(true);

            listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {//设置项点击监听
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    userEdit.setText(Alluser.get(i));//把选择的选项内容展示在EditText上
                    listPopupWindow.dismiss();//如果已经选择了，隐藏起来
                    ArrayList<String> data = new ArrayList<String>();
                    data = userSQLite.ToGetdata(MainActivity.address,Alluser.get(i)) ;
                    Log.e("溫度校正碼", String.valueOf(data));
                    ed5.setText(data.get(0));
                    ed6.setText(data.get(1));

                    String state = "";
                    if (Integer.valueOf(data.get(2)) == 4) {
                        state = "quick";
                    } else if (Integer.valueOf(data.get(2)) == 120) {
                        state = "sleep";
                    } else {
                        state = "normal";
                    }
                    value[0] = 0;
                    Log.e("srrrrr",state) ;
                    MainActivity.settings.edit().putString("state",state).commit();
                    buttonSetState(state);
                }
            });
            listPopupWindow.show();//把ListPopWindow展示出来
        }
    }

    Handler logouthandler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(Message msg) {
            final AlertDialog serverDialog = new AlertDialog.Builder(getActivity(),AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                    .setTitle(getResources().getString(R.string.chinsice0))
                    .setMessage(getResources().getString(R.string.serverbroke))
                    .setIcon(R.mipmap.ic_launcher)
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.confirm_button),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                    SetupFragment.buttonSetPush();
                                    isLogin = true;
                                    Log.e("dddd","islogin " + isLogin);
                                    //finish();
                                }
                            }).show();

        }
    };

}



