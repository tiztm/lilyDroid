package com.ztm;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
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

public class MyGridAdapter  extends BaseAdapter{

	
		private List list;
		private Context context;
		private String[] from;
		private int[] to;
		private int layoutid;
		private HashMap info;
		LayoutInflater myInflater;
		HashMap item; 

		public MyGridAdapter(Context context, List list, int layoutid, String[] from,int[] to)
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

				item = (HashMap) list.get(position);
				convertView = myInflater.inflate(layoutid, null);
				//convertView.setTag(item);
			
				
				int k=0;
			for (int i : to) {
					ImageView number_tv = (ImageView) convertView.findViewById(i);
					int number_temp = (Integer)item.get(from[k]);
					number_tv.setImageResource(number_temp);
					
					k++;
					
					String s = (String)item.get(from[k]);
					if(s.equals("[:]"))
						s = "[:$]";
					convertView.setTag(s);
			}
			
			return convertView;
		}		
	
	
	
}
