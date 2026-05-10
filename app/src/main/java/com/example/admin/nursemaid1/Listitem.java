package com.example.admin.nursemaid1;

import androidx.annotation.NonNull;

/**
 * Created by admin on 2017/12/19.
 */

public class Listitem  implements Comparable<Listitem>{                                                 //用來記錄tag的資訊

//    public boolean isEnable = false;
    String isRegist ;
    String name = "";
    String macaddress;
    String battery;
    String isenable;
    int isstate;
    int bpm;
    int timmer ;
    int spittimmer ;
    float temp;
    String data;
    private long id;
    int warnningLevel = 0;
    Kick kick;
    public Listitem() {}

    public Listitem(String name, String macaddress, String battery, String isenable, int bpm, float temp,String isRegist,Kick kick) {
        this.name=name;
        this.macaddress=macaddress;
        this.battery=battery;
        this.isenable=isenable;
        this.bpm=bpm;
        this.temp=temp;

//        this.isEnable=isEnable;
        this.isRegist=isRegist;
        this.kick = kick;
    }
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getBpm() {
        return bpm;
    }

    public String getBattery() {
        return battery;
    }

    public String getIsenable() {
        return isenable;
    }

    public float getTemp() {
        return temp;
    }

    public String getMacaddress() {
        return macaddress;
    }

    public String getName() {
        return name;
    }

    public String getData() {
        return data;
    }

    public int getTimmer() {
        return timmer;
    }

    public int getisState() {
        return isstate;
    }


    public void setBattery(String battery) {
        this.battery = battery;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIsenable(String isenable) {
        this.isenable = isenable;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public void setMacaddress(String macaddress) {
        this.macaddress = macaddress;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setisState(int state) {
        this.isstate = state;
    }

    public void setTimmer(int timmer) {
        this.timmer = timmer;
    }


    public String getisRegist() {
        return isRegist;
    }

    public void setisRegist(String isRegist) {
        this.isRegist = isRegist;
    }

    public int getspittimmer() {
        return spittimmer;
    }

    public void setspittimmer(int spittimmer) {
        this.spittimmer = spittimmer;
    }

    public Kick getKick(){return this.kick;}

    public void setKick(Kick kick){this.kick = kick;}

    @Override
    public int compareTo(@NonNull Listitem o) {
        if (warnningLevel == o.warnningLevel)
            return 0;

        if (warnningLevel > o.warnningLevel){
            return -1;
        }else
            return 1;
    }

}
