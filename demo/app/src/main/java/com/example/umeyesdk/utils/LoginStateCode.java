package com.example.umeyesdk.utils;


import android.content.Context;

import com.example.umeyesdk.R;

/**
 * 登陆状态码
 * @author yin
 *
 */
public class LoginStateCode
{
	public final static int LOGIN_OK=0;//登陆成功
	/*public final static int NPC_D_MPI_MON_ERROR_SYS_ERROR =1; //系统错误
	public final static int NPC_D_MPI_MON_ERROR_CONNECT_FAIL =2; //连接失败
	public final static int NPC_D_MPI_MON_ERROR_USERID_ERROR =3; //用户名错误
	public final static int NPC_D_MPI_MON_ERROR_USERPWD_ERROR =4; //密码错误
	public final static int NPC_D_MPI_MON_ERROR_USER_PWD_ERROR =5; //用户名或密码错误
	public final static int NPC_D_MPI_MON_ERROR_CONNECTING =6; //正在连接
	public final static int NPC_D_MPI_MON_ERROR_CONNECTED =7; //已连接
	public final static int NPC_D_MPI_MON_ERROR_PLAY_FAIL =8; //播放失败
	public final static int NPC_D_MPI_MON_ERROR_FILE_NONENTITY =9; //文件不存在
	public final static int NPC_D_MPI_MON_ERROR_EXEC_ORDER_CALL_FAIL =10; //执行命令调用失败
	public final static int NPC_D_MPI_MON_ERROR_EXEC_ORDER_RET_FAIL =11; //执行命令结果失败
	public final static int NPC_D_MPI_MON_ERROR_ALLOC_RES_FAIL =12; //分配资源失败
	public final static int NPC_D_MPI_MON_ERROR_NO_CONNECT_CAMERA =13; //未连接摄像机
	public final static int NPC_D_MPI_MON_ERROR_INNER_OP_FAIL =14; //内部操作失败，如内存操作失败
	public final static int NPC_D_MPI_MON_ERROR_PLAYING =15; //正在播放
	public final static int NPC_D_MPI_MON_ERROR_NO_PLAY =16; //未播放
	public final static int NPC_D_MPI_MON_ERROR_NONSUP_VENDOR =17; //不支持的厂家
	public final static int NPC_D_MPI_MON_ERROR_REJECT_ACCESS =18; //权限不够
	public final static int NPC_D_MPI_MON_ERROR_CAMERA_OFFLINE =19; //摄像机离线
	public final static int NPC_D_MPI_MON_ERROR_ACCOUNT_LOGINED =20; //帐号已登录
	public final static int NPC_D_MPI_MON_ERROR_ACCOUNT_HAVE_EXPIRED =21; //用户帐号已过有效期
	public final static int NPC_D_MPI_MON_ERROR_ACCOUNT_NO_ACTIVE =22; //用户帐号未激活
	public final static int NPC_D_MPI_MON_ERROR_ACCOUNT_DEBT_STOP =23; //用户帐号已欠费停机*/


	public final static int NPC_D_MPI_MON_ERROR_SYS_ERROR							=1	;		//系统错误
	public final static int NPC_D_MPI_MON_ERROR_CONNECT_FAIL						=2	;		//连接失败
	public final static int NPC_D_MPI_MON_ERROR_DBACCESS_FAIL						=3	;		//访问数据库失败
	public final static int NPC_D_MPI_MON_ERROR_ALLOC_RES_FAIL						=4	;		//分配资源失败
	public final static int NPC_D_MPI_MON_ERROR_INNER_OP_FAIL						=5	;		//内部操作失败，如内存操作失败
	public final static int NPC_D_MPI_MON_ERROR_EXEC_ORDER_CALL_FAIL				=6	;		//执行命令调用失败
	public final static int NPC_D_MPI_MON_ERROR_EXEC_ORDER_RET_FAIL					=7	;		//执行命令结果失败
	public final static int NPC_D_MPI_MON_ERROR_FILE_NONENTITY						=8	;		//文件不存在
	public final static int NPC_D_MPI_MON_ERROR_OTHER_FAIL							=9	;		//其它原因失败
	public final static int NPC_D_MPI_MON_ERROR_NET_ERROR							=10	;		//网络错误
	public final static int NPC_D_MPI_MON_ERROR_REDIRECT							=11	;		//服务器请求客户端重定向
	public final static int NPC_D_MPI_MON_ERROR_PARAM_ERROR							=12	;		//传入参数格式错误
	public final static int NPC_D_MPI_MON_ERROR_ERROR_FUNCID						=13	;		//错误的功能ID消息
	public final static int NPC_D_MPI_MON_ERROR_MSG_PAST_TIME						=14	;		//该消息ID已过时，即已作废
	public final static int NPC_D_MPI_MON_ERROR_SYS_NO_GRANT						=15	;		//系统未授权

