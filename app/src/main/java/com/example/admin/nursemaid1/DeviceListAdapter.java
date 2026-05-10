package com.example.admin.nursemaid1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.nursemaid1.dbcontrol.DeviceCourseDAO;
//import com.sinopulsar.nursemaid.R ;
import com.sinopulsar.nursemaid1.R ;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by admin on 2017/12/19.
 */


public class DeviceListAdapter extends ArrayAdapter<Listitem> implements View.OnClickListener {             //近端藍芽多數模式UI處理及顯示，根本上運作跟遠端多數一樣，只有最後一段的註冊tag那邊，近端不用考慮到


    static ArrayList<Listitem> dataSet = new ArrayList<>();                                                  //與遠端多數一樣，各有一個ArrayList紀錄tag資訊
    public ArrayList<HashMap<String, String>> enable_item = new ArrayList<>();
    Context mContext;
    private Listitem mListItem;
    // View lookup cache
    // int nameNumber = 0;
    //  MainActivity mainActivity = new MainActivity();
    DeviceCourseDAO courseDAO;


    private static class ViewHolder {
        TextView temp;
        TextView bpm;
        TextView name;
        TextView macaddress;
        TextView battery;
        ImageView isenable;
        ImageView status;

    }


    public DeviceListAdapter(ArrayList<Listitem> data, Context context) {
        super(context, R.layout.listlayout, data);
        this.dataSet = data;
        this.mContext = context;
        courseDAO = new DeviceCourseDAO(context);

    }


    public void additem(String macaddress, String data) {
        String batteryStr = data.substring(56, 58);
        int batteryInt = Integer.parseInt(batteryStr, 16);
        String breathStr = data.substring(30, 32);
        int breathValue = Integer.parseInt(breathStr, 16);
        float tempValue = Float.parseFloat(transBodyValue(data));
        String battery = batteryInt + "";
//        String isenable = "false";
        String isEnable = "1";
        String isRegist = "1";
        //String bpm = breathInt+" BPM";
        String tagNumString = MainActivity.nameNumber + "";
        if (tagNumString.length() == 1) {
            tagNumString = "0" + tagNumString;
        }
        Kick kick = new Kick();
        mListItem = new Listitem("", macaddress, null, isEnable, 0, 0,isRegist,kick);
        if (!checkExist(macaddress)) {
            mListItem = courseDAO.get(mListItem);
            //   mListItem.setName("Sinopulsar" + tagNumString);
            mListItem.setMacaddress(macaddress);
            mListItem.setBpm(breathValue);
            mListItem.setTemp(tempValue);
            mListItem.setBattery(battery);
            mListItem.setIsenable(isEnable);
            mListItem.setData(data);
//            mListItem.setisEnable(isEnable);
            mListItem.setisRegist(isRegist);
            mListItem.setTimmer(0);

            //kick
            String bodyValue = transBodyValue(data);
            String roomValue = transRoomValue(data);

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

        } else if (checkExist(macaddress)) {
            updateitem(macaddress, battery, isEnable, breathValue, tempValue, data);
        }
        notifyDataSetChanged();
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
                //Log.e("bletest","Multi remove");

        }
    }

    public void updateitem(String macaddress, String battery, String isenable, int bpm, float temp, String data) {

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
                dataSet.get(i).setTimmer(0);

                String bodyValue = transBodyValue(data);
                String roomValue = transRoomValue(data);
                //kick

                dataSet.get(i).kick.kick(Double.parseDouble(bodyValue),Double.parseDouble(roomValue));

                //
            }
        }

        sortDevice();
        notifyDataSetChanged();
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

    public AlertDialog setListItemNameDialog(int position) {
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
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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
            } else if (!SetupFragment.isC) {

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

            if (bodyTemp >= SetupFragment.highTemp || bodyTemp <= SetupFragment.lowTemp) {
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
        } else {
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
        if (pid.equals("06")) {
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
        if (MultipleFragment.regist_tags.contains(address)) {
            return true;
        } else
            return false;
    }

    private boolean tellEnable(String address) {
        // String n = "WetBeacon";
        // String n2 = "ThermoSensor";
        for (int i = 0; i < enable_item.size(); i++) {
            if (enable_item.get(i).get("tag").equals(address) && enable_item.get(i).get("enable").equals("0")) {
                return true;
            }
        }
        return false;
    }
}