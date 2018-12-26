package com.example.umeyesdk.utils;

import java.nio.ByteBuffer;

import com.Player.Core.PlayerCore;
import com.audio.g711adec;
import com.audio.junjiadpcmdec;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

public class MyRecoredThread extends Thread{
	private PlayerCore playercore;
	private junjiadpcmdec junjiadpcm_dec = null;
	private g711adec g711a_dec = null;
	private ByteBuffer pG711aBuffer = ByteBuffer.allocate(80 * 10 * 36);
	public MyRecoredThread(PlayerCore playercore) {
		super();
		this.playercore = playercore;
	}
	@Override
	public synchronized void run() {
		Log.d("auDecoder", "run");
		ThreadRecordAudio();
		/*
		 * try { while(true)//知道录音线程结束 { Thread.sleep(20); } }catch
		 * (Exception e) { e.printStackTrace(); }
		 */
	}
	@SuppressWarnings("deprecation")
	public void ThreadRecordAudio()// 录音时候一定要插上耳机否则全是噪音
	{
		AudioRecord recorder = null;
		try {
			// 获得录音缓冲区大小
			int bufferSize = AudioRecord.getMinBufferSize(
					playercore.RecordSamplingRate,
					AudioFormat.CHANNEL_CONFIGURATION_MONO,
					AudioFormat.ENCODING_PCM_16BIT);

			Log.e("", "录音缓冲区大小" + bufferSize);

			// 获得录音机对象
			recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
					playercore.RecordSamplingRate,
					AudioFormat.CHANNEL_CONFIGURATION_MONO,
					AudioFormat.ENCODING_PCM_16BIT, bufferSize * 10);

			recorder.startRecording();// 开始录音

			byte[] readBuffer = new byte[640 * 5];// 录音缓冲区
			int length = 0;
			int G711asize = 0;
			playercore.PPTisover = false;
			while (playercore.IsPPTaudio && playercore.ThreadisTrue) {

				if (playercore.audioppttype == PlayerCore.AUDIOPPT_G711A) {
					length = recorder.read(readBuffer, 0,
							playercore.RecordVocSize);// 1024);//640*2);//
					// 从mic读取音频数据
					if (length > 0 && length % 2 == 0) {
						Log.w("录音", "录音数据 长度是：" + length);
						// Log.d("正在录音", "录音数据 长度是："+length);
						ByteBuffer pInBuffer = ByteBuffer.wrap(readBuffer, 0,
								length);
						pInBuffer.position(0);
						if (g711a_dec == null) { // 语音
							g711a_dec = new g711adec();
						}
						G711asize = g711a_dec.EncodeOneFrame(pInBuffer,
								pG711aBuffer);
						if (playercore.GetOpenLog())
							Log.w("录音", "压缩后的录音数据 长度是：" + G711asize);
						playercore.SendPPTAudio(pG711aBuffer, G711asize, 1);
					}
				} else if (playercore.audioppttype == PlayerCore.AUDIOPPT_JUNJIADPCM)// 军集
				// ADPCM
				{
					length = recorder.read(readBuffer, 0,
							playercore.RecordVocSize);// 从mic读取音频数据
					if (length > 0 && length % 2 == 0) {
						ByteBuffer pInBuffer = ByteBuffer.wrap(readBuffer, 0,
								length);
						pInBuffer.position(0);
						if (junjiadpcm_dec == null) { // 语音
							junjiadpcm_dec = new junjiadpcmdec();
						}
						synchronized (junjiadpcm_dec) {
							G711asize = junjiadpcm_dec.EncodeOneFrame(
									pInBuffer, pG711aBuffer);
						}
						if (playercore.GetOpenLog())
							Log.w("录音", "压缩后的录音数据 长度是：" + G711asize);
						playercore.SendPPTAudio(pG711aBuffer, G711asize, 1);
					}
				} else {
					length = recorder.read(readBuffer, 0,
							playercore.RecordVocSize);// 1024);//640*2);//
					// 从mic读取音频数据
					if (length > 0 && length % 2 == 0) {
						ByteBuffer pInBuffer = ByteBuffer.wrap(readBuffer, 0,
								length);
						pInBuffer.position(0);
						if (playercore.GetOpenLog())
							Log.w("录音", "压缩后的录音数据 长度是：" + length);
						// FileOutputStream outputStream = new FileOutputStream(
						// file, true);
						// //outputStream.write(pInBuffer.array());
						// outputStream.write(pInBuffer.array(), 0, length);
						// outputStream.close();
						playercore.SendPPTAudio(pInBuffer, length, 0);
					}
				}

				Thread.sleep(10);
			}

			recorder.stop();
			recorder.release();
			recorder = null;
			playercore.PPTisover = true;
			playercore.IsPPTaudio = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
