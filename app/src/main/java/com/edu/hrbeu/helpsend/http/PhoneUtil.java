package com.edu.hrbeu.helpsend.http;


import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

public class PhoneUtil {

    public static String getPhoneNumber(Cursor cursor, Context mContext){
        String userPhone = "";
        if ( cursor.moveToFirst()){
            //   获得联系人记录的ID
            String contactId = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.Contacts._ID));
            //  获得联系人的名字
            String name = cursor.getString(cursor.getColumnIndex(
                    ContactsContract.Contacts.DISPLAY_NAME));
            Cursor phoneCursor=mContext.getContentResolver().query(ContactsContract.CommonDataKinds.
                    Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.
                    Phone.CONTACT_ID+"="+"?",new String[]{contactId},null);
            if(phoneCursor.moveToFirst()){
                userPhone = phoneCursor.getString(phoneCursor.getColumnIndex(
                        ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
        }
        return userPhone;
    }
}
