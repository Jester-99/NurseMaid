package com.example.admin.nursemaid1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

//import com.sinopulsar.nursemaid.R ;
import com.sinopulsar.nursemaid1.R ;

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

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MultipleFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MultipleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MultipleFragment extends Fragment {                           //這是多數頁面!!!!
    // TODO: Rename parameter arguments, choose names that match

    // TODO: Rename and change types of parameters
    private String mParam1;
    View myview;
    private ListView listView;
    static ArrayList<PushDevice> scanDeviceList = new ArrayList<>();
    static String regist_tags="";
    static int checken = 0 ;
    boolean isAlert = false;
    static boolean sec5_while = true;
    static DeviceListAdapter mDevicesListAdapter = null;
    ArrayList<String> sosMessage;
    ArrayList<Listitem> listitems;
    static customadapter customadapter = null;
    private OnFragmentInteractionListener mListener;

    public MultipleFragment() {
        // Required empty public constructor
    }

    public Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    //處理少量資訊或UI
                    //textView.setText(msg.obj.toString());
 //                   Toast.makeText(getActivity(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    //mRegistListAdapter.notifyDataSetChanged();
                    Message msg2 = new Message();
                    updateHandler.sendMessage(msg2);

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

    // TODO: Rename and change types and number of parameters
    public static MultipleFragment newInstance(/*String param1, String param2*/) {
        MultipleFragment fragment = new MultipleFragment();
 /*       Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

    static Handler updateHandler = new Handler(Looper.myLooper()) {                                                        //遠端多數畫面更新
        @Override
        public void handleMessage(Message msg) {
            customadapter.notifyDataSetChanged();
        }
    };

    static Handler updateHandler2 = new Handler(Looper.myLooper()) {                                                      //近端多數畫面更新
        @Override
        public void handleMessage(Message msg) {
            mDevicesListAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myview = inflater.inflate(R.layout.fragment_multiple, container, false);
        listView = (ListView) myview.findViewById(R.id.multilist);

//        listitems = new ArrayList<>();

        if(DeviceListAdapter.dataSet!=null){
            customadapter.dataSet = DeviceListAdapter.dataSet ;
            for(int j=0;j<customadapter.dataSet.size();j++){
                customadapter.dataSet.get(j).setspittimmer(0);
            }
        }
        customadapter= new customadapter(customadapter.dataSet,getContext());                     //推波介面
        mDevicesListAdapter = new DeviceListAdapter(DeviceListAdapter.dataSet,getContext());      //藍芽介面

        if (MainActivity.settings.getBoolean("isScan",true)) {                                  //是掃描模式的話，就是用近端藍芽的介面
            listView.setAdapter(mDevicesListAdapter);
            Message msg = new Message();
            updateHandler2.sendMessage(msg);

        } else {
            listView.setAdapter(customadapter);
            Message msg = new Message();
            updateHandler.sendMessage(msg);

            //  timer.schedule(new Sendtask(username), 0);
//            if (!sec5_thread.isAlive()) {
//                sec5_thread.start();
//                Log.e("threadstart","123") ;
//            }
        }

        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(!SetupFragment.isLogin) {
                    customadapter.setListItemNameDialog(i).show();
                }
            }
        });*/

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView parent, View view, int position, long id) {
                Log.e("CallLogActivity", view.toString() + "position=" + position);
                if (SetupFragment.isLogin) {
                    if (customadapter.dataSet.get(position).getIsenable().equals("1") && customadapter.dataSet.get(position).getisRegist().equals("1")) {
                        setRegistDialog(position).show();
                    } else if (customadapter.dataSet.get(position).getIsenable().equals("1") && customadapter.dataSet.get(position).getisRegist().equals("0")) {  //啟用不住測 0/1
                        setUnRegistDialog(position).show();
                    } else if (customadapter.dataSet.get(position).getIsenable().equals("0") && customadapter.dataSet.get(position).getisRegist().equals("0")) {  //0啟用 0註冊
                        setUnRegistDialog(position).show();
                    }
                }
                return true;
            }
        });


