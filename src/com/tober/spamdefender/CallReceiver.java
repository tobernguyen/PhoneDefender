package com.tober.spamdefender;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallReceiver extends BroadcastReceiver {
	private final String TAG = "CallReceiver";
	
	private final String DATABASE_FILE = "data.db";
	private final String BLACKLIST_TABLE = "tblBlackList";
	private final String CONTACT_NAME = "name";
	private final String CONTACT_PHONE_NUMBER = "phone_number";
	private final String BLOCK_ENABLE = "enable";
	ITelephony telephonyService;
	
	SQLiteDatabase database;
	
	Context context;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		Bundle bundle = intent.getExtras();
		
		final String incomingNumber = bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
		
		boolean a,b = false;
		
		if(a = isRinging() && (b = isNumberInBlackList(incomingNumber))){
			Log.e(TAG, "isRinging: " + a + ", isInBL: " + b);
			cancelCall();
		}
	}
	
	/*
	 * Check whether incoming number in BL or not
	 */
	private boolean isNumberInBlackList(String phoneNumber){
		try{
			database = context.openOrCreateDatabase(DATABASE_FILE, Context.MODE_PRIVATE, null);
			final String []columns = {CONTACT_NAME, CONTACT_PHONE_NUMBER, BLOCK_ENABLE};
			Cursor cursor = database.query(BLACKLIST_TABLE, columns, null, null, null, null, null);
			cursor.moveToFirst();
			
			while(!cursor.isAfterLast()){
				String currentPhone = cursor.getString(1).toString();
				if(PhoneNumberUtils.compare(phoneNumber, currentPhone)){
					if (Boolean.parseBoolean(cursor.getString(2).toString())){
						return true;
					}
					else return false;
				}
				cursor.moveToNext();
			}
			return false;
		}catch(SQLException ex){
			Log.e(TAG, ex.getMessage());
			return false;
		}
	}
	
	/*
	 * Setup for Telephony Service
	 */
	private boolean isRinging() {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		try {
			Class<?> c = Class.forName(tm.getClass().getName());
			Method m = c.getDeclaredMethod("getITelephony");
			m.setAccessible(true);
			telephonyService = (ITelephony) m.invoke(tm);

			return telephonyService.isRinging();

		} catch (Exception e) {
			e.printStackTrace();
			telephonyService = null;
		}

		return false;
	}

	private void cancelCall() {
		try {
			if (telephonyService != null)
				telephonyService.endCall();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
