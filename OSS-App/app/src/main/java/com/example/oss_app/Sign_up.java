package com.example.oss_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Sign_up extends AppCompatActivity {

    Button check_id, sign_up;
    EditText enter_id, enter_pw, enter_pw2, enter_name;
    TextView check_state;
    boolean ID_state;
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        check_id = (Button) findViewById(R.id.check_id);
        sign_up = (Button) findViewById(R.id.sign_up);
        enter_id = (EditText) findViewById(R.id.enter_id);
        enter_pw = (EditText) findViewById(R.id.enter_pw);
        enter_pw2 = (EditText) findViewById(R.id.enter_pw2);
        enter_name = (EditText) findViewById(R.id.enter_name);
        check_state = (TextView) findViewById(R.id.check_state);

        check_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enterID = enter_id.getText().toString();

                // count 변수는 test용
                if(count == 0)
                    ID_state = false;
                else if(count>0)
                    ID_state = true;

                if(ID_state == true)
                    check_state.setText("✔");
                else {
                    count = 1;
                    check_state.setText("✘");
                    Toast.makeText(v.getContext(), "Enter your other ID", Toast.LENGTH_SHORT).show();
                }
            }
        });

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ID = enter_id.getText().toString();
                String PW = enter_pw.getText().toString();
                String PW2 = enter_pw2.getText().toString();
                String Name = enter_pw.getText().toString();

                if(PW.equals(PW2) == true){
                    if(ID_state == true){
                        //데이터베이스에 저장
                        Toast.makeText(v.getContext(), "Success Sign up", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Sign_up.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
                else
                    Toast.makeText(v.getContext(), "Incorrect Password", Toast.LENGTH_SHORT).show();
            }
        });
    }
}