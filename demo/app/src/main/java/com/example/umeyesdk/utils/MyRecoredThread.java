package com.example.umeyesdk.utils;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.util.Log;

import com.Player.Core.PlayerCore;
import com.audio2.AacEncode;
import com.stream.UmRtc;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class MyRecoredThread extends Thread {
	private PlayerCore playercore;
	private AacEncode aacEncode2 = null;
	private ByteBuffer sendBuffer = ByteBuffer.allocate(80 * 10 * 36);
	private UmRtc webRtc;
	public static boolean needCompareData = false;

	public MyRecoredThread(PlayerCore playercore) {
		super();
		this.playercore = playercore;
		// webRtc = UmRtc.getInstance();
		needCompareData = true;
	}

	@Override
	public synchronized void run() {
		Log.d("RecordThread", "run");
		ThreadRecordAudio();
		Log.d("RecordThread", "end");
	}


	@SuppressWarnings("deprecation")
	public void ThreadRecordAudio()// 录音时候一定要插上耳机否则全是噪音
	{
		AudioRecord recorder = null;
		try {
			//           WebRtcUtils.webRtcNsInit(8000);
//            FileOutputStream tempaudioFileOutputStream = null;
//            FileOutputStream tempaudioFileOutputStream1 = null;
//            try {
//                tempaudioFileOutputStream = new FileOutputStream(CommenUtil.getExternalStorageFile(playercore.mContext) + "/tempaudio.pcm",
//                        true);
//                tempaudioFileOutputStream1 = new FileOutputStream(CommenUtil.getExternalStorageFile(playercore.mContext) + "/tempaudio1.pcm",
//                        true);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }

			int RecordSamplingRate = (playercore.audiotype == 5 ? 44100 : playercore.RecordSamplingRate);//许总说的
			// 获得录音缓冲区大小
			int bufferSize = AudioRecord.getMinBufferSize(
					RecordSamplingRate,
					AudioFormat.CHANNEL_CONFIGURATION_MONO,
					AudioFormat.ENCODING_PCM_16BIT);
			Log.e("RecordThread", "录音缓冲区大小" + bufferSize);

			// 获得录音机对象
			recorder = new AudioRecord(PlayerCore.RECORDER_VOICE,
					RecordSamplingRate,
					AudioFormat.CHANNEL_CONFIGURATION_MONO,
					AudioFormat.ENCODING_PCM_16BIT, bufferSize * 10);

			recorder.startRecording();// 开始录音

			byte[] readBuffer = playercore.audiotype == 5 ? new byte[2048] : new byte[640 * 5];// 录音缓冲区
			int length = 0;
			int G711asize = 0;
			playercore.PPTisover = false;
			while (playercore.IsPPTaudio && playercore.ThreadisTrue) {
				// Log.d("RecordThread", "while run");
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

						if (playercore.GetOpenLog())
							Log.w("录音", "压缩后的录音数据 长度是：" + G711asize);
						playercore.SendPPTAudio(sendBuffer, G711asize, 1);


					}
				} else if (playercore.audioppttype == PlayerCore.AUDIOPPT_JUNJIADPCM)// 军集
				{
					length = recorder.read(readBuffer, 0, playercore.RecordVocSize);// 从mic读取音频数据
					if (length > 0 && length % 2 == 0) {
						ByteBuffer pInBuffer = ByteBuffer.wrap(readBuffer, 0, length);
						pInBuffer.position(0);
						if (playercore.GetOpenLog())
							Log.w("录音", "压缩后的录音数据 长度是：" + G711asize);
						playercore.SendPPTAudio(sendBuffer, G711asize, 1);

					}
				} else {
					//对讲
					if (playercore.audiotype == 5) {// AAC
						length = recorder.read(readBuffer, 0, 2048);
						ByteBuffer pInBuffer = ByteBuffer.wrap(readBuffer, 0, length);
						pInBuffer.position(0);

						if (aacEncode2 == null) {
							aacEncode2 = AacEncode.createAudioType(1, RecordSamplingRate, playercore.RecordEncodePcmBitRate);
						}
						synchronized (aacEncode2) {
							sendBuffer.clear();
							G711asize = aacEncode2.aacEncode_EncodeFrame(pInBuffer, pInBuffer.array().length, sendBuffer);//G711asize为200-300
						}
						playercore.SendPPTAudio(sendBuffer, G711asize, 1);

					}
//                    else if (playercore.audiotype == 0) {// amr
//                        length = recorder.read(readBuffer, 0, 2048);
//                        ByteBuffer pInBuffer = ByteBuffer.wrap(readBuffer, 0, length);
//                        pInBuffer.position(0);
//                        if (playercore.GetOpenLog())
//                            Log.w("RecordThread", "amr录音数据 长度是：" + length);
//                        if (aacEncode2 == null) {
//                            aacEncode2 = AacEncode.createAudioType(HlsDecode.MEDIA_AUDIO_FORMAT_AMR, 1, RecordSamplingRate, playercore.RecordEncodePcmBitRate);
//                        }
//                        synchronized (aacEncode2) {
//                            sendBuffer.clear();
//                            G711asize = aacEncode2.aacEncode_EncodeFrame(pInBuffer, pInBuffer.array().length, sendBuffer);//G711asize为200-300
//                        }
//                        if (playercore.GetOpenLog())
//                            Log.w("RecordThread", "压缩后的amr录音数据 长度是：" + G711asize);
//                        if (G711asize > 0)
//                            playercore.SendPPTAudio(sendBuffer, G711asize, 1);
//                        else
//                            Log.e("SendPPTAudio", "发送非法数据");
//
//                    }
					else {
						length = recorder.read(readBuffer, 0, playercore.RecordVocSize);// 1024);//640*2);从mic读取音频数据
						if (length > 0 && length % 2 == 0) {
							ByteBuffer pInBuffer = ByteBuffer.wrap(readBuffer, 0, length);
							pInBuffer.position(0);
							if (playercore.GetOpenLog()) Log.w("录音", "压缩后的录音数据 长度是：" + length);
							if (webRtc != null && playercore.DoublePPT && UmRtc.enbaleUse) {//双向对讲,且开启webrtc的使用

								byte[] outArray = new byte[length];
								byte[] inArray = Arrays.copyOf(readBuffer, length);//获取数组指定长度
								//createFileWithByte(inArray,"process_before.pcm");
								webRtc.AecmProcess(inArray, outArray);//进行回音消除
								//createFileWithByte(outArray,"process_after.pcm");//写入本地文件测试
								ByteBuffer pOutBuffer = ByteBuffer.wrap(outArray, 0, length);
								pOutBuffer.position(0);
								playercore.SendPPTAudio(pOutBuffer, length, 0);

							} else {

//                                if (playercore.openWebRtcNs) {     //开启噪音消除
//                                    short[] shortData = new short[length >> 1];
//                                    pInBuffer.order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shortData);
//                                    Log.i("webrtc", "RecordSamplingRate ：" + RecordSamplingRate);
//                                    byte[] nsProcessData = shortToBytes(WebRtcUtils.webRtcNsProcess(8000, shortData.length, shortData));
//                                    ByteBuffer pOutBuffer = ByteBuffer.wrap(nsProcessData, 0, length);
////                                    tempaudioFileOutputStream1.write(nsProcessData);
////                                    tempaudioFileOutputStream.write(readBuffer,0,length);
//                                    playercore.SendPPTAudio(pOutBuffer, length, 0);
//                                } else {
								playercore.SendPPTAudio(pInBuffer, length, 0);
								//                              }

							}
						}
					}
				}
				Thread.sleep(10);
			}
//            tempaudioFileOutputStream.close();
//            tempaudioFileOutputStream1.close();
			recorder.stop();
			recorder.release();
			// WebRtcUtils.webRtcNsFree();
			recorder = null;
			playercore.PPTisover = true;
			playercore.IsPPTaudio = false;
			needCompareData = false;
			if (aacEncode2 != null) {
				synchronized (aacEncode2) {
					aacEncode2.destroy();
					aacEncode2 = null;
				}
			}
			Thread.sleep(50);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static byte[] shortToBytes(short[] shorts) {
		if (shorts == null) {
			return null;
		}
		byte[] bytes = new byte[shorts.length * 2];
		ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(shorts);

		return bytes;
	}
}

