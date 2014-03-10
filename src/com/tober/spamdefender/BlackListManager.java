package com.tober.spamdefender;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class BlackListManager extends Activity {

	private static Context context;
	
	private final String TAG = "Call Defender";

	private final String DATABASE_FILE = "data.db";
	private final String BLACKLIST_TABLE = "tblBlackList";
	private final String CONTACT_NAME = "name";
	private final String CONTACT_PHONE_NUMBER = "phone_number";
	private final String BLOCK_ENABLE = "enable";
	
	private final int OPEN_CONTACT_REQUEST_CODE = 1;
	
	private final String CREATE_TABLE = "Create table tblBlackList(name text, phone_number text primary key, enable boolean)";

	private static BlackListAdapter blackListAdapter = null;
	private static ArrayList<BlockedContact> blockedContactList = null;
	
	private static ListView contactListView = null;
	private ImageButton addContactButton = null;
	private EditText nameOnDialog = null;
	private EditText numberOnDialog = null;
	
	SQLiteDatabase database;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.black_list_manager_activity);
		
		context = getApplicationContext();
		initView();
		openBlackListDatabase();
		loadBlackListFromDatabase();
		

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onPause() {
		updateBlackListDatabase();
		super.onPause();
	}
	
	/*
	 * All functions on Database
	 */



	// Open database
	private void openBlackListDatabase() {
		database = openOrCreateDatabase(DATABASE_FILE, MODE_PRIVATE, null);
		try {
			database.execSQL(CREATE_TABLE);
		} catch (SQLException ex) {
			Log.e(TAG, ex.getMessage());
		}
		database.close();
	}
	
	// Load data from database
	private void loadBlackListFromDatabase(){
		Log.e(TAG, "loadBlackListFromDatabase()");
		blockedContactList = new ArrayList<BlockedContact>();
		
		try{
			database = openOrCreateDatabase(DATABASE_FILE, MODE_PRIVATE, null);
			final String []columns = {CONTACT_NAME, CONTACT_PHONE_NUMBER, BLOCK_ENABLE};
			Cursor cursor = database.query(BLACKLIST_TABLE, columns, null, null, null, null, null);
			cursor.moveToFirst();
			
			while(cursor.isAfterLast()==false){
				blockedContactList.add(new BlockedContact(
						cursor.getString(0).toString(),
						cursor.getString(1).toString(),
						Boolean.parseBoolean(cursor.getString(2).toString())));
						Log.e(TAG,"Item: "+cursor.getString(0).toString() + ", " + cursor.getString(1).toString() + ", " +Boolean.parseBoolean(cursor.getString(2)));
				cursor.moveToNext();
			updateListView();
			database.close();
			}
		}catch(SQLException ex){
			Log.e(TAG, ex.getMessage());
		}
		
	}
	
	// Update database with new data
	private void updateBlackListDatabase(){
		Log.e(TAG, "Enter updateBlackListDatabase()");
		database = openOrCreateDatabase(DATABASE_FILE, MODE_PRIVATE, null);
		database.execSQL("DROP TABLE " + BLACKLIST_TABLE);
		database.execSQL(CREATE_TABLE);
		Iterator<BlockedContact> iter = blockedContactList.iterator();
		
		while(iter.hasNext()){
			BlockedContact contact = (BlockedContact) iter.next();
			ContentValues values = new ContentValues();
			values.put(CONTACT_NAME, contact.getName());
			values.put(CONTACT_PHONE_NUMBER, contact.getNumber());
			values.put(BLOCK_ENABLE, contact.isEnable()?"true":"false");
			
			Log.e(TAG, values.toString());
			
			database.insert(BLACKLIST_TABLE, null, values);
		}
		Toast.makeText(getApplicationContext(), "Saving data...", Toast.LENGTH_SHORT).show();
	}
	
	/*
	 * Other functions
	 */
	
	//Initiate View
	private void initView(){
		contactListView = (ListView) findViewById(R.id.blockContactListView);
		addContactButton = (ImageButton) findViewById(R.id.addContactButton);
		
		//Listener for addContactButton
		addContactButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				LayoutInflater inflater = LayoutInflater.from(context);
				View dialogView = inflater.inflate(R.layout.add_contact_layout, null);
				

				
				final EditText contactEntered = (EditText) dialogView.findViewById(R.id.nameUserEnter);
				final EditText phoneEntered = (EditText) dialogView.findViewById(R.id.phoneUserEnter);
				final Button selectFromContactBtn = (Button) dialogView.findViewById(R.id.selectFromContactButton);
				
				nameOnDialog = contactEntered;
				numberOnDialog = phoneEntered;
				
				selectFromContactBtn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						openContact();
					}
				});
				
				
				AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
				builder.setView(dialogView);
				
				builder
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String name = contactEntered.getText().toString();
						String phoneNumber = phoneEntered.getText().toString();
						
						if(!phoneNumber.equals("")){
							blockedContactList.add(new BlockedContact(name, phoneNumber, true));
							updateListView();
						}
					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						
					}
				});
				
				final AlertDialog alertDialog = builder.create();
				

				
				alertDialog.show();
				

				
			}
		});
		
	
	}
	
	//Update contactListView
	private static void updateListView(){
		blackListAdapter = new BlackListAdapter(context, R.layout.contact_layout, blockedContactList);		
		contactListView.setAdapter(blackListAdapter);
	}
	
	//Open Contact Activity
	private void openContact(){
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
		startActivityForResult(intent, OPEN_CONTACT_REQUEST_CODE);
	}

	// Return name and number after choose contact
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(data!=null){
			Uri uri = data.getData();
			if(uri!=null){
				Cursor c = null;
				try{
					c=getContentResolver().query(uri, new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
					if(c!=null && c.moveToFirst())
					{
						String name=c.getString(0);
						String phoneNumber=c.getString(1).replaceAll("[ ( | ) | \\- ]", "");
						nameOnDialog.setText(name);
						numberOnDialog.setText(phoneNumber);
						
					}
				}finally{
					if(c!=null){
						c.close();
					}
				}
			}
		}
		
		
	}
	
	public static void deleteContactFormBlackList(int position){
		blockedContactList.remove(position);
		updateListView();
	}
}

