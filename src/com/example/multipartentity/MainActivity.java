package com.example.multipartentity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends Activity {

	private static final int SELECT_FILE1 = 1;
	private static final int SELECT_FILE2 = 2;
	String selectedPath1 = "NONE";
	String selectedPath2 = "NONE";
	TextView tv, res;
	ProgressDialog progressDialog;
	Button b1, b2, b3, upload2;
	HttpEntity resEntity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tv = (TextView) findViewById(R.id.tv);
		res = (TextView) findViewById(R.id.res);
		tv.setText(tv.getText() + selectedPath1 + "," + selectedPath2);
		b1 = (Button) findViewById(R.id.Button01);
		b2 = (Button) findViewById(R.id.Button02);
		b3 = (Button) findViewById(R.id.upload);
		upload2 = (Button) findViewById(R.id.upload2);
		b1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openGallery(SELECT_FILE1);
			}
		});
		b2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openGallery(SELECT_FILE2);
			}
		});
		b3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!(selectedPath1.trim().equalsIgnoreCase("NONE"))
						&& !(selectedPath2.trim().equalsIgnoreCase("NONE"))) {
					progressDialog = ProgressDialog.show(MainActivity.this, "",
							"Uploading files to server.....", false);
					Thread thread = new Thread(new Runnable() {
						public void run() {
							doFileUpload();
							runOnUiThread(new Runnable() {
								public void run() {
									if (progressDialog.isShowing())
										progressDialog.dismiss();
								}
							});
						}
					});
					thread.start();
				} else {
					Toast.makeText(getApplicationContext(),
							"Please select two files to upload.",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
		upload2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!(selectedPath1.trim().equalsIgnoreCase("NONE"))
						&& !(selectedPath2.trim().equalsIgnoreCase("NONE"))) {
					progressDialog = ProgressDialog.show(MainActivity.this, "",
							"Uploading files to server.....", false);
					Thread thread = new Thread(new Runnable() {
						public void run() {
							doFileUploadBase64();
							runOnUiThread(new Runnable() {
								public void run() {
									if (progressDialog.isShowing())
										progressDialog.dismiss();
								}
							});
						}
					});
					thread.start();
				} else {
					Toast.makeText(getApplicationContext(),
							"Please select two files to upload.",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

	}

	public void openGallery(int req_code) {

		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(
				Intent.createChooser(intent, "Select file to upload "),
				req_code);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			Uri selectedImageUri = data.getData();
			if (requestCode == SELECT_FILE1) {
				selectedPath1 = getPath(selectedImageUri);
				System.out.println("selectedPath1 : " + selectedPath1);
			}
			if (requestCode == SELECT_FILE2) {
				selectedPath2 = getPath(selectedImageUri);
				System.out.println("selectedPath2 : " + selectedPath2);
			}
			tv.setText("Selected File paths : " + selectedPath1 + ","
					+ selectedPath2);
		}
	}

	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	private void doFileUpload() {

		File file1 = new File(selectedPath1);
		File file2 = new File(selectedPath2);
		String urlString = "https://10.0.2.2/upload_server/upload.php";
		try {
			HttpClient client = getNewHttpClient();
			HttpPost post = new HttpPost(urlString);
			FileBody bin1 = new FileBody(file1);
			FileBody bin2 = new FileBody(file2);
			MultipartEntity reqEntity = new MultipartEntity();
			reqEntity.addPart("uploadedfile1", bin1);
			reqEntity.addPart("uploadedfile2", bin2);
			reqEntity.addPart("user", new StringBody("User"));
			post.setEntity(reqEntity);
			HttpResponse response = client.execute(post);
			resEntity = response.getEntity();
			final String response_str = EntityUtils.toString(resEntity);
			if (resEntity != null) {
				Log.i("RESPONSE", response_str);
				runOnUiThread(new Runnable() {
					public void run() {
						try {
							res.setTextColor(Color.GREEN);
							res.setText("n Response from server : n "
									+ response_str);
							Toast.makeText(
									getApplicationContext(),
									"Upload Complete. Check the server uploads directory.",
									Toast.LENGTH_LONG).show();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		} catch (Exception ex) {
			Log.e("Debug", "error: " + ex.getMessage(), ex);
		}
	}

	private void doFileUploadBase64() {

		File file1 = new File(selectedPath1);
		File file2 = new File(selectedPath2);
		String strFile1 = "";
		String strFile2 = "";
		byte[] data1;
		try {
			data1 = loadFile(file1);
			strFile1 = Base64.encodeToString(data1, Base64.NO_WRAP);
			byte[] data2 = loadFile(file1);
			strFile2 = Base64.encodeToString(data2, Base64.NO_WRAP);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String urlString = "https://10.0.2.2/upload_server/upload_b64.php";
		try {
			JSONObject jObj = new JSONObject();
			
			JSONArray jArr = new JSONArray();
			jArr.put(0, strFile1);
			jArr.put(1, strFile2);
			jObj.put("images", jArr);
			

			HttpRequest obj = new HttpRequest();
			obj.postData(urlString, jObj);
		} catch (Exception ex) {
			Log.e("Debug", "error: " + ex.getMessage(), ex);
		}
	}

	private static byte[] loadFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);

		long length = file.length();
		if (length > Integer.MAX_VALUE) {
			// File is too large
		}
		byte[] bytes = new byte[(int) length];
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "
					+ file.getName());
		}

		is.close();
		return bytes;
	}

	public HttpClient getNewHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}
}