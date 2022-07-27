package com.example.umeyesdk.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Process;
import android.util.Log;

import com.Player.Core.PlayerCore;
import com.Player.Source.SDKError;
import com.Player.Source.TSourceFrame;
import com.audio2.AacDecode;

import com.audio2.AacEncode;
import com.stream.UmRtc;
import com.stream.WebRtcUtils;
import com.video.h264.DecodeDisplay;
import com.video.h264.DecodeTimeStampLisenter;
import com.video.h264.DefualtRecoredThread;
import com.video.hls.HlsDecode;

public class MyAudioDecodeThread extends Thread implements DecodeTimeStampLisenter {
	private AacDecode aAcDecode = null;
	private AacEncode aacEncode2 = null;
	public static boolean openCancerNoise = false;
	private AudioTrack audioTrack = null;
	private PlayerCore playercore;
	private Boolean firstaudio = true;
	int iMinBufSize = 0;
	private ByteBuffer pPcmBuffer = ByteBuffer.allocate(160 * 10 * 36);
	private ByteBuffer pAacInBuffer = ByteBuffer.allocate(2048 * 10);
	private ByteBuffer pAacWriteMP4Buffer = ByteBuffer.allocate(80 * 10 * 36);
	DecodeDisplay decodeDisplay;
	//播放显示的时间戳，当前用来HLS音视频同步
	private long displayTimeStamps;

	public MyAudioDecodeThread(PlayerCore playercore,
									 DecodeDisplay decodeDisplay) {
		this.playercore = playercore;
		this.decodeDisplay = decodeDisplay;
	}

	@Override
	public void run() {
		Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
		Log.d("auDecoder", "run");
		AudioDecode();
		if (aAcDecode != null) {
			synchronized (aAcDecode) {
				if (aAcDecode != null) {
					aAcDecode.destroy();
					aAcDecode = null;
				}
			}
		}


		if (audioTrack != null) {
			synchronized (audioTrack) {
				if (audioTrack != null) {
					try {
						audioTrack.stop();
						audioTrack.release();
					} catch (Exception e) {
						e.printStackTrace();
					}
					audioTrack = null;
				}
			}
		}

		if (aacEncode2 != null) {
			synchronized (aacEncode2) {
				aacEncode2.destroy();
				aacEncode2 = null;
			}
		}
	}


