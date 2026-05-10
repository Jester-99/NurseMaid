package com.example.admin.nursemaid1;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
//import com.sinopulsar.nursemaid.R ;
import com.sinopulsar.nursemaid1.R;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cn.jpush.android.api.JPushInterface;

public class  Login_dialog extends Activity {                                                            //登入頁面
    Button login_btn, regist_btn, logout_btn;
    EditText account_ed, password_ed;
    static String username = "";
    String userpassword = "";
    String rid = MainActivity.rid;
    TextView rid_textView , account_TextView , password_TextView, textView;
    String token = "";
    String language = "";
    SharedPreferences preferences;
    MainActivity main = new MainActivity();
    ProgressDialog progress_dialog;
    SetupFragment setupFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFinishOnTouchOutside(false);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login_dialog);

        SysApplication.getInstance().addActivity(this);
        login_btn = (Button) findViewById(R.id.loginbutton);
        regist_btn = (Button) findViewById(R.id.registbutton);
        //  logout_btn = (Button)findViewById(R.id.logutbutton);
        account_ed = (EditText) findViewById(R.id.account_edit);
        password_ed = (EditText) findViewById(R.id.pwd_edit);
        rid = JPushInterface.getRegistrationID(getApplicationContext());
        Log.e("logrid",rid) ;
        rid_textView = (TextView) findViewById(R.id.rid_tv);
        rid_textView.setText(rid);
        textView = (TextView)findViewById(R.id.textView) ;
        textView.setText(getResources().getString(R.string.login_page_login_button));
        account_TextView = (TextView)findViewById(R.id.account_tv);
        password_TextView = (TextView)findViewById(R.id.password_tv);
        account_TextView.setText(R.string.login_page_ac);//帳號
        password_TextView.setText(R.string.login_page_pw);//密碼
        login_btn.setOnClickListener(login);
        regist_btn.setOnClickListener(register);
        login_btn.setText(R.string.login_page_login_button);
        regist_btn.setText(R.string.login_page_register_button);
        preferences = getSharedPreferences("UserAccount", 0);
        username = preferences.getString("account", "");
        userpassword = preferences.getString("password", "");
        if (!username.equals("") && !userpassword.equals("")) {
            //   startActivity(new Intent(Login_dialog.this,MainActivity.class));
            finish();
        }
        setupFragment = new SetupFragment();

    }

    @Override
    public void finish() {
        super.finish();
        if (username.equals("") || userpassword.equals("")) {
            //   startActivity(new Intent(Login_dialog.this,MainActivity.class));
            SysApplication.getInstance().exit();
        }

    }
    private boolean haveInternet() {
        boolean result = false;
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getActiveNetworkInfo();
        if (info == null || !info.isConnected()) {
            result = false;
        } else {
            if (!info.isAvailable()) {
                result = false;
            } else {
                result = true;
            }
        }

        return result;
    }
    Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    //處理少量資訊或UI
                    //textView.setText(msg.obj.toString());
                    Toast.makeText(Login_dialog.this, msg.obj.toString() , Toast.LENGTH_SHORT).show();
                    progress_dialog.dismiss();
                    //        Toast.makeText(MainActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
                    break;

                case 2:
                    //處理少量資訊或UI
                    //textView.setText("jpush:資料更新!!");
                    Toast.makeText(Login_dialog.this, "jpush:資料更新!!", Toast.LENGTH_SHORT).show();
                    //        Toast.makeText(MainActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
                    break;

                case 3:

                    String tokentmp = msg.obj.toString();

//////////////////////////////////////////////////insert/////////////////
                    TorkenDBhelper mdbhelper = new TorkenDBhelper(getApplicationContext());
                    SQLiteDatabase db = mdbhelper.getWritableDatabase();


                    String count = "SELECT * FROM rid";
                    Cursor mcursor = db.rawQuery(count, null);
                    mcursor.moveToFirst();
                    int icount = mcursor.getCount();
                    if (icount > 0) {
                        String id = "1"; //修改id為1的資料

                        ContentValues values = new ContentValues();

                        values.put(DBcontract.DBcol.COLUMN_Rrgister_ID, tokentmp);


                        db.update(DBcontract.DBcol.TABLE_NAME, values, DBcontract.DBcol._ID + "=" + id, null);
                    } else {
                        // Create a new map of values, where column names are the keys
                        ContentValues values = new ContentValues();
                        values.put(DBcontract.DBcol.COLUMN_Rrgister_ID, tokentmp);

                        db.insert(
                                DBcontract.DBcol.TABLE_NAME,
                                null,
                                values);

                        Log.e("sql:", "insert");
                    }


