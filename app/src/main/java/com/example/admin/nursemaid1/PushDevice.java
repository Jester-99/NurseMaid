package com.example.admin.nursemaid1;

/**
 * Created by ASUS on 2017/6/11.
 */
public class PushDevice implements Comparable<PushDevice> {                                             //沒甚麼用到
    private long id;
    private String mAddress;
    private String mName;
    private int mValue = 0;
    public boolean isEnable = false;
    public boolean isRegist = false;
    public PushDevice(){
    }
    public PushDevice(String address ){
        this.mAddress  = address;
        this.mName = "WetBeacon";
    }
    public String getDeviceMac(){
        return this.mAddress;
    }
    public void setValue(int value){
        mValue = value;
    }
    public int getValue(){return this.mValue; }
    public void setName(String name){mName = name; }
    public void setDeviceMac(String mAddress) {
        this.mAddress = mAddress;

    }
    public String getName(){ return mName; }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String show() {
        return "ID=" + getId() + "/Name=" + getName() + "/Address="
                + getDeviceMac();
    }
    @Override
    public int compareTo(PushDevice pushDevice) {
        if ((getValue()) == (pushDevice.getValue()))
            return 0;

        if ((getValue()) > (pushDevice.getValue())){
            return -1;
        }else
            return 1;


    }
}
