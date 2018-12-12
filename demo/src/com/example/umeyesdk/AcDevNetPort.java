package com.example.umeyesdk;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.stream.NewAllStreamParser;

public class AcDevNetPort extends Activity {
	String umid = "xmksit5p4dfu";// //xmumh9ckm0mu //umkss83g7brx
	TextView textshow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		textshow = new TextView(this);
		setContentView(textshow);
		setTitle("端口映射");
		textshow.setText("请稍后..\n");
		// String umid = getIntent().getStringExtra("umid");
		textshow.setText("测试UMID：" + umid + "\n请稍后..\n");
		String configPath = "//data//data//" + this.getPackageName() + "//" + "Config";
		NewAllStreamParser.sSaveModeDirName = configPath;

		new Thread() {

			@Override
			public void run() {
				int handle = NewAllStreamParser.DNPCreatePortServer(
						"app.umeye.cn", 8300, "sdktest", "sdktest");// //cloud.hzmr-tech.com
				if (handle == 0) {
					postMainThread("创建映射服务失败");
					return;
				} else
					postMainThread("创建映射服务成功");
				int state = NewAllStreamParser.DNPCheckSrvConnState(handle);
				int checkTimes = 0;
				try {
					while (state != 2) { // 2 为 已连接
						if (checkTimes >= 30) {
							postMainThread("连接超时。。");
							return;
						}
						Thread.sleep(1000);
						checkTimes++;
						state = NewAllStreamParser.DNPCheckSrvConnState(handle);
					}
					postMainThread("连接成功");
					// int port = NewAllStreamParser.DNPAddPort(handle, umid);
					// if (port == 0) {
					// postMainThread("创建端口失败:");
					// } else
					// postMainThread("创建端口成功： " + port);
					int port1 = NewAllStreamParser.DNPAddPortByChNo(handle,umid, 0);
					if (port1 == 0) {
						postMainThread("创建端口失败:" + port1);
					} else
						postMainThread("创建端口成功： " + port1);
					/**
					 * 通信处理
					 */
					NewAllStreamParser.DNPDelPort(handle, port1);
					postMainThread("删除端口");
					NewAllStreamParser.DNPDestroyPortServer(handle);
					postMainThread("销毁映射服务");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				super.run();
			}
		}.start();

	}

	public void postMainThread(final String info) {
		textshow.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				textshow.append(info + "\n");
				Log.d("postMainThread", "info:" + info);
			}
		});
	}

}
