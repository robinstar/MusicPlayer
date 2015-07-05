package com.robin.music;

/**
 * 该类用于存放本应用其他类在操作过程中用到的常量 这样做的好处是，降低耦合，便于维护
 * */
public class Constent {

    // 存放应用信息的sharepreferences文件名
    public static final String SHARE_PREFERENCE_NAME = "share_preference_name";
    // SharePreferences文件中存放audio_id的键值对的关键字
    public static final String SHARE_PREFERENCE_KEY_AUDIO_ID = "audio_id";
    // 当audio_id发生改变时service发送广播的aciton
    public static final String AUDIO_ID_CHANGED_ACTION = "william.intent.audioIdChanged";
    // 发送广播时作为变量名，将audioId放入intent中
    public static final String EXTRA_AUDIO_ID = "audio_id";

}
