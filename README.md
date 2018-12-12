# UMP2PSDK-Android

# 1.版本记录

|文档版本|版本说明|修改人|修改时间|
|----|---|---|---|
|1.0.1|增加设备状态接口说明|袁祖强|2018-7-13|
|1.0.0|初版|王伏|2018-7-4|

# 2.宏定义

## 用户模块错误码定义
```
错误码的定义应尽量兼容HTTP状态码，可以根据具体接口的需要进行扩展。
```

|错误码|HTTP状态码|说明|
|----|---|---|
|200|HTTP_OK|处理成功|
|400|HTTP_BAD_REQUEST|无效的请求,缺少参数|
|401|HTTP_UNAUTHORIZED|该用户在其他地方登入,当前登录失效|
|403|HTTP_FORBIDDEN|请求非法,比如:登录密码恢复出厂设置的时候,请求的用户没有在该手机登录过，删除分享|
|404|HTTP_NOT_FOUND|请求没找到；一般是指客户端的请求在服务端找不到相应的处理函数。例如：(1)不支持请求中指定的功能消息编号处理|
|405|HTTP_NOT_ALLOWED|请求不被允许；表示客户端没有足够权限，导致请求的消息内容，服务器不允许执行；例如：(1)客户端在请求该消息之前，没有登录成功|
|406|HTTP_NOT_ACCEPTABLE|请求不被接受；一般是指客户端请求不被服务端接受处理；例如：(1)登录时传入的用户名和/或密码不正确|
|409|HTTP_CONFLICT|请求发生冲突，数据库中已存在，比如用户已注册，设备id已添加|
|426|HTTP_UPGRADE_REQUIRED|客户端需要升级|
|429|HTTP_TOO_MANY_REQUESTS|请求次数太多,比如意见反馈只能1个小时一次|
|451|HTTP_ILLEGAL|非法访问；表示客户端发送的是非法请求；例如：(1)客户端发送的会话ID不正确|
|500|HTTP_INTERNAL_SERVER_ERROR|服务器错误,比如:数据库操作错误|
|501|HTTP_METHOD_NOT_IMPLEMENTED|指定方法未实现|
|502|HTTP_BAD_GATEWAY|无效的网关|
|503|HTTP_SERVICE_UNAVAILABLE|服务不可用|
|504|HTTP_GATEWAY_TIMEOUT|网关超时|
|505|HTTP_VERSION_NOT_SUPPORTED|该版本不受支持|
|507|HTTP_INSUFFICIENT_STORAGE|无效的存储空间，或者存储空间不足|
|508|HTTP_NO_ACTIVA_USER|账号未激活|
|509|HTTP_DISABLE_USER|账号停用|

## 设备控制模块错误码定义

|错误码|状态码|说明|
|----|---|---|
|1||连接中|
|2||播放中|
|3||连接失败|
|4||停止中|
|-102||密码错误|
|-101||用户名错误|
|-111||权限不够|
|0||准备就绪 |
|10||缓冲中|
|7||停止|
|其他||连接失败|

## 语言类型定义

|ID|详细说明|版本号|
|----|---|:---:|
|1|英文||
|2|简体||
|3|繁体||
|4|德文||
|5|法文||
|6|葡萄牙||
|7|俄文||
|8|意大利||
|9|土耳其||
|10|波兰||
|11|阿拉伯||
|12|波斯语||
|13|捷克语||
|14|丹麦语||
|15|芬兰语||
|16|希腊语||
|17|荷兰语||
|18|挪威语||
|19|西班牙语||
|20|瑞典语||
|21|葡萄牙语-巴西||
|22|印度语||
|23|日本||
|24|韩语||
|25|泰语||
|26|越南语||
|27|希伯来语||
|28|罗马尼亚语||
|29|匈牙利语||
|30|印度尼西亚语||
|31|乌克兰语||
|32|马来语||
|33|菲律宾语||
|34|立陶宛语||
|35|柬埔寨语||
|36|孟加拉语||
|37|南非语||
|38|冰岛语||
|39|白俄罗斯语||

## 节点类型定义

|ID|详细说明|版本号|
|----|---|:---:|
|0|目录|1.0|
|1|监控设备|1.0|
|2|监控设备通道|1.0|
|3|已过时|1.0|
|4|智能家居设备|3.0.0|
|5|智能家居设备通道|3.0.0|