	public final static int NPC_D_MPI_MON_ERROR_USERID_ERROR						=101;			//用户ID或用户名错误
	public final static int NPC_D_MPI_MON_ERROR_USERPWD_ERROR						=102;			//用户密码错误
	public final static int NPC_D_MPI_MON_ERROR_USER_PWD_ERROR						=103;			//用户名或密码错误
	public final static int NPC_D_MPI_MON_ERROR_CONNECTING							=104;			//正在连接
	public final static int NPC_D_MPI_MON_ERROR_CONNECTED							=105;			//已连接
	public final static int NPC_D_MPI_MON_ERROR_PLAY_FAIL							=106;			//播放失败
	public final static int NPC_D_MPI_MON_ERROR_NO_CONNECT_CAMERA					=107;			//未连接摄像机
	public final static int NPC_D_MPI_MON_ERROR_PLAYING								=108;			//正在播放
	public final static int NPC_D_MPI_MON_ERROR_NO_PLAY								=109;			//未播放
	public final static int NPC_D_MPI_MON_ERROR_NONSUP_VENDOR						=110;			//不支持的厂家
	public final static int NPC_D_MPI_MON_ERROR_REJECT_ACCESS						=111;			//权限不够
	public final static int NPC_D_MPI_MON_ERROR_CAMERA_OFFLINE						=112;			//摄像机离线
	public final static int NPC_D_MPI_MON_ERROR_ACCOUNT_LOGINED						=113;			//帐号已登录
	public final static int NPC_D_MPI_MON_ERROR_ACCOUNT_HAVE_EXPIRED				=114;			//用户帐号已过有效期
	public final static int NPC_D_MPI_MON_ERROR_ACCOUNT_NO_ACTIVE					=115;			//用户帐号未激活
	public final static int NPC_D_MPI_MON_ERROR_ACCOUNT_DEBT_STOP					=116;			//用户帐号已欠费停机
	public final static int NPC_D_MPI_MON_ERROR_USER_EXIST							=117;			//用户已注册
	public final static int NPC_D_MPI_MON_ERROR_NOT_ALLOW_REG_NOPERM				=118;			//不允许注册（不在许可表中）
	public final static int NPC_D_MPI_MON_ERROR_NOT_ALLOW_REG_ATBLACK				=119;			//不允许注册（在黑名单中）
	public final static int NPC_D_MPI_MON_ERROR_SECCODE_HAVE_EXPIRED				=120;			//验证码已过期
	public final static int NPC_D_MPI_MON_ERROR_SECCODE_ERROR						=121;			//验证码错误
	public final static int NPC_D_MPI_MON_ERROR_ACCOUNT_EXIST						=122;			//帐号已存在
	public final static int NPC_D_MPI_MON_ERROR_NO_IDLE_STREAMSERVER				=123;			//无空闲流媒体服务器
	public final static int NPC_D_MPI_MON_ERROR_USER_NO_LOGIN						=124;			//用户未登录
	public final static int NPC_D_MPI_MON_ERROR_ACCOUNT_LEN_ERROR					=125;			//帐号长度错误
	public final static int NPC_D_MPI_MON_ERROR_EMP_ACC_USERID_NOT_EXIST			=126;			//接收授权的用户ID不存在
	public final static int NPC_D_MPI_MON_ERROR_IPADDR_BAN_LOGIN					=127;			//IP地址禁止登录
	public final static int NPC_D_MPI_MON_ERROR_CLIENTID_NOT_ALLOW_LOGIN			=128;			//客户端ID不允许登录
	public final static int NPC_D_MPI_MON_ERROR_TIMESECT_NOT_ALLOW_CAMERA			=129;			//该时间段不允许访问该摄像机
	/**
	 * 返回相应的描述
	 * @param state
	 * @param context
	 * @return
	 */
	public static String GetDes(int state,Context context)
	{
		String des=context.getString(R.string.UNKNOWN_ERROR)+state;
		switch(state)
		{
			case NPC_D_MPI_MON_ERROR_CLIENTID_NOT_ALLOW_LOGIN:
				des=context.getString(R.string.no_right_login);
				break;
			case LOGIN_OK:
				des=context.getString(R.string.NPC_D_MPI_MON_ERROR_CONNECT_FAIL);
				break;
			case NPC_D_MPI_MON_ERROR_SYS_ERROR:
				des=context.getString(R.string.NPC_D_MPI_MON_ERROR_SYS_ERROR);
				break;
			case NPC_D_MPI_MON_ERROR_CONNECT_FAIL:
				des=context.getString(R.string.NPC_D_MPI_MON_ERROR_CONNECT_FAIL);
				break;
			case NPC_D_MPI_MON_ERROR_USERID_ERROR:
				des=context.getString(R.string.NPC_D_MPI_MON_ERROR_USERID_ERROR);
				break;
			case NPC_D_MPI_MON_ERROR_USERPWD_ERROR:
				des=context.getString(R.string.NPC_D_MPI_MON_ERROR_USERPWD_ERROR);
				break;
			case NPC_D_MPI_MON_ERROR_USER_PWD_ERROR:
				des=context.getString(R.string.NPC_D_MPI_MON_ERROR_USER_PWD_ERROR);
				break;
			case NPC_D_MPI_MON_ERROR_CONNECTING:
				des=context.getString(R.string.NPC_D_MPI_MON_ERROR_CONNECTING);
				break;
			case NPC_D_MPI_MON_ERROR_CONNECTED:
				des=context.getString(R.string.NPC_D_MPI_MON_ERROR_CONNECTED);
				break;
			case NPC_D_MPI_MON_ERROR_PLAY_FAIL:
				des=context.getString(R.string.NPC_D_MPI_MON_ERROR_PLAY_FAIL);
				break;
			case NPC_D_MPI_MON_ERROR_FILE_NONENTITY:
				des=context.getString(R.string.NPC_D_MPI_MON_ERROR_FILE_NONENTITY);
				break;
			case NPC_D_MPI_MON_ERROR_EXEC_ORDER_CALL_FAIL:
				des=context.getString(R.string.NPC_D_MPI_MON_ERROR_EXEC_ORDER_CALL_FAIL);
				break;
			case NPC_D_MPI_MON_ERROR_EXEC_ORDER_RET_FAIL:
				des=context.getString(R.string.NPC_D_MPI_MON_ERROR_EXEC_ORDER_RET_FAIL);
				break;
			case NPC_D_MPI_MON_ERROR_ALLOC_RES_FAIL:
				des=context.getString(R.string.NPC_D_MPI_MON_ERROR_ALLOC_RES_FAIL);
				break;
			case NPC_D_MPI_MON_ERROR_NO_CONNECT_CAMERA:
				des=context.getString(R.string.NPC_D_MPI_MON_ERROR_NO_CONNECT_CAMERA);
				break;
			case NPC_D_MPI_MON_ERROR_INNER_OP_FAIL:
				des=context.getString(R.string.NPC_D_MPI_MON_ERROR_INNER_OP_FAIL);
				break;
			case NPC_D_MPI_MON_ERROR_PLAYING:
				des=context.getString(R.string.NPC_D_MPI_MON_ERROR_PLAYING);
				break;
			case NPC_D_MPI_MON_ERROR_NO_PLAY:
				des=context.getString(R.string.NPC_D_MPI_MON_ERROR_NO_PLAY);
			case NPC_D_MPI_MON_ERROR_NONSUP_VENDOR:
				des=context.getString(R.string.NPC_D_MPI_MON_ERROR_NONSUP_VENDOR);
			case NPC_D_MPI_MON_ERROR_REJECT_ACCESS:
				des=context.getString(R.string.NPC_D_MPI_MON_ERROR_REJECT_ACCESS);
			case NPC_D_MPI_MON_ERROR_CAMERA_OFFLINE:
				des=context.getString(R.string.NPC_D_MPI_MON_ERROR_CAMERA_OFFLINE);
			case NPC_D_MPI_MON_ERROR_ACCOUNT_LOGINED:
				des=context.getString(R.string.NPC_D_MPI_MON_ERROR_ACCOUNT_LOGINED);
			case NPC_D_MPI_MON_ERROR_ACCOUNT_HAVE_EXPIRED:
				des=context.getString(R.string.NPC_D_MPI_MON_ERROR_ACCOUNT_NO_ACTIVE);
				break;
			case NPC_D_MPI_MON_ERROR_ACCOUNT_NO_ACTIVE:
				des=context.getString(R.string.NPC_D_MPI_MON_ERROR_CONNECTING);
			case NPC_D_MPI_MON_ERROR_ACCOUNT_DEBT_STOP:
				des=context.getString(R.string.NPC_D_MPI_MON_ERROR_ACCOUNT_DEBT_STOP);
//		default:
//			des=context.getString(R.string.note_login_again);
//			break;
		}

		return des;
	}
}
