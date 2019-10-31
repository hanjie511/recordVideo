package com.example.recordvideo;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadVideo {
	/*
	 * ע�⣺�÷�������Okhttp�����ܽ����ļ����ϴ����������ȷ����Ŀ�Ѿ�������okhttp����ذ�
	 */
	private static ProgressDialog progressDialog;

	public static void uploadVideo(final String url, final String filePath, final Context context,
			final Activity activity) {
		progressDialog = showRequestDialog(context);
		progressDialog.show();
		MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
		File file = new File(filePath);
		if (file.exists()) {
			RequestBody body = RequestBody.create(MediaType.parse("video/*"), file);
			builder.addFormDataPart("video", getFileName(), body);
			builder.addFormDataPart("video", getFileName(), body);
			MultipartBody postBody = builder.build();
			Request request = new Request.Builder().url(url).post(postBody).build();
			OkHttpClient client = new OkHttpClient();
			Call call = client.newCall(request);
			call.enqueue(new Callback() {

				@Override
				public void onResponse(Call arg0, Response resp) throws IOException {
					// TODO Auto-generated method stub
					String resp_str = resp.body().string();
					String msg = "";
					try {
						JSONObject object = new JSONObject(resp_str);
						msg = object.getString("result");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if ("0".equals(msg)) {
						activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								progressDialog.dismiss();
								Toast.makeText(context, "�ϴ��ɹ�", Toast.LENGTH_SHORT).show();
								Intent intent=new Intent();
								intent.setClass(activity, MainActivity.class);
								activity.startActivity(intent);
								activity.finish();
							}
						});
					} else {
						activity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								progressDialog.dismiss();
								Toast.makeText(context, "�ϴ�ʧ��", Toast.LENGTH_SHORT).show();	
							}
						});
					}

				}

				@Override
				public void onFailure(Call arg0, IOException arg1) {
					// TODO Auto-generated method stub

				}
			});
		}

	}

	private static String getFileName() {
		SimpleDateFormat format = new SimpleDateFormat("YYYYMMDD_HHmmss");
		String fileName = format.format(new Date());
		fileName = fileName + ".mp4";
		return fileName;
	}

	private static ProgressDialog showRequestDialog(Context context) {
		ProgressDialog dialog = new ProgressDialog(context);
		dialog.setTitle("�����ϴ�����");
		dialog.setMessage("���Ժ�....");
		dialog.setCancelable(false);
		return dialog;
	}
}
