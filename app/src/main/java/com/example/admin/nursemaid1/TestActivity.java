package com.example.admin.nursemaid1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

//import com.sinopulsar.nursemaid.R;
import com.sinopulsar.nursemaid1.R ;
import cn.jpush.android.api.JPushInterface;

public class TestActivity extends Activity {                                                            //jpush
    static String jpushtitle = "0" ;
    static String jpushcontent = "" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setText("用户自定义打开的Activity");
        Intent intent = getIntent();
        if (null != intent) {
            Bundle bundle = getIntent().getExtras();
            String title = null;
            String content = null;
            if (bundle != null) {
                title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
                content = bundle.getString(JPushInterface.EXTRA_ALERT);
                jpushtitle = title;
                jpushcontent = content;
            }
            //tv.setText("Title : " + title + "  " + "Content : " + content);
            logoutDialog().show();
        }
//        addContentView(tv, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    }

    private AlertDialog logoutDialog() {                                                                //用視窗顯示
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.pushset, null);
        // final BleDevice tmpitem =
        // mDevicesListAdapter.getItemAddress(position);
        AlertDialog.Builder tmpDialog = new AlertDialog.Builder(this);
        tmpDialog.setTitle(jpushtitle);
        tmpDialog.setMessage(jpushcontent);
        tmpDialog.setView(dialogView);
        tmpDialog.setPositiveButton(R.string.confirm_button, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                //if (haveInternet()) {
                finish();
            }
        });
        return tmpDialog.create();
    }
}
