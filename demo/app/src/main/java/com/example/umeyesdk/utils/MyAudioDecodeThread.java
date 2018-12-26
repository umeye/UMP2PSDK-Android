package com.example.umeyesdk.utils;

import java.nio.ByteBuffer;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import com.Player.Core.PlayerCore;
import com.Player.Source.SDKError;
import com.Player.Source.TSourceFrame;
import com.audio.aacDecode;
import com.audio.adpcmdec;
import com.audio.amrnbdec;
import com.audio.amrwbdec;
import com.audio.g711adec;
import com.audio.junjiadpcmdec;
import com.video.h264.DecodeDisplay;

public class MyAudioDecodeThread extends Thread {
	private g711adec g711a_dec = null;
	private aacDecode aAcDecode = null;
	private AudioTrack audioTrack = null;
	private amrnbdec amrnb_dec = null;
	private amrwbdec amrwb_dec = null;
	private adpcmdec adpcm_dec = null;
	private junjiadpcmdec junjiadpcm_dec = null;
	private PlayerCore playercore;
	private Boolean firstaudio = true;
	int iMinBufSize = 0;
	private ByteBuffer pPcmBuffer = ByteBuffer.allocate(160 * 10 * 36);
	DecodeDisplay decodeDisplay;

	public MyAudioDecodeThread(PlayerCore playercore,
							   DecodeDisplay decodeDisplay) {
		this.playercore = playercore;
		this.decodeDisplay = decodeDisplay;
	}

