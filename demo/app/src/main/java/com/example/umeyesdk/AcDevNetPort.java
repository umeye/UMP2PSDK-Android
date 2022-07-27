package com.example.umeyesdk;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stream.UmProtocol;

public class AcDevNetPort extends Activity {
	String serverIP = "119.29.237.152";
	String umid = "umksk8k2c799";// //xmumh9ckm0mu //umkss83g7brx
	TextView textshow;
	long clientHandle = 0;
	int port1 = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		LinearLayout linearLayout = new LinearLayout(this);
		Button bu = new Button(this);
		bu.setText("发送数据");
		bu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (socket != null) {
					new Thread() {

						@Override
						public void run() {
							try {
								OutputStream outputStream = socket
										.getOutputStream();
								String sent = "app send "
										+ System.currentTimeMillis();
								Log.i("sent", "sent:" + sent);
								outputStream.write(sent.getBytes());
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							super.run();
						}

					}.start();

				}

			}
		});

		linearLayout.addView(bu);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		textshow = new TextView(this);
		linearLayout.addView(textshow);
		setContentView(linearLayout);
		setTitle("端口映射");
		textshow.setText("请稍后..\n");
		// String umid = getIntent().getStringExtra("umid");
		textshow.setText("测试UMID：" + umid + "\n请稍后..\n");

		new Thread() {

			@Override
			public void run() {
				clientHandle = UmProtocol.DNPCreatePortServer(serverIP,
						8300, "sdktest", "sdktest");
				if (clientHandle == 0) {
					postMainThread("创建映射服务失败");
					return;
				} else
					postMainThread("创建映射服务成功");
				int state = UmProtocol
						.DNPCheckSrvConnState(clientHandle);
				int checkTimes = 0;
				try {
					while (state != 2) { // 2 为 已连接
						if (checkTimes >= 30) {
							postMainThread("连接超时。。");
							return;
						}
						Thread.sleep(1000);
						checkTimes++;
						state = UmProtocol
								.DNPCheckSrvConnState(clientHandle);
					}
					postMainThread("连接成功");

					int port1 = UmProtocol.DNPAddPortByChNo(
							clientHandle, umid, 0);
					if (port1 == 0) {
						postMainThread("创建端口失败:" + port1);
					} else
						postMainThread("创建端口成功： " + port1);
					connect(port1);
					/**
					 * 通信处理
					 */
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				super.run();
			}
		}.start();

	}

	Socket socket = null;

	public void connect(int port) {
		try {

			// 设置ip和prot
			String ip = "127.0.0.1";
			// 创建Socket
			socket = new Socket();
			// 设置发送地址
			SocketAddress addr = new InetSocketAddress(ip, port);
			socket.connect(addr, 10000);
			System.out.println("客户端连接成功");
			InputStream inputStream = socket.getInputStream();
			byte[] buffer = new byte[1024];
			int len = -1;
			while ((len = inputStream.read(buffer)) != -1) {
				String data = new String(buffer, 0, len);
				System.out
						.println("收到服务的数据---------------------------------------------:"
								+ data);
				postMainThread(data);
			}
			System.out.println("客户端断开连接");

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("客户端无法连接服务器");

		} finally {

		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			socket = null;
		}
		if (clientHandle != 0 && port1 != 0) {

			UmProtocol.DNPDelPort(clientHandle, port1);
			postMainThread("删除端口");
			UmProtocol.DNPDestroyPortServer(clientHandle);
			postMainThread("销毁映射服务");
		}
		super.onDestroy();
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