package com.tober.spamdefender;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	private final Context mContext = this;
	
	CallReceiver mCallReceiver;
	private static ActivityManager manager;
	
	private Button openBlackListManagerBtn = null;
	private Button registerServiceBtn = null;
	private Button unRegisterServiceBtn = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initResources();

	}

	/*
	 * Initiate resources
	 */
	
	private void initResources(){
		manager=(ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		mCallReceiver = new CallReceiver();
		registerServiceBtn = (Button) findViewById(R.id.registerBroadcastButton);
		registerServiceBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startCallBlockerService();
				
			}
		});
		
		unRegisterServiceBtn = (Button) findViewById(R.id.unregisterBroadcastButton);
		unRegisterServiceBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				stopCallBlockerService();				
			}
		});
		
		openBlackListManagerBtn = (Button) findViewById(R.id.openBlackListManagerBtn);
		openBlackListManagerBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, BlackListManager.class);
				startActivity(intent);
				
			}
		});
	}
	
	//-----------------------------------------------------------------------------
	//Call Blocker Methods
	//-----------------------------------------------------------------------------
	public void startCallBlockerService()
	{
		if(!checkIfServiceIsAlreadyRunning())
		{
				Intent i=new Intent(MainActivity.this, PhoneDefenderService.class);
				startService(i);
				Toast.makeText(mContext, "Service start successfully", Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(mContext, "Service is running already", Toast.LENGTH_SHORT).show();
		}
		
	}
	
	public void stopCallBlockerService()
	{
		if(checkIfServiceIsAlreadyRunning())
		{
			stopService(new Intent(MainActivity.this, PhoneDefenderService.class));
			Toast.makeText(mContext, "Service stop successfully", Toast.LENGTH_SHORT).show();
		}
		else{
			Toast.makeText(mContext, "Phone Defender hasn't activated yet!", Toast.LENGTH_SHORT).show();
		}
	}
	
	public static boolean checkIfServiceIsAlreadyRunning()
	{
		for(RunningServiceInfo service: manager.getRunningServices(Integer.MAX_VALUE))
		{
			if(PhoneDefenderService.class.getName().equals(service.service.getClassName()))
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
