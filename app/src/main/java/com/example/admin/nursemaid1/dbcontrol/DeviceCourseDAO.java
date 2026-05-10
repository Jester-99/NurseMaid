package com.example.admin.nursemaid1.dbcontrol;

/**
 * Created by Administrator on 2018/1/4.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

//import com.example.admin.nursemaid.Listitem;
import com.example.admin.nursemaid1.Listitem;

import java.util.ArrayList;
import java.util.List;

public class DeviceCourseDAO {                                                                          //紀錄多數畫面曾使用的tag
    public static final String TAG = "DeviceCourseDAO";

    // ���W��
    public static final String TABLE_NAME = "bledevice_table";

    // �s��������W�١A�T�w����
    public static final String KEY_ID = "_id";

    // �䥦������W��
    public static final String FIELD_DEVICE_NAME = "device_name";
    public static final String FIELD_USER_ADDRESS = "device_address";

    // �ϥΤW���ŧi���ܼƫإߪ�檺SQL���O
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NAME + " (" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + FIELD_DEVICE_NAME
            + " TEXT  DEFAULT NULL, " + FIELD_USER_ADDRESS
            + " TEXT  DEFAULT NULL UNIQUE)";

    // ��Ʈw����
    private SQLiteDatabase db;

    // �غc�l�A�@�몺���γ����ݭn�ק�
    public DeviceCourseDAO(Context context) {
        db = MyDBHelper.getDatabase(context);
        if(!db.isOpen()){
            db = MyDBHelper.getDatabase(context);
        }
    }

    // ������Ʈw�A�@�몺���γ����ݭn�ק�
    public void close() {
        db.close();
    }

    // �s�W�Ѽƫ��w������
    public Listitem insert(Listitem item) {
        // �إ߷ǳƷs�W��ƪ�ContentValues����
        ContentValues cv = new ContentValues();

        // �[�JContentValues����]�˪��s�W���
        // �Ĥ@�ӰѼƬO���W�١A �ĤG�ӰѼƬO��쪺���
        cv.put(FIELD_DEVICE_NAME, item.getName());
        cv.put(FIELD_USER_ADDRESS, item.getMacaddress());

        // �s�W�@����ƨè��o�s��
//		long id = db.replace(TABLE_NAME, null, cv);
        long id = db.insert(TABLE_NAME, null, cv);
        // �]�w�s��
        item.setId(id);
        // �^�ǵ��G
        return item;
    }

    // �ק�Ѽƫ��w������
    public boolean update(Listitem item) {
        // �إ߷ǳƭק��ƪ�ContentValues����
        ContentValues cv = new ContentValues();
      //  Log.d(TAG,item.show());
        // �[�JContentValues����]�˪��ק���
        // �Ĥ@�ӰѼƬO���W�١A �ĤG�ӰѼƬO��쪺���
        cv.put(FIELD_DEVICE_NAME, item.getName());
        cv.put(FIELD_USER_ADDRESS, item.getMacaddress());

        // �]�w�ק��ƪ����󬰽s��

        String where = KEY_ID + "=" + item.getId();
        // ����ק��ƨæ^�ǭק諸��Ƽƶq�O�_���\
        return db.update(TABLE_NAME, cv, where, null) > 0;
    }

    public boolean update(String address , String name) {
        // �إ߷ǳƭק��ƪ�ContentValues����
        ContentValues cv = new ContentValues();
        Listitem item = get(address);
        item.setName(name);

        //  Log.d(TAG,item.show());
        // �[�JContentValues����]�˪��ק���
        // �Ĥ@�ӰѼƬO���W�١A �ĤG�ӰѼƬO��쪺���
        cv.put(FIELD_DEVICE_NAME, item.getName());
        cv.put(FIELD_USER_ADDRESS, item.getMacaddress());

        // �]�w�ק��ƪ����󬰽s��
        String where = KEY_ID + "=" + item.getId();

        // ����ק��ƨæ^�ǭק諸��Ƽƶq�O�_���\
        return db.update(TABLE_NAME, cv, where, null) > 0;
    }

    // �R���Ѽƫ��w�s�������
    public boolean delete(long id) {
        // �]�w���󬰽s���A�榡���u���W��=��ơv
        String where = KEY_ID + "=" + id;
        // �R�����w�s����ƨæ^�ǧR���O�_���\
        return db.delete(TABLE_NAME, where, null) > 0;
    }

    // Ū���Ҧ��O�Ƹ��
    public List<Listitem> getAll() {
        List<Listitem> result = new ArrayList<Listitem>();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null,
                null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    // ���o���w�s������ƪ���
    public Listitem get(long id) {
        // �ǳƦ^�ǵ��G�Ϊ�����
        Listitem item = null;
        // �ϥνs�����d�߱���
        String where = KEY_ID + "=" + id;
        // ����d��

        Cursor result = db.query(TABLE_NAME, null, where, null, null, null,
                null, null);

        // �p�G���d�ߵ��G
        if (result.moveToFirst()) {
            // Ū���]�ˤ@����ƪ�����
            item = getRecord(result);
        }

        // ����Cursor����
        result.close();
        // �^�ǵ��G
        return item;
    }

    // ���o���w�W�٪���ƪ���
    public Listitem get(String address) {

        Log.d(address, "DAO_GET:" + address);
        Listitem item = null;
        String where = FIELD_USER_ADDRESS + " = '" + address + "'";

        if(db.isOpen()){
            Cursor result = db.query(TABLE_NAME, null, where, null, null, null,
                    null);
            if (result.getCount() > 0) {
                result.moveToNext();
                item = getRecord(result);
            }

            result.close();
        }

        if (item != null)
            return item;
        else {
            item = new Listitem();
            return item;
        }
    }

    // ���o���w�W�٪���ƪ���
    public Listitem get(Listitem tmpDevice) {
        Listitem item = null;
        String where = FIELD_USER_ADDRESS + " = '" + tmpDevice.getMacaddress() + "'";
        Cursor result = db.query(TABLE_NAME, null, where, null, null, null,
                null);

        if (result.getCount() > 0) {
            result.moveToNext();
            item = getRecord(result);
        }

        result.close();
        if (item != null)
            return item;
        else {
            return insert(tmpDevice);
        }
    }

    // ��Cursor�ثe����ƥ]�ˬ�����
    public Listitem getRecord(Cursor cursor) {
        // �ǳƦ^�ǵ��G�Ϊ�����
        Listitem result = new Listitem();

        result.setId(cursor.getLong(0));
        result.setName(cursor.getString(1));
        result.setMacaddress(cursor.getString(2));

        // �^�ǵ��G
        return result;
    }

    // ���o��Ƽƶq
    public int getCount() {
        int result = 0;

        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);

        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }

        return result;
    }
}
