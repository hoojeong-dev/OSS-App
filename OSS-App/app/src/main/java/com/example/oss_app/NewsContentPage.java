package com.example.oss_app;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class NewsContentPage extends AppCompatActivity {

    List<NewsListModel> models = NewsList.models;
    static int modelPosition;
    String category, key, title, content = null, keyword, summary, sttmsg = "null";
    static String[] newsContents;
    static int currentCount, count, contentcount, pageValue;
    TextView contentView, keywordView, summaryView;

    LayoutInflater setLayoutInflater, playLayoutInflater, sttLayoutInflater;
    LinearLayout settingLayout, playLayout, sttLayout;
    LinearLayout.LayoutParams setLayoutParams, playLayoutParams, sttLayoutParams;

    SoundPlay soundPlay = new SoundPlay();
    SpeechToText speechToText = new SpeechToText();
    HttpConnection httpConnection = new HttpConnection();
    final int PERMISSION = 1;
    Boolean sttState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity.pagenum=2;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_page);
        MainActivity.viewPoint = findViewById(R.id.view_point);

        if(MainActivity.mode == 0)
            MainActivity.viewPoint.setVisibility(View.INVISIBLE);
        else
            MainActivity.viewPoint.setVisibility(View.VISIBLE);

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

            if(MainActivity.mode == 0)
                MainActivity.viewPoint.setVisibility(View.INVISIBLE);
            else
                MainActivity.viewPoint.setVisibility(View.VISIBLE);

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
            if(MainActivity.mode == 0)
                MainActivity.viewPoint.setVisibility(View.INVISIBLE);
            else
                MainActivity.viewPoint.setVisibility(View.VISIBLE);
            contentView = (TextView) findViewById(R.id.content);
            contentView.setText(newsContents[contentcount]);
            pageValue = 1;
        }
        else if(contentcount == count){
            setContentView(R.layout.activity_summary_page);
            MainActivity.viewPoint = findViewById(R.id.view_point);
            if(MainActivity.mode == 0)
                MainActivity.viewPoint.setVisibility(View.INVISIBLE);
            else
                MainActivity.viewPoint.setVisibility(View.VISIBLE);
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
        MainActivity.viewPoint.setVisibility(View.GONE);
        settingLayout.setBackgroundColor(Color.parseColor("#99000000"));
        setLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        addContentView(settingLayout, setLayoutParams);
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

    public void pageView(View v){
        ImageButton pageView = findViewById(R.id.pageView);

        Intent intent = new Intent(v.getContext(), NewsContentScroll.class);
        intent.putExtra("position", modelPosition);
        v.getContext().startActivity(intent);

        MainActivity.pageType = 1;
        pageView.setBackground(ContextCompat.getDrawable(this, R.drawable.black_scroll));
    }

    public void tts(View v){
        playLayoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        playLayout = (LinearLayout) playLayoutInflater.inflate(R.layout.activity_sound_play, null);
        playLayout.setBackgroundColor(Color.parseColor("#99000000"));
        MainActivity.viewPoint = findViewById(R.id.view_point);
        MainActivity.viewPoint.setVisibility(View.GONE);
        playLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        if (category.equals("My")) {
            String contentUrl = "http://20.194.21.177:8000/tts/" + content;
            soundPlay.setPlayUrl(contentUrl);
            ((ViewManager) settingLayout.getParent()).removeView(settingLayout);
            addContentView(playLayout, playLayoutParams);
        } else {
            String msg = "", path = "category/" + category + "/" + key;

            if (pageValue == 1)
                msg = "읽어";
            else if (pageValue == 2)
                msg = "요약";
            else if(sttState && sttmsg.equals("읽어"))
                msg = "읽어";
            else if(sttState && sttmsg.equals("요약"))
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

                sttmsg = "null";
                sttState = false;
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
        if(MainActivity.mode == 0)
            MainActivity.viewPoint.setVisibility(View.INVISIBLE);
        else
            MainActivity.viewPoint.setVisibility(View.VISIBLE);

        ((ViewManager) playLayout.getParent()).removeView(playLayout);
        soundPlay.closePlayer();
    }

    TextView textView;
    Button button;
    Intent intentStt;
    SpeechRecognizer mRecognizer;

    public void stt(View v){
        sttLayoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sttLayout = (LinearLayout) sttLayoutInflater.inflate(R.layout.activity_speech_to_text, null);
        sttLayout.setBackgroundColor(Color.parseColor("#99000000"));
        MainActivity.viewPoint = findViewById(R.id.view_point);
        MainActivity.viewPoint.setVisibility(View.GONE);
        sttLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        if(Build.VERSION.SDK_INT >= 23){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO},PERMISSION);
        }
        speechToText.sttSet(NewsContentPage.this);
        ((ViewManager) settingLayout.getParent()).removeView(settingLayout);
        addContentView(sttLayout, sttLayoutParams);

        textView = findViewById(R.id.result);
        button = findViewById(R.id.stt);

        // RecognizerIntent 생성
        intentStt = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intentStt.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName()); // 여분의 키
        intentStt.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecognizer = SpeechRecognizer.createSpeechRecognizer(NewsContentPage.this); // 새 SpeechRecognizer 를 만드는 팩토리 메서드
                mRecognizer.setRecognitionListener(sttlistener); // 리스너 설정
                mRecognizer.startListening(intentStt); // 듣기 시작
            }
        });
    }

    private RecognitionListener sttlistener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            // 말하기 시작할 준비가되면 호출
            Toast.makeText(getApplicationContext(),"음성인식 시작",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBeginningOfSpeech() {
            // 말하기 시작했을 때 호출
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            // 입력받는 소리의 크기를 알려줌
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            // 말을 시작하고 인식이 된 단어를 buffer에 담음
        }

        @Override
        public void onEndOfSpeech() {
            // 말하기를 중지하면 호출
        }

        @Override
        public void onError(int error) {
            // 네트워크 또는 인식 오류가 발생했을 때 호출
            String message;

            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    message = "클라이언트 에러";
                    break;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    message = "찾을 수 없음";
                    break;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER 가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default:
                    message = "알 수 없는 오류임";
                    break;
            }

            Toast.makeText(getApplicationContext(), "에러 발생 : " + message,Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle results) {
            // 인식 결과가 준비되면 호출
            // 말을 하면 ArrayList에 단어를 넣고 textView에 단어를 이어줌
            ArrayList<String> matches =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            for(int i = 0; i < matches.size() ; i++){
                textView.setText(matches.get(i));
                System.out.println(matches.get(i));
            }

            if(SpeechToText.sttResult){
                if(MainActivity.mode == 0)
                    MainActivity.viewPoint.setVisibility(View.INVISIBLE);
                else
                    MainActivity.viewPoint.setVisibility(View.VISIBLE);

                ((ViewManager) sttLayout.getParent()).removeView(sttLayout);
            }

            sttmsg = (String) textView.getText();

            if(sttmsg.contains("읽어") || sttmsg.contains("일거") || sttmsg.contains("읽거")){
                sttmsg = "읽어";
                sttState = true;
            } else if(sttmsg.contains("요약") || sttmsg.contains("예약") || sttmsg.contains("얘약")){
                sttmsg = "요약";
                sttState = true;
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            // 부분 인식 결과를 사용할 수 있을 때 호출
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            // 향후 이벤트를 추가하기 위해 예약
        }
    };

    public void inVisibleStt(View v) {
        if(MainActivity.mode == 0)
            MainActivity.viewPoint.setVisibility(View.INVISIBLE);
        else
            MainActivity.viewPoint.setVisibility(View.VISIBLE);

        ((ViewManager) sttLayout.getParent()).removeView(sttLayout);
    }

    public void pageback(View v){
        Intent intent = new Intent(this, NewsList.class);
        intent.putExtra("value", category);
        startActivity(intent);
    }
}
