package com.robin.music;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabWidget;

import com.robin.music.AudioBrowserService.MusicTransport;

public class AudioMediaBrowserActivity extends Activity {

    // 正在播放的歌曲的id
    private int mAudioId;
    // 声明SharedPreferences引用
    SharedPreferences mPreferences;
    // 声明将要指向service对象的变量。该变量将会用于调用service中的方法
    MusicTransport mService;
    // 声明音乐播放控制面板ViewGroup的引用
    private ViewGroup play_control_view;
    // 声明TabWidget引用
    private TabWidget mTabWidget;
    // 声明LocalBroadcastManager引用
    LocalBroadcastManager mLocalBroadcastManager;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 通知栏透明显示
        // if (Build.VERSION.SDK_INT > 20) {
        // Window window = getWindow();
        // window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        // }

        // 指定本activity的布局
        setContentView(R.layout.activity_media_browser);
        // 获取SharePreferences对象
        mPreferences = getSharedPreferences(Constent.SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        // 从SharePreferences文件中获取上次播放的歌曲id
        mAudioId = mPreferences.getInt(Constent.SHARE_PREFERENCE_KEY_AUDIO_ID, -1);
        // 获得音乐播放控制面板的对象
        play_control_view = (ViewGroup) findViewById(R.id.song_play_view);
        // 更新音乐播放控制面板状态
        updatePlayControlView(mAudioId);

        // 初始化TabWidget，指定初次打开界面时显示的Tab
        mTabWidget = (TabWidget) findViewById(android.R.id.tabhost);
        mTabWidget.setCurrentTab(1);

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);

        // 获取服务并进行绑定
        Intent serviceIntent = new Intent();
        serviceIntent.setClass(this, AudioBrowserService.class);
        startService(serviceIntent);
        bindService(serviceIntent, mConn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        /**
         * 观察者模式四要素之一：注册
         * */
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constent.AUDIO_ID_CHANGED_ACTION);
        mLocalBroadcastManager.registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocalBroadcastManager.unregisterReceiver(mReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConn);
    }

    /**
     * 根据传入的audioid参数修改音乐播放控制面板的显示状态
     * */
    private void updatePlayControlView(int audioId) {
        if (audioId < 0) {
            play_control_view.setVisibility(View.GONE);
        } else {
            play_control_view.setVisibility(View.VISIBLE);
        }
    }

    // 绑定服务时要传入的服务连接参数
    private ServiceConnection mConn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        // 绑定服务的回调方法，服务连接成功之后调用，获得服务对象。
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = (MusicTransport) service;
        }
    };

    /**
     * 广播接收者，接收服务在播放的音乐改变时发送的广播，对控制面板中的信息进行修改，并更新全局变量mAudioId
     * */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String aciton = intent.getAction();
            if (Constent.AUDIO_ID_CHANGED_ACTION.equals(aciton)) {
                final int audioid = intent.getIntExtra(Constent.EXTRA_AUDIO_ID, mAudioId);
                if (mAudioId != audioid) {
                    boolean changeVisibility = (audioid < 0) ^ (mAudioId < 0);
                    mAudioId = audioid;
                    if (changeVisibility) {
                        updatePlayControlView(mAudioId);
                    }
                }
            }
        }
    };

}
