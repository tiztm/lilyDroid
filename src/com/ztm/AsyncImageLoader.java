package com.ztm;

import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

public class AsyncImageLoader {  
    private HashMap<String, SoftReference<Drawable>> imageCache;  
  
    public AsyncImageLoader() {  
        this.imageCache = new HashMap<String, SoftReference<Drawable>>();  
    }  
  
    // 下载图片   
    public static Drawable loadImageFromUrl(String url) {  
        InputStream localInputStream = null;  
        try {  
            localInputStream = (InputStream) new URL(url).getContent();  
            Drawable localDrawable = null;  
            if (localInputStream != null)  
                localDrawable = Drawable.createFromStream(localInputStream,  
                        "src");  
            return localDrawable;  
        } catch (Exception localException) {  
            localException.printStackTrace();  
            return null;  
        }  
    }  
  
    public HashMap<String, SoftReference<Drawable>> getImageCache() {  
        return imageCache;  
    }  
  
    public void setImageCache(  
            HashMap<String, SoftReference<Drawable>> imageCache) {  
        this.imageCache = imageCache;  
    }  
  
    @SuppressWarnings("unchecked")  
    public Drawable loadDrawable(String imageURL,  
            ImageCallback paramImageCallback) {  
  
        boolean bool = this.imageCache.containsKey(imageURL);  
        Drawable localDrawable = null;  
        if (bool) {  
            localDrawable = (Drawable) ((SoftReference) (imageCache)  
                    .get(imageURL)).get();  
            return localDrawable;  
        } else {  
            ImageHandler imageHandler = new ImageHandler(this,  
                    paramImageCallback, imageURL);  
            new ImageThread(this, imageURL, imageHandler).start();  
        }  
        return localDrawable;  
    }  
  
public abstract interface ImageCallback {  
        public abstract void imageLoaded(Drawable drawable, String imageUrl);  
    }  
  
    class ImageHandler extends Handler {  
        AsyncImageLoader asyncImageLoader;  
        ImageCallback imageCallback;  
        String imageUrl;  
  
        public ImageHandler(AsyncImageLoader asyncImageLoader,  
                ImageCallback paramImageCallback, String paramString) {  
            this.asyncImageLoader = asyncImageLoader;  
            this.imageCallback = paramImageCallback;  
            this.imageUrl = paramString;  
        }  
  
        public void handleMessage(Message paramMessage) {  
            if (this.imageCallback != null) {  
                Drawable localDrawable = (Drawable) paramMessage.obj;  
                // 将图片和图片的URL 传给回调函数，在回调函数中进行相应操作。   
                imageCallback.imageLoaded(localDrawable, this.imageUrl);  
            }  
        }  
    }  
  
    class ImageThread extends Thread {  
        private AsyncImageLoader asyncImageLoader;  
        private String imageURL;  
        private ImageHandler imageHandler;  
  
        public ImageThread(AsyncImageLoader asyncImageLoader, String imageURL,  
                ImageHandler imageHandler) {  
            this.asyncImageLoader = asyncImageLoader;  
            this.imageURL = imageURL;  
            this.imageHandler = imageHandler;  
        }  
  
        public void run() {  
            Drawable localDrawable = AsyncImageLoader  
                    .loadImageFromUrl(this.imageURL);  
            // 将新下载的图片存入imageCache   
            HashMap imageCache = asyncImageLoader.getImageCache();  
            String str = this.imageURL;  
            SoftReference localSoftReference = new SoftReference(localDrawable);  
            imageCache.put(str, localSoftReference);  
            Message localMessage = this.imageHandler.obtainMessage(0,  
                    localDrawable);  
            // 将图片 通过消息发送给hander   
            this.imageHandler.sendMessage(localMessage);  
        }  
    }  
}  

