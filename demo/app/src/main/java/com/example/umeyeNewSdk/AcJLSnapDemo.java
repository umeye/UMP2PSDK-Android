package com.example.umeyeNewSdk;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.Player.Core.PlayerClient;
import com.Player.Core.PlayerCore;
import com.Player.Core.CoustomFun.Entity.CFResponse;
import com.Player.Core.UserImg.UserImgEntity.AddImgStruct;
import com.Player.Core.UserImg.UserImgEntity.DelImgStruct;
import com.Player.Core.UserImg.UserImgEntity.UserImgCompareInfo;
import com.Player.Core.UserImg.UserImgEntity.UserImgSnapImgInfo;
import com.Player.Core.UserImg.UserImgEntity.UserImgStruct;
import com.Player.Source.OnP2PDataListener;
import com.example.umeyesdk.R;
import com.igexin.sdk.PushManager;
import com.Player.Core.Utils.CommenUtil;

import java.util.ArrayList;

public class AcJLSnapDemo extends Activity{
	private Button mBack;
	private String conn_parm = "VendorId=1009,DevId=uvks026d9sux,DevIp=,DevPort=0,DevUserName=admin,DevUserPwd=123,DevChNo=0,DevStreamNo=1";//此参数为playNode的connparm参数，此为演示，指定固定参数
	//也可由各连接数据指定，umsp协议 VendorId=1009 ，雄迈设备为2060
	private ImageView imgDown,imgCompare,imgSnap;
	private ImageView videoImg;
	public static final int  NPC_D_UMSP_CUSTOM_FUNCID_CMP_DATA = 65541;//对比数据
	public static final int  NPC_D_UMSP_CUSTOM_FUNCID_CAP_JPG = 65542;//上传实时抓拍图片
	public static final int  REQUEST_WHITE_USER = 2;//白名单
	public static final int  REQUEST_BLACK_USER = 1;//黑名单
	public static final int  REQUEST_NO_WHITE_BLACK_USER = 3;//非黑白名单

	//添加错误码对照
	public static final int	 WB_OK = 1;                                 //图片添加成功
	public static final int	 WB_FAILE = -1;	                            //图片添加失败
	public static final int	 WB_COLLECT_ERROR = -2;                     //图片提取特征值失败
	public static final int	 WB_FILEINDEX_ERROR = -3;                   //图片名称或编号重复
	public static final int  WB_LIB_FULL = -4;                          //名单库已满，无法添加
	public static final int	 WB_ADD_TIME_OUT = -5;                      //图片添加超时
	public static final int	 WB_PARA_ERROR	= -6;                       //参数错误
	public static final int	 WB_FILE_BIG		= -7;                	//图片过大，添加失败（上限960*960）
	public static final int	 WB_SPACE_ERROR	= -8;                       //设备存储空间不足
	public static final int	 WB_FILE_OPEN_ERROR	= -9;                   //文件打开失败
	public static final int	 WB_NO_DBFILE	= -10;                      //未检测到数据库
	public static final int	 WB_FILE_ERROR	= -11;                      //图片读取失败
	public static final int	 WB_DBFILE_BAD	= -12;                      //数据库文件损坏
	public static final int	 WB_PIC_QUALITY_ERROR = -13;                //图片质量差，无法添加
	public static final int	 WB_FILE_WHSIZE_ERROR = -14;                //图片宽高不能为奇数
	public static final int	 WB_FILE_FACE_ERROR	 = -15;                 //检测人脸失败（无人脸或多张人脸）
	public static final int	 WB_PIC_FORMAT_ERROR = -16;                 //图片格式错误（支持JPG）

	//报警类型定义
	public int NPC_D_MON_CSD_ALARM_EVENT_BLACK_MODE			=			22;              //黑名单报警
	public int NPC_D_MON_CSD_ALARM_EVENT_WHILE_MODE			=			23;              //白名单报警
	public int NPC_D_MON_CSD_ALARM_EVENT_NON_WHILE_MODE		=			24;              //非白名单报警

	//参数设置功能id定义
	public static final int JL_JSON_SET_CONFIG = 65790; // 设置配置
	public static final int JL_JSON_GET_CONFIG = 65791;	// 获取配置

