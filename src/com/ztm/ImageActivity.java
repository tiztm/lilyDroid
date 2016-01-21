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
			
	        //����ȫ��  
	        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
	                WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		}
		else if(ConstParam.isFull.equals("4"))
		{
			
	        //����ȫ��  
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
					Toast.makeText(ImageActivity.this, "���ǵ�һ��ͼƬ!", Toast.LENGTH_SHORT)
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
					Toast.makeText(ImageActivity.this, "�������һ��ͼƬ!", Toast.LENGTH_SHORT)
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
	
	
	// �˵���   
    final private int menuSettings=Menu.FIRST;  
    final private int menuReset=Menu.FIRST+1;  
    private static final int REQ_SYSTEM_SETTINGS = 0;    

    //�����˵�   
    @Override    
    public boolean onPrepareOptionsMenu(Menu menu) 
    {        
    	return true;
    }

    @Override  
    public boolean onCreateOptionsMenu(Menu menu)  
    {  
        // �����˵�   
    	menu.add(Menu.NONE, menuReset, 2, "��λ");  
        menu.add(Menu.NONE, menuSettings, 2, "����");  
        
        return super.onCreateOptionsMenu(menu);  
    }  
    //�˵�ѡ���¼�����   
    @Override  
    public boolean onMenuItemSelected(int featureId, MenuItem item)  
    {  
        switch (item.getItemId())  
        {  
            case menuSettings:  
                //����ͼƬ
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
	 * ����ͼƬ�����ַ��ȡͼƬ��byte[]��������
	 * 
	 * @param urlPath
	 *            ͼƬ�����ַ
	 * @return ͼƬ����
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
	 * ��ȡInputStream���ݣ�תΪbyte[]��������
	 * 
	 * @param is
	 *            InputStream����
	 * @return ����byte[]����
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
	 * ��������ͼƬ��ַ��������ȡ����ͼƬ
	 * 
	 * @param urlPath
	 *            ����ͼƬ��ַ����
	 * @return ����Bitmap�������͵�����
	 */
	public Bitmap zoomDrawable(String urlPath) {

		

		byte[] imageByte = getImageFromURL(urlPath.trim());

		// �����ǰ�ͼƬת��Ϊ����ͼ�ټ���
		
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
		// �����ǰ�ͼƬת��Ϊ����ͼ�ټ���
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
			Toast.makeText(ImageActivity.this, "ԭͼ̫���ڴ治�㣡", Toast.LENGTH_SHORT)
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
		if(status.equals(Environment.MEDIA_MOUNTED)){//�ж��Ƿ���SD��
			
		}
		else{
			Toast.makeText(ImageActivity.this, "�����SD��", Toast.LENGTH_SHORT)
			.show();
			return;
		}
		
		
		File sdcardDir = Environment.getExternalStorageDirectory();
		
		newPath = sdcardDir.getPath() + "/lilyDroid/Images";// newPath�ڳ�����Ҫ����

		
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

		Toast.makeText(this, "ͼƬ������ " + newPath + bitName + ".jpg", 1000)
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