//////////////////////////////////////////////////insert/////////////////
//////////////////////////////////////////////////select/////////////////
                    db = mdbhelper.getReadableDatabase();
                    Cursor c = db.rawQuery("SELECT * FROM " + DBcontract.DBcol.TABLE_NAME + " WHERE _id = 1", null);

                    c.moveToFirst();
                    String itemrId = c.getString(
                            c.getColumnIndexOrThrow(DBcontract.DBcol.COLUMN_Rrgister_ID)
                    );

                    Log.e("sql:", itemrId);
//////////////////////////////////////////////////select/////////////////
                    token = itemrId;
                    //textView.setText("登入成功");
                    Toast.makeText(Login_dialog.this, getResources().getText(R.string.login_success_toast) + "Token:"+token, Toast.LENGTH_SHORT).show();
                    SetupFragment.isLogin = true;

                    getSharedPreferences("UserAccount", 0).edit()
                            .putString("account", username)
                            .putString("password", userpassword)
                            .putString("rid", rid)
                            .putString("token", token)
                            .putString("language", language).commit();

                    // startActivity(new Intent(Login_dialog.this,MainActivity.class));
                    //  SysApplication.getInstance().exit();
                    MainActivity.settings.edit().putBoolean("isScan",false).commit();
                    MainActivity.settings.edit().putString("login", "1").commit();

                    Intent intent = new Intent() ;
                    intent.setClass(Login_dialog.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);//设置不要刷新将要跳到的界面
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//它可以关掉所要到的界面中间的activity
                    startActivity(intent);


                    progress_dialog.dismiss();
                    //tokentext.setText("token:"+token);
                    break;
            }
        }
    };


    View.OnClickListener login = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (haveInternet()) {
                if (!account_ed.getText().toString().equals("") && !password_ed.getText().toString().equals("")) {
                    username = account_ed.getText().toString();
                    userpassword = password_ed.getText().toString();
                    progress_dialog = new ProgressDialog(Login_dialog.this);
                    progress_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progress_dialog.setMessage(getResources().getText(R.string.login_process).toString());
                    progress_dialog.setIndeterminate(false);
                    progress_dialog.setCancelable(false);
                    //progress_dialog.show();
                    login(username, userpassword);

                    final AlertDialog dlg = progress_dialog;
                    dlg.show();

                    final Timer t = new Timer();
                    t.schedule(new TimerTask() {
                        public void run() {
                            //10秒自動消失
                            Log.e("dialogin","now "+dlg.isShowing());
                            if(dlg.isShowing()){
                                dlg.dismiss();
                                t.cancel();
                                Message msg = new Message();
                                loginhandler.sendMessage(msg);
                            }
                        }
                    }, 5000);
                /*account = account_ed.getText().toString();
                finish();*/
                } else {
                    Toast.makeText(Login_dialog.this, R.string.login_can_not_null, Toast.LENGTH_SHORT).show();
                }
            } else
                Toast.makeText(Login_dialog.this,R.string.check_internet, Toast.LENGTH_LONG).show();


        }
    };

    View.OnClickListener register = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (haveInternet()) {
                if (!account_ed.getText().toString().equals("") && !password_ed.getText().toString().equals("")) {
                    String username = account_ed.getText().toString();
                    String password = password_ed.getText().toString();
                    progress_dialog = new ProgressDialog(Login_dialog.this);
                    progress_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    //  progress_dialog.setMessage(getResources().getText(R.string.login_process).toString());
                    progress_dialog.setIndeterminate(false);
                    progress_dialog.setCancelable(false);
                    //progress_dialog.show();
                    register(username, password);
                    //   rid = JPushInterface.getRegistrationID(getApplicationContext());
                    // rid_textView.setText(rid);

                    final AlertDialog dlgre = progress_dialog;
                    dlgre.show();

                    final Timer tr = new Timer();
                    tr.schedule(new TimerTask() {
                        public void run() {
                            //10秒自動消失
                            Log.e("dialogin","now "+dlgre.isShowing());
                            if(dlgre.isShowing()){
                                dlgre.dismiss();
                                tr.cancel();
                                Message msg = new Message();
                                registerhandler.sendMessage(msg);
                            }
                        }
                    }, 5000);
                } else {
                    Toast.makeText(Login_dialog.this, R.string.login_can_not_null, Toast.LENGTH_SHORT).show();
                }
            }else
                Toast.makeText(Login_dialog.this, R.string.check_internet, Toast.LENGTH_LONG).show();
        }

    };

    private void register(final String username, final String password) {

        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();

                String postParameter = "username=" + username + "&password=" + password+"&app=1-android";
                Log.e("string=", postParameter);
                URL url = null;

                try {
                    url = new URL("https://"+MainActivity.ip+"/jpushex/user_register.php?" + postParameter);
                    Log.e("register_url", String.valueOf(url));

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
                        //Log.e("rigi","??");
                    // reading your response

                    is.close();
                    http.disconnect();// close your connection
                    String res = buffer.toString();
                    JSONObject jObject = new JSONObject(res);
                    String ret = jObject.getString("RetCode");
                    Log.e("ret", ret);
                    Log.e("res:", res);

                    if (ret.equals("0")) {
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = getResources().getText(R.string.regist_account_success);
                        mHandler.sendMessage(msg);
                    } else if (ret.equals("1")) {
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj =getResources().getText(R.string.regist_account_fail);
                        mHandler.sendMessage(msg);
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                    Log.e("print","??");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        };
        thread.start();
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

    private void login(final String username, final String password) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();

                rid = JPushInterface.getRegistrationID(getApplicationContext());
                language = (String) getResources().getText(R.string.language);
                String postParameter = "username=" + username + "&password=" + password + "&registrationid=" + rid + "&language="+language+"&app=1-android";
                Log.e("string=", postParameter);
                URL url = null;

                try {
                    url = new URL("https://"+MainActivity.ip+"/jpushex/login.php?" + postParameter);

                    HttpURLConnection http = null;
                    if (url.getProtocol().toLowerCase().equals("https")) {
                        trustAllHosts();
                        HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
                        https.setHostnameVerifier(DO_NOT_VERIFY);
                        http = https;
                    } else {
                        http = (HttpURLConnection) url.openConnection();
                    }


//                    url = new URL("https://"+MainActivity.ip+"/jpushex/login.php?" + postParameter);
//                    Log.e("login_url", String.valueOf(url));
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


                    if (ret.equals("0")) {
                        String token = jObject.getString("Token");
                        JSONArray jsonArr = jObject.getJSONArray("Devices");
                        //MainActivity.mRegistListAdapter.regist_item = new ArrayList<>();
                        for (int i = 0; i < jsonArr.length(); i++) {  // **line 2**
                            //HashMap<String, String> login_map = new HashMap<>();
                            JSONObject childJSONObject = jsonArr.getJSONObject(i);
                            String mac = childJSONObject.getString("mac_addr");
                            String enable = childJSONObject.getString("Enable");
                            //  login_map.put("mac",mac);
                            // MainActivity.mRegistListAdapter.regist_item.add(mac);
                            //login_map.put("enable",enable);
                            //  MainActivity.enableList.add(login_map);
                            Log.e("enable", enable);
                            Log.e("mac", mac);
                        }
                        MultipleFragment.sec5_while = true ;
                        Message msg = new Message();
                        msg.what = 3;
                        msg.obj = token;
                        mHandler.sendMessage(msg);


                    } else if (ret.equals("1")) {
                        String RetMsg = jObject.getString("RetMsg");

                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = getResources().getText(R.string.login_account_not_exist_toast);
                        mHandler.sendMessage(msg);
                    } else if (ret.equals("2")) {
                        String RetMsg = jObject.getString("RetMsg");

                        Message msg = new Message();
                        msg.what = 1;

                        msg.obj = getResources().getText(R.string.login_password_not_correct_toast);
                        mHandler.sendMessage(msg);
                    } else if (ret.equals("3")) {
                        String RetMsg = jObject.getString("RetMsg");

                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = "已經登入?";
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {                          //如果按返回建會檢查之前使用者藍芽是否開啟
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
        }
        return true;
    }

    Handler loginhandler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(Message msg) {
            final AlertDialog serverDialog = new AlertDialog.Builder(Login_dialog.this,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                    .setTitle(getResources().getString(R.string.chinsice0))
                    .setMessage(getResources().getString(R.string.serverbroke))
                    .setIcon(R.mipmap.ic_launcher)
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.confirm_button),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                    SetupFragment.buttonSetScan();
                                    setupFragment.isLogin= false;
                                    Log.e("dddd","islogin " + setupFragment.isLogin);
                                    finish();
                                }
                            }).show();

        }
    };

    Handler registerhandler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(Message msg) {
            new AlertDialog.Builder(Login_dialog.this,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                    .setTitle(getResources().getString(R.string.chinsice0))
                    .setMessage(getResources().getString(R.string.serverbrokeregi))
                    .setIcon(R.mipmap.ic_launcher)
                    .setPositiveButton(getResources().getString(R.string.confirm_button),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                    setupFragment.isLogin= false;
                                    //
                                    Intent intent = new Intent() ;
                                    intent.setClass(Login_dialog.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);//设置不要刷新将要跳到的界面
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//它可以关掉所要到的界面中间的activity
                                    startActivity(intent);
                                }
                            }).show();

        }
    };

}
