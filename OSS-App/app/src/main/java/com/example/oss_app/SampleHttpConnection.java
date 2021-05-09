package com.example.oss_app;

import android.app.Activity;
import android.os.Bundle;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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

public class SampleHttpConnection extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_http_connection);
    }

    public void send(View v) {
        String mes = "읽어줘";
        String path = "category/2/1";

        JSONObject loginForm = new JSONObject();
        try {
            loginForm.put("subject", "request");
            loginForm.put("msg", mes);
            loginForm.put("path", path);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), loginForm.toString());

        postRequest(MainActivity.postUrl, body);
    }

    public void postRequest(String postUrl, RequestBody postBody) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
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
                        TextView result = findViewById(R.id.result);
                        result.setText("Failed to Connect to Server. Please Try Again.");
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView result = findViewById(R.id.result);
                        try {
                            String loginResponseString = response.body().string().trim();

                            if (loginResponseString.equals("failure")) {
                                result.setText("Login Failed. Invalid username or password.");
                            }

                            else{
                                soundPlay sound = new soundPlay();
                                sound.setsoundPlay(loginResponseString);
                                Intent intent = new Intent(SampleHttpConnection.this, soundPlay.class);
                                startActivity(intent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            result.setText("Something went wrong. Please try again later.");
                        }
                    }
                });
            }
        });
    }
}