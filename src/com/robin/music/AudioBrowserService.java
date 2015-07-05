package com.robin.music;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

@SuppressLint("NewApi")
public class AudioBrowserService extends Service {

    // 定义个service的入口，当有对象绑定service时，将该变量传给绑定的对象，用以调用service的方法
    public MusicTransport mTransport;

    // 正在播放的或者上次退出播放的歌曲的id
    public int audioId = -1;

    // 用于存放应用信息的sharepreferences文件，每次播放歌曲的时候讲歌曲的id保存到该文件中
    public SharedPreferences mPreferences;

    // 声明LocalBroadcastManager用于在播放歌曲的id发生改变的时候发送局部广播
    private LocalBroadcastManager mLocalBroadcastManager;

    @Override
    public void onCreate() {
        super.onCreate();
        // 根据文件名称获得应用的sharepreferences文件
        mPreferences = getSharedPreferences(Constent.SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        // 获得sharepreferences文件中保存的歌曲的id，将值赋给audioId，默认值为-1
        audioId = mPreferences.getInt(Constent.SHARE_PREFERENCE_KEY_AUDIO_ID, -1);

        // LocalBroadcastManager是单例模式，通过类名.getInstance(Context)方法获取对象
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mTransport;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class MusicTransport extends Binder {
        public int getAudioId() {
            return audioId;

        }
    }

    /**
     * 当播放id发生改变时，调用onAudioIdChanged()方法发送广播
     * */
    void onAudioIdChanged() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.putExtra(Constent.EXTRA_AUDIO_ID, audioId);
        broadcastIntent.setAction(Constent.AUDIO_ID_CHANGED_ACTION);
        mLocalBroadcastManager.sendBroadcast(broadcastIntent);
    }

}