//        customadapter.additem("BT","XX:XX:XX:XX",87,1,37,47);
//        customadapter.additem("BT1","XX:XX:XX:yy",77,1,45,65);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Message msg = new Message();
                    msg.what = 2;
                    mHandler.sendMessage(msg);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        
        return myview;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MultipleFragment.checken = 0 ;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("onResumeM","onResumeM") ;
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
  /*      if (context instanceof OnFragmentInteractionListener) {
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

    private AlertDialog setRegistDialog(final int position) {
        // final BleDevice tmpitem = mDevicesListAdapter.getItemAddress(position);
//        final PushDevice pushDevice = scanDeviceList.get(position);
        final View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_setregist, null);
        AlertDialog.Builder tmpDialog = new AlertDialog.Builder(getActivity());
        tmpDialog.setTitle(R.string.tag_regist_ask_title);
        tmpDialog.setMessage(R.string.tag_regist_ask_content);
        tmpDialog.setView(dialogView);
        tmpDialog.setPositiveButton(R.string.register_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                //Log.e("註冊的TAGGGGGGGG", pushDevice.getDeviceMac());
                //match(customadapter.dataSet.get(position).macaddress, "true");

                if (SetupFragment.isLogin) {
                    //  String enable = mRegistListAdapter.enable_item.get(position).get("enable");
                    //final PushDevice pushDevice = scanDeviceList.get(position);
                    // CharSequence number = ((TextView) view).get`Text();
                    match(customadapter.dataSet.get(position).macaddress, "true");
                    Message msg = new Message();
                    updateHandler.sendMessage(msg);

                }               // mRegistListAdapter.notifyDataSetChanged();
               // mRegistListAdapter.notifyDataSetChanged();
            }
        });
        return tmpDialog.create();
    }

    private AlertDialog setUnRegistDialog(final int position) {
        // final BleDevice tmpitem = mDevicesListAdapter.getItemAddress(position);
//        final PushDevice pushDevice = scanDeviceList.get(position);
        final View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_setregist, null);
        AlertDialog.Builder tmpDialog = new AlertDialog.Builder(getActivity());
        tmpDialog.setTitle(R.string.chang_tag_status_title);

        tmpDialog.setView(dialogView);
        tmpDialog.setPositiveButton(R.string.unregister_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                if (SetupFragment.isLogin) {

//                    Log.e("註冊的TAGGGGGGGG", pushDevice.getDeviceMac());
                    removematch(customadapter.dataSet.get(position).macaddress);
                    Message msg = new Message();
                    updateHandler.sendMessage(msg);
                }
                //mRegistListAdapter.notifyDataSetChanged();
            }
        });
        tmpDialog.setNegativeButton(R.string.cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });
        if (customadapter.dataSet.get(position).getIsenable().equals("1")) {              //起用
            tmpDialog.setMessage(R.string.chang_tag_status_content_tag_disable);
            tmpDialog.setNeutralButton(R.string.enable_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    if (SetupFragment.isLogin) {

                        //Log.e("註冊的TAGGGGGGGG", pushDevice.getDeviceMac());
                        match(customadapter.dataSet.get(position).macaddress, "true");
                        Message msg = new Message();
                        updateHandler.sendMessage(msg);
                    }
                    //mRegistListAdapter.notifyDataSetChanged();
                }
            });
        } else if (customadapter.dataSet.get(position).getIsenable().equals("0")) {                  //禁用
            tmpDialog.setMessage(R.string.chang_tag_status_content_tag_enable);
            tmpDialog.setNeutralButton(R.string.disable_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                        //Log.e("註冊的TAGGGGGGGG", pushDevice.getDeviceMac());
                    if (SetupFragment.isLogin) {

                        match(customadapter.dataSet.get(position).macaddress, "false");
                        Message msg = new Message();
                        updateHandler.sendMessage(msg);
                    }
                    //mRegistListAdapter.notifyDataSetChanged();
                }
            });
        }
        return tmpDialog.create();
    }




    private void match(final String mac, final String ableBoolean) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                Log.e("able",ableBoolean);
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

                        Message msg = new Message();
                        msg.what = 1;
                        if (ableBoolean.equals("true")) {
                            msg.obj = getResources().getText(R.string.enable_success);
                        } else if (ableBoolean.equals("false")) {
                            msg.obj = getResources().getText(R.string.disable_success);
                        }
//                        getdata();
                        mHandler.sendMessage(msg);
                        Message msg2 = new Message();
                        msg2.what = 2;
                        mHandler.sendMessage(msg2);

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

    private void removematch(final String mac) {
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
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = getResources().getText(R.string.unregist_success);
                        mHandler.sendMessage(msg);
                        for (int i = 0; i < customadapter.dataSet.size(); i++) {
                            if (customadapter.dataSet.get(i).macaddress.equals(mac)) {
                                customadapter.dataSet.get(i).setisRegist("1");
                                customadapter.dataSet.get(i).setIsenable("1");

                                //  mRegistListAdapter.notifyDataSetChanged();
                            }
                        }
                        SingleFragment.dataSet.setisRegist("1");
                        //check = 1;
                        // timer.schedule(new Sendtask(username), 0);
//                        getdata();
                        Message msg2 = new Message();
                        msg2.what = 2;
                        mHandler.sendMessage(msg2);
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
                        msg.obj = "原本就沒配對";
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

}
