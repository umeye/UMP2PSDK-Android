package com.example.umeyesdk.entity;


import java.io.Serializable;
import java.util.List;

public class DevAbilityLevel implements Serializable {

    /**
     * Operation : 109
     * Request_Type : 0
     * Result : 1
     * Value : {"Record_Time":0,"Record_Range":0,"Record_Speed":0,"Record_File_Date":0,"Cloud_Storage":0,"Talk_Radio":[1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1],"Talk_Front_End":[1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1]}
     */

    private int Operation;
    private int Request_Type;
    private int Result;
    private ValueBean Value;

    public int getOperation() {
        return Operation;
    }

    public void setOperation(int Operation) {
        this.Operation = Operation;
    }

    public int getRequest_Type() {
        return Request_Type;
    }

    public void setRequest_Type(int Request_Type) {
        this.Request_Type = Request_Type;
    }

    public int getResult() {
        return Result;
    }

    public void setResult(int Result) {
        this.Result = Result;
    }

    public ValueBean getValue() {
        return Value;
    }

    public void setValue(ValueBean Value) {
        this.Value = Value;
    }

    public static class ValueBean implements Serializable {
        /**
         * Record_Time : 0
         * Record_Range : 0
         * Record_Speed : 0
         * Record_File_Date : 0
         * Cloud_Storage : 0
         * Talk_Radio : [1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1]
         * Talk_Front_End : [1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1]
         * <p>
         * Light_Conf：[1,1,0,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1]
         */

        private int Record_Time;
        private int Record_Range;
        private int Record_Speed;
        private int Record_File_Date;
        private int Cloud_Storage;
        private List<Integer> Talk_Local;
        private List<Integer> Talk_Radio;
        private List<Integer> Talk_Front_End;
        private List<Integer> Light_Conf;
        private int Lamp_Light;
        /**
         * 是否支持图像控制，0：不支持，1支持
         */
        private int Picture_Ctrl;
        /**
         * 是否支持倒装，0：不支持，1支持
         */
        private int Enable_Inversion;
        /**
         * 是否支持修改密码，0,1都是支持，2不支持(0：支持，兼容老设备)
         */
        private int Update_Pwd;
        private int Enable_4G;
        private int Enable_Custom_Login;
        private int Enable_Milti_Alarm;

        private int Enable_Ota;

        private int Enable_Storage;

        public int getEnable_Ota() {
            return Enable_Ota;
        }

        /**
         * 整形	设备类型，1：IPC，2：NVR，3：DVR(0：支持，兼容老设备)
         */
        private int Dev_Type;

        /**
         * 是否支持隐私遮挡，1：支持，2：不支持(0：不支持，兼容老设备)（对接功能ID：117）
         */
        private int Enable_Privacy;
        /**
         * 图像设置
         */
        private int EConfigPic;

        /**
         * 双目摄像头类型，0：非双目，1：单路双目摄像头，2：双路双目摄像头
         */
        private int Eyes = 0;
        /**
         * dvr功能菜单
         */
        public Functions funcs;


        /**
         *
         */
        public ChFunctions ch_funcs;

        public int[] events;


        public int getEyes() {
            return Eyes;
        }

        public void setEyes(int eyes) {
            Eyes = eyes;
        }

        public int getEConfigPic() {
            return EConfigPic;
        }

        public void setEConfigPic(int EConfigPic) {
            this.EConfigPic = EConfigPic;
        }

        public int getEnable_Privacy() {
            return Enable_Privacy;
        }

        public int getDev_Type() {
            return Dev_Type;
        }

        public void setDev_Type(int dev_Type) {
            Dev_Type = dev_Type;
        }

        public void setEnable_Privacy(int enable_Privacy) {
            Enable_Privacy = enable_Privacy;
        }

        public void setEnable_Ota(int enable_Ota) {
            Enable_Ota = enable_Ota;
        }

        public int getPicture_Ctrl() {
            return Picture_Ctrl;
        }

