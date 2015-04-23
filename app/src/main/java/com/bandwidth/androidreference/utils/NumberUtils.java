package com.bandwidth.androidreference.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

public class NumberUtils {

    public static String removeExtraCharacters(String phoneNumber) {
        return phoneNumber.replaceAll("[+()\\s-]", "");
    }

    public static String getPrettyPhoneNumber(String phoneNumber) {

        StringBuilder prettyNumber = new StringBuilder(removeExtraCharacters(phoneNumber));

        if (prettyNumber.length() > 0 && prettyNumber.charAt(0) == '1') {
            prettyNumber.insert(1, " ");
            if (prettyNumber.length() >= 5 && prettyNumber.length() <= 12) {
                prettyNumber.insert(2, "(");
                prettyNumber.insert(6, ") ");
                if (prettyNumber.length() >= 11) {
                    prettyNumber.insert(11, "-");
                }
            }
        }
        else {
            if (prettyNumber.length() >= 4 && prettyNumber.length() < 8) {
                prettyNumber.insert(3, "-");
            }
            else if (prettyNumber.length() >= 8 && prettyNumber.length() <= 10) {
                prettyNumber.insert(0, "(");
                prettyNumber.insert(4, ") ");
                prettyNumber.insert(9, "-");
            }
        }

        return prettyNumber.toString();
    }

    public static String fromSipUri(String uri) {
        String number = uri;
        if (uri.contains("+")) {
            number = uri.substring(uri.indexOf("+") + 1, uri.indexOf("@"));
        }
        else if (uri.contains(":")) {
            number = uri.substring(uri.indexOf(":") + 1, uri.indexOf("@"));
        }
        return getPrettyPhoneNumber(number);
    }

    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = contentResolver.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return phoneNumber;
        }
        if(cursor.moveToFirst()) {
            String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
            cursor.close();
            return contactName;
        }
        else {
            return phoneNumber;
        }
    }
}
