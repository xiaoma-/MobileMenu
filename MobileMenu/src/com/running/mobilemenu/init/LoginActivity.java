package com.running.mobilemenu.init;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.running.mobilemenu.R;
import com.running.mobilemenu.dao.impl.FoodDaoImpl;
import com.running.mobilemenu.dao.impl.FoodImageDaoImpl;
import com.running.mobilemenu.dao.impl.FoodTypeDaoImpl;
import com.running.mobilemenu.dao.impl.UserDaoImpl;
import com.running.mobilemenu.meals.NewOpenTabActivity;
import com.running.mobilemenu.model.Food;
import com.running.mobilemenu.model.FoodImage;
import com.running.mobilemenu.model.User;
import com.running.mobilemenu.utils.BaseActivity;
import com.running.mobilemenu.utils.DBHelper;
import com.running.mobilemenu.utils.ImageDownload;
import com.running.mobilemenu.utils.MyApplication;
import com.running.mobilemenu.utils.ServiceHttpPost;
import com.running.update.UpdateService;
import com.tgb.lk.ahibernate.util.TableHelper;

/**
 * @className LoginActivity
 * @author xiaoma
 * @Description 用户登录
 * @date 2013-3-13 下午4:56:31
 */
public class LoginActivity extends BaseActivity implements OnClickListener,
		OnItemClickListener, OnDismissListener {

	private ProgressDialog progressDialog; // 进度条
	private String mEmail;
	private String mPassword;

	private EditText mEmailView;
	private EditText mPasswordView;
	private Map jsonString;
	private FoodDaoImpl foodsDao;// 菜品数据数据源
	private FoodImageDaoImpl foodImageDao;// 菜品数据数据源
	private FoodTypeDaoImpl foodTypeDao;// 菜品数据数据源
	private ProgressDialog mSaveDialog = null;// 更新进度显示
	private String saveMessage;

	private ArrayList<User> mList = new ArrayList<User>();
	private List<Map<String, String>> userMap = new ArrayList<Map<String, String>>();
	private ArrayList<String> userStr = new ArrayList<String>();
	private PopupWindow mPopup;

	private boolean mShowing;
	private ArrayAdapter<String> mAdapter;
	private ListView mListView;
	private boolean mInitPopup;

	private UserDaoImpl userDao;
	private CheckBox view_rememberMe;
	private boolean flg = true;
	private String groupId;//更新批次号

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		foodsDao = new FoodDaoImpl(LoginActivity.this);
		userDao = new UserDaoImpl(LoginActivity.this);
		foodImageDao = new FoodImageDaoImpl(LoginActivity.this);
		foodTypeDao = new FoodTypeDaoImpl(LoginActivity.this);
		init();
		setContentView(R.layout.login_activity);
		upView();
		isNetworkConnected(this);

	}

	public void init() {
		mList = (ArrayList<User>) userDao.find();
		for (User user : mList) {
			Map map = new HashMap<String, String>();
			map.put(user.getUserName(), user.getPrassWord());
			map.put("userName", user.getUserName());
			userStr.add(user.getUserName());
			userMap.add(map);
		}
	}

	public void upView() {
		view_rememberMe = (CheckBox) findViewById(R.id.loginCheckBox);
		mPasswordView = (EditText) findViewById(R.id.password);
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.clearFocus();
		mPasswordView.clearFocus();
		if (userMap.size() > 0) {
			String user = userMap.get(0).get("userName").toString();
			String pwd = userMap.get(0).get(user).toString();
			mEmailView.setText(user);
			if (!"".equals(pwd)) {
				mPasswordView.setText(pwd);
				view_rememberMe.setChecked(true);
				flg = false;
			}
		}

		Button mSignButtonView = (Button) findViewById(R.id.sign_in_button);
		mSignButtonView.setFocusable(true);
		// 如果密码也保存了,则直接让登陆按钮获取焦点
		if (mPasswordView.getText().toString().length() > 0) {
			mSignButtonView.requestFocus();
		}

		findViewById(R.id.login_show).setOnClickListener(this);
		mSignButtonView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
		});
		mEmailView.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (count <= 0) {
					mPasswordView.setText("");
					view_rememberMe.setChecked(false);
				}

			}
		});
	}

	private boolean isRememberMe() {
		if (view_rememberMe.isChecked()) {
			return true;
		}
		return false;
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// super.onCreateOptionsMenu(menu);
	// getMenuInflater().inflate(R.menu.login_activity, menu);
	// return true;
	// }

	/**
	 * @Title: attemptLogin
	 * @Description: 用户名、密码格式验证
	 * @param
	 * @return void
	 * @throws
	 */
	public void attemptLogin() {

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_user_required));
			focusView = mEmailView;
			cancel = true;
		} else if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_pwd_required));
			focusView = mPasswordView;
			cancel = true;
		}

		if (cancel) {
			focusView.requestFocus();
		} else {

			toService();
		}
	}
	private void addUser(){
		User user = new User();
		user.setUserName(mEmail);
		String sql = "select * from user_info where user_name = ?";
		if (isRememberMe()) {
			user.setPrassWord(mPassword);
			List<User> l = userDao.rawQuery(sql, new String[] { mEmail });
			if (l.size() > 0) {
				l.get(0).setPrassWord(mPassword);
				userDao.update(l.get(0));
			} else {
				userDao.insert(user);
			}
		} else {
			user.setPrassWord(null);
			List<User> l = userDao.rawQuery(sql, new String[] { mEmail });
			if (l.size() == 0) {
				userDao.insert(user);
			}
		}
	}
	/**
	 * @Title: toService
	 * @Description: 联网请求鉴权
	 * @param
	 * @return void
	 * @throws
	 */
	public String result;

	private void toService() {
		progressDialog = ProgressDialog.show(LoginActivity.this, "", "正在请求...",
				true);
		Thread thread = new Thread() {
			Message message = new Message();
			public void run() {
				try {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("method", "loginAuth");
					map.put("userName", mEmail);
					map.put("passWord", mPassword);
					map.put("clientId", MyApplication.clientId);
					map.put("systemName", MyApplication.clientName);
					map.put("systemVersion", MyApplication.clientSystemName);
					map.put("versionNum", String.valueOf(MyApplication.localVersion));
					map.put("versionName", MyApplication.localName);
					if (isNetworkConnected(LoginActivity.this)) {
						result = ServiceHttpPost.httpPost(map);
					} else {
						result = "900";
					}

				} catch (Exception e) {
					// TODO: handle exception
					message.what = 4;
					mHandler.sendMessage(message);
					e.printStackTrace();
				}
				
				if ("300".equals(result)||"".equals(result)||null==result) {
					message.what = 4;
				} else if ("900".equals(result)) {
					message.what = 5;
				} else if("504".equals(result)){
					message.what = 4;
				}else {
					jsonString = ServiceHttpPost.GetMessageMap(result);
					if (null != jsonString) {
						String type = (String) jsonString.get("type");
						System.out.println("type: " + type);
						if ("success".equals(type)) {
							addUser();
							message.what = 1;
						} else if ("update".equals(type)) {
							addUser();
							message.what = 2;
						} else {
							message.what = 3;
						}
					}
				}

				mHandler.sendMessage(message);
			};
		};
		thread.start();
	}

	/**
	 * @Description: 验证通过业务处理
	 */
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				// 更新完列表数据，则关闭对话框
				progressDialog.dismiss();
				Intent intent = new Intent(LoginActivity.this,
						NewOpenTabActivity.class);
				LoginActivity.this.startActivity(intent);
				MyApplication.userName = mEmail;
				break;
			case 2:
				// 菜品数据有更新
				progressDialog.dismiss();
				getlist();
				MyApplication.userName = mEmail;
				break;
			case 3:

				// 更新完列表数据，则关闭对话框
				progressDialog.dismiss();
				message(1);
				break;
			case 4:
				// 更新完列表数据，则关闭对话框
				progressDialog.dismiss();
				message(2);
				break;
			case 5:
				// 更新完列表数据，则关闭对话框
				progressDialog.dismiss();
				message(3);
				break;
			}
		}
	};

	// 更新菜品数据
	public void getlist() {
		mSaveDialog = new ProgressDialog(LoginActivity.this);
		mSaveDialog.setTitle("更新菜品数据");
		mSaveDialog.setMessage("数据正在下载中，请稍等...");
		// 进度条是否不明确
		mSaveDialog.setIndeterminate(false);
		mSaveDialog.setCancelable(true);
		mSaveDialog.show();
		new Thread(contentRun).start();
	}

	// public List<FoodImage> images = new ArrayList<FoodImage>();
	// 访问服务器下载数据
	private Runnable contentRun = new Runnable() {

		@Override
		public void run() {
			if (jsonString.containsKey("groupId")) groupId = (String) jsonString.get("groupId");
			if (jsonString.containsKey("append")) {
				List<Food> lists = JSON.parseArray(jsonString.get("append")
						.toString(), Food.class);

				for (Food food : lists) {
					if (null != food) {
						new Mypic(food.getPictureURL()).start();
						System.out.println("-----edit" + food.getPictureURL());
						foodsDao.insert(food, false);
					}
				}
			}
			if (jsonString.containsKey("edit")) {
				List<Food> lists = JSON.parseArray(jsonString.get("edit")
						.toString(), Food.class);

				for (Food food : lists) {
					if (null != food) {
						new Mypic(food.getPictureURL()).start();
						System.out.println("-----edit" + food.getPictureURL());
						foodsDao.delete(food.getSid());
						foodsDao.insert(food, false);
					}
				}
			}
			if (jsonString.containsKey("delete")) {
				List<Food> lists = JSON.parseArray(jsonString.get("delete")
						.toString(), Food.class);
				for (Food food : lists) {
					if (null != food) {
						foodsDao.delete(food.getSid());
					}
				}
			}
			if (jsonString.containsKey("image")) {
				List<FoodImage> images = JSON.parseArray(jsonString
						.get("image").toString(), FoodImage.class);
				new Thread(new PictureRun(images)).start();
				for (FoodImage image : images) {
					if (null != image) {
						foodImageDao.insert(image, false);
					}
				}

			}

		}

	};

	class Mypic extends Thread {
		private String imageStr;

		public Mypic(String imageStr) {
			this.imageStr = imageStr;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			try {
				if (!"".equals(imageStr)) {
					try {
						new ImageDownload().saveFile(MyApplication.ImageUrl
								+ imageStr);
						Log.d("ImageDownload", "单个图片下载图片成功[" + imageStr + " , "
								+ MyApplication.ImageUrl + imageStr + "]");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						Log.e("ImageDownload", "单个图片下载图片失败[" + imageStr + " , "
								+ MyApplication.ImageUrl + imageStr + "]");
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		}
	}

	// 线程下载图片
	class PictureRun implements Runnable {
		private List<FoodImage> images;

		public PictureRun(List<FoodImage> images) {
			this.images = images;
		}

		@Override
		public void run() {
			try {
				if (images.size() > 0) {
					for (FoodImage foodImage : images) {
						if (null != foodImage) {
							try {
								new ImageDownload()
										.saveFile(MyApplication.ImageUrl
												+ foodImage.getImageUrl());
								Log.d("ImageDownload",
										"下载图片成功[" + foodImage.getId() + " , "
												+ MyApplication.ImageUrl
												+ foodImage.getImageUrl() + "]");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								Log.e("ImageDownload",
										"下载图片失败[" + foodImage.getId() + " , "
												+ MyApplication.ImageUrl
												+ foodImage.getImageUrl() + "]");
								e.printStackTrace();
							}
						}
					}
				}
				saveMessage = "图片保存成功";

			} catch (Exception e) {
				// TODO Auto-generated catch block
				saveMessage = "图片保存失败";
				e.printStackTrace();
			}
			threadHistory.start();
			Message message = new Message();
			message.what = 1;
			messageHandler.sendMessage(messageHandler.obtainMessage());
		}

	};
	Thread threadHistory = new Thread() {
		public void run() {
			try {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("method", "updateHistory");
				map.put("userName", mEmail);
				map.put("clientId", MyApplication.clientId);
				map.put("type", "success");
				map.put("groupId", groupId);
				if (isNetworkConnected(LoginActivity.this)) {
					result = ServiceHttpPost.httpPost(map);
				} else {
					result = "900";
				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	};
	private Handler messageHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mSaveDialog.dismiss();
			Intent intent = new Intent(LoginActivity.this,
					NewOpenTabActivity.class);
			LoginActivity.this.startActivity(intent);
		}
	};

	private void message(int i) {

		String str = "用户名或密码输入有误！";
		if (2 == i)
			str = "网络异常请求超时！";
		if (3 == i)
			str = "网络异常请检查网络！";
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.login_show) {
			if (userStr != null && userStr.size() > 0 && !mInitPopup) {
				mInitPopup = true;
				initPopup();
			}
			if (mPopup != null) {
				if (!mShowing) {
					mPopup.showAsDropDown(mEmailView, 0, -5);
					mShowing = true;
				} else {
					mPopup.dismiss();
				}
			}
		}
	}

	private void initPopup() {
		mAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line, userStr);
		mListView = new ListView(this);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
		int height = ViewGroup.LayoutParams.WRAP_CONTENT;
		int width = findViewById(R.id.login_uesr).getWidth();
		System.out.println(width);
		mPopup = new PopupWindow(mListView, width, height, true);
		mPopup.setOutsideTouchable(true);
		mPopup.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.login_botton_press));
		mPopup.setOnDismissListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		String name = userStr.get(position);
		mEmailView.setText(name);
		boolean flag = true;
		for (Map map : userMap) {
			if (name.equals(map.get("userName")) && null != map.get("userName")) {
				if (null != map.get(name) && !"".equals(map.get(name))) {
					mPasswordView.setText(map.get(name).toString());
					flag = false;
				}

				break;
			}
		}
		if (flag) {
			mPasswordView.setText("");
			view_rememberMe.setChecked(false);
		}
		mPopup.dismiss();
	}

	@Override
	public void onDismiss() {
		mShowing = false;
	}

	public void register_user(View view) {
		// Intent intent = new
		// Intent(LoginActivity.this,RegisterActivity.class);
		// LoginActivity.this.startActivity(intent);
		Builder alert = new AlertDialog.Builder(this);
		switch (view.getId()) {
		case R.id.register_user:
			alert.setMessage("是否同意短信开通业务？");
			alert.setPositiveButton("同意", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					sendSms("KTGJ");
				}
			});
			alert.setNegativeButton("取消", null).show();
			break;
		case R.id.longin_get_pwd:
			alert.setMessage("是否同意短信找回密码？");
			alert.setPositiveButton("同意", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					sendSms("ZHMM");
				}
			});
			alert.setNegativeButton("取消", null).show();
			break;
		default:
			break;
		}
		
	}

	private void sendSms(String smsContent) {
		String SENT_SMS_ACTION = "SENT_SMS_ACTION";
		PendingIntent mPI = PendingIntent.getBroadcast(getApplicationContext(),
				0, new Intent(SENT_SMS_ACTION), 0);
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage("10657023001188", null, smsContent, mPI, null);// 发送信息到指定号码

		getApplicationContext().registerReceiver(new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				switch (getResultCode()) {
				case Activity.RESULT_OK:
					Toast.makeText(context, "短信发送成功", Toast.LENGTH_SHORT)
							.show();
					break;
				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					break;
				case SmsManager.RESULT_ERROR_RADIO_OFF:
					break;
				case SmsManager.RESULT_ERROR_NULL_PDU:
					break;
				}
			}
		}, new IntentFilter(SENT_SMS_ACTION));
	}

	
}
