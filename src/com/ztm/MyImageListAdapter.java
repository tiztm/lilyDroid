package com.ztm;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyImageListAdapter  extends BaseAdapter{

	
		private List list;
		private Context context;
		private String[] from;
		private int[] to;
		private int layoutid;
		private HashMap info;
		LayoutInflater myInflater;
		HashMap item; 

		public MyImageListAdapter(Context context, List list, int layoutid, String[] from,int[] to)
		{
			this.context = context;
			this.list = list;
			this.from = from;
			this.to = to;
			this.layoutid = layoutid;
		}
		
		public int getCount() {
			
			return list.size();
		}
		
		public Object getItem(int position) {
			
			return list.get(position);
		}

		public long getItemId(int position) {
			
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			myInflater = LayoutInflater.from(context);
			Resources resources = context.getResources();
				item = (HashMap) list.get(position);
				convertView = myInflater.inflate(layoutid, null);
				convertView.setTag(item);
			
				int k=0;
			for (int i : to) {
				
				if(k==1)
				{
					
					ImageView number_tv = (ImageView) convertView.findViewById(i);
					Object object = item.get(from[k]);
					if(object!=null)
					{
					
					int number_temp = (Integer)object;
					Drawable drawable = resources.getDrawable(number_temp);
					number_tv.setImageDrawable(drawable);
					}
				
				}
				else
				{
					TextView number_tv = (TextView) convertView.findViewById(i);
					String number_temp = (String)item.get(from[k]);
					
					number_tv.setText(number_temp);
				}
				
				k++;
			}
			
			return convertView;
		}		
	
	
	
}