	private PlayerCore player;
	private PlayerClient pc;
	private String TAG = "img_function";
	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what){
				case 0x11:
					imgDown.setImageBitmap((Bitmap) msg.obj);
					break;
				case 0x12:
					imgCompare.setImageBitmap((Bitmap)msg.obj);
					break;
				case 0x13:
					imgSnap.setImageBitmap((Bitmap) msg.obj);
					break;
			}
			return false;
		}
	});
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_img_test);
		mBack = (Button) findViewById(R.id.back_btn);
		mBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		imgDown = (ImageView) findViewById(R.id.test_get_img);
		imgSnap = (ImageView) findViewById(R.id.test_snap_img);
		imgCompare = (ImageView) findViewById(R.id.test_compare_img);
		videoImg = (ImageView) findViewById(R.id.video_img);
		player = new PlayerCore(AcJLSnapDemo.this);
		player.setP2pPortDataCallback(new OnP2PDataListener() {//设置回调数据

			@Override
			public void callBackData(long camrea, int function, long pData, int size) {
				Log.i(TAG,"camrea->" + camrea +  " function->" + function + " size is->"  + size);
				switch (function){
					case NPC_D_UMSP_CUSTOM_FUNCID_CMP_DATA://转对比数据结构体
						UserImgCompareInfo userImgCompareInfo = pc.JLImageCompareInfo(pData,size);
						if(userImgCompareInfo != null){
							Log.i(TAG,"compare change success");
							Bitmap bitmap = CommenUtil.getBitmapFromByte(userImgCompareInfo.i_snapImg);
							if(bitmap != null){
								Message message = new Message();
								message.what = 0x12;
								message.obj = bitmap;
								handler.sendMessage(message);
							}
						}
						break;
					case NPC_D_UMSP_CUSTOM_FUNCID_CAP_JPG://转抓拍图片结构体
						UserImgSnapImgInfo userImgSnapImgInfo = pc.JLImageSnapInfo(pData,size);
						if(userImgSnapImgInfo != null){
							Log.i(TAG,"snap info change success");
							Bitmap bitmap = CommenUtil.getBitmapFromByte(userImgSnapImgInfo.i_faceImg);
							if(bitmap != null){
								Message message = new Message();
								message.what = 0x13;
								message.obj = bitmap;
								handler.sendMessage(message);
							}
						}
						break;
				}
			}


			@Override
			public void callBackDataEx(long i, int i1, byte[] bytes, int i2) {

			}
		});
		player.InitParam(conn_parm, -1, videoImg);
		player.Play();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				pc = new PlayerClient();
				int ret = pc.CameraConnect(conn_parm);//连接设备
				if(ret == 1){//设备连接成功
					ArrayList<UserImgStruct> list =  pc.getAllUserImg(conn_parm,REQUEST_WHITE_USER);//白名单
					if(list != null && list.size() > 0){
						Log.i(TAG,list.size() + "");
						UserImgStruct userImgStruct = list.get(0);
						userImgStruct.iBWMode = REQUEST_WHITE_USER;//原始数据结构体中未对类型赋值，手动赋值
						Bitmap bitmap = pc.getUserImgByIndex(conn_parm,userImgStruct);
						if(bitmap != null){
							Message message = new Message();
							message.what = 0x11;
							message.obj = bitmap;
							handler.sendMessage(message);
						}

						DelImgStruct delImgStruct = new DelImgStruct();
						delImgStruct.i_iBWMode = list.get(0).iBWMode;
						delImgStruct.i_iFileIndex = list.get(0).iFileIndex;
						delImgStruct.i_iLibIndex = list.get(0).iLibIndex;
						delImgStruct.i_sFileName = list.get(0).sFileName;
						int delRet = pc.deleteUserImg(conn_parm,delImgStruct);
						Log.i("img_function_del_ret",delRet + "");
						ArrayList<UserImgStruct> listDel =  pc.getAllUserImg(conn_parm,REQUEST_WHITE_USER);
						if(list != null){
							Log.i("img_function",listDel.size() + "");
						}
					}
					AddImgStruct addImgStruct = new AddImgStruct();
					addImgStruct.i_iCtrlType = 0;//客户端下发
					addImgStruct.i_iBWMode = REQUEST_WHITE_USER;//白名单
					addImgStruct.i_sImgId = "sadsdasa";
					addImgStruct.i_sImgName = "android中文05";
					int addRet = pc.addUserImg(conn_parm,addImgStruct,"/sdcard/android_test.jpg");//添加图片较多要求,参考错误码
					Log.i(TAG,addRet + "");//对比错误码
					ArrayList<UserImgStruct> listadd =  pc.getAllUserImg(conn_parm,REQUEST_WHITE_USER);//重新获取
					if(listadd != null){
						Log.i(TAG,listadd.size() + "");
					}

					//参数设置，设备布撤防都用此接口，请求内容参考具体设备通讯协议
					CFResponse cfResponse = pc.CallCustomFuncExExHaveConnect(conn_parm, JL_JSON_GET_CONFIG,new String("{\"Name\":\"SmartFace.Param\"}").getBytes());//传入自定义json数据
					if(cfResponse.ret == 0){
						Log.i(TAG,"获取成功");
						Log.i(TAG,cfResponse.responseJson);
					}else{
						Log.i(TAG,"获取失败");
					}

					//服务器布撤防，参考其他布放模块，只是存在报警类型的改变
					pc.CameraDisconnect();//断开设备
				}else{//设备连接失败
					Log.i(TAG,"设备离线，连接失败!");
				}
			}
		});
		t.start();
	}

}
