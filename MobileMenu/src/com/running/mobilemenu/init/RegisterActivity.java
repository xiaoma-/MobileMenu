package com.running.mobilemenu.init;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.running.mobilemenu.R;
import com.running.mobilemenu.utils.BaseActivity;
import com.running.mobilemenu.utils.ServiceHttpPost;


public class RegisterActivity extends BaseActivity implements OnClickListener{
	
	private LinearLayout inPhone;
	private LinearLayout inAuthCode;
	private LinearLayout authButtons;
	private LinearLayout inUser;
	private Button button; // 提交按钮
	private EditText phoneText; // 手机号码输入框
	private EditText authCode; // 验证码输入框
	private String phoneNo; // 手机号码
	private String authCodeStr; // 验证码
	private TextView textPhone;
	private Button sendAuthBtn; // 校验按钮
	private Button getAuthBtn; // 重新发送按钮
	private static String TAG = "UserRegisterActivity";
	private ProgressDialog progressDialog; // 进度条
	private Runnable runnable;// 处理线程
	private String jsonString;
	//private ProgressDialog progressDialog; // 进度条
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 采用无标题模式
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		upviews();
		// init();
	}

	private void upviews() {
		setContentView(R.layout.register_activity);
		inPhone = (LinearLayout) findViewById(R.id.inPhone);
		inAuthCode = (LinearLayout) findViewById(R.id.inAuthCode);
		authButtons = (LinearLayout) findViewById(R.id.authButtons);
		textPhone = (TextView) findViewById(R.id.textPhone);
		button = (Button) findViewById(R.id.phoneBtn);
		sendAuthBtn = (Button) findViewById(R.id.sendAuthBtn);
		getAuthBtn = (Button) findViewById(R.id.getAuthBtn);
		phoneText = (EditText) findViewById(R.id.editPhone);
		authCode = (EditText) findViewById(R.id.authCode);
		phoneText.clearFocus();
		button.setOnClickListener(this);
		getAuthBtn.setOnClickListener(this);
		sendAuthBtn.setOnClickListener(this);
		findViewById(R.id.re_back_btn).setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub

		switch (view.getId()) {
		case R.id.phoneBtn: {
			attemptLogin(1);
			break;
		}
		case R.id.getAuthBtn: {
			Toast.makeText(getApplicationContext(), "重新获得验证码!",
					Toast.LENGTH_LONG).show();
			attemptLogin(1);
			break;
		}
		case R.id.sendAuthBtn: {

			attemptLogin(2);
			break;
		}
		case R.id.re_back_btn: {
			// 返回键
			Intent intent = new Intent(RegisterActivity.this,
					LoginActivity.class);
			RegisterActivity.this.startActivity(intent);
			this.finish();
			break;
		}
		default:
			break;
		}

	}

	private void init() {
		phoneText
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						// TODO Auto-generated method stub
						return false;
					}

				});

	}

	/**
	 * 试图登录或注册指定帐号登录表单。 如果有形式错误(无效的字段,等等), 提出了错误和没有实际的登录尝试。
	 */
	public void attemptLogin(int num) {
		// Reset errors.
		phoneText.setError(null);
		authCode.setError(null);
		// Store values at the time of the login attempt.
		phoneNo = phoneText.getText().toString();
		authCodeStr = authCode.getText().toString();
		boolean cancel = false;
		View focusView = null;

		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(phoneNo);

		// Check for a valid password.
		if (TextUtils.isEmpty(phoneNo)) {
			phoneText.setError("手机号码不能为空！");
			focusView = phoneText;
			cancel = true;
		} else if (!m.matches()) {
			phoneText.setError("手机号码格式不正确！");
			focusView = phoneText;
			cancel = true;
		}
		if (num == 2) {
			if (TextUtils.isEmpty(authCodeStr)) {
				authCode.setError("验证码不能为空！");
				focusView = authCode;
				cancel = true;
			}
		}
		if (cancel) {
			focusView.requestFocus();
		} else {
			toService(num);
			Log.d(TAG, "jsonString:" + jsonString);

		}
	}

	/* 定义一个倒计时的内部类 */
	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			getAuthBtn.setText("重新验证码");
			getAuthBtn.setBackgroundResource(R.drawable.user_button);
			getAuthBtn.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			getAuthBtn.setClickable(false);
			getAuthBtn.setText("重获【" + millisUntilFinished / 1000 + "】秒");
			getAuthBtn.setBackgroundResource(R.drawable.time_user_button);
		}
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if (progressDialog != null)
					progressDialog.dismiss();
				authString();
				break;
			case 2:
				if ("true".equals(jsonString)) {
					Intent intent = new Intent();
					intent.setClass(RegisterActivity.this,
							RegisterMessageActivity.class);
					intent.putExtra("phoneNo", phoneNo);
					startActivity(intent);
				} else {
					progressDialog.dismiss();
					Builder builder = new AlertDialog.Builder(
							RegisterActivity.this);
					builder.setMessage("请检查验证码或重新获取！");
					builder.setPositiveButton("确定", null);
					builder.show();
				}
				break;
			}
		}
	};

	private void authString() {
		if ("exist".equals(jsonString)) {
			// 关闭ProgressDialog
			// progressDialog.dismiss();
			new AlertDialog.Builder(this).setMessage("你的手机号码已经注册过了请直接登录！")
					.setPositiveButton("确定", null).show();
		} else {
			// 关闭ProgressDialog
			// progressDialog.dismiss();

			new AlertDialog.Builder(this)
					.setMessage("验证码已发送，请注意查收，一分钟后未获的请重新获得！")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									inPhone.setVisibility(View.GONE);
									inAuthCode.setVisibility(View.VISIBLE);
									authButtons.setVisibility(View.VISIBLE);
									new TimeCount(60000, 1000).start();
									textPhone.setText("手机号码：" + phoneNo);

									// 显示验证码
									// authCode.setVisibility(View.VISIBLE);
								}
							}).show();
		}

	}

	private void initProgressDialog() {
		progressDialog = new ProgressDialog(this);// 生成一个进度条
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("正在请求...");
		progressDialog.show();
		mHandler.postDelayed(runnable, 1);
	}

	private void toService(final int num) {
		 initProgressDialog();
		Thread mThread = new Thread() {

			public void run() {
				
				try {
					if (num == 1) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("method", "createVerifyCode");
						map.put("telphone", phoneNo);
						map.put("authCode", "0");
						jsonString = ServiceHttpPost.GetMessageString(map);
					} else if (num == 2) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("method", "createVerifyCode");
						map.put("telphone", phoneNo);
						map.put("authCode", authCode.getText().toString());
						jsonString = ServiceHttpPost.GetMessageString(map);
					}
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Message message = new Message();
				message.what = num;
				mHandler.sendMessage(message);
			}

		};
		mThread.start();
	}
}