        public void setPicture_Ctrl(int picture_Ctrl) {
            Picture_Ctrl = picture_Ctrl;
        }

        public int getRecord_Time() {
            return Record_Time;
        }

        public void setRecord_Time(int Record_Time) {
            this.Record_Time = Record_Time;
        }

        public int getRecord_Range() {
            return Record_Range;
        }

        public void setRecord_Range(int Record_Range) {
            this.Record_Range = Record_Range;
        }

        public int getRecord_Speed() {
            return Record_Speed;
        }

        public void setRecord_Speed(int Record_Speed) {
            this.Record_Speed = Record_Speed;
        }

        public int getRecord_File_Date() {
            return Record_File_Date;
        }

        public int getEnable_4G() {
            return Enable_4G;
        }

        public void setEnable_4G(int enable_4G) {
            Enable_4G = enable_4G;
        }

        public int getEnable_Custom_Login() {
            return Enable_Custom_Login;
        }

        public void setEnable_Custom_Login(int enable_Custom_Login) {
            Enable_Custom_Login = enable_Custom_Login;
        }

        public int getEnable_Milti_Alarm() {
            return Enable_Milti_Alarm;
        }

        public void setEnable_Milti_Alarm(int enable_Milti_Alarm) {
            Enable_Milti_Alarm = enable_Milti_Alarm;
        }

        public void setRecord_File_Date(int Record_File_Date) {
            this.Record_File_Date = Record_File_Date;
        }

        public int getCloud_Storage() {
            return Cloud_Storage;
        }

        public void setCloud_Storage(int Cloud_Storage) {
            this.Cloud_Storage = Cloud_Storage;
        }

        public List<Integer> getTalk_Local() {
            return Talk_Local;
        }

        public int getLamp_Light() {
            return Lamp_Light;
        }

        public void setLamp_Light(int lamp_Light) {
            Lamp_Light = lamp_Light;
        }

        public void setTalk_Local(List<Integer> talk_Local) {
            Talk_Local = talk_Local;
        }

        public List<Integer> getTalk_Radio() {
            return Talk_Radio;
        }

        public void setTalk_Radio(List<Integer> Talk_Radio) {
            this.Talk_Radio = Talk_Radio;
        }

        public List<Integer> getTalk_Front_End() {
            return Talk_Front_End;
        }

        public void setTalk_Front_End(List<Integer> Talk_Front_End) {
            this.Talk_Front_End = Talk_Front_End;
        }

        public List<Integer> getLight_Conf() {
            return Light_Conf;
        }

        public void setLight_Conf(List<Integer> light_Conf) {
            Light_Conf = light_Conf;
        }

        public int getEnable_Inversion() {
            return Enable_Inversion;
        }

        public void setEnable_Inversion(int enable_Inversion) {
            Enable_Inversion = enable_Inversion;
        }

        public int getUpdate_Pwd() {
            return Update_Pwd;
        }

        public void setUpdate_Pwd(int update_Pwd) {
            Update_Pwd = update_Pwd;
        }

        public int getEnable_Storage() {
            return Enable_Storage;
        }

        public void setEnable_Storage(int enable_Storage) {
            Enable_Storage = enable_Storage;
        }


    }

    public static class Functions implements Serializable {
        //          "time": 1,                      // 设备时间配置
//         "disk": 1,                      // 设备磁盘管理
//         "reset": 1,                     // 设备重置
//         "reboot": 1,                    // 设备重启
//         "info": 1,                      // 设备信息
//         "ch_cfg": 1,                    // 通道配置，APP根据该字段判断是否显示通道配置切换控件
//         "ch_record": 1,                 // 通道录像管理
//         "ch_video_flip": [1,2,1,1]      // 通道视频翻转配置
//         "ch_video_encode": [1,1,1,1]    // 通道视频编码配置
//         "ch_sound": [1,1,1,1]           // 通道声音配置
//         "ch_light_strategy": [1,1,1,1]  // 通道收光策略
//         "ch_use_scene": [1,1,1,1],      // 通道使用场景配置
//         "ch_lens": [1,1,1,1],           // 通道镜头配置
//         "ch_ptz_align": [1,1,1,1],      // 通道云台校正配置
//         "ch_reset": [1,1,1,1],          // 通道重置
//         "ch_reboot": [1,1,1,1],         // 通道重启
//         "ch_info": [1,1,1,1],            // 通道信息

