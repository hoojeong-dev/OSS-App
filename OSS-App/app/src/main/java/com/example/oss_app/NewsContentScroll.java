package com.example.oss_app;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class NewsContentScroll extends AppCompatActivity {

    List<NewsListModel> models = NewsList.models;
    FragmentPagerAdapter adapterViewPager;
    static int modelPosition, currentPosition;
    String category, key, content = null, sttmsg = "null", title;
    static int count;
    ViewPager vpPager;

    LayoutInflater setLayoutInflater, playLayoutInflater, sttLayoutInflater;
    LinearLayout settingLayout, playLayout, sttLayout;
    LinearLayout.LayoutParams setLayoutParams, playLayoutParams, sttLayoutParams;

    SpeechToText speechToText = new SpeechToText();
    SoundPlay soundPlay = new SoundPlay();
    HttpConnection httpConnection = new HttpConnection();

    final int PERMISSION = 1;
    Boolean sttState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_view);
        MainActivity.viewPoint = findViewById(R.id.view_point);

        if(MainActivity.mode == 0)
            MainActivity.viewPoint.setVisibility(View.INVISIBLE);
        else
            MainActivity.viewPoint.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        modelPosition = intent.getExtras().getInt("position");

        count = 0;

        title = models.get(modelPosition).getTitle();
        category = models.get(modelPosition).getCategory();
        key = models.get(modelPosition).getKey();
        content = models.get(modelPosition).getContent();

        vpPager = (ViewPager) findViewById(R.id.vpPager);
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

        if(SpeechToText.resultType){
            sttmsg = SpeechToText.sttResult;
            loadTTS();
        }