## 设备类型定义

|ID|详细说明|版本号|
|----|---|:---:|
|0|监控设备：DVR、NVR|1.0|
|1|监控设备通道|1.0|
|2|智能家居设备-开关|3.0.0|
|3|智能家居通道-开关通道|3.0.0|

## 设备连接方式定义

|ID|详细说明|版本号|
|----|---|:---:|
|0|IP/域名直连|1.0|
|1|流媒体|1.0|
|2|P2P云|1.0|
|3|云流媒体|1.0|
|4|未知|1.0|

# 3.SDK启动模块

## 初始化启动参数（com.Player.web.websocket.ClientCore）
```
   /**
     * @brief 设置连接的服务器信息（Set up server information ）
     * 
     * @param context
     *            上下文 （Context）
     * @param aHost
     *            服务器地址（Server）
     * @param aPort
     *            服务器端口 （Port）
     * @param aClientId
     *            客户端唯一标识码 （Client unique identification code ）
     * @param aLangId
     *            客户端语言（language ）
     * @param aCustomFlag
     *            客户端定制标识码（Client custom identification code ）
     * @param aClientVer
     *            客户端版本号 （Client version）
     * @param aClientTime
     *            客户端当前时间 （ Client current，可为空字符串
     * @param sBackDoMainIp  
     *          域名解析失败采用的备用ip，可为空值，可为空字符串
     */
    public void setupHost(Context context, String aHost, int aPort,
            String aClientId, int aLangId, String aCustomFlag,
            String aClientVer, String aClientTime,String sBackDoMainIp)
```

## 启动sdk（com.Player.web.websocket.ClientCore）(此为异步方法，需等待handler 数据响应成功，方可进行用户系统操作，和设备模块操作)
```
    /**
     * @brief 优先选择用户选择的服务器（Preference server for user selection ）
     * 
     * @param con
     * @param handler
     */
    public void getCurrentBestServer(Context con, final Handler handler)

   /**
     * @brief 设置连接的服务器信息（Set up server information ）
     * 
     * @param context
     *            上下文 （Context）
     * @param aHost
     *            服务器地址（Server）
     * @param aPort
     *            服务器端口 （Port）
     * @param aClientId
     *            客户端唯一标识码 （Client unique identification code ）
     * @param aLangId
     *            客户端语言（language ）参考语言宏定义
     * @param aCustomFlag
     *            客户端定制标识码（Client custom identification code ）
     * @param aClientVer
     *            客户端版本号 （Client version）
     * @param aClientTime
     *            客户端当前时间 （ Client current，可为空字符串
     * @param sBackDoMainIp  
     *          域名解析失败采用的备用ip，可为空值，可为空字符串
     */
    public void setupHost(Context context, String aHost, int aPort,
            String aClientId, int aLangId, String aCustomFlag,
            String aClientVer, String aClientTime,String sBackDoMainIp)
```

# 4.用户系统模块（com.example.umeyesdk.api.WebSdkApi） 

## 注册
```
    /**
     * @param aUserId       用户ID 24位 , 限定字母，数字，下划线
     * @param aPassword     密码   20位 , 限定字母，数字，下划线
     * @param user_email    邮箱   32位 , 合法邮箱 
     * @param nickName      昵称   32位 , 限定英文，数字，下划线  
     * @param user_phone    手机号码 32位 , 限定数字
     * @param user_id_card  用户身份证id 24位
     */
    public static void registeredUser(final Context context,
            final ClientCore clientCore, String aUserId, String aPassword,
            String user_email, String nickName, String user_phone,
            String user_id_card)
```

## 登录
```
    /**
     * @param userName  用户ID, 24位, 限定字母，数字，下划线
     * @param password  密码, 20位, 限定字母，数字，下划线
     **/
    public static void loginServerAtUserId(final Context context,
            final ClientCore clientCore, String userName, String password,
            final Handler handler)
```

## 注销
```
    /**
     * @param disableAlarm  是否取消报警推送
     * @param handler
     */
    public static void logoutServer(final Activity activity,
            final ClientCore clientCore, int disableAlarm)
```

