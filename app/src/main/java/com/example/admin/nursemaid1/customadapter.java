package com.example.admin.nursemaid1;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin.nursemaid1.dbcontrol.DeviceCourseDAO;
//import com.sinopulsar.nursemaid.R ;
import com.sinopulsar.nursemaid1.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by admin on 2017/12/19.
 */


public class customadapter extends ArrayAdapter<Listitem> implements View.OnClickListener {                     //"遠端多數畫面"顯示及處理


    static ArrayList<Listitem> dataSet = new ArrayList<>();                                                     //把收到186的tag資訊放到ArrayList裡面，等等用它來顯示畫面要呈現的資訊
    Context mContext;
    private Listitem mListItem;                                                                               //宣告class Listitem 裡面有tag名稱,pid等等資訊
    // View lookup cache
    // int nameNumber = 0;
  //  MainActivity mainActivity = new MainActivity();
    static DeviceCourseDAO courseDAO;                                                                           //手機資料庫

    private static class ViewHolder {
        TextView temp;
        TextView bpm;
        TextView name;
        TextView macaddress;
        TextView battery;
        ImageView isenable;
        ImageView status;

    }


    public customadapter(ArrayList<Listitem> data, Context context) {
        super(context, R.layout.listlayout, data);
        this.dataSet = data;
        this.mContext = context;
        courseDAO = new DeviceCourseDAO(context);

    }


    public void additem(String macaddress, String data ,String enable, int state) {                             //新增新的tag資料
        String batteryStr = data.substring(56, 58);
        int batteryInt = Integer.parseInt(batteryStr, 16);
        String breathStr = data.substring(30, 32);
        int breathValue = Integer.parseInt(breathStr, 16);
        float tempValue = Float.parseFloat(transBodyValue(data));
        String battery = batteryInt + "";
//        String isenable = "true";
//        boolean isEnable = true;
        String isRegist = "0";
        //String bpm = breathInt+" BPM";
        String tagNumString = MainActivity.nameNumber + "";
        if (tagNumString.length() == 1) {
            tagNumString = "0" + tagNumString;
        }
        //kick
        Kick kick = new Kick();
        Log.e("state",String.valueOf(state));
        mListItem = new Listitem("", macaddress, null, enable, 0, 0,isRegist,new Kick());
        //
        if (!checkExist(macaddress)) {                          //如果之前有沒收到過此tag就新增他
            mListItem = courseDAO.get(mListItem);
            //   mListItem.setName("Sinopulsar" + tagNumString);
            mListItem.setMacaddress(macaddress);
            mListItem.setBpm(breathValue);
            mListItem.setTemp(tempValue);
            mListItem.setBattery(battery);
//            mListItem.setIsenable(isenable);
            mListItem.setData(data);
            mListItem.setIsenable(enable);
            mListItem.setisRegist(isRegist);
            mListItem.setisState(state);
            //kick
            String bodyValue = transBodyValue(data);
            String roomValue = transRoomValue(data);
            Log.e("bodyValue1",bodyValue) ;
            Log.e("roomValue1",roomValue) ;

            mListItem.setKick(kick);
            mListItem.kick.kick(Double.parseDouble(bodyValue),Double.parseDouble(roomValue));
            //
            if (mListItem.getName().equals("")) {
                boolean existName = false;
                for (int i = 0; i < MainActivity.tagNameList.size(); i++) {
                    if (MainActivity.tagNameList.size() > 0) {
                        if (MainActivity.tagNameList.get(i).get("macaddress").equals(macaddress)) {
                            String name = MainActivity.tagNameList.get(i).get("name") + "";
                            mListItem.setName(name);
                            existName = true;
                            break;
                        }
                    }
                }
                if (!existName) {
                    mListItem.setName("Sinopulsar" + tagNumString);
                    MainActivity.tagNameMap.put("macaddress", macaddress);
                    MainActivity.tagNameMap.put("name", "Sinopulsar" + tagNumString);
                    MainActivity.tagNameList.add(MainActivity.tagNameMap);
                    MainActivity.tagNameMap = new HashMap<>();
                    MainActivity.nameNumber++;
                }
            }
            dataSet.add(mListItem);

        } else if (checkExist(macaddress)) {                                                    //如果有收過就call updateitem()這個函式，更新它的資訊
            updateitem(macaddress, battery, enable, breathValue, tempValue, data,isRegist,state);
        }

//        notifyDataSetChanged();
    }

    boolean checkExist(String mac) {
        for (int i = 0; i < dataSet.size(); i++) {
            if (dataSet.get(i).macaddress.equals(mac)) {
                return true;
            }
        }
        return false;
    }

