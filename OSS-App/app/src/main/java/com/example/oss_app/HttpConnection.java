package com.example.oss_app;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

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

public class HttpConnection {

    String soundUrl;
    String postUrl = "http://20.194.21.177:8000/ai";

    public String sendTTS(String msg, String path) {

        JSONObject reqForm = new JSONObject();
        try {
            reqForm.put("subject", "request");
            reqForm.put("msg", msg);
            reqForm.put("path", path);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        soundUrl = null;
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), reqForm.toString());

        postRequest(postUrl, body);

        while(soundUrl==null) {
            System.out.println(".....ing");
        }

        return soundUrl;
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
                e.printStackTrace();
                soundUrl = "failure";
            }

            @Override
            public void onResponse(Call call, final Response response) {
                try {
                    final String responseString = response.body().string().trim();
                    if (responseString.equals("failure")) {
                        soundUrl = "failure";
                    }
                    else {   // 성공했을 때
                        soundUrl = responseString;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}