## 忘记密码-发送邮件到注册邮箱
```
    /**
     * @param user_id   需要重置密码的用户名
     * @param language  发送邮件的语言，具体参考文档语言宏定义
     */
    public static void resetUserPassword(final Context context,
            final ClientCore clientCore, String user_id, int language)
```

## 修改密码
```
    /**
     * @param oldPassword   旧密码，20位 , 限定字母，数字，下划线
     * @param newPassword   新密码，20位 , 限定字母，数字，下划线
     */
    public static void modifyUserPassword(final Context context,
            final ClientCore clientCore, String oldPassword, String newPassword)
```

## 获取设备列表
```
    /**
     * @param parent_node_id    父节点ID
     * @param page_index        分页功能，指定从第几页开始，是可选的，默认不分页；
     * @param page_size         分页功能，每页返回的记录数，是可选的，默认不分页；
     */
    public static void getNodeList(final Context context,
            final ClientCore clientCore, String parent_node_id, int page_index,
            int page_size, final Handler handler)
```

## 增加设备
```
    /**
     * @param node_name         名称 28位， 限定中文，字母，数字，下划线  
     * @param parent_node_id    父节点ID
     * @param node_type         节点类型 : 0表示目录节点、1表示设备节点、2表示摄像机节点
     * @param conn_mode         连接模式 ：参见设备连接模式定义
     * @param vendor_id         厂商ID，当node_type不为0的时候是必须的，取值1009。
     * @param dev_umid          设备umid 28位，限定字母，数字，下划线        
     * @param dev_addr          设备IP或者域名
     * @param dev_port          设备端口 umsp协议默认5800
     * @param dev_user          设备用户名 60位，限制字母，数字，下划线
     * @param dev_passwd        设备密码 60位，限制字母，数字，下划线
     * @param dev_ch_num        设备通道数 dvr/nvr通道数
     * @param dev_ch_no         设备通道号 node_type为2时有效，添加dvr/nvr指定的通道号
     * @param dev_stream_no     设备请求码流 0:主码流，1，子码流
     */
    public static void addNodeInfo(final Context context,
            final ClientCore clientCore, String node_name, String parent_node_id,
            int node_type, int conn_mode, int vendor_id, String dev_umid,
            String dev_addr, int dev_port, String dev_user, String dev_passwd,
            int dev_ch_num, int dev_ch_no, int dev_stream_no,
            final Handler handler)
```

## 修改设备
```
    /**
     * @param node_id       节点ID
     * @param node_name     名称 28位 限定中文，字母，数字，下划线 
     * @param dev_umid      设备umid 28位 限定字母，数字，下划线
     * @param dev_addr      设备IP地址/域名
     * @param dev_port      设备端口
     * @param dev_user      设备用户名     60位 限定字母，数字，下划线 
     * @param dev_passwd    设备密码          
     * @param dev_ch_no     摄像机节点才有效， nvr/dvr的通道号 0开始
     * @param dev_stream_no 码流  0 主码流 1子码流
     */
    public static void modifyNodeInfo(final Context context,
            final ClientCore clientCore, String node_id, String node_name,
            int node_type, int vendor_id, String dev_umid, String dev_addr,
            int dev_port, String dev_user, String dev_passwd, int dev_ch_no,
            int dev_stream_no, final Handler handler)
```

## 删除设备
```
    /**
     * @param node_id     节点ID
     * @param node_type   节点类型            
     * @param id_type     ID类型，0：用户id,1:设备组id,2:授权设备，默认填写0
     */
    public static void deleteNodeInfo(final Context context,
            final ClientCore clientCore, String node_id, int node_type,
            int id_type, final Handler handler)
```

## 获取设备状态
```
    /** 
     * @param devs 设备umid列表
     */
     public static void getDeviceStateByUmid(ClientCore clientCore,List<String> devs)
```

## 查询报警记录
```
    public static void queryAlarmList(ClientCore clientCore)
```

## 删除全部报警记录
```
    public static void deleteAllAlarm(ClientCore clientCore)
```

## 删除指定id报警记录

```
    /**
     * @param alarms_ids alarm_ids 报警id数组
     */
    public static void deleteAlarmByIds(ClientCore clientCore,String[] alarm_ids)
```

