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
import java.util.Set;

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
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;
import android.widget.AdapterView.OnItemClickListener;

public class BlogActivity extends Activity {


	String blogUrl = "http://bbs.nju.edu.cn/vd59879/blogdoc?userid=";
//	private String dataUrl = "";
//	private int datamsg = -1;
//	int runningTasks = 0;
//	private String data;
//	private ProgressDialog progressDialog = null;
	private ListView listView;
	String visitType = "101";
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		Resources res = getResources();
		Drawable drawable = res.getDrawable(R.drawable.bkcolor);
		this.getWindow().setBackgroundDrawable(drawable);
		
		setContentView(R.layout.blogarea);
		
		if(ConstParam.isFull.equals("2"))
		{
			//设置无标题  
	        requestWindowFeature(Window.FEATURE_NO_TITLE);  
		}
		else if(ConstParam.isFull.equals("3"))
		{
			//设置无标题  
	        requestWindowFeature(Window.FEATURE_NO_TITLE);  
	        //设置全屏  
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
	                WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		}
		else if(ConstParam.isFull.equals("4"))
		{
			
	        //设置全屏  
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
	                WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		}
		
		if(ConstParam.isChange)
		{
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		
		LinearLayout mLoadingLayout=(LinearLayout)findViewById(R.id.linearLayout1);
		
		mLoadingLayout.setVisibility(btnBarVis);
		
		Intent intent = getIntent();
		String result = intent.getStringExtra("name");
		if(result!=null)
		{
			setTitle(result+"的博客");
			curAreaName = result;
			blogUrl = blogUrl+curAreaName+"&t=";
			NetTraffic.getUrlHtml(BlogActivity.this, blogUrl+visitType, Const.BLOGAREA,handler);
		}
		else
		{
			String blogUserName = intent.getStringExtra("blogUserName");
			curAreaName = blogUserName;
			blogUrl = blogUrl+curAreaName+"&t=";
			setTitle(blogUserName+"的博客");
			String data = intent.getStringExtra("data");
			chaToArea(data);
		}
	}
	
	
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
	 * 捕获按键事件
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(btnBarVis == View.VISIBLE)
			{
				LinearLayout mLoadingLayout=(LinearLayout)findViewById(R.id.linearLayout1);
				mLoadingLayout.setVisibility(getBtnRevtVis());
				return true;
			}
		} 
		
		
		// 如果是返回键,直接返回到桌面
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			LinearLayout mLoadingLayout=(LinearLayout)findViewById(R.id.linearLayout1);
			if(mLoadingLayout!=null)
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
	 *  消息控制器，用来更新界面，因为在普通线程是无法用来更新界面的
	 */
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			NetTraffic.runningTasks--;
			if (msg.what != Const.MSGPSTNEW && NetTraffic.data.equals("error")) {
				displayMsg("你的网络貌似有点小问题~");
			} else {
				switch (msg.what) {
				case Const.BLOGAREA:
					chaToArea(NetTraffic.data);
					break;
					
					
				case Const.BLOGTOPIC:
					chaToTopic(NetTraffic.data);
					break;
					
					
				case Const.BLOGPAGE:
					chaToPage(NetTraffic.data);
					break;
					
				case Const.BLOGNEW:
					chaToNew(NetTraffic.data);
					break;
				case Const.BLOGDONEW:
					chaToDoNew(NetTraffic.data);
					break;
					
					
				case 12345:
					if(moreTextView!=null)
					{
					 moreTextView.setVisibility(View.VISIBLE);
			         loadProgressBar.setVisibility(View.GONE);
					}
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
	
	private void chaToDoNew(String data) {
		// TODO Auto-generated method stub
		if(data.contains("meta http-equiv='Refresh'"))
		{
			displayMsg("撰写博文成功");
			return;
		}
		else
		{
			displayMsg("撰写博文出现错误");
			return;
		}
	}
	View acdlgView ;
	String newVisitType;
	ImageButton btnlog;
	private void chaToNew(String data) {
		// TODO Auto-generated method stub
		if(data.contains("反动以及其他违法信息"))
		{
			//final String blogcocon = topicUrl.replace("blogcon", "blogdocomment");
			newVisitType = "101";
			LayoutInflater factory = LayoutInflater.from(BlogActivity.this);
			acdlgView = factory.inflate(R.layout.blogacdlg, null);
			Builder altDlg = new AlertDialog.Builder(
					BlogActivity.this).setTitle("撰写博文").setView(
					acdlgView).setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {

							EditText titleEdit = (EditText) acdlgView
							.findViewById(R.id.edt_cont);
							String cont = titleEdit
									.getText().toString();
		
							titleEdit = (EditText) acdlgView
									.findViewById(R.id.edt_title);
							String title = titleEdit.getText().toString();

							if(title.length()<1)
								title = "没标题";
							cont = StringUtil.getStrBetter(cont);
							
							
							if (ConstParam.isBackWord && ConstParam.backWords != null && ConstParam.backWords.length() > 0) {
								cont += "\n-\n" + ConstParam.signColor + ConstParam.backWords + "[m\n";
							}
							
							try {
								title = URLEncoder.encode(title, "GB2312");
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								title = "NoTitle";
							}
							String url =  "http://bbs.nju.edu.cn/blogdopost?title="
								+ title+"&perm="+newVisitType;;
							NameValuePair[] newVp = { new NameValuePair("text", cont) };
							NetTraffic.postUrlHtml( BlogActivity.this,url,newVp, Const.BLOGDONEW,handler);
							
						}

					});

			altDlg.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
						public void onClick(
								DialogInterface dialoginterface, int i) {

						}
					});
			
			btnlog = (ImageButton) acdlgView.findViewById(R.id.btn_smy);
			btnlog.setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {
					
					getSmilyGrid();
				}
			});
			
			btnlog = (ImageButton) acdlgView.findViewById(R.id.btn_lock);
			btnlog.setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {
					if(newVisitType.equals("101"))
					{
						displayMsg("发表至 好友区");
						newVisitType = "102";
						btnlog.setImageResource(R.drawable.b1022);
					}
					else if(newVisitType.equals("100"))
					{
						displayMsg("发表至 公开区");
						newVisitType = "101";
						btnlog.setImageResource(R.drawable.b1012);
					}
					else if(newVisitType.equals("102"))
					{
						displayMsg("发表至 私人区");
						newVisitType = "100";
						btnlog.setImageResource(R.drawable.b1002);
					}
				}
			});
			
			
			
			
			
			
			
			EditText titleEdit = (EditText) acdlgView.findViewById(R.id.edt_to);
			
			if (ConstParam.sLength < 300) {
				titleEdit = (EditText) acdlgView.findViewById(R.id.edt_cont);

				LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) titleEdit
						.getLayoutParams(); // 取控件mGrid当前的布局参数
				linearParams.height = 70;// 当控件的高强制设成75象素

				titleEdit.setLayoutParams(linearParams);

			}
			

			AlertDialog dlg = altDlg.create();
			dlg.show();
			
			
			
		}
		else
		{
			displayMsg("无权发表博客");
			return;
		}
		//text 
		
		
	}
	
	MyGridAdapter saImageItems;
	private void getSmilyGrid() {
		
		   final Dialog dialog = new Dialog(BlogActivity.this, R.style.FullHeightDialog);  
		   dialog.setContentView(R.layout.smilydlg);  
		   
		   GridView   findViewById =(GridView ) dialog.findViewById(R.id.updater_faceGrid);
		   if(saImageItems==null)
		   {
		   Set<String> keySet = StringUtil.smilyAll.keySet();
		   ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();  
           
		   for (String string : keySet) {
			   HashMap<String, Object> map = new HashMap<String, Object>();  
			   map.put("ItemImage", StringUtil.smilyAll.get(string));//添加图像资源的ID    
			   map.put("ItemText", string);//添加图像资源的ID    
			   lstImageItem.add(map);
		   }

           //生成适配器的ImageItem <====> 动态数组的元素，两者一一对应    
             saImageItems = new MyGridAdapter(this,   
                                                       lstImageItem,//数据来源     
                                                       R.layout.gridview_emotion_item,  
                                                       //动态数组与ImageItem对应的子项            
                                                       new String[] {"ItemImage","ItemText"},     
                                                           
                                                       //ImageItem的XML文件里面的一个ImageView  
                                                       new int[] {R.id.imageview_iv});    
		   }
               
             findViewById.setAdapter(saImageItems);    
             findViewById.setOnItemClickListener(new OnItemClickListener() {  
                 public void onItemClick(AdapterView<?> parent, View view,  
                         int position, long id) {  
                      
                	 
                     EditText titleEdit = (EditText) acdlgView
						.findViewById(R.id.edt_cont);
                     if(titleEdit!=null)
                     {
                    	 titleEdit.append(view.getTag().toString());
                     }
                     dialog.dismiss();
                 }  
             });  
             
             dialog.show();  
		   
		
	}
	
	
	private void chaToPage(String data) {
		// TODO Auto-generated method stub
		
		 List<TopicInfo> areaTopicNew = getAreaTopic(data);
		 
		 TopicInfo topicInfo2 = areaTopic.get(areaTopic.size()-1);
		 int s = Integer.parseInt(topicInfo2.getNums());
		 for (TopicInfo topicInfo : areaTopicNew) {
			 int n = Integer.parseInt(topicInfo.getNums());
			if(s>n)
			{
				areaTopic.add(topicInfo);
			}
		}
		 while(areaTopic.size()>200)
		 {
			 for(int i=0;i<20;i++)
			 {
				 areaTopic.remove(0);
			 }
			 scrollY-=20;
		 }
		 //areaTopic.re
		
		 chaToArea(null);
		
		
	}
	
	String curAreaName;
	String oldType;
	/**
	 * 跳转到blog界面
	 * 
	 * @param AreaData
	 */
	private void chaToArea(String AreaData) {
		if(AreaData!=null&&AreaData.contains("访问本页的权限"))
		{
			displayMsg("您没有访问本页的权限!");
			visitType = oldType;
			return;
		}
		
		
		listView = (ListView) findViewById(R.id.topicList);
		
		Button btnNew = (Button) findViewById(R.id.btnNew);
		btnNew.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				NetTraffic.getUrlHtml(BlogActivity.this, "http://bbs.nju.edu.cn/blogpost?t=101", Const.BLOGNEW,handler);
				
			}
		});

		ImageTextButton btn = (ImageTextButton) findViewById(R.id.btn100);
		if(visitType.equals("100"))
		{
			btn.setIcon(R.drawable.b1001);
		}
		else
		{
			btn.setIcon(R.drawable.b100);
			
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				oldType = visitType;
				visitType = "100";scrollY = 0;
				NetTraffic.getUrlHtml(BlogActivity.this, blogUrl+visitType, Const.BLOGAREA,handler);
				
			}
		});
		}

		btn = (ImageTextButton) findViewById(R.id.btn101);
		
		if(visitType.equals("101"))
		{
			btn.setIcon(R.drawable.b1011);
		}
		else
		{
			btn.setIcon(R.drawable.b101);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				oldType = visitType;
				visitType = "101";scrollY = 0;
				NetTraffic.getUrlHtml(BlogActivity.this, blogUrl+visitType, Const.BLOGAREA,handler);
				
			}
		});
		}
		
		btn = (ImageTextButton) findViewById(R.id.btn102);
		
		if(visitType.equals("102"))
		{
			btn.setIcon(R.drawable.b1021);
		}
		else
		{
			btn.setIcon(R.drawable.b102);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				oldType = visitType;
				visitType = "102";
				scrollY = 0;
				NetTraffic.getUrlHtml(BlogActivity.this, blogUrl+visitType, Const.BLOGAREA,handler);
				
			}
		});
		
		}
		
		
		
		
		 
		if(moreTextView==null)
		{
			addPageMore();
		}
		
		
		if (AreaData != null) {
			areaTopic = getAreaTopic(AreaData);
		}
		convtAreaTopics();
		
		if(moreTextView!=null)
		{
			 moreTextView.setVisibility(View.VISIBLE);
	         loadProgressBar.setVisibility(View.GONE);
	         listView.setSelection(scrollY);
	       
		}
		
		
		
		

	}
	int scrollY;
	TextView moreTextView=null;
	LinearLayout loadProgressBar;
	/**
     * 在ListView中添加"加载更多"
     */
    private void addPageMore(){
        View view=LayoutInflater.from(this).inflate(R.layout.list_page_load, null);
        moreTextView=(TextView)view.findViewById(R.id.more_id);
        loadProgressBar=(LinearLayout)view.findViewById(R.id.load_id);
        
        moreTextView.setOnClickListener(new OnClickListener() {
          
            public void onClick(View v) {
                //隐藏"加载更多"
            	System.out.println(areaNowTopic+"");
            	if(areaNowTopic<2)
            	{
            		displayMsg("该博客没有更多内容");
            		return;
            	}
            	//userid=tiztm&start=4
            	
            	String url = blogUrl+visitType+"&start="+(areaNowTopic-21);
            	NetTraffic.getUrlHtml(BlogActivity.this,url, Const.BLOGPAGE,handler);
            	
            	scrollY = listView.getFirstVisiblePosition()+1;
                moreTextView.setVisibility(View.GONE);
                //显示进度条
                loadProgressBar.setVisibility(View.VISIBLE);
                
                
               
            }
        });
        listView.addFooterView(view);
    }
	
	
	
	private void storeAreaName() {
		String areaName = "";
		for (String name : ConstParam.blogNamList) {
			areaName += name + ",";
		}
		if (areaName.length() > 1) {
			areaName = areaName.substring(0, areaName.length() - 1);
		}
		SharedPreferences sharedPreferences = getSharedPreferences("LilyDroid",
				Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();// 获取编辑器
		editor.putString("blogName", areaName);
		editor.commit();
	}
	
	
	private void chaToTopic(String topicData) {
		char s = 10;
		String backS = s + "";
		String nbs = "<br>";
		topicData = topicData.replaceAll(backS,nbs );
		Document doc = Jsoup.parse(topicData);
		Elements scs = doc.getElementsByTag("textarea");
		if (scs.size() != 1) {
			displayMsg("获取博客内容失败!");
		} else {
			Element textArea = scs.get(0);
			String infoView = nbs + textArea.text();
			
			int indexOf = topicData.indexOf(">现有评论");
			String comt="";
			if(indexOf>0)
			{
				String substring = topicData.substring(indexOf+5);
				int indexOf2 = substring.indexOf("条<");
				if(indexOf2>0)
				{
					comt=substring.substring(0,indexOf2);
				}
				else
				{
					comt="0";
				}
			}
			else
			{
				comt="0";
			}
			
			infoView = StringUtil.getBetterTopic(infoView);
			
			infoView+=nbs+"-"+nbs+"[1;34m(现有评论  "+comt+" 条)[m "+nbs;

			String withSmile = StringUtil.addSmileySpans(infoView,null);
			
			Intent intent = new Intent(BlogActivity.this,
					BlogTopicActivity.class);
			intent.putExtra("withSmile", withSmile);
			intent.putExtra("topicUrl", topicUrl);
			//intent.putExtra("comt", topicUrl);
			
			
			startActivity(intent);
		}

	}
	
	
	
	
	int areaNowTopic = 0;
	/**
	 * 解析获取的页面 处理讨论区的话题列表
	 * 
	 * @param data
	 * @return
	 */
	private List<TopicInfo> getAreaTopic(String data) {
		List<TopicInfo> tiList = new ArrayList<TopicInfo>();
		//尚未建立blog
		
			Document doc = Jsoup.parse(data);
			Elements tds = doc.getElementsByTag("td");
			int curPos = 0;
			int line = 4;
			int getTopicNo = 0;
			boolean isOK = false;
			while (curPos < tds.size()-3) {
				 	if(!isOK)
				 	{
				 		String text = tds.get(curPos).text();
				 		
					    if(text.equals("管理"))
					    {
					    	isOK = true;
					    	//判断下是否有<nobr>标签
					    }
					    curPos++;
					    continue;
				 	}
				
				
					TopicInfo ti = new TopicInfo();
					ti.setLink((tds.get(curPos + 2).getElementsByTag("a")).get(0)
							.attr("href"));// 设置title
					ti.setTitle("◇ "+tds.get(curPos + 2).text());// 设置title
	
					String date = DateUtil.formatDateToStrNoWeek(DateUtil
							.getDatefromStrNoWeek(tds.get(curPos + 1).text()));
					if (date == null || date.equals("null"))
						ti.setPubDate(tds.get(curPos + 1).text());
					else
						ti.setPubDate(date);
					//ti.setAuthor(tds.get(curPos + 2).text());
					ti.setHot(tds.get(curPos + 3).text());
					String notext = tds.get(curPos).text();
					ti.setNums(notext);
					tiList.add(ti);
					if (getTopicNo == 0) {
	
						if (notext != "" && Character.isDigit(notext.charAt(0))) {
							areaNowTopic = Integer.parseInt(notext);
							getTopicNo = 1;
						}
						if(curPos < tds.size()-4)
						{
						String del = tds.get(curPos+4).text();
						if (del!=null&&del != "" && !Character.isDigit(del.charAt(0))) {
							line = 5;
						}
						}
						
					}
				curPos += line;
			}											
		Collections.reverse(tiList);
		return tiList;
	}
	
	
	List<TopicInfo> areaTopic = null;
	private List<String> LinkAdr;
	/**
	 * 将由HTML页面转出的数据转化为ListView可读的形式 供BLOG使用
	 */
	private void convtAreaTopics() {

		LinkAdr = new ArrayList<String>();

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		for (TopicInfo topicInfo : areaTopic) {

			Map<String, Object> map = new HashMap<String, Object>();

			String title = topicInfo.getTitle();
			
			int indexOf = title.indexOf('(');
			if(indexOf>-1)
			{
				title = title.substring(0,indexOf);
			}
			
			map.put("topictitle", title);

			map.put("topicau", topicInfo.getNums()+" 人气:" + topicInfo.getHot());
			map.put("topicother", topicInfo.getPubDate());

			list.add(map);

			LinkAdr.add("http://bbs.nju.edu.cn/" + topicInfo.getLink());

		}
		if (list.size() > 0) {

			SimpleAdapter adapter = new SimpleAdapter(this, list,
					R.layout.vlist, new String[] { "topictitle", "topicau",
							"topicother"}, new int[] { R.id.topictitle,
							R.id.topicau, R.id.topicother});
			listView.setAdapter(adapter);
			// 添加点击
			listView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {

					if(arg2>=LinkAdr.size()) return;
					topicUrl = LinkAdr.get(arg2);

					if (topicUrl == null)
						return;

					
					NetTraffic.getUrlHtml(BlogActivity.this,topicUrl, Const.BLOGTOPIC,handler);

				}
			});
		}
	}
	
	String topicUrl ="";
	
	
	
	
	private void displayMsg(String msg) {
		Toast.makeText(BlogActivity.this, msg, Toast.LENGTH_SHORT)
				.show();
	}
	

	

}