        public String pk;
        public String ck;
        public int time;
        public int disk;
        /**
         * 录像和存储管理,针对IPC
         */
        public int[] ch_record_disk;
        public int reboot;
        public int reset;
        public int firmware;
        public int info;

        public int auto_maint;
        public int simple_restore;
        public int full_restore;


        public int ch_cfg;
        public int[] ch_osd;
        public int[] ch_record;
        public int[] ch_video_encode;
        public int[] ch_sound;
        public int[] ch_light;
        public int[] ch_light_strategy;
        public int[] ch_use_scene;
        public int[] ch_lens;
        public int[] ch_ptz_align;
        public int[] ch_reboot;
        public int[] ch_reset;
        public int[] ch_info;
        /**
         * // 通道对讲-广播方式
         */
        public int[] ch_talk_bc;

        public String[] ch_names;

        public int[] ch_repel_cfg;
        /**
         * 通道数
         */
        public int ch_num;

        /**
         * "ch_alarm_motion": [1,1,1,1],   // 通道移动侦测配置
         * "ch_alarm_block": [1,1,1,1],    // 通道视频遮挡配置
         * "ch_alarm_areain": [1,1,1,1],   // 通道区域入侵配置
         * "ch_alarm_vparking": [1,1,1,1], // 通道车辆违停配置
         * "ch_alarm_vretrograde": [1,1,1,1], // 通道车辆逆行配置
         * "ch_alarm_boundary": [1,1,1,1], // 通道越界检测配置
         * "ch_alarm_absent": [1,1,1,1],   // 通道人员离岗配置
         * "ch_alarm_staying": [1,1,1,1],  // 通道人员逗留配置
         * "ch_alarm_fire": [1,1,1,1],     // 通道火灾报警
         */
        public int[] ch_alarm_motion;
        public int[] ch_alarm_block;
        public int[] ch_alarm_areain;
        public int[] ch_alarm_vparking;
        public int[] ch_alarm_vretrograde;
        public int[] ch_alarm_boundary;
        public int[] ch_alarm_absent;
        public int[] ch_alarm_staying;

        public int wifi_cfg;

        public int scanner_cfg;
        public int scanner_search;
        public int[] scanner_cfg_chs;

    }

    public static class ChFunctions implements Serializable {

        /**
         * // 通道码流类型，数值的二进制表示:高位为预留位，低四位分别表示(超清|第三码流|子|主)，支持子|主码流：00000011，10进制为：3
         */
        public int ch_stream;
        /**
         * 通道录像码流类型，数值的二进制表示:高位为预留位，低四位分别表示(超清|第三码流|子|主)，支持子|主码流：00000011，10进制为：3
         */

        public int ch_rec_stream;
        /**
         * 录像和存储管理,针对IPC
         */
        public int ch_record_disk;
        public int ch_reboot;
        public int ch_reset;
        public int ch_osd;
        public int ch_record;
        public int ch_video_encode;
        public int ch_sound;
        public int ch_light;
        public int ch_light_strategy;
        public int ch_use_scene;
        public int ch_lens;
        public int ch_ptz_align;
        public int ch_info;

        /**
         * 通道对讲-单播方式
         */
        public int ch_talk_uc;
        /**
         * 通道对讲-广播方式
         */
        public int ch_talk_bc;
        public int ch_repel_cfg;

        public int ch_alarm_human;

        public int ch_alarm_motion;
        public int ch_alarm_block;
        public int ch_alarm_areain;
        public int ch_alarm_vparking;
        public int ch_alarm_vretrograde;
        public int ch_alarm_boundary;
        public int ch_alarm_absent;
        public int ch_alarm_staying;
        public int wifi_cfg;
    }
}
