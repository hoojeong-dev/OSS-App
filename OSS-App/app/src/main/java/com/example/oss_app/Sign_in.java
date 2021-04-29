package com.example.oss_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Sign_in extends AppCompatActivity {

    Button check_id, sign_in;
    EditText enter_id, enter_pw;
    TextView check_state;
    boolean signIn_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        check_id = (Button) findViewById(R.id.check_id);
        sign_in = (Button) findViewById(R.id.sign_in);
        enter_id = (EditText) findViewById(R.id.enter_id);
        enter_pw = (EditText) findViewById(R.id.enter_pw);
        check_state = (TextView) findViewById(R.id.check_state);

        String realID = "fujeong15", realPW = "0604";

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enterID = enter_id.getText().toString();
                String enterPW = enter_pw.getText().toString();

                // 다른 클래스를 생성해서 입력 아이디와 저장 아이디, 비밀번호가 동일한지 확인 필요
                // 해당 결과를 boolean 값으로 전달
                // signIn_state = ~~~
                // 현재는 둘이 비교하는 형식으로 되어있음

                if(enterID.equals(realID)){
                    if(enterPW.equals(realPW)){
                        Intent intent = new Intent(Sign_in.this, CategoryPage.class);
                        startActivity(intent);
                    }
                    else
                        Toast.makeText(v.getContext(), "Wrong Password", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(v.getContext(), "Wrong ID", Toast.LENGTH_SHORT).show();
            }
        });
    }
}