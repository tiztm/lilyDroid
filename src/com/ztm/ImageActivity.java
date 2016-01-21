package com.ztm;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.sonyericsson.zoom.ImageZoomView;
import com.sonyericsson.zoom.SimpleZoomListener;
import com.sonyericsson.zoom.ZoomState;
import com.sonyericsson.zoom.SimpleZoomListener.ControlType;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ZoomControls;
import android.widget.ImageView.ScaleType;

public class ImageActivity extends Activity {

	/** Image zoom view */
	private ImageZoomView mZoomView;

	/** Zoom state */
	private ZoomState mZoomState;

	/** Decoded bitmap image */
	private Bitmap image;

	/** On touch listener for zoom view */
	private SimpleZoomListener mZoomListener;

	String url = "";
	
	private int curNo = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		
		 if(ConstParam.isFull.equals("3"))
		{
			
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

		setContentView(R.layout.image);

		Intent intent = getIntent();

		String result = intent.getStringExtra("mUrl");

		url = result != null ? result
				: "http://bbs.nju.edu.cn/file/T/tiztm/belldandy.jpg";
		
		if(ConstParam.tpList!=null&&ConstParam.tpList.size()>0)
		{
			for (TopicPics tp : ConstParam.tpList) {
				if(tp.getLink().equals(url))
				{
					curNo = tp.getNo();
					break;
				}
			}
		}

		mZoomView = (ImageZoomView) findViewById(R.id.pic);
		
		mZoomState = new ZoomState();
		mZoomView.setZoomState(mZoomState);
		mZoomListener = new SimpleZoomListener();
		mZoomListener.setZoomState(mZoomState);
		mZoomListener.setControlType(ControlType.PAN);

		

		mZoomView.setOnTouchListener(mZoomListener);
		//Drawable drawable = 

		
		
		setMyBitMapFromUrl(url);
		 View findViewById = findViewById(R.id.topicll);
		
		 findViewById.bringToFront();
		ImageButton btn = (ImageButton) findViewById(R.id.btn_pre);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if(curNo<2) 
				{
					Toast.makeText(ImageActivity.this, "这是第一张图片!", Toast.LENGTH_SHORT)
					.show();
					return;
				}
					curNo--;
					Log.d("curNo", curNo+"");
					setMyBitMapFromNo(curNo);
			}
		});
		btn = (ImageButton) findViewById(R.id.btn_next);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if(curNo>ConstParam.tpList.size()-1) 
				{
					Toast.makeText(ImageActivity.this, "这是最后一张图片!", Toast.LENGTH_SHORT)
					.show();
					return;
				}
					curNo++;
					setMyBitMapFromNo(curNo);
					
			}

		
		});
		
		btn = (ImageButton) findViewById(R.id.btn_zoomIn);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				float z = mZoomState.getZoom() + 0.25f;
				mZoomState.setZoom(z);
				mZoomState.notifyObservers();
			}

		
		});
		
		
		btn = (ImageButton) findViewById(R.id.btn_zoomOut);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				float z = mZoomState.getZoom() - 0.25f;
				mZoomState.setZoom(z);
				mZoomState.notifyObservers();
			}

		
		});
		

		btn = (ImageButton) findViewById(R.id.btn_save);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				try {
					saveMyBitmap(url.substring(url.lastIndexOf('/'),url.lastIndexOf('.')));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		
		});
		
		
		
		
		


	}
	
	private void setMyBitMapFromNo(int curNo) {
		// TODO Auto-generated method stub
		String url="";
		if(ConstParam.tpList!=null&&ConstParam.tpList.size()>0)
		{
			for (TopicPics tp : ConstParam.tpList) {
				if(curNo == tp.getNo())
				{
					url = tp.getLink();
					break;
				}
			}
		}
		
		setMyBitMapFromUrl(url);
		
	}
	
	boolean canRec =true;
	private void setMyBitMapFromUrl(String url)
	{
		canRec = true;
		image = fetchDrawable(url);
		
		mZoomView.setImage(image);
		resetZoomState();
	}
	
	
	// 菜单项   
    final private int menuSettings=Menu.FIRST;  
    final private int menuReset=Menu.FIRST+1;  
    private static final int REQ_SYSTEM_SETTINGS = 0;    

    //创建菜单   
    @Override    
    public boolean onPrepareOptionsMenu(Menu menu) 
    {        
    	return true;
    }

    @Override  
    public boolean onCreateOptionsMenu(Menu menu)  
    {  
        // 建立菜单   
    	menu.add(Menu.NONE, menuReset, 2, "复位");  
        menu.add(Menu.NONE, menuSettings, 2, "保存");  
        
        return super.onCreateOptionsMenu(menu);  
    }  
    //菜单选择事件处理   
    @Override  
    public boolean onMenuItemSelected(int featureId, MenuItem item)  
    {  
        switch (item.getItemId())  
        {  
            case menuSettings:  
                //保存图片
            	try {
					saveMyBitmap(url.substring(url.lastIndexOf('/'),url.lastIndexOf('.')));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            case menuReset: 
            	resetZoomState();
                break;  
            default:  
                break;  
        }  
        return super.onMenuItemSelected(featureId, item);  
    }  

    public static byte[] getFile(String path) throws Exception {  
        byte[] b = null;  
        File file = new File(path);  
  
        FileInputStream fis = null;  
        ByteArrayOutputStream ops = null;  
        try {  
  
            if (!file.exists()) {  
               
                return null;
            }  
            if (file.isDirectory()) {  
            	   return null;
            }  
  
            byte[] temp = new byte[2048];  
  
            fis = new FileInputStream(file);  
            ops = new ByteArrayOutputStream(2048);  
  
            int n;  
            while ((n = fis.read(temp)) != -1) {  
                ops.write(temp, 0, n);  
            }  
            b = ops.toByteArray();  
        } catch (Exception e) {  
        	b=null;
            throw new Exception();  
        } finally {  
            if (ops != null) {  
                ops.close();  
            }  
            if (fis != null) {  
                fis.close();  
            }  
        }  
        return b;  
    }  

	public Bitmap fetchDrawable(String source) {

		Bitmap drawable = null;
		if (source.startsWith("http")) {
			
			String  path =StringUtil.picTempFiles.get(source);
			if(path==null) drawable = zoomDrawable(source);
			else
			{
			byte[] file=null;
			try {
				file = getFile(path);
			} catch (Exception e) {
				
			}
			if(file!=null)
			{
				drawable = getDrawFromByte(file);
			}
			else
			{
				drawable = zoomDrawable(source);
			}
			}
		
			
		} else {
			return null;
		}

		return drawable;

	}

	/**
	 * 根据图片网络地址获取图片的byte[]类型数据
	 * 
	 * @param urlPath
	 *            图片网络地址
	 * @return 图片数据
	 */
	public byte[] getImageFromURL(String urlPath) {
		byte[] data = null;
		InputStream is = null;
		HttpURLConnection conn = null;
		try {
			URL url = new URL(urlPath);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			// conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(6000);
			is = conn.getInputStream();
			if (conn.getResponseCode() == 200) {
				data = readInputStream(is);
			} else {
				data = null;
				return data;
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			conn.disconnect();
			try {
				if(is!=null)
					is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return data;
	}

	/**
	 * 读取InputStream数据，转为byte[]数据类型
	 * 
	 * @param is
	 *            InputStream数据
	 * @return 返回byte[]数据
	 */
	public byte[] readInputStream(InputStream is) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length = -1;
		try {
			while ((length = is.read(buffer)) != -1) {
				baos.write(buffer, 0, length);
			}
			baos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] data = baos.toByteArray();
		try {
			is.close();
			baos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	/**
	 * 根据网络图片地址集批量获取网络图片
	 * 
	 * @param urlPath
	 *            网络图片地址数组
	 * @return 返回Bitmap数据类型的数组
	 */
	public Bitmap zoomDrawable(String urlPath) {

		

		byte[] imageByte = getImageFromURL(urlPath.trim());

		// 以下是把图片转化为缩略图再加载
		
		return getDrawFromByte(imageByte);

	}
	

	public Bitmap getDrawFromByte(byte[] imageByte )
	{
		
		
		
		Bitmap bitmaps;

		if(imageByte==null)
		{
			canRec = false;
			bitmaps = drawableToBitmap(ConstParam.misphotoDraw	);
			return bitmaps;
		}
		// 以下是把图片转化为缩略图再加载
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		
		Bitmap bitmap = BitmapFactory.decodeByteArray(imageByte, 0,
				imageByte.length, options);
		
		options.inJustDecodeBounds = false;
		options.inPurgeable = true;
		options.inInputShareable = true;
		
		int allByte = options.outWidth*options.outHeight;
		int radio = allByte/4000000;
		if(radio>1)
		{
			options.inSampleSize = radio+1;
		}
		else
		{
			options.inSampleSize  =1;
		}
		mZoomState.setmPicX(options.outWidth/options.inSampleSize);
		mZoomState.setmPicY(options.outHeight/options.inSampleSize);
		
		
		mZoomState.setmRadio((float)(options.outWidth*1.0/options.outHeight));

		try {
			bitmaps = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length,
					options);
		} catch (Exception e) {
			Toast.makeText(ImageActivity.this, "原图太大！内存不足！", Toast.LENGTH_SHORT)
			.show();
			bitmaps = null;
		}
		
		return  bitmaps;
	}
	

	public String getSDPath() {

		File SDdir = null;

		boolean sdCardExist =

		Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);

		if (sdCardExist) {

			SDdir = Environment.getExternalStorageDirectory();

		}

		if (SDdir != null) {

			return SDdir.toString();

		}

		else {

			return null;

		}

	}

	String newPath = "";



	public void saveMyBitmap(String bitName) throws IOException {

		Bitmap bmp = image;
		//createSDCardDir();
		
		String status=Environment.getExternalStorageState();
		if(status.equals(Environment.MEDIA_MOUNTED)){//判断是否有SD卡
			
		}
		else{
			Toast.makeText(ImageActivity.this, "请插入SD卡", Toast.LENGTH_SHORT)
			.show();
			return;
		}
		
		
		File sdcardDir = Environment.getExternalStorageDirectory();
		
		newPath = sdcardDir.getPath() + "/lilyDroid/Images";// newPath在程序中要声明

		
		File f = new File(newPath + bitName + ".jpg");

		f.createNewFile();

		FileOutputStream fOut = null;

		try {

			fOut = new FileOutputStream(f);

		} catch (FileNotFoundException e) {

			e.printStackTrace();

		}

		bmp.compress(Bitmap.CompressFormat.JPEG, 80, fOut);

		try {

			fOut.flush();

		} catch (IOException e) {

			e.printStackTrace();

		} finally {
			fOut.close();

		}

		Toast.makeText(this, "图片保存于 " + newPath + bitName + ".jpg", 1000)
				.show();

	}

	public Bitmap drawableToBitmap(Drawable drawable) {
		Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
		return bitmap;

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(canRec)
		{
		image.recycle();
		}
		mZoomView.setOnTouchListener(null);
		mZoomState.deleteObservers();
	}

	private void resetZoomState() {
		mZoomState.setPanX(0.5f);
		mZoomState.setPanY(0.5f);

		final int mWidth = image.getWidth();
		final int vWidth = mZoomView.getWidth();

		mZoomState.setZoom(1f);
		mZoomState.notifyObservers();

	}
}