/*
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(vpPager);
 */
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return 3;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                case 1:
                    count++;
                    return ContentPage.newInstance(0, modelPosition, count);
                case 2:
                    return SummaryPage.newInstance(1, modelPosition);
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }

    public void setting(View v) {
        setLayoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        settingLayout = (LinearLayout) setLayoutInflater.inflate(R.layout.activity_setting_view, null);
        MainActivity.viewPoint = findViewById(R.id.view_point);
        MainActivity.viewPoint.setVisibility(View.GONE);
        settingLayout.setBackgroundColor(Color.parseColor("#99000000"));
        setLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        addContentView(settingLayout, setLayoutParams);

        ImageButton mode = (ImageButton) findViewById(R.id.mode);
        if(MainActivity.mode == 0) {
            mode.setBackground(ContextCompat.getDrawable(this, R.drawable.black_eye_2));
        }
        else if(MainActivity.mode == 1) {
            mode.setBackground(ContextCompat.getDrawable(this, R.drawable.black_eye));
        }

        if(category.equals("My")) {
            LinearLayout removeBack = findViewById(R.id.removeBack);
            removeBack.setBackground(ContextCompat.getDrawable(this, R.drawable.button_circle));
            ImageButton remove = findViewById(R.id.remove);
            remove.setVisibility(View.VISIBLE);
        }

        LinearLayout magnifier_back = findViewById(R.id.magnifier_back);
        ImageButton magnifier = findViewById(R.id.magnifier);

        magnifier_back.setVisibility(View.GONE);
        magnifier.setVisibility(View.GONE);
    }

    public void inVisibleSetting(View v) {
        if(MainActivity.mode == 0)
            MainActivity.viewPoint.setVisibility(View.INVISIBLE);
        else
            MainActivity.viewPoint.setVisibility(View.VISIBLE);
        ((ViewManager) settingLayout.getParent()).removeView(settingLayout);
    }

    //시선 or 터치 함수
    public void controlMode(View v){
        ImageButton mode = (ImageButton) findViewById(R.id.mode);
        if(MainActivity.mode == 0) {
            MainActivity.mode = 1;
            mode.setBackground(ContextCompat.getDrawable(this, R.drawable.black_eye));
            MainActivity.viewPoint.setVisibility(View.VISIBLE);
        }
        else if(MainActivity.mode == 1) {
            MainActivity.mode = 0;
            mode.setBackground(ContextCompat.getDrawable(this, R.drawable.black_eye_2));
            MainActivity.viewPoint.setVisibility(View.GONE);
        }
    }

    // view mode 설정
    public void pageView(View v){
        ImageButton pageView = findViewById(R.id.pageView);

        Intent intent = new Intent(v.getContext(), NewsContentPage.class);
        intent.putExtra("position", modelPosition);
        v.getContext().startActivity(intent);

        MainActivity.pageType = 2;
        pageView.setBackground(ContextCompat.getDrawable(this, R.drawable.black_touch));
    }

    // tts 재생
    public void tts(View v) {
        playLayoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        playLayout = (LinearLayout) playLayoutInflater.inflate(R.layout.activity_sound_play, null);
        playLayout.setBackgroundColor(Color.parseColor("#99000000"));
        MainActivity.viewPoint = findViewById(R.id.view_point);
        MainActivity.viewPoint.setVisibility(View.GONE);
        playLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        if (category.equals("My")) {
            String contentUrl = "http://20.194.21.177:8000/tts/" + content;
            soundPlay.setPlayUrl(contentUrl);
            addContentView(playLayout, playLayoutParams);
        } else {
            String msg = "", path = "category/" + category + "/" + key;

            if (currentPosition == 0 || currentPosition == 1)
                msg = "읽어";
            if (currentPosition == 2)
                msg = "요약";
            System.out.println("=================" + msg);

            String soundUrl = httpConnection.sendTTS(msg, path);

            if (soundUrl.equals("failure")) {
                Toast.makeText(getApplicationContext(), "Failed to Connect to Server. Please try again later.", Toast.LENGTH_LONG).show();
                soundPlay.closePlayer();
            } else {
                soundPlay.setPlayUrl(soundUrl);
                ((ViewManager) settingLayout.getParent()).removeView(settingLayout);
                addContentView(playLayout, playLayoutParams);
            }
        }
    }

    public void loadTTS(){
        playLayoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        playLayout = (LinearLayout) playLayoutInflater.inflate(R.layout.activity_sound_play, null);
        playLayout.setBackgroundColor(Color.parseColor("#99000000"));
        MainActivity.viewPoint = findViewById(R.id.view_point);
        MainActivity.viewPoint.setVisibility(View.GONE);
        playLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        String msg = "", path = "category/" + category + "/" + key;

        if(SpeechToText.resultType && sttmsg.equals("읽어"))
            msg = "읽어";
        else if(SpeechToText.resultType && sttmsg.equals("요약"))
            msg = "요약";

        String soundUrl = httpConnection.sendTTS(msg, path);

        if (soundUrl.equals("failure")) {
            Toast.makeText(getApplicationContext(), "Failed to Connect to Server. Please try again later.", Toast.LENGTH_LONG).show();
            soundPlay.closePlayer();
        } else {
            soundPlay.setPlayUrl(soundUrl);
            addContentView(playLayout, playLayoutParams);

            sttmsg = "null";
            SpeechToText.resultType = false;
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
        if(MainActivity.mode == 0)
            MainActivity.viewPoint.setVisibility(View.INVISIBLE);
        else
            MainActivity.viewPoint.setVisibility(View.VISIBLE);

        ((ViewManager) playLayout.getParent()).removeView(playLayout);
        soundPlay.closePlayer();
    }

    public void stt(View v){
        Intent intent = new Intent(this, SpeechToText.class);
        intent.putExtra("pageName", "NewsContentPage");
        intent.putExtra("position", modelPosition);
        startActivity(intent);
    }

    public void pageback(View v){
        Intent intent = new Intent(this, NewsList.class);
        intent.putExtra("value", category);
        startActivity(intent);
    }

    public void removeContents(View v){
        AlertDialog.Builder oDialog = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog);

        oDialog.setMessage("해당 글을 삭제하시겠습니까?")
                .setTitle("eye-world")
                .setPositiveButton("아니오", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        System.out.println("취소");
                    }
                })
                .setNeutralButton("예", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        NewsList.removeContents = title;
                        Toast.makeText(getApplicationContext(), "삭제했습니다.", Toast.LENGTH_LONG).show();
                    }
                })
                .setCancelable(false) // 백버튼으로 팝업창이 닫히지 않도록 한다.
                .show();
    }
}