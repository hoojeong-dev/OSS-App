package com.example.oss_app;

import android.app.Activity;
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

public class HttpConnection extends Activity {

    static String soundUrl;
    String postUrl = "http://20.41.87.42/ai";

    public String send(String msg, String path) {

        JSONObject reqForm = new JSONObject();
        try {
            reqForm.put("subject", "request");
            reqForm.put("msg", msg);
            reqForm.put("path", path);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), reqForm.toString());

        postRequest(postUrl, body);

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
                Log.d("FAIL", e.getMessage());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        soundUrl = "failure";
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String ResponseString = response.body().string().trim();

                            if (ResponseString.equals("failure")) {
                                soundUrl = "failure";
                            }

                            else{   // 성공했을 때
                                soundUrl = ResponseString;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            soundUrl = "failure";
                        }
                    }
                });
            }
        });
    }
}
