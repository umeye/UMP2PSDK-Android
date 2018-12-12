package com.example.umeyeNewSdk;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.Player.Core.PlayerDownFileCore;
import com.Player.Source.TDownFrame;
import com.Player.Source.TVideoFile;
import com.example.umeyesdk.AppMain;
import com.example.umeyesdk.R;
import com.example.umeyesdk.adpter.SearchDownloadAdapter;
import com.example.umeyesdk.utils.Constants;
import com.example.umeyesdk.utils.LocalFile;
import com.example.umeyesdk.utils.Show;
import com.example.umeyesdk.utils.ShowProgress;

public class AcSearchDownLoadList extends Activity {
	private ListView lvResult;
	private SearchDownloadAdapter adapter;
	private Button btnBack;
	private ImageButton btnSearch;
	private TextView tvTitle;
	private List<TVideoFile> data;
	public static TVideoFile VideoFile;
	public static String title;
	ShowProgress showProgress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_search_record_result);
		TextView tvSize = (TextView) findViewById(R.id.tvEnd);
		tvSize.setText("文件大小");
		InitView();
	}

	public void InitView() {
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setText(Constants.UMID + "");

		btnBack = (Button) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new OnClick());

		btnSearch = (ImageButton) findViewById(R.id.btnSearch);
		btnSearch.setOnClickListener(new OnClick());

		lvResult = (ListView) findViewById(R.id.lvResult);
		adapter = new SearchDownloadAdapter(this, AcSearchRecord.data);
		lvResult.setAdapter(adapter);
		lvResult.setOnItemClickListener(new OnItemClick());

		title = getIntent().getStringExtra("title");
		data = AcSearchRecord.data;

	}

	public void OpenDownFile(String fileName) {
		if (fileName.endsWith(".3gp")) {
			Intent it = new Intent(Intent.ACTION_VIEW);
			File f = new File(fileName);
			Uri uri = Uri.fromFile(f);
			it.setDataAndType(uri, "video/3gp");
			startActivity(it);
		} else if (fileName.endsWith(".jpg")) {
			Intent it = new Intent(Intent.ACTION_VIEW);
			File f = new File(fileName);
			Uri uri = Uri.fromFile(f);
			it.setDataAndType(uri, "image/*");
			startActivity(it);
		}

	}

	/**
	 * 启动文件下载
	 * 
	 * @param tVideoFile
	 * @throws IOException
	 */
	public void startDown(final TVideoFile tVideoFile) throws IOException {
		boolean b = LocalFile.CreateDirectory(Constants.UserVideoDir);
		if (b == false) {
			return;
		}
		String filename = tVideoFile.FileName.replace('/', '_');
		final String fileptah = Constants.UserVideoDir + "/" + filename;
		File file = new File(fileptah);
		if (file.exists()) {
			if (file.length() != 0) {
				OpenDownFile(fileptah);
				return;
			} else
				file.delete();

		}
		file.createNewFile();
		showProgress = new ShowProgress(AcSearchDownLoadList.this);
		showProgress.show();
		new DownThread(tVideoFile, file).start();
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			showProgress.dismiss();
			switch (msg.what) {
			case 1:
				Show.toast(AcSearchDownLoadList.this, "下载完成");
				String filePath = (String) msg.obj;
				OpenDownFile(filePath);
				break;
			case -1:
				Show.toast(AcSearchDownLoadList.this, "文件下载失败");
				break;
			case -2:
				Show.toast(AcSearchDownLoadList.this, "文件创建失败");
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	public class DownThread extends Thread {
		TVideoFile tVideoFile;
		private File fileptah;

		public DownThread(TVideoFile tVideoFile, File fileptah) {
			super();
			this.tVideoFile = tVideoFile;
			this.fileptah = fileptah;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			PlayerDownFileCore playerDownFileCore = null;
			try {
				DataOutputStream out = new DataOutputStream(
						new BufferedOutputStream(new FileOutputStream(fileptah)));

				// TODO Auto-generated method stub 下载类 传入设备umid，用户名，密码
				playerDownFileCore = new PlayerDownFileCore(Constants.UMID,
						Constants.user, Constants.password);
				// 传入需要下载的文件名，大小
				int ret = playerDownFileCore.StartDownFileByUMID(
						tVideoFile.FileName, tVideoFile.nFileSize);
				if (ret != 0) {
					int trydowntime = 0;
					int DownOver = 0;// 结束标识
					try {
						while (DownOver != 1) {
							TDownFrame mFrame = playerDownFileCore
									.GetDownFileData();
							int currentPosition = playerDownFileCore
									.GetDownPos();
							DownOver = mFrame.DownOver;
							if (mFrame != null && mFrame.iData != null) {
								if (mFrame.iData.length != 0) {
									trydowntime = 0;
									out.write(mFrame.iData);
									Log.d("GetDownFileData",
											"GetDownFileData size:"
													+ mFrame.iData.length
													+ ",下载进度："
													+ currentPosition + "%"
													+ ",结束标志："
													+ mFrame.DownOver);
								} else {
									Thread.sleep(100);

									Log.d("trydowntime", "trydowntime: "
											+ trydowntime);
									trydowntime++;
								}

							} else {
								Thread.sleep(100);
								Log.d("trydowntime", "trydowntime: "
										+ trydowntime);
								trydowntime++;
							}
							if (trydowntime > 100) {
								out.flush();
								out.close();
								fileptah.delete();
								handler.sendEmptyMessage(-1);
								return;
							}
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					out.flush();
					out.close();

					handler.sendMessage(Message.obtain(handler, 1,
							fileptah.getAbsolutePath()));
				} else {
					AppMain appMain = (AppMain) getApplicationContext();
					handler.sendEmptyMessage(-1);
					Log.e("StartDownFile", "StartDownFile Failed:"
							+ appMain.getPlayerclient().GetLastErrorEx());
					fileptah.delete();
				}

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				handler.sendEmptyMessage(-2);
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				handler.sendEmptyMessage(-3);
				e.printStackTrace();
			}
			if (playerDownFileCore != null) {
				playerDownFileCore.StopDownFile();
			}

			super.run();
		}

	}

	class OnItemClick implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			VideoFile = data.get(position);

			try {
				startDown(VideoFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public class OnClick implements OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.btnBack:
				finish();
				break;
			case R.id.btnSearch:
				onSearchRequested();
				break;
			}
		}
	}
}
