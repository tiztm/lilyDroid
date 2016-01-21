package com.ztm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.widget.Toast;

public class ConnectivityReceiver extends BroadcastReceiver {   
	  
	@Override  
	public void onReceive(Context context, Intent intent) {   

		//����������ӷ��� 

		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 

		// State state = connManager.getActiveNetworkInfo().getState(); 

		State state = connManager.getNetworkInfo( 

		ConnectivityManager.TYPE_WIFI).getState(); // ��ȡ��������״̬ 

		if (State.CONNECTED == state) { // �ж��Ƿ�����ʹ��WIFI���� 

		//success = true; 
			ConstParam.isWifi = true;

		} 
		else
		{
			ConstParam.isWifi = false;
		}

//		 
//
//		state = connManager.getNetworkInfo( 
//
//		ConnectivityManager.TYPE_MOBILE).getState(); // ��ȡ��������״̬ 
//
//		if (State.CONNECTED != state) { // �ж��Ƿ�����ʹ��GPRS���� 
//
//		success = true; 
//
//		} 
//
//		

	}
	  
}  