## 设置用户推送
```
    /**
     * @param enable_push， 启用或禁用推送，1 表示启用，0 表示禁用，是必须的，
     * @param client_lang   客户端语言，参见语言宏定义
     * @param client_token  个推给出的clientId
     * @param disable_push_other_users   是否禁止向相同客户端ID 或客户端Token的其它用户进行推送，1表示禁止，0表示允许；
     * @param unread_count  把推送的未读记录数设置为指定的值。默认置0
     */
    public static void setUserPush(ClientCore clientCore,int enable_push, int client_lang,
            String client_token, int disable_push_other_users,int unread_count)
```

## 设备服务器进行布防（主要用于免登陆情况下设备布防，其实也是通过参数组成playnode中的连接参数）
```
    /**
     * @param opCode        操作码，1 表示布防/布防通知，2 表示撤防，3 表示取消布防通知
     * @param client_token  个推clientId
     * @param alarm_events  布防事件类型数组，参见事件类型宏定义
     * @param devName       设备名称
     * @param devUmid       设备umid
     * @param devUser       设备用户名
     * @param devPassword   设备密码
     * @param iChNo         设备通道号 
     */
    
    public static void setDeviceAlarm(ClientCore clientCore,final int opCode,String client_token,int [] alarm_events,
            String devName, String devUmid, String devUser, String devPassword,
            int iChNo)
```

## 设备服务器进行布防（主要用于登陆情况下设备布放）
```
    /**
     * 设备端服务器进行布防(主用于登录模式下进行服务器布放)
     * @param playNode      播放节点数据
     * @param opCode        为1时布防 为2时撤防 为4撤销所有设备布防
     * @param client_token  个推的client_id
     * @param alarm_events  报警事件类型，参见报警类型宏定义
     */
    public static void setDeviceAlarm(ClientCore clientCore, PlayNode node, final int opCode,String client_token,int [] alarm_events)
```
    
## 查询设备报警状态
```
    /**
     * @param sDevId       设备id playNode.node.sdevId  或者 devItemInfo.dev_id
     * @param client_token 个推clientId
     */
    public static void getDeviceAlarm(ClientCore clientCore,String sDevId,final String client_token)
```

#5.设备控制模块接口（com.Player.Core.PlayCore）

## 设备播放参数初始化
```
    /**
     * 初始化参数
     * @param tmpDeviceNo        连接参数
     * @param tmpMediaStreamType 码流设置， 0：为主码流，1：为辅码流，-1：账户登录、免登陆添加时默认的码流（umid直连默认辅码流）
     * @param ImageViewObj       显示ImageVIew
     */
    public void InitParam(String tmpDeviceNo,ImageView ImageViewObj)
```

## 开始播放
```
   public boolean Play()   
```

## 停止播放
```
    /**
     * 耗时任务，建议子线程执行
     */
   public boolean Stop()   
```

## 获取设备播放状态
```
    /**
     * 获取当前的播放状态，具体状态值参考播放状态码宏定义
     */
   public int PlayCoreGetCameraPlayerState()   
   
   
```

## 设置设备报警检测状态
```
    /**
     * @brief 设置移动侦测报警参数，
     * @param DeviceNo 连接参数 PlayNode.connparm
     * @param joCfgObj 输入参数 具体参数类 TAlarmMotionDetect 对象，TAlarmMotionDetect.bIfEnable 标志设备是否开启报警检测
     * @return 返回0表示成功，其它表示失败
     */
    public int CameraSetAlarmMotion(String DeviceNo, TAlarmMotionDetect joCfgObj)   
   
   
```

## 获取设备报警检测状态
```
    /**
     * @brief 获取移动侦测报警参数，返回0表示成功，其它表示失败
     * @param DeviceNo      连接参数  playNode.connParm
     * @param joCfgObj      预先实例化一个，获取成功后，对象中存在数值，TAlarmMotionDetect.bIfEnable 标志设备是否开启报警检测
     * @Tip   示例： TAlarmMotionDetect alarmMotion = new TAlarmMotionDetect();
     *         int ret= CameraGetAlarmMotion( DeviceNo, alarmMotion);
     *         if(ret==0){ //成功 }else{ //失败 }
     */
    public int CameraGetAlarmMotion(String DeviceNo, TAlarmMotionDetect joCfgObj)
   
   
```
