package com.example.recordvideo;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;

public class PlayVideoActivity extends Activity  {
	MediaController controller;
	SurfaceView surfaceView;
	SurfaceHolder holder;
	MediaPlayer player;
	String path="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_video);
		File video_path=new File(getVideoPath());
		path=video_path.toString();
		initView();
	}
	private void initView() {
		surfaceView=(SurfaceView) findViewById(R.id.video_surface);
		player=new MediaPlayer();
        try {
            player.setDataSource(path);
            holder=surfaceView.getHolder();
            holder.addCallback(new MyCallBack());
            player.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
        	@Override
        	public void onPrepared(MediaPlayer mp) {
        		player.start();
        		player.setLooping(true);
        	}
        });
	}
	public void cancel(View v) {
		Intent intent=new Intent();
		intent.setClass(PlayVideoActivity.this,MainActivity.class);
		startActivity(intent);
		if(deleteFile()==true) {
			finish();
		}else {
			Toast.makeText(PlayVideoActivity.this, "Œƒº˛…æ≥˝ ß∞‹£¨«Î÷ÿ ‘£°£°£°", Toast.LENGTH_SHORT).show();
		}
	}
	private  boolean deleteFile() {
		boolean flag=false;
		File file=new File(path);
		if(file.exists()) {
			flag=file.delete();
		}
		return flag;
	}
	public void confirm(View v) {
		String url="http://192.168.1.219:8081/bdsz/app/saveVideo.html";
		UploadVideo.uploadVideo(url, path, PlayVideoActivity.this,PlayVideoActivity.this);
	}
	private String getVideoPath() {
		Intent intent=getIntent();
		String path=intent.getStringExtra("path");
		return path;
	}
	private class MyCallBack implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            player.setDisplay(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    }
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		player.stop();
		player.release();
	}
	
}
