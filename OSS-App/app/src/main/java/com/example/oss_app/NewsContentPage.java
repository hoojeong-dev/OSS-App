package com.example.oss_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class NewsContentPage extends AppCompatActivity {

    List<NewsListModel> models = NewsList.models;
    static int modelPosition;
    String category, key, title, content = null, keyword, summary;
    static String[] newsContents;
    static int currentCount, count, contentcount, pageValue;
    TextView contentView, keywordView, summaryView;

    LayoutInflater setLayoutInflater, playLayoutInflater;
    LinearLayout settingLayout, playLayout;
    LinearLayout.LayoutParams setLayoutParams, playLayoutParams;

    SoundPlay soundPlay = new SoundPlay();
    HttpConnection httpConnection = new HttpConnection();

    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2, fab3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity.pagenum=2;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_page);
        MainActivity.viewPoint = findViewById(R.id.view_point);

        Intent intent = getIntent();
        modelPosition = intent.getExtras().getInt("position");

        pageValue = 1;

        title = models.get(modelPosition).getTitle();
        category = models.get(modelPosition).getCategory();
        key = models.get(modelPosition).getKey();
        content = models.get(modelPosition).getContent();
        keyword = models.get(modelPosition).getKeywords();
        summary = models.get(modelPosition).getSummary();

        setNewContents();

        contentView = (TextView) findViewById(R.id.content);
        contentView.setText(title);
        contentcount = 0;
        System.out.println("contentcount : " + contentcount);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);



/*
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(vpPager);
 */
    }

    // 뷰에 띄울 텍스트 설정
    public void setNewContents() {

        String[] sample = new String[1000];
        int maxLength = 180;
        int textLen = content.length();
        int loopCnt = textLen / maxLength + 1;
        String result = "";
        count = 1;
        currentCount = -2;

        for (int i = 0; i < loopCnt; i++) {
            int lastIndex = (i + 1) * maxLength;

            if (textLen > lastIndex) {
                result = content.substring(i * maxLength, lastIndex);
                System.out.println(result);
                sample[count] = result;
                count++;
            } else {
                result = content.substring(i * maxLength);
                sample[count] = result;
                count++;
            }
        }

        System.out.println("count : " + count);
        newsContents = new String[count];

        for(int i=1; i<count; i++){
            newsContents[i] = sample[i];
        }
    }

    // 이전 페이지로
    public void previous(View v){
        System.out.println(contentcount + "---------------" + count);
        contentcount--;

        if(contentcount > 0 && contentcount <= count - 1){
            setContentView(R.layout.activity_content_page);
            MainActivity.viewPoint = findViewById(R.id.view_point);

            contentView = (TextView) findViewById(R.id.content);
            contentView.setText(newsContents[contentcount]);
            pageValue = 1;
        }
        else if(contentcount == 0){
            contentView.setText(title);
            pageValue = 1;
        }
        else if(contentcount < 0){
            contentView.setText(title);
            pageValue = 1;
            Toast.makeText(getApplicationContext(), "First Page", Toast.LENGTH_LONG).show();
        }
    }


    // 다음 페이지로
    public void next(View v){
        System.out.println(contentcount + "---------------" + count);
        contentcount++;

        if(contentcount > 0 && contentcount <= count - 1){
            setContentView(R.layout.activity_content_page);
            MainActivity.viewPoint = findViewById(R.id.view_point);
            contentView = (TextView) findViewById(R.id.content);
            contentView.setText(newsContents[contentcount]);
            pageValue = 1;
        }
        else if(contentcount == count){
            setContentView(R.layout.activity_summary_page);
            MainActivity.viewPoint = findViewById(R.id.view_point);
            keywordView = (TextView) findViewById(R.id.keywords);
            keywordView.setText(keyword);
            summaryView = (TextView) findViewById(R.id.summary);
            summaryView.setText(summary);
            pageValue = 2;
        }
        else if(contentcount > count){
            pageValue = 2;
            Toast.makeText(getApplicationContext(), "Last Page", Toast.LENGTH_LONG).show();
        }
    }

    public void setting(View v) {

        setLayoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        settingLayout = (LinearLayout) setLayoutInflater.inflate(R.layout.activity_setting_view, null);
        MainActivity.viewPoint = findViewById(R.id.view_point);
        settingLayout.setBackgroundColor(Color.parseColor("#99000000"));
        setLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        addContentView(settingLayout, setLayoutParams);
    }

    public void inVisibleSetting(View v) {
        ((ViewManager) settingLayout.getParent()).removeView(settingLayout);
    }

    //터치 함수
    public void touch(View v) {
        //MainActivity.startTracking();
    }

    //아이트래킹 함수
    public void tracking(View v) {
        //MainActivity.stopTracking();
    }

    public void fab(View v){
        anim();
    }

    // control mode 설정
    public void fab1_controlmode(View v){

    }

    // view mode 설정
    public void fab2_viewmode(View v){
        anim();
        Intent intent = new Intent(v.getContext(), NewsContentScroll.class);
        intent.putExtra("position", modelPosition);
        v.getContext().startActivity(intent);

        MainActivity.pageType = 1;
    }

    // tts 재생
    public void fab3_tts(View v) {
        anim();
        playLayoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        playLayout = (LinearLayout) playLayoutInflater.inflate(R.layout.activity_sound_play, null);
        playLayout.setBackgroundColor(Color.parseColor("#99000000"));
        playLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        if (category.equals("My")) {
            String contentUrl = "http://20.194.21.177:8000/tts/" + content;
            soundPlay.setPlayUrl(contentUrl);
            addContentView(playLayout, playLayoutParams);
        } else {
            String msg = "", path = "category/" + category + "/" + key;

            if (pageValue == 1)
                msg = "읽어";
            if (pageValue == 2)
                msg = "요약";
            System.out.println("=================" + msg);

            String soundUrl = httpConnection.sendTTS(msg, path);

            if (soundUrl.equals("failure")) {
                Toast.makeText(getApplicationContext(), "Failed to Connect to Server. Please try again later.", Toast.LENGTH_LONG).show();
                soundPlay.closePlayer();
            } else {
                soundPlay.setPlayUrl(soundUrl);
                addContentView(playLayout, playLayoutParams);
            }
        }
    }

    public void playbtn(View v) {
        soundPlay.playAudio();
    }

    public void pausebtn(View v) {
        soundPlay.pauseAudio();
    }

    public void restartbtn(View v) {
        soundPlay.resumeAudio();
    }

    public void stopbtn(View v) {
        soundPlay.stopAudio();
    }

    public void inVisiblePlay(View v) {
        ((ViewManager) playLayout.getParent()).removeView(playLayout);
        soundPlay.closePlayer();
    }

    public void anim() {
        if (isFabOpen) {
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            isFabOpen = false;
        } else {
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            fab3.setClickable(true);
            isFabOpen = true;
        }
    }
}