	@SuppressWarnings("deprecation")
	void AudioDecode() {

		while (playercore.ThreadisTrue) {
			try {
				//Thread.sleep(20);
				Thread.sleep(2);
				if (!playercore.ThreadisTrue)
					return;
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			while (playercore.ThreadisTrue
					&& playercore.GetPlayerState() == SDKError.Statue_PLAYING)// MENetworkFilter.CurStatu!=SDKError.Statue_WaitBuffer
			{
				if (!playercore.ThreadisTrue)
					return;
				try {
					if (playercore.IsPausing)// 暂停
					{
						Thread.sleep(20);
						continue;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				TSourceFrame mFrame = null;
				long startDecodeTime = 0;
				try {
					startDecodeTime = System.currentTimeMillis();
					mFrame = playercore.GetNextAudioFrame();// null;//playercore.mPacketaudio.getAudiocurrent();
//                    if (playercore.GetOpenLog())
//                        Log.d("AudioDecode", "mPacket_au left "
//                                + playercore.GetAudioFrameLeft());
				} catch (OutOfMemoryError e) {
					Log.e("OutOfMemoryError",
							"AudioDecode OutOfMemoryError.........");
					mFrame = null;
					break;
				}
				if (mFrame == null) {
					break;
				} else {
					// Log.e("GetNextAudioFrame", "mFrame.iLen :" + mFrame.iLen);
					if (playercore.GetOpenLog())
						Log.d("GetNextFrame", "AudioFrame timestamp:" + mFrame.iPTS + ",mFrame.iFrameFlag=" + mFrame.iFrameFlag);
				}
				if (playercore.audioToUpCallBack != null) {// 存在音频上层处理回调，底层不再进行处理。
					playercore.audioToUpCallBack.getFrameData(mFrame);
					continue;
				}
				if (mFrame.iFrameFlag == 2 || mFrame.iFrameFlag == 1) {// 因播放完或错误导致停止
					decodeDisplay.CurrentPlayTime = playercore
							.GetFileAllTime_Int();
					if (playercore.onFinishListener != null) {
						if (mFrame.iFrameFlag == 2) {
							playercore.onFinishListener.onComplete();
						} else {
							playercore.onFinishListener.onError();
						}
					}

					Log.d("total and current", "Audio GetFileAllTime_Int:"
							+ decodeDisplay.CurrentPlayTime + ",因播放完或错误导致停止");
					break;
				}
				try {
					if (firstaudio)// 第一次缓冲一下这样可以不卡点
					{
						Log.e("mFrame.iAudioSampleRate", "mFrame.iAudioSampleRate--->" + mFrame.iAudioSampleRate);
						if (mFrame.iAudioSampleRate == 32000) {// 如果音频的采样率是3200，播放音频采样率为3200，默认为8000
							Log.e("mFrame.iAudioSampleRate",
									"mFrame.iAudioSampleRate--->3200");
							playercore.PlayerSamplingRate = 32000;
						}
						if (mFrame.iAudioSampleRate <= 0) {
							mFrame.iAudioSampleRate = 8000;
						}
						playercore.PlayerSamplingRate = mFrame.iAudioSampleRate;

						iMinBufSize = android.media.AudioTrack
								.getMinBufferSize(
										playercore.PlayerSamplingRate,// 8000,
										AudioFormat.CHANNEL_CONFIGURATION_MONO,
										AudioFormat.ENCODING_PCM_16BIT);
						if (iMinBufSize == AudioTrack.ERROR_BAD_VALUE
								|| iMinBufSize == AudioTrack.ERROR) {
							// return ;
							Log.e("AudioTrack",
									"AudioTrack error----------------------------->iMinBufSize:"
											+ iMinBufSize);
						}
						// Thread.sleep(200);
						Thread.sleep(50);
						firstaudio = false;
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				//如果非aac要用各自解码库转PCM(除了aac外其他编码，底层已对它们处理，上层不用再处理)，所以直接对pcm编码aac,丢给mp4编码库
				try {
					//录像
					if (decodeDisplay != null && decodeDisplay.mp4make != null) {
						if (playercore.GetIsSnapVideo()
								&& !playercore.IsOnPauseSnapVideo) {
							decodeDisplay.mp4make.initAudioParam(
									mFrame.EncodeType, mFrame.iAudioSampleRate,
									mFrame.iLen);
							if (playercore != null
									&& playercore.mp4RecordInfo != null)
								playercore.mp4RecordInfo.size = mFrame.iData.length
										+ playercore.mp4RecordInfo.size;
							byte[] buffer = mFrame.iData.clone();
							if (playercore.audiotype != 5 && PlayerCore.isNewRecordMode) {//设备非aac，底层返pcm；设备aac，底层返aac
								int aacEncodePriorSize = 2048;
								int iLength;
								if (pAacInBuffer.position() + buffer.length > aacEncodePriorSize) {//如果当前的指针加上输入数组长度大于2048个字节
									iLength = pAacInBuffer.position() + buffer.length - aacEncodePriorSize;
									pAacInBuffer.put(buffer, 0, buffer.length - iLength);//先填完2048个长度
								} else {
									iLength = buffer.length;
									pAacInBuffer.put(buffer, 0, buffer.length);//当前position没到2048，则继续填充，每次填充后position自动为最后+1个位置
								}

								if (pAacInBuffer.position() >= aacEncodePriorSize) {//当塞到2048个字节了
									if (aacEncode2 == null) {
										aacEncode2 = AacEncode.createAudioType(1, playercore.PlayerSamplingRate, 16000);
									}

									pAacInBuffer.position(0);//将指针置为0
									pAacInBuffer.limit(aacEncodePriorSize);//aac输入每次取2048进行编码，因此limit设为2048

									int size;
									synchronized (aacEncode2) {
										pAacWriteMP4Buffer.clear();
										size = aacEncode2.aacEncode_EncodeFrame(pAacInBuffer, aacEncodePriorSize, pAacWriteMP4Buffer);
									}
									if (iLength < buffer.length) {//然后超过2048的多余部分则从position=0指针位置覆盖填充
										pAacInBuffer.put(buffer, iLength, buffer.length - iLength);
									}
									decodeDisplay.mp4make.writeaudioframe(
											pAacWriteMP4Buffer.array(), size);
								}
							} else {
								if (PlayerCore.isNewRecordMode) {
									decodeDisplay.mp4make.writeaudioframe(
											buffer, buffer.length);
								} else {
									decodeDisplay.mp4make.writeaudioframe(
											buffer, buffer.length, mFrame.iPTS);
								}
							}
						} else {
							pAacInBuffer.clear();
						}
					} else {
						pAacInBuffer.clear();
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				if (playercore.GetOpenLog()) {
					Log.w("mFrame.EncodeType", "mFrame.EncodeType:"
							+ mFrame.EncodeType);
				}

				if (mFrame.EncodeType == 11)
					playercore.audiotype = 2;

				else if (mFrame.EncodeType == 12)// 10-20代表音频 10PCM 11G711a
					// 12AMRNB
					// 13AMRWB 14AAC 15ADPCM 16 ADPCM_DJ
					playercore.audiotype = 0;
				else if (mFrame.EncodeType == 13)
					playercore.audiotype = 1;
				else if (mFrame.EncodeType == 15)// ADPCM
					playercore.audiotype = 3;
				else if (mFrame.EncodeType == 16)// ADPCM
					playercore.audiotype = 4;
				else if (mFrame.EncodeType == 17)// AAC
					playercore.audiotype = 5;
				if (audioTrack == null)// 有声音了才创建音频播放
				{
					if (playercore.audiotype == 1)// wb
					{
						WebRtcUtils.webRtcNsInit(1600);
						audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
								16000, AudioFormat.CHANNEL_CONFIGURATION_MONO,
								AudioFormat.ENCODING_PCM_16BIT, iMinBufSize,
								AudioTrack.MODE_STREAM);

//                        Log.e("new AudioTrack111", "  AudioManager.STREAM_MUSIC: "+AudioManager.STREAM_MUSIC
//                                +"  16000: "+16000
//                                +"  AudioFormat.CHANNEL_CONFIGURATION_MONO: "+AudioFormat.CHANNEL_CONFIGURATION_MONO
//                                +"  AudioFormat.ENCODING_PCM_16BIT: "+AudioFormat.ENCODING_PCM_16BIT
//                                +"  iMinBufSize: "+iMinBufSize
//                                +"  AudioTrack.MODE_STREAM: "+AudioTrack.MODE_STREAM);

						Log.e("AudioTrack",
								"AudioTrack: PlayerSamplingRate=16000");
					} else {
						try {
							playercore.PlayerSamplingRate = mFrame.iAudioSampleRate;
							WebRtcUtils.webRtcNsInit(playercore.PlayerSamplingRate);
							audioTrack = new AudioTrack(
									AudioManager.STREAM_MUSIC,
									playercore.PlayerSamplingRate,
									AudioFormat.CHANNEL_CONFIGURATION_MONO,
									AudioFormat.ENCODING_PCM_16BIT,
									iMinBufSize, AudioTrack.MODE_STREAM);

//                            Log.e("new AudioTrack222", "  AudioManager.STREAM_MUSIC: "+AudioManager.STREAM_MUSIC
//                                    +"  playercore.PlayerSamplingRate: "+playercore.PlayerSamplingRate
//                                    +"  AudioFormat.CHANNEL_CONFIGURATION_MONO: "+AudioFormat.CHANNEL_CONFIGURATION_MONO
//                                    +"  AudioFormat.ENCODING_PCM_16BIT: "+AudioFormat.ENCODING_PCM_16BIT
//                                    +"  iMinBufSize: "+iMinBufSize
//                                    +"  AudioTrack.MODE_STREAM: "+AudioTrack.MODE_STREAM);

						} catch (OutOfMemoryError e) {
							Log.d("AudioDecode ",
									"new AudioTrack OutOfMemoryError.........");
							continue;
						} catch (Exception e) {

//                            Log.d("AudioTrack",
//                                    "init Exception.........");
							e.printStackTrace();
							continue;
						}
					}

					if (audioTrack != null)
						try {
							audioTrack.play();
						} catch (Exception e) {
							e.printStackTrace();
						}

				}
				if (playercore.voicePause) {
					if (audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
						try {
							audioTrack.stop();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else {
					if (audioTrack.getPlayState() == AudioTrack.PLAYSTATE_STOPPED) {
						audioTrack.play();
					}
				}
				try {
					ByteBuffer pInBuffer = ByteBuffer.wrap(
							mFrame.iData.clone(), 0, mFrame.iLen);// 这个地方不加个克隆直播回退就会出问题,解码器会修改里面的内容
					pInBuffer.position(0);
					int Pcmsize = 0;
					if (playercore.voicePause)// 声音不播放
					{
						try {
							Thread.sleep(10);
						} catch (Exception e) {
							e.printStackTrace();
						}
						continue;
					} else if (playercore.audiotype == 0) {
						if (aAcDecode == null) { // 语音
							aAcDecode = AacDecode.createAudioType(HlsDecode.MEDIA_AUDIO_FORMAT_AMR, audioTrack.getChannelCount(), audioTrack.getSampleRate());
						}
						synchronized (aAcDecode) {
							pPcmBuffer.clear();
							Pcmsize = aAcDecode.aacDecode_DecodeFrame(pInBuffer, pInBuffer.array().length,//10 * 1024,
									pPcmBuffer);

						}
					} else if (playercore.audiotype == 1)// amrwb
					{

					} else if (playercore.audiotype == 2)// g711
					{

					} else if (playercore.audiotype == 3)// IMA adpcm 是4:1的压缩比率
					{

					} else if (playercore.audiotype == 4)// DONGJI IMA adpcm
					// 是4:1的压缩比率
					{

					} else if (playercore.audiotype == 5) {

						if (playercore.GetOpenLog())
							Log.w("aAcDecode", "audio is AAcDecode");
						if (aAcDecode == null) { // 语音
							aAcDecode = AacDecode.createAudioType(audioTrack.getChannelCount(), audioTrack.getSampleRate());
						}
						synchronized (aAcDecode) {
							pPcmBuffer.clear();
							Pcmsize = aAcDecode.aacDecode_DecodeFrame(pInBuffer, pInBuffer.array().length,//10 * 1024,
									pPcmBuffer);

						}
					} else {//目前除了aac，其他设备编码的底层都是返回pcm给上层
						if (playercore.GetOpenLog())
							Log.w("adpcm", "audio is pcm");
						Pcmsize = mFrame.iLen;
						pPcmBuffer = ByteBuffer.wrap(mFrame.iData, 0,
								mFrame.iLen);
					}

					if (Pcmsize > 0) {
						if (playercore.GetOpenLog())
							Log.d("AudioDecode", "audiotype=" + playercore.audiotype + "mPacket_au left "
									+ playercore.GetAudioFrameLeft()
									+ " Pcmsize is:" + Pcmsize);

						if (pPcmBuffer != null)
							pPcmBuffer.position(0);

						byte[] pcmData = new byte[Pcmsize];
						pPcmBuffer.get(pcmData, 0, Pcmsize);
						//真正的播放时间为时间戳+解码时间
						displayTimeStamps = mFrame.iPTS + System.currentTimeMillis() - startDecodeTime;
						if (audioTrack == null)
							return;
						if (playercore.DoublePPT) {// 双向对讲
							if (playercore.openWebRtcNs) {     //开启噪音消除
								short[] shortData = new short[pcmData.length >> 1];
								ByteBuffer.wrap(pcmData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shortData);
								byte[] nsProcessData = shortsToBytes(WebRtcUtils.webRtcNsProcess(audioTrack.getSampleRate(), shortData.length, shortData));
								audioTrack.write(nsProcessData, 0, nsProcessData.length);// 输入数据到播放队列
							} else {
								audioTrack.write(pcmData, 0, Pcmsize);// 输入数据到播放队列
							}
							if (DefualtRecoredThread.needCompareData
									&& UmRtc.enbaleUse) {// 需要进行回音消除
								Log.i("webrtc", "java compare data"
										+ pcmData.length + "");
								UmRtc.getInstance().AcemCompareData(pcmData);// 写入参考数据
							}
						} else {
							if (!playercore.IsPPTaudio) {// 对讲关闭情况下
								if (playercore.openWebRtcNs) { //开启噪音消除
									short[] shortData = new short[pcmData.length >> 1];
									ByteBuffer.wrap(pcmData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shortData);
									byte[] nsProcessData = shortsToBytes(WebRtcUtils.webRtcNsProcess(audioTrack.getSampleRate(), shortData.length, shortData));
									audioTrack.write(nsProcessData, 0, nsProcessData.length);// 输入数据到播放队列
								} else {
									audioTrack.write(pcmData, 0, Pcmsize);// 输入数据到播放队列
								}
							}
						}
						int leftvideoframe = playercore.GetAudioFrameLeft();
						if (playercore.GetOpenLog())
							Log.w("Audio Decode", "Audio left:"
									+ leftvideoframe + ",coast time:" + (System.currentTimeMillis() - startDecodeTime));
					} else {
						if (playercore.audiotype == 0) {//amr
							if (aAcDecode != null) {
								synchronized (aAcDecode) {
									aAcDecode.destroy();
									aAcDecode = null;
								}
							}
							aAcDecode = AacDecode.createAudioType(HlsDecode.MEDIA_AUDIO_FORMAT_AMR, audioTrack.getChannelCount(), audioTrack.getSampleRate());
						} else if (playercore.audiotype == 1) {

						} else if (playercore.audiotype == 2)// g711
						{
						} else if (playercore.audiotype == 5) {
							if (aAcDecode != null) {
								synchronized (aAcDecode) {
									aAcDecode.destroy();
									aAcDecode = null;
								}
							}
							aAcDecode = AacDecode.createAudioType(audioTrack.getChannelCount(), audioTrack.getSampleRate());
						}
						Log.d("Audiodecode statu", "Audiodecode fail.........");
					}
					mFrame = null;
				} catch (OutOfMemoryError e1) {
					mFrame = null;
					Log.e("AudioDecode ",
							"AudioDecode OutOfMemoryError.........");
					e1.printStackTrace();
				} catch (Exception e) {
					mFrame = null;
					e.printStackTrace();
				}
			}
		}
		WebRtcUtils.webRtcNsFree();
	}

	private byte[] shortsToBytes(short[] data) {
		byte[] buffer = new byte[data.length * 2];
		int shortIndex, byteIndex;
		shortIndex = byteIndex = 0;
		for (; shortIndex != data.length; ) {
			buffer[byteIndex] = (byte) (data[shortIndex] & 0x00FF);
			buffer[byteIndex + 1] = (byte) ((data[shortIndex] & 0xFF00) >> 8);
			++shortIndex;
			byteIndex += 2;
		}
		return buffer;
	}

	@Override
	public long getDisplayTimeStamps() {
		return displayTimeStamps;
	}
}
