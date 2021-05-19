package com.example.oss_app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends Activity {

    String postUrl = "http://52.231.75.96:8000/userLogin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void register(View v) {
        EditText useridView = findViewById(R.id.userid);
        EditText usernameView = findViewById(R.id.username);
        EditText userpwView = findViewById(R.id.userpw);
        EditText userpw2View = findViewById(R.id.userpw2);

        String userid = useridView.getText().toString().trim();
        String username = usernameView.getText().toString().trim();
        String userpw = userpwView.getText().toString().trim();
        String userpw2 = userpw2View.getText().toString().trim();

        if (userid.length() == 0 || username.length() == 0 || userpw.length() == 0 || userpw2.length() == 0) {
            Toast.makeText(getApplicationContext(), "Something is wrong. Please check your inputs.", Toast.LENGTH_LONG).show();
        } else {
            JSONObject registrationForm = new JSONObject();
            try {
                registrationForm.put("subject", "register");
                registrationForm.put("id", userid);
                registrationForm.put("name", username);
                registrationForm.put("pw", userpw);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), registrationForm.toString());

            postRequest(postUrl, body);
        }
    }

    public void postRequest(String postUrl, RequestBody postBody) {
        OkHttpClient client = new OkHttpClient();

        final Request request = new Request.Builder()
                .url(postUrl)
                .post(postBody)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                Log.d("FAIL", e.getMessage());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Failed to Connect to Server. Please Try Again.", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) {
                try {
                    final String responseString = response.body().string().trim();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (responseString.equals("success")) {
                                Toast.makeText(getApplicationContext(), "Registration completed successfully.", Toast.LENGTH_LONG).show();
                                finish();
                            } else if (responseString.equals("username")) {
                                Toast.makeText(getApplicationContext(), "Username already exists. Please chose another username.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Something went wrong. Please try again later.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}