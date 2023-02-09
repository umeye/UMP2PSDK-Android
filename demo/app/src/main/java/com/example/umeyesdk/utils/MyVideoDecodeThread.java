package com.example.umeyesdk.utils;

import java.nio.ByteBuffer;

import android.os.Message;
import android.util.Log;
import android.view.View;

import com.Player.Core.PlayerCore;
import com.Player.Source.TSourceFrame;
import com.video.VideoFrameInfor;
import com.video.h264.DecodeDisplay;
import com.video.h264.DecodeTimeStampLisenter;
import com.video.h264.DisplayHandler;
import com.video.h264.H264DecodeInterface;
import com.video.h264.UmVideoDecode;

	public class MyVideoDecodeThread extends Thread implements DecodeTimeStampLisenter {
		DecodeDisplay decodeDisplay;
		PlayerCore playercore;
		private H264DecodeInterface myH264Decode = null;
		private int FrameRate = 0;// 动态帧率
		DisplayHandler displayHandler;
		int lastvideoencode = 0;// 上一次的视频编码格式，如果不一样了要重置
		/**
		 * 是否内存溢出
		 */
		boolean RgbHaveOutOfMemory = false;
		int RgbOutOfMemoryIndex = 0;
		private int LastDecodeVideoWidth = 352;// 176;// 352;
		private int LastDecodeVideoHeight = 288;// 144; // 288;

		////播放显示的时间戳，当前用来HLS音视频同步
		private long displayTimeStamps;

		DecodeTimeStampLisenter decodeTimeStampLisenter;

		public MyVideoDecodeThread(DecodeDisplay decodeDisplay) {
			this.playercore = decodeDisplay.playercore;
			this.decodeDisplay = decodeDisplay;
			this.displayHandler = decodeDisplay.displayHandler;
			//yuvThreads = new Yuv2RGBThreads(PlayerCore.decodeCpuNums);
		}

		public void setDecodeTimeStampLisenter(DecodeTimeStampLisenter decodeTimeStampLisenter) {
			this.decodeTimeStampLisenter = decodeTimeStampLisenter;
		}

		@Override
		public void run() {
			Log.d("VideoThreadDecode", "VideoThreadDecode run");
			VideoDecode();
			FrameRate = 0;
			RgbOutOfMemoryIndex = 0;
			RgbHaveOutOfMemory = false;
			decodeDisplay.pYuvBuffer = null;
			//yuvThreads.stopDecode();
			if (myH264Decode != null) {
				synchronized (myH264Decode) {
					if (myH264Decode != null) {
						myH264Decode.destroy();
						myH264Decode = null;
					}

				}
			}
			Log.d("VideoThreadDecode", "VideoThreadDecode exit");
		}


		synchronized void VideoDecode() {
			decodeDisplay.CurrentPlayTime = 0;
			int decodeindex = 0;
			long decodetimeaverage = 0;
			long videoStarTime = 0;
			// long AllFrameRate = 0;
			long videoDecodeStarTime = 0;

			int onSecondFrame = 0;
			long onSecondTimeMiles = 0;
			playercore.isHaveVideodata = false;
			boolean isLostIFrame = false;

			while (playercore.ThreadisTrue) {
				try {
					//截圖
					decodeDisplay.isSnap();
					if (playercore.IsPausing)// 暂停
					{
						Thread.sleep(20);
						continue;
					}

					TSourceFrame mFrame = null;
					int leftvideo = playercore.GetVideoFrameLeft();


					// 超过PlayerCore.frameLostMax开始丢帧
					if (leftvideo > PlayerCore.LostFrameMax && playercore.GetPlayModel() == 0 && playercore.ServerType != 100
							&& playercore.ServerType != PlayerCore.HLSSERVER)// 丢帧机制
					{
						isLostIFrame = true;
						while (leftvideo > 0) {

							leftvideo = playercore.GetVideoFrameLeft();
							mFrame = playercore.GetNextVideoFrame();
							Log.d("VideoDecode", "缓冲区还剩余多少帧: " + leftvideo);
							if (mFrame == null) {
								if (playercore.GetOpenLog())
									Log.d("VideoDecode", "取完缓存队列，退出丢帧");
								break;
							}
							if (mFrame.Framekind == 1)// 等到遇到I帧,VI帧时候才退出丢帧
							{
								if (playercore.GetOpenLog())
									Log.d("VideoDecode", "遇到I帧，退出丢帧");
								break;
							}
							decodeDisplay.isRecord(mFrame);
							if (!playercore.ThreadisTrue)
								return;
						}
					} else {
						mFrame = playercore.GetNextVideoFrame();// 新加入
						//if(mFrame != null){
						//Log.i("frame kind->",mFrame.Framekind + "");
						//}
					}

					if (mFrame == null) {
						Thread.sleep(10);
						continue;
					} else {
						displayTimeStamps = mFrame.iPTS;
						if (playercore.GetOpenLog())
							Log.d("GetNextFrame", "VideoFrame timestamp:" + mFrame.iPTS);
					}

					if (playercore.ServerType == PlayerCore.HLSSERVER) {

						if (mFrame.iVideoFrameRate <= 0) {
							mFrame.iVideoFrameRate = 15;
						}
					} else {
						if (mFrame.iVideoFrameRate <= 0) {
							mFrame.iVideoFrameRate = 25;
						}
					}


//				Log.d("frame_rate", "mFrame: " + mFrame.iVideoFrameRate);

					videoStarTime = System.currentTimeMillis();
					decodeindex++;

//				mFrame.index = decodeindex;
					//小心设备给的帧的帧率与实际不符合，导致录像时候跳帧
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
						Log.d("total and current", "GetFileAllTime_Int:"
								+ decodeDisplay.CurrentPlayTime + ",因播放完或错误导致停止");
						continue;
					}
					if (mFrame.iData[0] == -1 && mFrame.iData[1] == -40
							&& mFrame.iData[2] == -1 && mFrame.iData[3] == -32
							&& mFrame.iData[6] == 0x4A && mFrame.iData[7] == 0x46
							&& mFrame.iData[8] == 0x49 && mFrame.iData[9] == 0x46)// MJPG富视安的
					{
						mFrame.EncodeType = 3;
					}
					if (isLostIFrame) {
						if (mFrame.Framekind == 1) {
							if (playercore.GetOpenLog())
								Log.d("VideoDecode", "丢帧后，遇到I帧:" + mFrame.Framekind);
							isLostIFrame = false;

						} else {
							if (playercore.GetOpenLog())
								Log.d("VideoDecode", "丢帧后，继续寻找下一I帧");
							decodeDisplay.isRecord(mFrame);

							continue;
						}
					}
					if (playercore.GetPlayModel() == 2) { // i帧模式
						if (mFrame.Framekind != 1) {
							decodeDisplay.isRecord(mFrame);
							continue;
						}
					} else {

						decodeDisplay.isRecord(mFrame);
					}
					decodeDisplay.dataCount += (mFrame.iLen);
					ByteBuffer pInBuffer = ByteBuffer.wrap(mFrame.iData, 0,
							mFrame.iLen);

					playercore.PowerLeft = mFrame.nParam2;// 对于东集来说是设备剩余电量
					playercore.DeviceVersionNo = mFrame.nParam1;

					pInBuffer.position(0);
					if (displayHandler.pRGBBuffer != null)
						displayHandler.pRGBBuffer.position(0);

					int DecodeLength = 0;
					VideoFrameInfor tmpFrameInfor = null;
					{
						if (myH264Decode != null) {
							if (mFrame.EncodeType != lastvideoencode)// 编码格式有切换
							{
								synchronized (myH264Decode) {
									if (myH264Decode != null)
										myH264Decode.destroy();
									myH264Decode = null;
									System.gc();
								}
							}
						}
						lastvideoencode = mFrame.EncodeType;
						if (myH264Decode == null) {
							myH264Decode = UmVideoDecode.createDecodeByType(
									playercore, mFrame.EncodeType, displayHandler);
							if (myH264Decode == null) {
								return;
							}
						}

						if (myH264Decode != null) {
							int ret = 0;
							long iSpan01 = 0;
							long iSpan02 = 0;
							long iSpan03 = 0;
							long iSpan04 = 0;
							synchronized (myH264Decode)// 同步解码器变量 主要是跟释放的时候同步
							{
								if (playercore.GetOpenLog())
									iSpan01 = System.currentTimeMillis();
								ret = myH264Decode.DecodeOneFrameEx(pInBuffer,
										mFrame.iLen);
								if (playercore.GetOpenLog())
									iSpan02 = System.currentTimeMillis();
								if (ret > 0) {
									if (videoDecodeStarTime == 0) {
										videoDecodeStarTime = System
												.currentTimeMillis();
									}
									int tmpwidht = myH264Decode.GetPictureWidth();
									int tmpheight = myH264Decode.GetPictureHeight();
									// 创建RGBbuffer
									if (!createRGBBuffer(tmpwidht, tmpheight)) {
										continue;
									}
									if (playercore.GetOpenLog())
										iSpan03 = System.currentTimeMillis();
									if (playercore.DisplayMode == 1) {
										if (decodeDisplay.pYuvBuffer != null) {
											{
												tmpFrameInfor = myH264Decode
														.GetYuv(decodeDisplay.pYuvBuffer);
											}
										}
									} else {
										tmpFrameInfor = myH264Decode.Yuv2Rgb(
												displayHandler.pRGBBuffer,
												playercore.FMT_RGB);
									}
									if (playercore.GetOpenLog()) {
										iSpan04 = System.currentTimeMillis();
										long decost = (iSpan02 - iSpan01);
										long yuvcost = (iSpan04 - iSpan03);
										long allcost = decost + yuvcost;
										Log.w("DisplayThread",
												"Decode cost:"
														+ decost
														+ ", Yuv2Rgb cost:"
														+ yuvcost
														+ ", all cost:"
														+ allcost
														+ ", VideoLeft:"
														+ playercore
														.GetVideoFrameLeft()
														+ ", 编码类型："
														+ mFrame.EncodeType
														+ ",input length:"
														+ mFrame.iLen);

									}
								} else {
									Log.w("DisplayThread", "Decode fail....");//
								}
							}

							if (playercore.DisplayMode == 0) {
								if (tmpFrameInfor != null) {
									decodeDisplay.VideoWidth = tmpFrameInfor.VideoWidth;
									decodeDisplay.VideoHeight = tmpFrameInfor.VideoHeight;
									DecodeLength = tmpFrameInfor.DecodeLength;
								} else {
									decodeDisplay.VideoWidth = 0;
									decodeDisplay.VideoHeight = 0;
									DecodeLength = 0;
								}
							}
						}
					}
					// 如果解码成功，把解码出来的图片显示出来
					if (decodeDisplay.mImageView != null && decodeDisplay.mImageView.getVisibility() != View.VISIBLE)// 如果所属的ImageView隐藏就不贴图
					{
						Thread.sleep(20);
//					Log.e("mImageView",
//							"mImageView.getVisibility() == View.GONE");
						continue;
					}

					if (mFrame.dwPlayPos > 0)// 录像回放
						decodeDisplay.CurrentPlayTime = mFrame.dwPlayPos;// 毫秒为单位*1000);
					else
						decodeDisplay.CurrentPlayTime = mFrame.iPTS;// 毫秒为单位*1000);
					if (mFrame.iPTS != 0) {
						decodeDisplay.CurrentTime = mFrame.iPTS;
					}

					if (playercore.DisplayMode == 1)
						DecodeLength = 1;
					if (DecodeLength > 0) {
						if (playercore.DisplayMode == 0 || mFrame.EncodeType == 3)// JPG方式的还是要走老一套
						{
							if (decodeDisplay.mImageView != null)// ImageView方式贴图
							{
								if (decodeDisplay.mImageView.getVisibility() != View.GONE) // 隐藏的就不贴图
								{
									if (playercore.GetOpenLog())
										Log.w("发送贴图事件", "");
									Message msg = displayHandler.obtainMessage(1);
									msg.arg1 = decodeDisplay.VideoWidth;
									msg.arg2 = decodeDisplay.VideoHeight;
									msg.obj = decodeDisplay.isRecord;
									displayHandler.sendMessage(msg);// DisplayHandler.obtainMessage());
								}
							}
						}
						decodetimeaverage += System.currentTimeMillis()
								- videoStarTime;
						int leftvideoframe = playercore.GetVideoFrameLeft();
						if (decodeindex < 1)
							Log.w("Decode", "decodeindex:" + decodeindex
									+ "cost time:" + decodetimeaverage
									/ decodeindex + " " + decodeDisplay.VideoWidth
									+ " X " + decodeDisplay.VideoHeight + "FPS:"
									+ playercore.FrameRate + "left:"
									+ leftvideoframe);
						else {
							if (playercore.GetOpenLog())
								Log.w("Decode", "decodeindex:" + decodeindex
										+ "cost time:" + decodetimeaverage
										/ decodeindex + " "
										+ decodeDisplay.VideoWidth + " X "
										+ decodeDisplay.VideoHeight + "FPS:"
										+ playercore.FrameRate + "left:"
										+ leftvideoframe);
						}

						// 直播或者监控
						{
							if (playercore.GetPlayModel() == 0
									&& playercore.ServerType != 100 && playercore.ServerType != PlayerCore.HLSSERVER)// 实时模式
							{
								if (playercore.GetOpenLog())
									Log.w("Decode", "实时模式");
								playercore.FrameRate = (int) ((1000 * decodeindex) / (System
										.currentTimeMillis() - videoDecodeStarTime));
							} else if (playercore.GetPlayModel() == 1)// 流畅模式
							{
								if (FrameRate == 0) {
									FrameRate = 15;
								}
								if (playercore.PtzControling) {
									if (playercore.GetOpenLog())
										Log.w("Decode", "云台控制模式");
									Thread.sleep(10);
									playercore.FrameRate = (int) ((1000 * decodeindex) / (System
											.currentTimeMillis() - videoDecodeStarTime));
								} else {
									Thread.sleep(getNextSleepTime(videoStarTime, System.currentTimeMillis(), leftvideoframe));
								}
							} else if (playercore.GetPlayModel() == 2)// I帧模式
							{
								if (playercore.GetOpenLog())
									Log.w("Decode", "省电模式");
								Thread.sleep(10);
								playercore.FrameRate = (int) ((1000 * decodeindex) / (System
										.currentTimeMillis() - videoDecodeStarTime));
							} else if (playercore.ServerType == 100
									&& playercore.ControlMp4PlaySpeed != 0) {
								int iSpan = (int) (System.currentTimeMillis() - videoStarTime);
								int times = 1000 / playercore.ControlMp4PlaySpeed
										- iSpan;
								// Log.w("Decode", "录像延时：" + times);
								if (times > 0) {
									Thread.sleep(times);
								}

							} else if (playercore.ServerType == PlayerCore.HLSSERVER) {

								playercore.FrameRate = mFrame.iVideoFrameRate;
								int m_dwShouldSpanMS = GetFrameTime(mFrame.iVideoFrameRate);//根据帧率确定显示时间
								int iSpan = (int) (System.currentTimeMillis() - videoStarTime);
								int iShoud = m_dwShouldSpanMS - iSpan;
								//真正的视频显示时间为时间戳+解码时间
								displayTimeStamps = displayTimeStamps + iSpan;
								long audioTimeStamps = decodeTimeStampLisenter.getDisplayTimeStamps();
								if (iShoud > 0) {
									//以音频时间戳为基准，控制下一帧视频解码时间，以此来音视频同步
									if (audioTimeStamps > 0 && audioTimeStamps - displayTimeStamps > iShoud) {
										if (playercore.GetOpenLog())
											Log.d("HlsVideoDecode", "Audio is faster than video");
										Thread.sleep(iShoud / 2);
									} else {
										if (playercore.GetOpenLog())
											Log.d("HlsVideoDecode", "video display delay:" + iShoud + "ms");
										Thread.sleep(iShoud);
									}

								}

							}
						}

						onSecondFrame++;
						if (onSecondTimeMiles == 0) {
							onSecondTimeMiles = System.currentTimeMillis();
						} else {
							if (System.currentTimeMillis() - onSecondTimeMiles > 1000) {
								onSecondTimeMiles = System.currentTimeMillis();
								decodeDisplay.DisplayFrameRate = onSecondFrame;
								// Log.i("DisplayFrameRate","DisplayFrameRate:"+DisplayFrameRate);
								onSecondFrame = 0;
							}
						}
						// 设置进度
						if (!playercore.isHaveVideodata) {
							playercore.setProgress(100);
						}
						playercore.isHaveVideodata = true;// 获得了正确的视频数据
					} else {
						Log.w("Decode statu", "Decode fail... mFrame.iLen is:"
								+ mFrame.iLen + ",编码类型：" + mFrame.EncodeType + ",帧类型：" + mFrame.Framekind);
					}
					mFrame = null;
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

			displayHandler.sendMessage(displayHandler.obtainMessage(0));
		}

		public int GetFrameTime(int FrameRate) {
			int iRet;
			iRet = (1000 / FrameRate);
			return iRet;
		}

		public boolean createRGBBuffer(int tmpwidht, int tmpheight) {
			if (displayHandler.pRGBBuffer == null) {
				if (RgbHaveOutOfMemory) {
					RgbOutOfMemoryIndex++;
					if (RgbOutOfMemoryIndex < 10) {
						Log.e("RgbOutOfMemoryIndex", "RgbOutOfMemoryIndex is "
								+ RgbOutOfMemoryIndex);
						return false;
					}
				}
				try {
					LastDecodeVideoWidth = myH264Decode.GetPictureWidth();
					LastDecodeVideoHeight = myH264Decode.GetPictureHeight();

					if (playercore.DisplayMode == 1)// 只支持565的RGB
					{
						if (decodeDisplay.pYuvBuffer == null)
							decodeDisplay.pYuvBuffer = ByteBuffer
									.allocate((tmpwidht * tmpheight * 3) >> 1);
						displayHandler.pRGBBuffer = ByteBuffer
								.allocate((tmpwidht + 10) * tmpheight * 3 << 1);
					} else {
						if (playercore.FMT_RGB == H264DecodeInterface.FMT_RGBA32) {
							displayHandler.pRGBBuffer = ByteBuffer
									.allocate((tmpwidht + 10) * tmpheight << 2);
						} else {
							if (decodeDisplay.pYuvBuffer == null)
								decodeDisplay.pYuvBuffer = ByteBuffer
										.allocate((tmpwidht * tmpheight * 3) >> 1);
							displayHandler.pRGBBuffer = ByteBuffer
									.allocate((tmpwidht + 10) * tmpheight * 3 << 1);
						}
					}
				} catch (OutOfMemoryError e) {
					Log.e("OutOfMemoryError",
							"pRGBBuffer OutOfMemoryError.........");
					RgbHaveOutOfMemory = true;
					try {
						decodeDisplay.StopPlayerCore();
						decodeDisplay.RealeaseGC();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					e.printStackTrace();
					return false;
				}
				RgbOutOfMemoryIndex = 0;
				RgbHaveOutOfMemory = false;
			} else if (LastDecodeVideoWidth != tmpwidht
					|| LastDecodeVideoHeight != tmpheight) {
				try {
					if (LastDecodeVideoWidth * LastDecodeVideoHeight < tmpwidht
							* tmpheight) {
						synchronized (displayHandler.pRGBBuffer) {
							displayHandler.pRGBBuffer = null;
							decodeDisplay.pYuvBuffer = null;
							System.gc();
							if (playercore.DisplayMode == 1)// 只支持565的RGB
							{

								displayHandler.pRGBBuffer = ByteBuffer
										.allocate((tmpwidht + 10) * tmpheight << 1);
							} else {
								if (playercore.FMT_RGB == H264DecodeInterface.FMT_RGBA32) {
									displayHandler.pRGBBuffer = ByteBuffer
											.allocate((tmpwidht + 10) * tmpheight << 2);
								} else {
									decodeDisplay.pYuvBuffer = ByteBuffer
											.allocate((tmpwidht * tmpheight * 3) >> 1);
									displayHandler.pRGBBuffer = ByteBuffer
											.allocate((tmpwidht + 10) * tmpheight << 1);
								}
							}
						}
					}
				} catch (OutOfMemoryError e) {
					Log.e("OutOfMemoryError",
							"pRGBBuffer OutOfMemoryError.........");
					try {
						decodeDisplay.RealeaseGC();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
				// *****************hyl add
				// 20141024*************************
				LastDecodeVideoWidth = tmpwidht;
				LastDecodeVideoHeight = tmpheight;
				// if (myH264Decode != null)// 尺寸改变了重置解码器
				// {
				// synchronized (myH264Decode) {
				// if (myH264Decode != null)
				// myH264Decode.destroy();
				// myH264Decode = null;
				// System.gc();
				// myH264Decode = new LysH264Decode();
				// myH264Decode.initEx(lastvideoencode);
				// }
				// }
				return true;
				// ******************************************
			}
			return true;
		}

		@Override
		public long getDisplayTimeStamps() {
			return 0;
		}

		/**
		 * @param startDecodeTime 解码开始时间戳
		 * @param endDecodeTime   解码结束时间戳
		 * @param leftVideoFrames 剩余帧数
		 * @return
		 */
		long getNextSleepTime(long startDecodeTime, long endDecodeTime, int leftVideoFrames) {

			int Multiple = 0;    //倍数
			int Remainder = 0;    //余数
			int Rote = 0;
			int Time = 0;      //休眠时间

			Multiple = leftVideoFrames / 25;
			Remainder = leftVideoFrames % 25;
			switch (Multiple) {
				case 0:
					switch (Remainder) {
						case 0:
						case 1:
						case 2:
							Rote = 9;
							break;
						case 3:
						case 4:
							Rote = 11;
							break;
						case 5:
						case 6:
							Rote = 13;
							break;
						case 7:
						case 8:
							Rote = 15;
							break;
						default:
							Rote = Remainder + 15;
							break;
					}
					break;
				case 1:
					Rote = (int) (1.5 * 25);
					break;
				case 2:
					Rote = (int) (1.75 * 25);
					break;
				case 3:
					Rote = 2 * 25;
					break;
				case 4:
					Rote = (int) (2.25 * 25);
					break;
				default:
					Rote = 5 * 25;
					break;
			}

			Time = GetFrameTime(Rote);
			long tempTime = (Time - (endDecodeTime - startDecodeTime));
			// Log.d("getNextSleepTime", "leftVideoFrames:" + leftVideoFrames + ",tempTime:" + tempTime);
			if (tempTime > 0) {
				return tempTime;
			} else
				return 1;
		}
	}

