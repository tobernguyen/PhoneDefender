package com.tober.spamdefender;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class BlackListAdapter extends ArrayAdapter<BlockedContact> {

	Context context = null;
	int layoutId;
	ArrayList<BlockedContact> blockedContactList = null;
	
	public BlackListAdapter(Context context, int resource,
			ArrayList<BlockedContact> objects) {
		super(context, resource, objects);
		
		this.context =context;
		this.layoutId = resource;
		this.blockedContactList=objects;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		View vi=convertView;
		if(convertView == null) 
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			vi = inflater.inflate(R.layout.contact_layout, null);
		}
		
		final TextView contactNameTv = (TextView) vi.findViewById(R.id.contactNameTv);
		final TextView contactPhoneTv = (TextView) vi.findViewById(R.id.contactNumberTv);
		final ImageButton enableBtn = (ImageButton) vi.findViewById(R.id.enableBtn);
		final ImageButton deleteContactBtn = (ImageButton) vi.findViewById(R.id.deleteContactBtn);
		
		final BlockedContact contact = blockedContactList.get(position);
		
		contactNameTv.setText(contact.getName());
		contactPhoneTv.setText(contact.getNumber());
		
		
		/*
		 * Configuration for Enable/Disable block btn
		 */
		if (contact.isEnable() == false){
			enableBtn.setImageResource(R.drawable.ic_defend_disabled);
		}
		
		enableBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(contact.isEnable()){
					contact.setEnable(false);
					enableBtn.setImageResource(R.drawable.ic_defend_disabled);
					Toast.makeText(context, contact.getName() + " can call you", Toast.LENGTH_SHORT).show();
				}else{
					contact.setEnable(true);
					enableBtn.setImageResource(R.drawable.ic_defend);
					Toast.makeText(context, contact.getName() + " can not call you", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		deleteContactBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				BlackListManager.deleteContactFormBlackList(position);
			}
		});
		
		
		
		return vi;
		
	}
	
	
	
	
}
