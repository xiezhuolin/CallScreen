package com.acewill.call;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends Activity {

	private EditText       et_ip;
	private EditText       et_port;
	private EditText       et_loop;
	private ProgressDialog progressDialog;
	private RadioGroup     rg_sex;
	private RadioGroup     rg_speed;
	private RadioButton    rb_female;
	private RadioButton    rb_male;
	private RadioButton    rb_quick;
	private RadioButton    rb_slow;
	private TextView       version_tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		et_ip = (EditText) findViewById(R.id.et_ip);
		et_port = (EditText) findViewById(R.id.et_port);
		et_loop = (EditText) findViewById(R.id.et_loop);
		rg_sex = (RadioGroup) findViewById(R.id.rg_sex);
		rg_speed = (RadioGroup) findViewById(R.id.rg_speed);

		rb_female = (RadioButton) findViewById(R.id.rb_female);
		rb_male = (RadioButton) findViewById(R.id.rb_male);
		rb_quick = (RadioButton) findViewById(R.id.rb_quick);
		rb_slow = (RadioButton) findViewById(R.id.rb_slow);
		version_tv = (TextView) findViewById(R.id.version_tv);
		version_tv.setText(getAPPVersionName());
		findViewById(R.id.btn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				checkConnect("http://" + et_ip.getText().toString() + ":"
						+ et_port.getText().toString()
						+ "/AcewillKDS/callCustomer.html");
			}
		});
		getIp();
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			progressDialog.dismiss();
			if (msg.what == 0) {
				startWebActivity();
			} else {
				Toast.makeText(MainActivity.this, "连接服务器异常,请检查相关配置",
						Toast.LENGTH_LONG).show();
			}

		}

		;
	};

	public String getAPPVersionName() {
		int            currentVersionCode = 0;
		String         appVersionName     = "";
		PackageManager manager            = getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
			appVersionName = info.versionName; // 版本名
			currentVersionCode = info.versionCode; // 版本号
			System.out.println(currentVersionCode + " " + appVersionName);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return appVersionName;
	}

	private void getIp() {
		SharedPreferences sharedPreferences = getSharedPreferences("url",
				Context.MODE_PRIVATE);
		final String ip    = sharedPreferences.getString("ip", "192.168.1.");
		final String port  = sharedPreferences.getString("port", "8080");
		final int    loop  = sharedPreferences.getInt("loop", 1);
		final int    sex   = sharedPreferences.getInt("sex", 0);
		final int    speed = sharedPreferences.getInt("speed", 0);

		et_ip.setText(ip);
		et_port.setText(port);
		et_loop.setText(loop + "");
		if (sex == 0) {
			rb_female.setChecked(true);
		} else {
			rb_male.setChecked(true);
		}
		if (speed == 0) {
			rb_quick.setChecked(true);
		} else {
			rb_slow.setChecked(true);
		}

		checkConnect("http://" + ip + ":" + port
				+ "/AcewillKDS/callCustomer.html");
	}

	private void startWebActivity() {
		String url = et_ip.getText().toString() + ":"
				+ et_port.getText().toString();
		setIp(et_ip.getText().toString(), et_port.getText().toString());


		SharedPreferences sharedPreferences = getSharedPreferences("url",
				Context.MODE_PRIVATE);
		final int loop  = sharedPreferences.getInt("loop", 1);
		final int sex   = sharedPreferences.getInt("sex", 0);
		final int speed = sharedPreferences.getInt("speed", 0);

		Intent intent = new Intent(MainActivity.this, CallActivity_New.class);
		intent.putExtra("url", url);
		intent.putExtra("sex", sex);
		intent.putExtra("speed", speed);
		intent.putExtra("loop", loop);
		startActivity(intent);
	}

	private void setIp(String ip, String port) {
		SharedPreferences sharedPreferences = getSharedPreferences("url",
				Context.MODE_PRIVATE);
		Editor edit = sharedPreferences.edit();
		edit.putString("ip", ip);
		edit.putString("port", port);
		String string = et_loop.getText().toString();
		if (!TextUtils.isEmpty(string)) {
			edit.putInt("loop", Integer.valueOf(string));
		} else {
			edit.putInt("loop", 1);
		}
		if (rb_female.isChecked()) {
			edit.putInt("sex", 0);
		} else {
			edit.putInt("sex", 1);
		}
		if (rb_slow.isChecked()) {
			edit.putInt("speed", 1);
		} else {
			edit.putInt("speed", 0);
		}
		edit.commit();
	}

	private void checkConnect(final String path) {
		progressDialog = ProgressDialog.show(this, "正在连接服务器", "请稍等");
		new Thread() {
			public void run() {

				try {
					URL url = new URL(path);
					HttpURLConnection connection = (HttpURLConnection) url
							.openConnection();
					connection.setConnectTimeout(3000);
					connection.connect();
					if (connection.getResponseCode() == 200) {
						handler.sendEmptyMessage(0);
						return;
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				handler.sendEmptyMessage(1);
			}

			;
		}.start();

	}
}