    public void removeitem(String macaddress) {

        for (int i = 0; i < dataSet.size(); i++) {
            if (dataSet.get(i).macaddress.equals(macaddress))
                dataSet.remove(i);
        }
    }

    public void updateitem(String macaddress, String battery, String isenable, int bpm, float temp, String data,String isRegist,int state) {

        for (int i = 0; i < dataSet.size(); i++) {
            if (dataSet.get(i).macaddress.equals(macaddress)) {
                mListItem = courseDAO.get(dataSet.get(i));
                String name = mListItem.getName();
                if(!name.equals("")){
                    dataSet.get(i).setName(name);
                }
                dataSet.get(i).setTemp(temp);
                dataSet.get(i).setIsenable(isenable);
                dataSet.get(i).setBpm(bpm);
                dataSet.get(i).setBattery(battery);
                dataSet.get(i).setData(data);
                dataSet.get(i).setisRegist(isRegist);
                dataSet.get(i).setisState(state);
                String bodyValue = transBodyValue(data);
                String roomValue = transRoomValue(data);
                //kick
                dataSet.get(i).kick.kick(Double.parseDouble(bodyValue),Double.parseDouble(roomValue));
                if(dataSet.get(i).kick.isKick && tellEnable(dataSet.get(i).macaddress)){
                    //dataSet.get(i).kick.push(dataSet.get(i).macaddress); 不用
                    Log.e("isKick",String.valueOf(dataSet.get(i).kick.isKick));
                }
            }
        }

        sortDevice();
//        notifyDataSetChanged();
        //      notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

        int position = (Integer) v.getTag();
        Object object = getItem(position);
        Listitem listitem = (Listitem) object;
        //Toast.makeText(mContext,"CLICK",Toast.LENGTH_SHORT);
        //   setListItemNameDialog(position).show();
        switch (v.getId()) {
     /*       case R.id.item_info:
                Snackbar.make(v, "Release date " +dataModel.getFeature(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                break;*/
        }
    }

    /*public AlertDialog setListItemNameDialog(int position) {                                                                                  //更改tag名子，此為舊版本功能
        final View dialogView = LayoutInflater.from(mContext).inflate(R.layout.dialog_setlistitemname, null);
        final Listitem tmpitem = getItemAddress(position);
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
                        tmpitem.setName(tmpString);
                        setItemName(tmpitem);
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

    public Listitem getItemAddress(int position) {
        return dataSet.get(position);
    }

    public void setItemName(Listitem listitem) {
        //Log.d(TAG, "address=" + address);
        // mNames.set(position, name);
        int position = dataSet.indexOf(listitem);
        if (position != -1) {
            dataSet.get(position).setName(listitem.getName());
            if (courseDAO.update(dataSet.get(position))) {
                Toast.makeText(mContext, "Edit Name Success", Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(activity, "error", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext, "position = -1 ,error", Toast.LENGTH_SHORT).show();
        }
    }*/

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {                             //當你使用notifyDataSetChanged()時會更新dataset的資訊，dataset裡面有幾筆資料就會跑幾筆
        // Get the data item for this position
        Listitem listitem = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.listlayout, parent, false);
            //        viewHolder.button = (Button) convertView.findViewById(R.id.buttonlist);
            //        viewHolder.txt = (TextView) convertView.findViewById(R.id.textViewlist);
            viewHolder.macaddress = (TextView) convertView.findViewById(R.id.itemmacaddress);
            viewHolder.temp = (TextView) convertView.findViewById(R.id.itemtemp);
            viewHolder.bpm = (TextView) convertView.findViewById(R.id.itembpm);
            viewHolder.battery = (TextView) convertView.findViewById(R.id.itembattery);
            viewHolder.name = (TextView) convertView.findViewById(R.id.itemname);
            viewHolder.isenable = (ImageView) convertView.findViewById(R.id.itemenable);
            viewHolder.status = (ImageView) convertView.findViewById(R.id.itemstatus);
            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        //  Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        // result.startAnimation(animation);
        lastPosition = position;
        String data = listitem.getData();
        viewHolder.name.setText(listitem.getName());
        viewHolder.macaddress.setText(listitem.getMacaddress());
        viewHolder.bpm.setText(listitem.getBpm() + " BPM");
        if(listitem.getBpm()==255) {
            viewHolder.bpm.setText("---");
        }
        viewHolder.name.setText(listitem.getName());
        viewHolder.battery.setText(listitem.getBattery() + "%");

