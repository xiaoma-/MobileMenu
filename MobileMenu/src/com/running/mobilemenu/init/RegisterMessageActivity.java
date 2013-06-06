package com.running.mobilemenu.init;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.running.mobilemenu.R;
import com.running.mobilemenu.meals.NewOpenTabActivity;
import com.running.mobilemenu.utils.ServiceHttpPost;

/**
 * @className RegisterMessageActivity
 * @author 用户注册验证注册码
 * @Description TODO
 * @date 2013-1-31 下午3:21:35
 */
public class RegisterMessageActivity extends Activity implements
		OnClickListener {
	private Button loginButton; // 登录按钮
	private EditText userName; // 用户名
	private EditText passWord; // 密码
	private TextView registerButton; // 注册按钮
	private String userNameString;// 填写用户名
	private String passWordString;// 填写密码
	private String telphone;
	private ProgressDialog progressDialog; // 进度条

	private String getString;// f服务器内用
	private View focusView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		telphone = (String) getIntent().getExtras().getString("phoneNo");
		// 采用无标题模式
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		upviews();
		// init();
	}

	private void upviews() {
		setContentView(R.layout.register_message_activity);
		userName = (EditText) findViewById(R.id.addPassWord1);
		passWord = (EditText) findViewById(R.id.addPassWord2);
		loginButton = (Button) findViewById(R.id.AddloginButton);
		ImageView yanzheng = (ImageView) findViewById(R.id.mag_back_btn);
		loginButton.setOnClickListener(this);
		yanzheng.setOnClickListener(this);
		// registerButton = (TextView) findViewById(R.id.loginRegiter);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.AddloginButton:
			attemptLogin();
			break;
		case R.id.mag_back_btn:
			Intent backintent = new Intent();
			backintent.setClass(RegisterMessageActivity.this,
					RegisterActivity.class);
			startActivity(backintent);
			break;
		default:
			break;
		}
	}

	private void init() {
		// 登录事件监听
		loginButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				attemptLogin();
				// Toast.makeText(getApplicationContext(), "重新获得验证码!",
				// Toast.LENGTH_LONG).show();
			}

		});

	}

	public void attemptLogin() {
		// 初始化
		userName.setError(null);
		passWord.setError(null);
		// 获得输入内容
		userNameString = userName.getText().toString();
		passWordString = passWord.getText().toString();
		boolean cancel = false;

		// 内容判断
		if (TextUtils.isEmpty(userNameString)) {
			userName.setError("密码不能为空！");
			focusView = userName;
			cancel = true;
		} else if (TextUtils.isEmpty(passWordString)) {
			passWord.setError("确认密码不能为空！");
			focusView = passWord;
			cancel = true;
		}else if (userNameString.equals(passWordString)) {
			passWord.setError("两次密码输入不一致！");
			focusView = passWord;
			cancel = true;
		}
		if (cancel) {
			focusView.requestFocus();
		} else {
			// 显示ProgressDialog
			toService();
		}
	}
	private void toService() {
		progressDialog = ProgressDialog.show(RegisterMessageActivity.this, "", "正在请求...", true);
		Thread mThread = new Thread() {
			
			public void run() {
				try {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("method", "createVerifyCode");
					map.put("telphone", telphone);
					map.put("authCode", "1");
					map.put("passWord", passWordString);
					getString = ServiceHttpPost.GetMessageString(map);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				Message message = new Message();
				message.what = 1;
				mHandler.sendMessage(message);
			};
		};
		mThread.start();
	}
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				progressDialog.dismiss();
				setDialog();
				break;
			}
		}
	};

	private void setDialog() {
		String jsonString = userNameString + "," + passWordString;
		if ("success".equals(getString)) {
			new AlertDialog.Builder(this)
					.setMessage("注册成功！" + jsonString)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									// 转向我的
									Intent intent = new Intent();
									intent.setClass(
											RegisterMessageActivity.this,
											NewOpenTabActivity.class);
									startActivity(intent);
									finish();

								}
							}).show();
		} 
	}
}
