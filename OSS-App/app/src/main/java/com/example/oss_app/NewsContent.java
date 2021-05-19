package com.example.oss_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

import me.relex.circleindicator.CircleIndicator;

public class NewsContent extends AppCompatActivity {

    List<NewsListModel> models = NewsList.models;
    FragmentPagerAdapter adapterViewPager;
    static int modelPosition, currentPosition;
    String category, key, title, content;
    TextView titleView;

    LayoutInflater setLayoutInflater, playLayoutInflater;
    LinearLayout settingLayout, playLayout;
    LinearLayout.LayoutParams setLayoutParams, playLayoutParams;

    SoundPlay soundPlay = new SoundPlay();
    HttpConnection httpConnection = new HttpConnection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_content);

        Intent intent = getIntent();
        modelPosition = intent.getExtras().getInt("position");

        category = models.get(modelPosition).getCategory();
        key = models.get(modelPosition).getKey();
        content = models.get(modelPosition).getContent();

        title = models.get(modelPosition).getTitle();
        titleView = (TextView) findViewById(R.id.title);
        titleView.setText(title);

        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        vpPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(vpPager);
    }

    public void setting(View v){

        setLayoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        settingLayout = (LinearLayout) setLayoutInflater.inflate(R.layout.activity_setting_page, null);
        settingLayout.setBackgroundColor(Color.parseColor("#99000000"));
        setLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        addContentView(settingLayout, setLayoutParams);
    }

    public void inVisibleSetting(View v){
        ((ViewManager)settingLayout.getParent()).removeView(settingLayout);
    }

    //시선 or 터치 함수
    public void controlMode(View v){

    }

    //폰트 사이즈 설정 함수 -> 다른 기능으로 대체 가능
    public void fontSize(View v){

    }

    public void play(View v){

        playLayoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        playLayout = (LinearLayout) playLayoutInflater.inflate(R.layout.activity_sound_play, null);
        playLayout.setBackgroundColor(Color.parseColor("#99000000"));
        playLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        if(category.equals("My")){

            String contentUrl = "http://52.231.75.96:8000/tts/" + content;
            soundPlay.setPlayUrl(contentUrl);
        }

        else{
            String msg = "", path = "category/"+ category + "/" + key;

            if(currentPosition == 0)
                msg = "읽어";
            if(currentPosition == 1)
                msg = "요약";
            System.out.println("=================" + msg);

            String soundUrl = httpConnection.sendTTS(msg, path);

            if(soundUrl.equals("failure")){
                Toast.makeText(getApplicationContext(), "Failed to Connect to Server. Please try again later.", Toast.LENGTH_LONG).show();
                soundPlay.closePlayer();
            }
            else {
                soundPlay.setPlayUrl(soundUrl);
                addContentView(playLayout, playLayoutParams);
            }
        }
    }

    public void playbtn(View v){
        soundPlay.playAudio();
    }
    public void pausebtn(View v){
        soundPlay.pauseAudio();
    }
    public void restartbtn(View v){
        soundPlay.resumeAudio();
    }
    public void stopbtn(View v){
        soundPlay.stopAudio();
    }

    public void inVisiblePlay(View v){
        ((ViewManager)playLayout.getParent()).removeView(playLayout);
        soundPlay.closePlayer();
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 2;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return ContentPage.newInstance(0, modelPosition);
                case 1:
                    return SummaryPage.newInstance(1, modelPosition);
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }
    }
}