        String pid = data.substring(18, 20);
        if (pid.equals("03")) {
            viewHolder.temp.setVisibility(View.VISIBLE);
            Float bodyTemp = null ;
            if (SetupFragment.isC) {
                bodyTemp = listitem.getTemp()  ;
                if (listitem.getTemp() != 100) {
                    String temp = listitem.getTemp() + "";
                    if (temp.length() == 4) {//只有小數點後一位
                        viewHolder.temp.setText(listitem.getTemp() + "0°C");
                    } else
                        viewHolder.temp.setText(listitem.getTemp() + "°C");
                } else
                    viewHolder.temp.setText(listitem.getTemp() + "0°C");
            } else if (!SetupFragment.isC) {                                                            //如果是溫度單位不是度C

                float bodyTempF = (float) (listitem.getTemp() * 1.8 + 32);
                Log.e("bodyTempF"+position,String.valueOf(bodyTempF)) ;
                DecimalFormat df = new DecimalFormat("#.##");//取小數後2位
                String bodyTempFstr = df.format(bodyTempF);
                bodyTemp = Float.valueOf(bodyTempFstr) ;
//                listitem.setTemp(Float.parseFloat(bodyTempFstr));
                if (bodyTempF < 100) {
                    String temp = bodyTempF + "";
                    if (temp.length() == 4) {//只有小數點後一位
                        viewHolder.temp.setText(bodyTempFstr + "0°F");
                    } else
                        viewHolder.temp.setText(bodyTempFstr + "°F");
                } else if (bodyTempF >= 100) {
                    String temp = bodyTempF + "";
                    if (temp.length() == 5) {//只有小數點後一位
                        viewHolder.temp.setText(bodyTempFstr + "0°F");
                    } else
                        viewHolder.temp.setText(bodyTempFstr + "°F");
                }
            }

            if (bodyTemp >= SetupFragment.highTemp || bodyTemp <= SetupFragment.lowTemp) {                          //將溫度數字變為紅色
                if (!data.substring(20, 21).equals("1") && (!data.substring(21, 22).equals("1") || !data.substring(21, 22).equals("2"))) {
                    listitem.warnningLevel = 1;
                }
                viewHolder.temp.setTextColor(Color.RED);
            } else {
                if (!data.substring(20, 21).equals("1") && (!data.substring(21, 22).equals("1") || !data.substring(21, 22).equals("2"))) {
                    listitem.warnningLevel = 0;
                }
                viewHolder.temp.setTextColor(Color.BLACK);
            }

        } else if (pid.equals("05")||pid.equals("06")) {
            viewHolder.temp.setVisibility(View.INVISIBLE);
        }
        if (listitem.getBpm() >= SetupFragment.fastBreath || listitem.getBpm() <= SetupFragment.slowBreath) {
            listitem.warnningLevel = 1;
            viewHolder.bpm.setTextColor(Color.RED);
        } else
            viewHolder.bpm.setTextColor(Color.BLACK);
        //  viewHolder.battery.;
        //  viewHolder.isenable.setBackground();
        // Return the completed view to render on screen

        String status = data.substring(21, 22);
        if (data.substring(20, 21).equals("1")) {
            //   mSpit.setImageResource(R.drawable.spit2);
            if (pid.equals("05")) {
                viewHolder.status.setVisibility(View.VISIBLE);
                viewHolder.status.setImageResource(R.drawable.spit3);
                listitem.warnningLevel = 4;
            }
        } else {                                                            //狀態值
            switch (status) {
                case "0":
//                    viewHolder.status.setVisibility(View.VISIBLE);
//                    viewHolder.status.setImageResource(R.drawable.kick3);

                    viewHolder.status.setVisibility(View.INVISIBLE);

                    // listitem.warnningLevel = 0;
                    break;
                case "1":
                    viewHolder.status.setVisibility(View.INVISIBLE);
                    break;
                case "2":
                    viewHolder.status.setVisibility(View.VISIBLE);
                    viewHolder.status.setImageResource(R.drawable.escape3);
                    listitem.warnningLevel = 3;
                    break;
                case "3":
                    viewHolder.status.setVisibility(View.INVISIBLE);
                    break;
                case "4":
                    viewHolder.status.setVisibility(View.INVISIBLE);
                    break;
                case "5":
                    viewHolder.status.setVisibility(View.VISIBLE);
                    viewHolder.status.setImageResource(R.drawable.wake3);
                    listitem.warnningLevel = 2;
                    break;
                case "6":
                    viewHolder.status.setVisibility(View.INVISIBLE);
                    break;
            }
        }
        if (pid.equals("06")) {                                                                     //當有尿溼狀態時已以尿濕為主
            if (data.substring(20, 21).equals("1")) {//06偵測尿溼狀況
                if (DeviceListAdapter.dataSet.get(position).getspittimmer() == 0) {
                    DeviceListAdapter.dataSet.get(position).setspittimmer(1) ;
                } else if (DeviceListAdapter.dataSet.get(position).getspittimmer() >= 360) {
                    viewHolder.status.setVisibility(View.VISIBLE);
                    viewHolder.status.setImageResource(R.drawable.spit3);
                }
            } else if (data.substring(20, 21).equals("0")) {//0即為正常
                viewHolder.status.setVisibility(View.INVISIBLE);
                DeviceListAdapter.dataSet.get(position).setspittimmer(0) ;
                switch (status) {
                    case "0":
                        viewHolder.status.setVisibility(View.INVISIBLE);
                        break;
                    case "1":
                        viewHolder.status.setVisibility(View.INVISIBLE);
                        break;
                    case "2":
                        viewHolder.status.setVisibility(View.VISIBLE);
                        viewHolder.status.setImageResource(R.drawable.escape3);
                        listitem.warnningLevel = 3;
                        break;
                    case "3":
                        viewHolder.status.setVisibility(View.INVISIBLE);
                        break;
                    case "4":
                        viewHolder.status.setVisibility(View.INVISIBLE);
                        break;
                    case "5":
                        viewHolder.status.setVisibility(View.VISIBLE);
                        viewHolder.status.setImageResource(R.drawable.wake3);
                        listitem.warnningLevel = 2;
                        break;
                    case "6":
                        viewHolder.status.setVisibility(View.INVISIBLE);
                        break;
                }
            }
        }
        Resources r = this.getContext().getResources();


//        final String name = MultipleFragment.scanDeviceList.get(position).getName();

