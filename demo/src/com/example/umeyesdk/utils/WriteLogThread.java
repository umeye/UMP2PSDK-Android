package com.example.umeyesdk.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import com.Player.Core.PlayerClient;

@SuppressLint("SdCardPath")
public class WriteLogThread extends Thread {
	static final String TAG = "WriteLogThread";
	static final String DIR = "/sdcard/QVEye/log/";
	int MAXSIZE = 10;// 文件最大数 ，大于等于此，删除第一个文件
	PlayerClient pc;
	public static boolean isRun = false;
	FileOutputStream outputStream;

	public WriteLogThread(PlayerClient pc) {
		this.pc = pc;
	}

	public boolean isRun() {
		return isRun;
	}

	@Override
	public synchronized void run() {
		try {
			isRun = true;

			// if (!Utility.isSDCardAvaible()) {
			// isRun = false;
			// }
			String s;
			String r = System.getProperty("line.separator");// 换行
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
			outputStream = isFileExsitToOutStream(DIR,
					format.format(new Date()) + ".txt");
			Log.d(TAG, "开启调试模式");
			if (outputStream == null) {
				isRun = false;
			}
			while (isRun) {
				if (pc != null) {
					s = pc.CLTGetLogData(1000);
					if (!TextUtils.isEmpty(s)) {
						Log.d(TAG, s);
						s = s + r;
						outputStream.write(s.getBytes());
					}

				} else {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
			if (outputStream != null) {
				outputStream.flush();
				outputStream.close();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	FileOutputStream isFileExsitToOutStream(String dir, String fileName) {
		FileOutputStream outFileStream = null;
		try {

			File fileDir = new File(dir);

			if (CreateDirectory(dir)) {

				return null;
			}
			/**
			 * 
			 */
			File[] listFile = fileDir.listFiles();
			if (listFile != null) {
				if (listFile.length >= MAXSIZE) {
					File deleteFile = null;
					for (int i = 0; i < listFile.length; i++) {
						if (i == 0) {
							deleteFile = listFile[i];
						} else {
							if (deleteFile.lastModified() > listFile[i]
									.lastModified()) {
								deleteFile = listFile[i];
							}
						}

					}
					if (deleteFile != null) {
						deleteFile.delete();
					}
				}
			}
			File Configfile = new File(dir + '/' + fileName);
			if (!Configfile.exists()) {

				Configfile.createNewFile();

			}
			outFileStream = new FileOutputStream(Configfile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outFileStream;
	}

	public boolean CreateDirectory(String dir) {
		File file = new File(dir);
		boolean b = false;
		if (!file.exists()) {
			b = file.mkdirs();
			System.out.println("不存在，创建文件夹" + b + dir);
			return b;
		} else {
			System.out.println("存在文件夹" + dir);
			return true;
		}
	}
}
