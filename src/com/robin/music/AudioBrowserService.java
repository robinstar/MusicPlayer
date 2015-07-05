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

    // �����service����ڣ����ж����serviceʱ�����ñ��������󶨵Ķ������Ե���service�ķ���
    public MusicTransport mTransport;

    // ���ڲ��ŵĻ����ϴ��˳����ŵĸ�����id
    public int audioId = -1;

    // ���ڴ��Ӧ����Ϣ��sharepreferences�ļ���ÿ�β��Ÿ�����ʱ�򽲸�����id���浽���ļ���
    public SharedPreferences mPreferences;

    // ����LocalBroadcastManager�����ڲ��Ÿ�����id�����ı��ʱ���;ֲ��㲥
    private LocalBroadcastManager mLocalBroadcastManager;

    @Override
    public void onCreate() {
        super.onCreate();
        // �����ļ����ƻ��Ӧ�õ�sharepreferences�ļ�
        mPreferences = getSharedPreferences(Constent.SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        // ���sharepreferences�ļ��б���ĸ�����id����ֵ����audioId��Ĭ��ֵΪ-1
        audioId = mPreferences.getInt(Constent.SHARE_PREFERENCE_KEY_AUDIO_ID, -1);

        // LocalBroadcastManager�ǵ���ģʽ��ͨ������.getInstance(Context)������ȡ����
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
     * ������id�����ı�ʱ������onAudioIdChanged()�������͹㲥
     * */
    void onAudioIdChanged() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.putExtra(Constent.EXTRA_AUDIO_ID, audioId);
        broadcastIntent.setAction(Constent.AUDIO_ID_CHANGED_ACTION);
        mLocalBroadcastManager.sendBroadcast(broadcastIntent);
    }

}
