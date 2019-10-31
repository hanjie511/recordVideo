package com.example.recordvideo;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.recordvideo.CircleButtonView.OnLongClickListener;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.provider.SyncStateContract.Constants;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
public class MainActivity extends Activity {
	CamcorderProfile profile = null;
	CircleButtonView circle_btn;
	Button cancel_btn;
	Button confirm_btn;
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	MediaRecorder recorder;
	private Camera mCamera;
	private CameraPreview mPreview;
	private boolean isRecording = false;
	MediaRecorder mediaRecorder;
	static String videoPath="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
	}
	public void cancel(View v) {
		finish();
	}
	private void initView() {
		circle_btn = (CircleButtonView) findViewById(R.id.recorder_btn_recorder);
		cancel_btn = (Button) findViewById(R.id.cancel_btn_recorder);
		mCamera = getCameraInstance();
		// Create our Preview view and set it as the content of our activity.
		mPreview = new CameraPreview(this, mCamera);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(mPreview);
		circle_btn.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public void onRecordFinishedListener() {
				// TODO Auto-generated method stub
				releaseMediaRecorder();
				Intent intent=new Intent();
				intent.setClass(MainActivity.this, PlayVideoActivity.class);
				intent.putExtra("path", videoPath);
				startActivity(intent);
				finish();
			}

			@Override
			public void onNoMinRecord(int currentTime) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLongClick() {
				// TODO Auto-generated method stub

				// initialize video camera
				if (prepareVideoRecorder()) {
					// Camera is available and unlocked, MediaRecorder is prepared,
					// now you can start recording
					mediaRecorder.start();
					// inform the user that recording has started
					isRecording = true;
				}
			}

		});
	}

	private boolean prepareVideoRecorder() {
		mediaRecorder = new MediaRecorder();
		// Step 1: Unlock and set camera to MediaRecorder
		mCamera.unlock();
		mediaRecorder.setCamera(mCamera);

		// Step 2: Set sources
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

		// Step 3: Set output format and encoding (for versions prior to API Level 8)
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
		
		mediaRecorder.setVideoSize(1920, 1080);
		mediaRecorder.setOrientationHint(90);
		mediaRecorder.setVideoFrameRate(30);
		mediaRecorder.setVideoEncodingBitRate(3 * 1024 * 1024);
		// Step 4: Set output file
		mediaRecorder.setOutputFile(getOutputMediaFile().toString());

		// Step 5: Set the preview output
		mediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());
		// Step 6: Prepare configured MediaRecorder
		try {
			mediaRecorder.prepare();
		} catch (IllegalStateException e) {
			releaseMediaRecorder();
			return false;
		} catch (IOException e) {
			releaseMediaRecorder();
			return false;
		}
		return true;
	}

	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
			c.setDisplayOrientation(90);
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile() {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"MyCameraApp");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
		videoPath=mediaFile.toString();
		return mediaFile;
	}

	@Override
	protected void onPause() {
		super.onPause();
		releaseMediaRecorder(); // if you are using MediaRecorder, release it first
		releaseCamera(); // release the camera immediately on pause event
	}

	private void releaseMediaRecorder() {
		if (mediaRecorder != null) {
			mediaRecorder.reset(); // clear recorder configuration
			mediaRecorder.release(); // release the recorder object
			mediaRecorder = null;
			mCamera.lock(); // lock camera for later use
		}
	}

	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.release(); // release the camera for other applications
			mCamera = null;
		}
	}
}