        viewHolder.isenable.setImageDrawable(r.getDrawable(R.drawable.btsearching));
                    viewHolder.battery.setVisibility(convertView.VISIBLE);
                    viewHolder.bpm.setVisibility(convertView.VISIBLE);
                    //viewHolder.temp.setVisibility(View.VISIBLE);
//                    MultipleFragment.scanDeviceList.get(position).isEnable = true;
//                    MultipleFragment.scanDeviceList.get(position).isRegist = true;

        if(dataSet.get(position).getisState() == 0){                                                   //getisState是0時，代表他已經失效，資訊都顯示00
            viewHolder.battery.setText("");
            viewHolder.bpm.setTextColor(Color.BLACK);
            viewHolder.temp.setTextColor(Color.BLACK);
            viewHolder.bpm.setText("00");
            viewHolder.temp.setText("00");
            viewHolder.temp.setVisibility(convertView.VISIBLE);
            viewHolder.status.setVisibility(convertView.VISIBLE);
            viewHolder.status.setVisibility(convertView.INVISIBLE);
            if (tellRegist(dataSet.get(position).macaddress)) {                                       //看他有沒有註冊，有的話再看有沒有啟用推波
                if (!tellEnable(dataSet.get(position).macaddress)) {
                    viewHolder.isenable.setImageResource(R.drawable.bell_disable);
                } else if (tellEnable(dataSet.get(position).macaddress)) {
                    viewHolder.isenable.setImageResource(R.drawable.bell_enable);
                }
            } else{
                viewHolder.battery.setText("");
                viewHolder.bpm.setText("---");
                viewHolder.temp.setVisibility(convertView.INVISIBLE);
                viewHolder.status.setVisibility(convertView.INVISIBLE);
            }
        } else if (tellRegist(dataSet.get(position).macaddress)) {
            if (!tellEnable(dataSet.get(position).macaddress)) {
                viewHolder.isenable.setImageResource(R.drawable.bell_disable);
            }
            else if(tellEnable(dataSet.get(position).macaddress)) {
                viewHolder.isenable.setImageResource(R.drawable.bell_enable);
            }
        } else{
            viewHolder.battery.setText("");
            viewHolder.bpm.setText("---");
            viewHolder.temp.setVisibility(convertView.INVISIBLE);
            viewHolder.status.setVisibility(convertView.INVISIBLE);
        }
        return convertView;
    }
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

    public void sortDevice() {
        Collections.sort(dataSet);
    }

    private boolean tellRegist(String address) {
        // String n = "WetBeacon";
        // String n2 = "ThermoSensor";
        for (int i = 0; i < dataSet.size(); i++) {
            if (dataSet.get(i).macaddress.equals(address) && dataSet.get(i).getisRegist().equals("0")) {   //0是註冊
                return true;
            }
        }
        return false;
    }

    private boolean tellEnable(String address) {
        // String n = "WetBeacon";
        // String n2 = "ThermoSensor";
        for (int i = 0; i < dataSet.size(); i++) {
            if (dataSet.get(i).macaddress.equals(address) && dataSet.get(i).getIsenable().equals("0")) {    //0是啟用
                return true;
            }
        }
        return false;
    }


}