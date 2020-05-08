package com.example.umeyesdk.utils;

import android.content.Context;

import com.example.umeyesdk.R;

/**
 * 错误码定义
 */
public class Errors {

    /// connection failed
    public static final int
            UM_WEB_API_ERROR_ID_CONN = 0,
    /// 协议头解析错误[protocol head parsing error]
    UM_WEB_API_ERROR_ID_HEAD = 1,
    /// 消息ID解析错误[message ID parsing error]
    UM_WEB_API_ERROR_ID_MSGID = 2,
    /// 协议主体解析错误[protocol bouncer parsing error]
    UM_WEB_API_ERROR_ID_BODY = 3,
    ///  成功[successful]
    UM_WEB_API_ERROR_ID_SUC = 200,
    /// 无效的请求，缺少参数，比如必填参数没有填写数据[invalid request, lacking parameters, such as not filling in requested parameters]
    UM_WEB_API_ERROR_ID_BAD_REQUEST = 400,
    /// 该用户在其他地方登录，当前登录失效
    UM_WEB_API_ERROR_ID_HTTP_UNAUTHORIZED = 401,
    /// 数据非法，请求被拒绝，比如：登录密码恢复出厂设置的时候，请求的用户没有在该手机登录过，就会提示该错误码[request rejected, for example, after the password is reset to factory setting, if the user did not sign in on the phone before, the error code pops out]
    UM_WEB_API_ERROR_ID_FORBIDDEN = 403,
    /// 请求没找到。不支持该功能的请求[request not found. the request for this function unsupported]
    UM_WEB_API_ERROR_ID_NOT_FOUND = 404,
    /// 非法请求。未登录，请登录[illegal request. unlogged in, pls login]
    UM_WEB_API_ERROR_ID_NOT_ALLOWED = 405,
    /// 请求不被接受。参数错误，比如：用户名不正确[request unaccepted. parameter error, such as wrong user name]
    UM_WEB_API_ERROR_ID_NOT_ACCEPTABLE = 406,
    /// 请求发送冲突，数据中数据库已经存在，比如用户已注册，设备id已添加[request conflict, for example, the registered user name already exists]
    UM_WEB_API_ERROR_ID_CONFLICT = 409,
    /// 请求用户太多[too many requesting users]
    UM_WEB_API_ERROR_ID_TOO_MANY_REQUESTS = 429,
    /// 请求被拒绝。登录失效，请重新登录[request rejected. Login failed. Pls login again.]
    UM_WEB_API_ERROR_ID_ILLEGAL = 451,
    /// 服务器错误，常见错误：1、数据库操作错误，查询不到符合条件的数据，2、请求外部资源失败
    UM_WEB_API_ERROR_ID_DB = 500,
    /// 账号未激活
    UM_WEB_API_ERROR_ID_USER_NOT_ACTIVE = 508,
    /// 账号停用
    UM_WEB_API_ERROR_ID_USER_NOT_ENABLE = 509,
    /// 超过最大限制
    UM_WEB_API_ERROR_ID_MAX_LIMIT = 510,
    /// 邮箱地址错误
    UM_WEB_API_ERROR_EMALL = 511,
    /// 密码错误
    UM_WEB_API_ERROR_PASSWORD = 512,
    //验证码错误
    UM_WEB_API_ERROR_VERIFY_CODE = 514,
    /// 用户名不存在
    UM_WEB_API_ERROR_ID_USER = 899999,
    /// 获取认证服务器错误，比如：用户名不存在，网络不好
    UM_WEB_API_ERROR_ID_GETQUTH = 899998,
    /// 分享设备失败，不能分享给自己
    UM_WEB_API_ERROR_ID_SHARK_USER = 899997,
    /// 成功
    UM_WEB_API_SUCCESS = 200;


    /**
     *
     * @param context
     * @param code 错误码
     * @param defStr 其他提示
     */
    public static void showDesc(Context context, int code, int defStr) {
        int sError = defStr;
        switch (code) {
            case UM_WEB_API_ERROR_ID_CONN:
                sError = R.string.connect_fail;
                break;
            case UM_WEB_API_ERROR_ID_HEAD:
            case UM_WEB_API_ERROR_ID_MSGID:
            case UM_WEB_API_ERROR_ID_BODY:
                break;
            case UM_WEB_API_ERROR_ID_NOT_FOUND://请求没找到；一般是指客户端的请求在服务端找不到相应的处理函数。例如：(1)不支持请求中指定的功能消息编号处理
                sError = R.string.not_support_api;
                break;
            case UM_WEB_API_ERROR_ID_BAD_REQUEST://无效的请求,缺少参数
                sError = R.string.format_err;
                break;
            case UM_WEB_API_ERROR_ID_NOT_ALLOWED://请求不被允许；表示客户端没有足够权限，导致请求的消息内容，服务器不允许执行；例如：(1)客户端在请求该消息之前，没有登录成功
            case UM_WEB_API_ERROR_ID_ILLEGAL://非法访问；表示客户端发送的是非法请求；例如：(1)客户端发送的会话ID不正确
                sError = R.string.outdate_relogin;
                break;
            case UM_WEB_API_ERROR_ID_NOT_ACCEPTABLE://请求不被接受；一般是指客户端请求不被服务端接受处理。请求数据库不存在的数据；例如：(1)登录时传入的用户名和/或密码不正确
//                    sError = R.string.login_name_pwd_err;
                sError = R.string.verify_failed;
                break;
            case UM_WEB_API_ERROR_ID_USER:
                sError = R.string.username_nonexistent;
                break;
            case UM_WEB_API_ERROR_ID_CONFLICT://请求发生冲突，数据库中已存在，比如用户已注册，设备id已添加
                sError = R.string.uid_exits_not_add_dev;
                break;
            case UM_WEB_API_ERROR_PASSWORD://密码错误
                sError = R.string.passworderro;
                break;
            case UM_WEB_API_ERROR_VERIFY_CODE://验证码错误
                sError = R.string.register_fail_by_errcode;
                break;
            case UM_WEB_API_ERROR_EMALL://邮箱地址错误
                sError = R.string.email_address_error2;
                break;
            case UM_WEB_API_ERROR_ID_TOO_MANY_REQUESTS://请求次数太多,比如意见反馈只能1个小时一次
                sError = R.string.request_too_many_times;
                break;

        }
        Show.toast(context,sError);
    }

}
