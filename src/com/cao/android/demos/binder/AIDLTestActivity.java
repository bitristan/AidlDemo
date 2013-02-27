/*****************Copyright (C), 2010-2015, FORYOU Tech. Co., Ltd.********************/
package com.cao.android.demos.binder;

import java.text.MessageFormat;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.cao.android.demos.binder.aidl.AIDLActivity;
import com.cao.android.demos.binder.aidl.AIDLService;
import com.cao.android.demos.binder.aidl.Rect1;

/**
 * @Filename: AIDLTestActivity.java
 * @Author: slcao
 * @CreateDate: 2011-5-16
 * @Description: description of the new class
 * @Others: comments
 * @ModifyHistory:
 */
public class AIDLTestActivity extends Activity {
	private Button btnOk;
	private Button btnCancel;
	private Button btnCallBack;


	private void Log(String str) {
		Log.d(Constant.TAG, "------ " + str + "------");
	}

	private AIDLActivity mCallback = new AIDLActivity.Stub() {
//		public void performAction() throws RemoteException {
//			Log("AIDLActivity.performAction");
//			Toast.makeText(AIDLTestActivity.this, "this toast is called from service", 1).show();
//		}

		@Override
		public void performAction(Rect1 rect) throws RemoteException {
			Log("AIDLActivity.performAction");
			Log(MessageFormat.format("rect[bottom={0},top={1},left={2},right={3}]", rect.bottom,rect.top,rect.left,rect.right));
			Toast.makeText(AIDLTestActivity.this, "this toast is called from service", 1).show();
		
		}
	};

	AIDLService mService;
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log("connect service");
			mService = AIDLService.Stub.asInterface(service);
			try {
				mService.registerTestCall(mCallback);
			} catch (RemoteException e) {

			}
		}


		public void onServiceDisconnected(ComponentName className) {
			Log("disconnect service");
			mService = null;
		}
	};


	@Override
	public void onCreate(Bundle icicle) {
		Log("AIDLTestActivity.onCreate");
		super.onCreate(icicle);
		setContentView(R.layout.aidl_activity);
		btnOk = (Button) findViewById(R.id.btn_ok);
		btnCancel = (Button) findViewById(R.id.btn_cancel);
		btnCallBack = (Button) findViewById(R.id.btn_call_back);
		btnOk.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log("AIDLTestActivity.btnOk");
				Bundle args = new Bundle();
				Intent intent = new Intent();
				intent.setAction("com.cao.android.demos.binder.AIDLTestService");
				intent.putExtras(args);
				bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
				startService(intent);
			}
		});
		btnCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Log("AIDLTestActivity.btnCancel");
				unbindService(mConnection);
				// stopService(intent);
			}
		});
		btnCallBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log("AIDLTestActivity.btnCallBack");
				try {
					mService.invokCallBack();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
}
