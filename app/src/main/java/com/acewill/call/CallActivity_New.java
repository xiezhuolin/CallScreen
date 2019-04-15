package com.acewill.call;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;

public class CallActivity_New extends Activity {
	private String    url;
	// private MyAdapter callAdapter;
	private MyAdapter readyAdapter;
	private MyAdapter doneAdapter;
	private String    directory;
	private int loopCount = 0;
	private MediaPlayer mediaPlayer;
	private ArrayList<String> callIds = new ArrayList<String>();
	private TextView  tv_call;
	private boolean   isRun;
	private long      lastTime;
	private int       loop;
	private int       sex;
	private int       speed;
	private SoundPool soundPool;
	private boolean   isPlay;
	private Map<String, Long> map = new HashMap<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_new);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		Intent intent = getIntent();
		url = "http://" + intent.getStringExtra("url");
		loop = intent.getIntExtra("loop", 1);
		sex = intent.getIntExtra("sex", 0);
		speed = intent.getIntExtra("speed", 0);
		loopCount = loop;
		initView();
		isRun = true;
		lastTime = System.currentTimeMillis();
		directory = Environment.getExternalStorageDirectory() + "/acewill/";
		File file = new File(directory);
		if (!file.exists()) {
			file.mkdir();
		}
		// new Thread()
		// for (File fileItme : file.listFiles()) {
		// fileItme.delete();
		// }
		new Thread() {
			public void run() {
				while (isRun) {
					getOrder();
					SystemClock.sleep(3000);
				}
			}

			;
		}.start();
		try {
			initSoundPool();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Log.e(TAG, "method>handleMessage");
				if (loopCount > 0) {
					playSoundPool(String.valueOf(msg.what));
				} else {
					playNext();
				}
			}
		};

	}

	private void initSoundPool() throws IOException {
		soundPool = new SoundPool(12, AudioManager.STREAM_RING, 1);
		soundPool.load(getAssets().openFd("sound/female/1/1.mp3"), 1);
		soundPool.load(getAssets().openFd("sound/female/1/2.mp3"), 1);
		soundPool.load(getAssets().openFd("sound/female/1/3.mp3"), 1);
		soundPool.load(getAssets().openFd("sound/female/1/4.mp3"), 1);
		soundPool.load(getAssets().openFd("sound/female/1/5.mp3"), 1);
		soundPool.load(getAssets().openFd("sound/female/1/6.mp3"), 1);
		soundPool.load(getAssets().openFd("sound/female/1/7.mp3"), 1);
		soundPool.load(getAssets().openFd("sound/female/1/8.mp3"), 1);
		soundPool.load(getAssets().openFd("sound/female/1/9.mp3"), 1);
		soundPool.load(getAssets().openFd("sound/female/1/0.mp3"), 1);
		soundPool.load(getAssets().openFd("sound/female/1/prefix.mp3"), 1);
		soundPool.load(getAssets().openFd("sound/female/1/tailfix.mp3"), 1);

	}

	private Handler handler;
	private ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

	private void playSoundPool(final String callNo) {
		Log.e(TAG, "method>playSoundPool");

		singleThreadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				Log.e(TAG, "ThreadName" + Thread.currentThread().getName());
				playSound(callNo);
			}
		});
		//		new Thread() {
		//			public void run() {
		//				playSound(callNo);
		//			}
		//
		//			;
		//		}.start();
		//
	}

	private void playSound(final String callNo) {
		Log.e(TAG, "method>playSound" + ",callNo>" + callNo);
		Log.e("play:", callNo);
		isPlay = true;
		int priority = 100;
		soundPool.play(11, 1, 1, priority, 0, 1);
		try {
			Thread.sleep(800);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		priority--;
		char[] cc = callNo.toCharArray();
		for (char c : cc) {
			int numericValue = Character.getNumericValue(c);
			if (numericValue == 0) {
				soundPool.play(10, 1, 1, priority, 0, 1);
			} else {
				soundPool.play(numericValue, 1, 1, priority, 0, 1);
			}
			priority--;
			try {
				Thread.sleep(800);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		soundPool.play(12, 1, 1, priority, 0, 1);
		try {
			Thread.sleep(2500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		isPlay = false;
		loopCount--;

		try {
			handler.sendEmptyMessage(Integer.valueOf(callNo));
		} catch (NumberFormatException e) {
			handler.sendEmptyMessage(Integer.valueOf(callNo));
			e.printStackTrace();
		}

	}

	//	private char[] mCharsLowCase = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
	//			'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B',
	//			'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
	//			'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
	//	private char[] mCharsUpCase  = new char[]{};

	private void initView() {
		GridView lv_ready = (GridView) findViewById(R.id.lv_ready);
		GridView lv_done  = (GridView) findViewById(R.id.lv_done);
		tv_call = (TextView) findViewById(R.id.tv_call);

		// callAdapter = new MyAdapter();
		readyAdapter = new MyAdapter();
		doneAdapter = new MyAdapter();

		// lv_call.setAdapter(callAdapter);
		lv_ready.setAdapter(readyAdapter);
		lv_done.setAdapter(doneAdapter);
	}

	class MyAdapter extends BaseAdapter {
		private ArrayList<String> data = new ArrayList<String>();

		public void setData(ArrayList<String> data) {
			this.data = data;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(CallActivity_New.this,
						R.layout.lv_item_new, null);
			}
			TextView tv_number = (TextView) convertView
					.findViewById(R.id.tv_number);
			tv_number.setText(data.get(position));
			return convertView;
		}

	}

	private void getOrder() {
		OkHttpUtils
				.get()
				.url(url + "/AcewillKDS/getOrderWorkingInfo.do")
				.build()
				.execute(
						new GenericsCallback<OrderModel>(
								new JsonGenericsSerializator()) {

							@Override
							public void onError(Call call, Exception e, int id) {
								e.printStackTrace();
							}

							@Override
							public void onResponse(OrderModel response, int id) {
								Log.e(TAG, "resposne>>" + response);
								if (response.success && isRun) {
									readyAdapter
											.setData(response.workingOrders);
									doneAdapter
											.setData(response.finishedOrders);
									for (OrderModel.CallOrder item : response.callOrders) {
										Long aLong = map.get(item.fetchID);
										//判断有没有，如果有的话，就判断时间是否相同，如果相同就不叫了，如果id相同，时间不同，则认为是不同的两个号
										if (aLong != null && aLong == item.callTime) {
											//存在，但是时间相同
											//不再重复添加
										} else {
											//不存在,或者已存在，但是时间不同
											map.put(item.fetchID, item.callTime);
											callIds.add(item.fetchID);
											Log.e(TAG, "map>>" + map);
											Log.e(TAG, "callIds>" + callIds);
										}
									}
									playNext();
								}
								if (response.success && response.workingOrders != null && response.workingOrders
										.size() == 0 && response.finishedOrders != null &&
										response.finishedOrders
												.size() == 0 && response.callOrders != null && response.callOrders
										.size() == 0)
									tv_call.setText("");
							}
						});
	}

	private static final String TAG = "CallActivity_New";

	private void playNext() {

		// if (mediaPlayer == null) {
		if (!isPlay && callIds.size() > 0) {
			loopCount = loop;
			Log.e(TAG, "playNext" + ",callIds>" + callIds.get(0));
			tv_call.setText(callIds.get(0));
			if (callIds.get(0).length() >= 4) {
				tv_call.setTextSize(160);
			} else {
				tv_call.setTextSize(200);
			}

			// mediaPlayer = new MediaPlayer();
			// getSoundPath(callIds.get(0));
			// try {
			// File sound = CombineSoundController.getSound(this,
			// callIds.get(0), directory,sex,speed);
			// play(sound);
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// stopPlay();
			// }
			playSoundPool(callIds.get(0));
			callIds.remove(0);
		}
		// }
	}

	private void getSoundPath(String callid) {
		OkHttpUtils
				.post()
				.url(url + "/AcewillKDS/getSound.do")
				.addParams("fetchid", callid)
				.build()
				.execute(
						new GenericsCallback<SoundModel>(
								new JsonGenericsSerializator()) {

							@Override
							public void onError(Call call, Exception e, int id) {
							}

							@Override
							public void onResponse(SoundModel response, int id) {
								if (response.success) {
									loopCount = response.calltimes;
									// play(url + "/AcewillKDS/sound/combine/"
									// + response.sound);
									getSound(response.sound);
								}
							}
						});
	}

	private void getSound(String soundName) {
		OkHttpUtils.get().url(url + "/AcewillKDS/sound/combine/" + soundName)
				.build().execute(new FileCallBack(directory, soundName) {

			@Override
			public void onError(Call call, Exception e, int id) {

			}

			@Override
			public void onResponse(File response, int id) {
				play(response);
			}

		});
	}

	private void play(final File file) {

		try {
			mediaPlayer.setDataSource(new FileInputStream(file).getFD(), 0,
					file.length());
			mediaPlayer.prepareAsync();
			mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {
					// 装载完毕回调
					mediaPlayer.start();
				}

			});
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					loopCount--;
					if (loopCount <= 0) {
						System.out.println("stop play");
						stopPlay();
					} else {
						System.out.println("replay");
						replay();
					}
				}
			});
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void replay() {
		if (mediaPlayer != null && isRun) {
			mediaPlayer.start();
		}
	}

	private void play(String path) {
		mediaPlayer = new MediaPlayer();
		try {
			mediaPlayer.setDataSource(path);
			mediaPlayer.prepareAsync();
			// mediaPlayer.setLooping(true);
			mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer mp) {
					// 装载完毕回调
					replay();
				}

			});
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

				@Override
				public void onCompletion(MediaPlayer mp) {
					loopCount--;
					if (loopCount > 0) {
						replay();
					} else {
						stopPlay();
					}
				}
			});
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onDestroy() {
		isRun = false;
		stopPlay();
		super.onDestroy();
	}

	private void stopPlay() {
		if (callIds.size() > 0) {
			String remove = callIds.remove(0);
			deleteMp3();
		}
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();
			mediaPlayer = null;
		}
		loopCount = loop;
		playNext();
	}

	private void saveLastTime() {
		SharedPreferences sharedPreferences = getSharedPreferences("call",
				Context.MODE_PRIVATE);
		Editor edit = sharedPreferences.edit();
		edit.putLong("time", lastTime);
		edit.commit();
	}

	private void getLastTime() {
		SharedPreferences sharedPreferences = getSharedPreferences("call",
				Context.MODE_PRIVATE);
		lastTime = sharedPreferences.getLong("time", 0);
	}

	private void deleteMp3() {
		try {
			File[] list = new File(directory).listFiles();
			if (list != null && list.length > 0) {
				for (File file : list) {
					file.delete();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
