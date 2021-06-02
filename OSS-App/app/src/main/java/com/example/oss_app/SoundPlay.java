package com.example.oss_app;

import android.media.MediaPlayer;

import java.io.IOException;

// xml 수정 + view로 모든 페이지에 ㄱㄱ
public class SoundPlay {

    public static String url;
    MediaPlayer player;
    int position = 0;

    public void setPlayUrl(String url){
        this.url = url;
    }

    public void playAudio() {
        try {
            closePlayer();

            System.out.println(url+"===============");
            player = new MediaPlayer();
            player.setDataSource(url);
            player.prepare();
            player.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 현재 일시정지가 되었는지 중지가 되었는지 헷갈릴 수 있기 때문에 스위치 변수를 선언해 구분할 필요가 있다. (구현은 안했다.)
    public void pauseAudio() {
        if (player != null) {
            position = player.getCurrentPosition();
            player.pause();
        }
    }

    public void resumeAudio() {
        if (player != null && !player.isPlaying()) {
            player.seekTo(position);
            player.start();
        }
    }

    public void stopAudio() {
        if(player != null && player.isPlaying()){
            player.stop();
        }
    }

    /* 녹음 시 마이크 리소스 제한. 누군가가 lock 걸어놓으면 다른 앱에서 사용할 수 없음.
     * 따라서 꼭 리소스를 해제해주어야함. */
    public void closePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }
}