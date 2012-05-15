package de.paolo.android;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PaoloArrayAdapter extends ArrayAdapter<String>{
	private Context context;
	private ArrayList<String> items;
	private int resource;
	
	public PaoloArrayAdapter(Context context, int resource, ArrayList<String> items) {
		super(context, resource, items);
		this.context = context;
		this.resource = resource;
		this.items = items;
	}
 
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 
		View listView = inflater.inflate(resource, parent, false);
		
		TextView textView = (TextView) listView.findViewById(R.id.paoloField);
		
		//check if even and change bg
		int n = position % 2;
		int p;
		if(n == 0) {
			textView.setBackgroundResource(R.drawable.leftbubble2);
			
			p = (int) Math.round(textView.getPaddingLeft() * 3);
			textView.setPadding(p, textView.getPaddingTop(),
					textView.getPaddingRight(), textView.getPaddingBottom());
		}else{
			textView.setBackgroundResource(R.drawable.rightbubble2);
			
			p = (int) Math.round(textView.getPaddingRight() * 2);
			textView.setPadding(textView.getPaddingLeft(), textView.getPaddingTop(),
					p, textView.getPaddingBottom());
		}
		
		textView.setText(items.get(position));
		
		
		
		return listView;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		return false;
	}
	
	
}
