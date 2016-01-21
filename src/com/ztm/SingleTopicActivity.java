package com.ztm;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.NameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.sonyericsson.zoom.ImageTextButton;
import com.sonyericsson.zoom.ImageZoomView;
import com.sonyericsson.zoom.SimpleZoomListener;
import com.sonyericsson.zoom.ZoomState;
import com.sonyericsson.zoom.SimpleZoomListener.ControlType;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;
import android.widget.AdapterView.OnItemClickListener;

public class SingleTopicActivity extends Activity  implements OnTouchListener,
OnGestureListener {
	
	private GestureDetector mGestureDetector;

	String topicUrl;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		
		super.onCreate(savedInstanceState);
		mGestureDetector = new GestureDetector(this);
		Resources res = getResources();
		Drawable drawable = res.getDrawable(R.drawable.bkcolor);
		this.getWindow().setBackgroundDrawable(drawable);
		setTitle("Œƒ’¬‰Ø¿¿");
		Intent intent = getIntent();
		String topicData = intent.getStringExtra("data");
		
		
		if(ConstParam.isFull.equals("2"))
		{
			//…Ë÷√Œﬁ±ÍÃ‚  
	        requestWindowFeature(Window.FEATURE_NO_TITLE);  
		}
		else if(ConstParam.isFull.equals("3"))
		{
			//…Ë÷√Œﬁ±ÍÃ‚  
	        requestWindowFeature(Window.FEATURE_NO_TITLE);  
	        //…Ë÷√»´∆¡  
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
	                WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		}
		else if(ConstParam.isFull.equals("4"))
		{
			
	        //…Ë÷√»´∆¡  
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
	                WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		}
		
		
		char s = 10;
		String backS = s + "";
		String nbs = "<br>";
		topicData = topicData.replaceAll(backS,nbs );
		Document doc = Jsoup.parse(topicData);
		Elements scs = doc.getElementsByTag("textarea");
		
			Element textArea = scs.get(0);
			String infoView = nbs + textArea.text();


			infoView = StringUtil.getBetterTopic(infoView);
			
			String withSmile = StringUtil.addSmileySpans(infoView,null);
		
		
		

		setContentView(R.layout.singletopic);
		
		if(ConstParam.isChange)
		{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		
		LinearLayout mLoadingLayout=(LinearLayout)findViewById(R.id.topicll);
		
		
		
		TextView textView = (TextView) findViewById(R.id.label);
		textView.setText(StringUtil.getSmilyStr(withSmile,getResources()));
		textView.setTextSize(ConstParam.txtFonts);
		if (ConstParam.isTouch) {
		textView.setOnTouchListener(this);
		textView.setFocusable(true);
		textView.setLongClickable(true);
		}
		
		/*
			final String blogcocon = topicUrl.replace("bbscon", "bbspst");
			

			Button btnPre = (Button) findViewById(R.id.btn_huifu);
			btnPre.setOnClickListener(new OnClickListener() {
	
				public void onClick(View v) {
					LinearLayout mLoadingLayout=(LinearLayout)findViewById(R.id.topicll);
					 btnBarVis = View.GONE;
					 mLoadingLayout.setVisibility(btnBarVis);
					NetTraffic.getUrlHtml(SingleTopicActivity.this,blogcocon, Const.BLOGRE,handler);
				}
			});
			*/
			

			mLoadingLayout.setVisibility(btnBarVis);
		
		
		
	}
	
	
	boolean isNoBar = false;
	int btnBarVis = View.GONE;
	private int getBtnRevtVis()
	{
		if(btnBarVis ==  View.VISIBLE)
		{
			btnBarVis = View.GONE;
			
		}
		else
		{
			btnBarVis = View.VISIBLE;
		}
		return btnBarVis;
			
	}
	
	/**
	 * ≤∂ªÒ∞¥º¸ ¬º˛
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// »Áπ˚ «∑µªÿº¸,÷±Ω”∑µªÿµΩ◊¿√Ê
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(btnBarVis == View.VISIBLE)
			{
				LinearLayout mLoadingLayout=(LinearLayout)findViewById(R.id.topicll);
				mLoadingLayout.setVisibility(getBtnRevtVis());
				return true;
			}
			

		} 
		
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			LinearLayout mLoadingLayout=(LinearLayout)findViewById(R.id.topicll);
			if(mLoadingLayout!=null&&!isNoBar)
			{
				mLoadingLayout.setVisibility(getBtnRevtVis());
				return true;
			}
			else
			{
				return super.onKeyDown(keyCode, event);
			}
		}
		
			return super.onKeyDown(keyCode, event);
		
	}
	
	
	/**
	 *  œ˚œ¢øÿ÷∆∆˜£¨”√¿¥∏¸–¬ΩÁ√Ê£¨“ÚŒ™‘⁄∆’Õ®œﬂ≥Ã «Œﬁ∑®”√¿¥∏¸–¬ΩÁ√Êµƒ
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			NetTraffic.runningTasks--;
			if (msg.what != Const.MSGPSTNEW && NetTraffic.data.equals("error")) {
				displayMsg("ƒ„µƒÕ¯¬Á√≤À∆”–µ„–°Œ Ã‚~");
			} else {
				switch (msg.what) {
				case Const.BLOGCOMT:
					chaToComment(NetTraffic.data);
					break;
					
				case	Const.BLOGRE:
					chaToRe(NetTraffic.data);
					break;
					
				case	Const.BLOGDORE:
					chaToDoRe(NetTraffic.data);
					break;
					
				default:
					break;
				}
			}
			if (NetTraffic.runningTasks < 1) {
				NetTraffic.runningTasks = 0;
				NetTraffic.progressDialog.dismiss();
			}

		}

	
		
	};
	
	
	private void chaToDoRe(String data) {
		// TODO Auto-generated method stub
		if(data.contains("meta http-equiv='Refresh'"))
		{
			displayMsg("∑¢±Ì∆¿¬€≥…π¶");
			return;
		}
		else
		{
			displayMsg("Œﬁ»®∑¢±Ì∆¿¬€");
			return;
		}
	}

	
	
	private void chaToRe(String data) {
		// TODO Auto-generated method stub
		if(data.contains("∑¥∂Ø“‘º∞∆‰À˚Œ•∑®–≈œ¢"))
		{
			final String blogcocon = topicUrl.replace("blogcon", "blogdocomment");
			LayoutInflater factory = LayoutInflater
			.from(SingleTopicActivity.this);
			final View acdlgView = factory.inflate(R.layout.editdlg, null);
			Builder altDlg = new AlertDialog.Builder(
					SingleTopicActivity.this).setTitle("∑¢±Ì∆¿¬€").setView(
					acdlgView).setPositiveButton("»∑∂®",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							EditText titleEdit = (EditText) acdlgView
									.findViewById(R.id.edt_cont);
							String cont = StringUtil.getStrBetter(titleEdit
									.getText().toString());

							if (ConstParam.isBackWord && ConstParam.backWords != null && ConstParam.backWords.length() > 0) {
								cont += "\n-\n" + ConstParam.signColor + ConstParam.backWords + "[m\n";
							}
							
							
							String url = blogcocon;


							NameValuePair[] newVp = { new NameValuePair("text", cont) };

					

							NetTraffic.postUrlHtml( SingleTopicActivity.this,url,newVp, Const.BLOGDORE,handler      );
							
						}

					});

			altDlg.setNegativeButton("»°œ˚",
					new DialogInterface.OnClickListener() {
						public void onClick(
								DialogInterface dialoginterface, int i) {

						}
					});

			AlertDialog dlg = altDlg.create();
			dlg.show();
			
			
			
		}
		else
		{
			displayMsg("Œﬁ»®∑¢±Ì∆¿¬€");
			return;
		}
		//text 
		
		
	}
	
	private void displayMsg(String msg) {
		Toast.makeText(SingleTopicActivity.this, msg, Toast.LENGTH_SHORT)
				.show();
	}
	
	private void chaToComment(String topicData) {
		char s = 10;
		String backS = s + "";
		String nbs = "<br>";
		topicData = topicData.replaceAll(backS,nbs );
		Document doc = Jsoup.parse(topicData);
		Elements scs = doc.getElementsByTag("textarea");
		if (scs.size() != 1) {
			displayMsg("√ª”–∆¿¬€!");
		} else {
			Element textArea = scs.get(0);
			
			
			String infoView = nbs + textArea.text();
			
			if(infoView.length()>5)
			{
			
			infoView = StringUtil.getBetterTopic(infoView);

			String withSmile = StringUtil.addSmileySpans(infoView,null);
			
			Intent intent = new Intent(SingleTopicActivity.this,
					SingleTopicActivity.class);
			
			intent.putExtra("withSmile", withSmile);
			startActivity(intent);
			}
			else
			{
				displayMsg("√ª”–∆¿¬€!");
			}
		}

	}

	public boolean onTouch(View arg0, MotionEvent arg1) {
		return mGestureDetector.onTouchEvent(arg1);
	}

	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public boolean onSingleTapUp(MotionEvent arg0) {
		ScrollView sv = (ScrollView) findViewById(R.id.scrollView);
		float x = arg0.getRawX();
		float y = arg0.getRawY();
		// µ„ª˜…œ∑≠∫Õµ„ª˜œ¬∑≠
		if (y > sv.getHeight() - ConstParam.sLength / 6 && x > (ConstParam.sWidth * 3 / 4)) {
			sv.scrollBy(0, sv.getHeight() - 20);
			return true;
		}
		if (y < ConstParam.sLength / 3 && x > (ConstParam.sWidth * 3 / 4)) {
			sv.scrollBy(0, 20 - sv.getHeight());
			return true;
		}
		return false;
	}
	
	
	
	
}