package com.mr_ladeng.videoplayerdemo;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class DensityUtil {

	/**
	 * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	
	/**
	 * 
	 * @param context
	 * @param filePath
	 * 普通安装
	 */
	public static void install(Context context, String filePath) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
	}
	
	/** 
	 * 静默安装 
	 * @param file 
	 * @return 
	 */  
	public static int slientInstall(File file) {  
	    int result = 1 ;  
	    Process process = null;  
	    OutputStream out = null;  
	    try {  
	        process = Runtime.getRuntime().exec("su");  
	        out = process.getOutputStream();  
	        DataOutputStream dataOutputStream = new DataOutputStream(out);  
	        dataOutputStream.writeBytes("chmod 777 " + file.getPath() + "\n");  //手机没有root权限的时候会 java.io.IOException: write failed: EPIPE (Broken pipe)
	        dataOutputStream.writeBytes("LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install -r " +   file.getPath());  
	        //在4.0 以上系统版本必须要加上  LD_LIBRARY_PATH=/vendor/lib:
	        // 提交命令     卸载也是同样pm uninstall命令 
	        dataOutputStream.flush();  
	        // 关闭流操作  
	        dataOutputStream.close();  
	        out.close();  
	        int value = process.waitFor();  
	          
	        // 代表成功  
	        if (value == 0) {  
	            result = 11 ;  
	        } else if (value == 1) { // 失败  
	            //result = 12 ;  
	            result = 14 ;  
	        } else { // 未知情况  
	            result = 13 ;  
	        }  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	        result = 14 ;
	    } catch (InterruptedException e) {  
	        e.printStackTrace();  
	        result = 14 ;
	    }  
	      
	    return result ;  
	}  
	
	
}