	@Override
	public void run() {

		Log.d("auDecoder", "run");
		AudioDecode();
		if (aAcDecode != null) {
			synchronized (aAcDecode) {
				if (aAcDecode != null) {
					aAcDecode.Cleanup();
					aAcDecode = null;
				}
			}
		}
		if (amrnb_dec != null) {
			synchronized (amrnb_dec) {
				if (amrnb_dec != null) {
					amrnb_dec.Cleanup();
					amrnb_dec = null;
				}
			}
		}

		if (amrwb_dec != null) {
			synchronized (amrwb_dec) {
				if (amrwb_dec != null) {
					amrwb_dec.Cleanup();
					amrwb_dec = null;
				}

			}
		}

		if (audioTrack != null) {
			synchronized (audioTrack) {
				if (audioTrack != null) {
					audioTrack.stop();
					audioTrack.release();
					audioTrack = null;
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	void AudioDecode() {
		while (playercore.ThreadisTrue) {
			try {
				// Thread.sleep(30);
				Thread.sleep(2);
				if (!playercore.ThreadisTrue)
					return;
				// if (playercore.voicePause) {
				// Thread.sleep(100);
				// continue;
				// }
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
				TSourceFrame mFrame = null;// new TSourceFrame();
				// mFrame = playercore.mPacketaudio.deQueue();
				// playercore.GetAudioFrameLeft()
				try {
					mFrame = playercore.GetNextAudioFrame();// null;//playercore.mPacketaudio.getAudiocurrent();

				} catch (OutOfMemoryError e) {
					Log.e("OutOfMemoryError",
							"AudioDecode OutOfMemoryError.........");
					mFrame = null;
					break;
				}
				if (mFrame == null) {
					break;
				}
				// Log.d("mFrame", "audio frameFlag："+mFrame.iFrameFlag );
				if (mFrame.iFrameFlag == 2 || mFrame.iFrameFlag == 1) {// 因播放完或错误导致停止
					decodeDisplay.SetCurrentPlayTime(playercore
							.GetFileAllTime_Int());
					Log.d("total and current", "GetFileAllTime_Int:因播放完或错误导致停止");
					break;
				}
				try {
					if (firstaudio)// 第一次缓冲一下这样可以不卡点
					{
						if (mFrame.iAudioSampleRate == 32000) {// 如果音频的采样率是3200，播放音频采样率为3200，默认为8000
							Log.e("mFrame.iAudioSampleRate",
									"mFrame.iAudioSampleRate--->3200");
							playercore.PlayerSamplingRate = 32000;
						}
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

				try {
					if (decodeDisplay != null && decodeDisplay.mp4make != null) {
						if (playercore.GetIsSnapVideo()) {
							decodeDisplay.mp4make.initAudioParam(
									mFrame.EncodeType, mFrame.iAudioSampleRate,
									mFrame.iLen);
							decodeDisplay.mp4make.writeaudioframe(
									mFrame.iData.clone(), mFrame.iPTS);
						}
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				// Log.w("mFrame.EncodeType",
				// "mFrame.EncodeType:"+mFrame.EncodeType);
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
						audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
								16000, AudioFormat.CHANNEL_CONFIGURATION_MONO,
								AudioFormat.ENCODING_PCM_16BIT, iMinBufSize,
								AudioTrack.MODE_STREAM);

						Log.e("AudioTrack",
								"AudioTrack: PlayerSamplingRate=16000");
					} else {
						try {

							audioTrack = new AudioTrack(
									AudioManager.STREAM_MUSIC,
									playercore.PlayerSamplingRate,
									AudioFormat.CHANNEL_CONFIGURATION_MONO,
									AudioFormat.ENCODING_PCM_16BIT,
									iMinBufSize, AudioTrack.MODE_STREAM);
						} catch (OutOfMemoryError e) {
							Log.d("AudioDecode ",
									"new AudioTrack OutOfMemoryError.........");
							e.printStackTrace();
							continue;
						}
					}

					if (audioTrack != null)
						audioTrack.play();
				}
				if (playercore.voicePause) {
					if (audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
						audioTrack.stop();

					}
				} else {
					if (audioTrack.getPlayState() == AudioTrack.PLAYSTATE_STOPPED) {
						audioTrack.play();
					}
				}

				// playercore.mPacketPPTaudio.enQueue(mFrame);
				/*
				 * if(true) { break; }
				 */

				if (mFrame == null)
					continue;
				try {
					// ByteBuffer pInBuffer
					// =ByteBuffer.wrap(mFrame.iData,0,mFrame.iLen);
					ByteBuffer pInBuffer = ByteBuffer.wrap(
							mFrame.iData.clone(), 0, mFrame.iLen);// 这个地方不加个克隆直播回退就会出问题
					// 解码器会修改里面的内容
					// ByteBuffer pInBuffer
					// =ByteBuffer.wrap(mFrame.iData,320,mFrame.iLen-320);
					/*
					 * for(int i=0;i<mFrame.iLen;i++) { int
					 * datavalue=(mFrame.iData[i]+256)%256; if(datavalue!=0x96
					 * && datavalue!=0x97) Log.e("噪音","噪音值 is:"+datavalue);
					 * //else // Log.e("非噪音","iData[i] is:"+mFrame.iData[i]); }
					 */
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
						if (amrnb_dec == null) {
							amrnb_dec = new amrnbdec();
						}
						Pcmsize = amrnb_dec.DecodeOneFrame(pInBuffer,
								pPcmBuffer);
					} else if (playercore.audiotype == 1)// amrwb
					{
						if (amrwb_dec == null) {
							amrwb_dec = new amrwbdec();
						}
						Pcmsize = amrwb_dec.DecodeOneFrame(pInBuffer,
								pPcmBuffer);
					} else if (playercore.audiotype == 2)// g711
					{
						if (playercore.GetOpenLog())
							Log.w("g711a", "audio is g711a");
						if (g711a_dec == null) { // 语音
							g711a_dec = new g711adec();
						}
						synchronized (g711a_dec) {
							Pcmsize = g711a_dec.DecodeOneFrame(pInBuffer,
									pPcmBuffer);
						}
					} else if (playercore.audiotype == 3)// IMA adpcm 是4:1的压缩比率
					{
						if (playercore.GetOpenLog())
							Log.w("adpcm", "audio is adpcm");
						if (adpcm_dec == null) { // 语音
							adpcm_dec = new adpcmdec();
						}
						synchronized (adpcm_dec) {
							// Pcmsize=adpcm_dec.DecodeOneFrame(pInBuffer,
							// pPcmBuffer);
							Pcmsize = adpcm_dec.DecodeOneFrame(pInBuffer,
									pPcmBuffer);
						}
						/*
						 * if(junjiadpcm_dec==null) { //语音 junjiadpcm_dec=new
						 * junjiadpcmdec(); } synchronized (junjiadpcm_dec) {
						 * Pcmsize=junjiadpcm_dec.DecodeOneFrame(pInBuffer,
						 * pPcmBuffer); }
						 */

						int endtime = (int) (System.currentTimeMillis());
					} else if (playercore.audiotype == 4)// DONGJI IMA adpcm
					// 是4:1的压缩比率
					{
						if (playercore.GetOpenLog())
							Log.w("adpcmdj", "audio is adpcmdj");
						if (junjiadpcm_dec == null) { // 语音
							junjiadpcm_dec = new junjiadpcmdec();
						}
						synchronized (junjiadpcm_dec) {
							Pcmsize = junjiadpcm_dec.DecodeOneFrame(pInBuffer,
									pPcmBuffer);
						}
						int endtime = (int) (System.currentTimeMillis());
					} else if (playercore.audiotype == 5) {

						if (playercore.GetOpenLog())
							Log.w("aAcDecode", "audio is AAcDecode");
						if (aAcDecode == null) { // 语音
							aAcDecode = new aacDecode();
						}
						synchronized (aAcDecode) {
							Pcmsize = aAcDecode.DecodeOneFrame(pInBuffer,
									pPcmBuffer, 10 * 1024);

						}
					} else {
						if (playercore.GetOpenLog())
							Log.w("adpcm", "audio is pcm");
						Pcmsize = mFrame.iLen;
						pPcmBuffer = ByteBuffer.wrap(mFrame.iData, 0,
								mFrame.iLen);
					}

					if (Pcmsize > 0) {
						if (playercore.GetOpenLog())
							Log.d("AudioDecode", "mPacket_au left "
									+ playercore.GetAudioFrameLeft()
									+ " Pcmsize is:" + Pcmsize);
						if (pPcmBuffer != null)
							pPcmBuffer.position(0);

						byte[] pcmData = new byte[Pcmsize];
						pPcmBuffer.get(pcmData, 0, Pcmsize);
						if (audioTrack == null)
							return;

						if (playercore.DoublePPT) {
							audioTrack.write(pcmData, 0, Pcmsize);
						} else {
							if (!playercore.IsPPTaudio) {
								audioTrack.write(pcmData, 0, Pcmsize);
							}
						}
						// if (!playercore.IsPPTaudio || playercore.DoublePPT)//
						// 在PPT时候不播放出声音
						// // 当DoublePPT是双向语音时候也播放出声音
						// {
						// //
						// Log.d("AudioDecode","mPacket_au left "+playercore.mPacketaudio.size()+" Pcmsize is:"+Pcmsize);
						// // audioTrack.write(pcmData, 0, Pcmsize);
						// audioTrack.write(pcmData, 0, Pcmsize);
						// }

						int leftvideoframe = playercore.GetAudioFrameLeft();
						if (playercore.GetOpenLog())
							Log.w("Audio Decode", "Audio left:"
									+ leftvideoframe);
					} else {
						if (playercore.audiotype == 0) {
							if (amrnb_dec != null) {
								synchronized (amrnb_dec) {
									amrnb_dec.Cleanup();
									amrnb_dec = null;
								}
							}
							amrnb_dec = new amrnbdec();
						} else if (playercore.audiotype == 1) {
							if (amrwb_dec != null) {
								synchronized (amrwb_dec) {
									amrwb_dec.Cleanup();
									amrwb_dec = null;
								}
							}
							amrwb_dec = new amrwbdec();
						} else if (playercore.audiotype == 2)// g711
						{
							g711a_dec = null;
							g711a_dec = new g711adec();
						} else if (playercore.audiotype == 5) {
							if (aAcDecode != null) {
								synchronized (aAcDecode) {
									aAcDecode.Cleanup();
									aAcDecode = null;
								}
							}
							aAcDecode = new aacDecode();
						}
						Log.d("Audiodecode statu", "Audiodecode fail.........");
					}
					mFrame = null;
				} catch (OutOfMemoryError e1) {
					mFrame = null;
					Log.d("AudioDecode ",
							"AudioDecode OutOfMemoryError.........");
				}
			}
		}
	}
}
