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

    // ���ڲ��ŵĸ�����id
    private int mAudioId;
    // ����SharedPreferences����
    SharedPreferences mPreferences;
    // ������Ҫָ��service����ı������ñ����������ڵ���service�еķ���
    MusicTransport mService;
    // �������ֲ��ſ������ViewGroup������
    private ViewGroup play_control_view;
    // ����TabWidget����
    private TabWidget mTabWidget;
    // ����LocalBroadcastManager����
    LocalBroadcastManager mLocalBroadcastManager;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ֪ͨ��͸����ʾ
        // if (Build.VERSION.SDK_INT > 20) {
        // Window window = getWindow();
        // window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        // }

        // ָ����activity�Ĳ���
        setContentView(R.layout.activity_media_browser);
        // ��ȡSharePreferences����
        mPreferences = getSharedPreferences(Constent.SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        // ��SharePreferences�ļ��л�ȡ�ϴβ��ŵĸ���id
        mAudioId = mPreferences.getInt(Constent.SHARE_PREFERENCE_KEY_AUDIO_ID, -1);
        // ������ֲ��ſ������Ķ���
        play_control_view = (ViewGroup) findViewById(R.id.song_play_view);
        // �������ֲ��ſ������״̬
        updatePlayControlView(mAudioId);

        // ��ʼ��TabWidget��ָ�����δ򿪽���ʱ��ʾ��Tab
        mTabWidget = (TabWidget) findViewById(android.R.id.tabhost);
        mTabWidget.setCurrentTab(1);

        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);

        // ��ȡ���񲢽��а�
        Intent serviceIntent = new Intent();
        serviceIntent.setClass(this, AudioBrowserService.class);
        startService(serviceIntent);
        bindService(serviceIntent, mConn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        /**
         * �۲���ģʽ��Ҫ��֮һ��ע��
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
     * ���ݴ����audioid�����޸����ֲ��ſ���������ʾ״̬
     * */
    private void updatePlayControlView(int audioId) {
        if (audioId < 0) {
            play_control_view.setVisibility(View.GONE);
        } else {
            play_control_view.setVisibility(View.VISIBLE);
        }
    }

    // �󶨷���ʱҪ����ķ������Ӳ���
    private ServiceConnection mConn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        // �󶨷���Ļص��������������ӳɹ�֮����ã���÷������
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = (MusicTransport) service;
        }
    };

    /**
     * �㲥�����ߣ����շ����ڲ��ŵ����ָı�ʱ���͵Ĺ㲥���Կ�������е���Ϣ�����޸ģ�������ȫ�ֱ���mAudioId
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
