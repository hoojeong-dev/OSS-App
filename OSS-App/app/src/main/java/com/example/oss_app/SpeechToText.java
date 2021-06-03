package com.example.oss_app;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

public class SpeechToText extends AppCompatActivity {

    TextView textView;
    Button button;
    Intent intentPage, intent;
    SpeechRecognizer mRecognizer;
    final int PERMISSION = 1;
    static String pageName;
    static Boolean resultType = false;
    ArrayList<String> matches = null;
    static String sttResult = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech_to_text);
        MainActivity.viewPoint = findViewById(R.id.view_point);
        MainActivity.viewPoint.setVisibility(View.INVISIBLE);

        intentPage = getIntent();
        pageName = intentPage.getExtras().getString("pageName");

        // 안드로이드 6.0버전 이상인지 체크해서 퍼미션 체크
        if(Build.VERSION.SDK_INT >= 23){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.INTERNET,
                    Manifest.permission.RECORD_AUDIO},PERMISSION);
        }

        textView = findViewById(R.id.result);
        button = findViewById(R.id.stt);

        // RecognizerIntent 생성
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getPackageName()); // 여분의 키
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecognizer = SpeechRecognizer.createSpeechRecognizer(SpeechToText.this); // 새 SpeechRecognizer 를 만드는 팩토리 메서드
                mRecognizer.setRecognitionListener(listener); // 리스너 설정
                mRecognizer.startListening(intent); // 듣기 시작
            }
        });
    }

    private RecognitionListener listener = new RecognitionListener() {
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
            matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            textView.setText(matches.get(0));
            sttResult = matches.get(0);

            if(sttResult.contains("읽어") || sttResult.contains("읽거") || sttResult.contains("일거"))
                sttResult = "읽어";

            else if(sttResult.contains("요약") || sttResult.contains("예약") || sttResult.contains("얘약"))
                sttResult = "예약";

            resultType = true;
            Toast.makeText(getApplicationContext(), "잠시만 기다려주세요 ...", Toast.LENGTH_SHORT).show();

            try {
                Thread.sleep(8000);

                if(pageName.equals("NewsContentPage")){
                    Intent intentBack = new Intent(SpeechToText.this, NewsContentPage.class);
                    intentBack.putExtra("position", NewsContentPage.modelPosition);
                    startActivity(intentBack);
                } else if(pageName.equals("NewsContentScroll")){
                    Intent intentBack = new Intent(SpeechToText.this, NewsContentScroll.class);
                    intentBack.putExtra("position", NewsContentPage.modelPosition);
                    startActivity(intentBack);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
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

        if(pageName.equals("NewsContentPage")){
            Intent intentBack = new Intent(this, NewsContentPage.class);
            intentBack.putExtra("position", NewsContentPage.modelPosition);
            startActivity(intentBack);
        } else if(pageName.equals("NewsContentScroll")){
            Intent intentBack = new Intent(this, NewsContentScroll.class);
            intentBack.putExtra("position", NewsContentPage.modelPosition);
            startActivity(intentBack);
        }
    }
}