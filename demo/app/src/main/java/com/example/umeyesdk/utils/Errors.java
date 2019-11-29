package com.example.umeyesdk.utils;

import android.content.Context;

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
    /// 请求不被接受。参数错误，比如：用户名或密码不正确[request unaccepted. parameter error, such as wrong user name or password]
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
    // 权限错误
    UM_WEB_API_ERROR_NO_PERMISSION = 513,
    // 验证码错误
    UM_WEB_API_ERROR_VERIFY_CODE = 514,
    /// 用户名不存在
    UM_WEB_API_ERROR_ID_USER = 899999,
    /// 获取认证服务器错误，比如：用户名不存在，网络不好
    UM_WEB_API_ERROR_ID_GETQUTH = 899998,
    /// 分享设备失败，不能分享给自己
    UM_WEB_API_ERROR_ID_SHARK_USER = 899997;


    public static void showDesc(int aErrorId) {
        switch (aErrorId) {
            case UM_WEB_API_ERROR_ID_CONN:
                break;
            case UM_WEB_API_ERROR_ID_HEAD:
            case UM_WEB_API_ERROR_ID_MSGID:
            case UM_WEB_API_ERROR_ID_BODY:
                break;
            case UM_WEB_API_ERROR_ID_NOT_FOUND:
                break;
            case UM_WEB_API_ERROR_ID_BAD_REQUEST:
                break;
            case UM_WEB_API_ERROR_ID_NOT_ALLOWED:
            case UM_WEB_API_ERROR_ID_ILLEGAL:
                break;
            case UM_WEB_API_ERROR_ID_NOT_ACCEPTABLE:
                break;
            case UM_WEB_API_ERROR_ID_USER:
                break;
            case UM_WEB_API_ERROR_ID_CONFLICT:
                break;
            case UM_WEB_API_ERROR_NO_PERMISSION:
                break;
            case UM_WEB_API_ERROR_VERIFY_CODE:
                break;
            case UM_WEB_API_ERROR_ID_TOO_MANY_REQUESTS:
                break;
        }
    }

}
