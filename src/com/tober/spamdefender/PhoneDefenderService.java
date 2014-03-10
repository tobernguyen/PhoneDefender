package com.tober.spamdefender;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class PhoneDefenderService extends Service{

	final String TAG = "PhoneDefenderService";
	
	CallReceiver callReceiver;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void onCreate() {
		Log.e(TAG,"Enter onCreate()");
		callReceiver = new CallReceiver();
		IntentFilter intentFilter = new IntentFilter("android.intent.action.PHONE_STATE");
		intentFilter.setPriority(1);
		registerReceiver(callReceiver, intentFilter);
		super.onCreate();
	}


	@Override
	public void onDestroy() {
		Log.e(TAG, "Enter onDestroy()");
		unregisterReceiver(callReceiver);
		super.onDestroy();
	}
	
	
}
