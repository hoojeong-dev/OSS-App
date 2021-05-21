package com.example.oss_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewsList extends Activity {

    ListView listView;
    NewsListAdapter adapter;
    static List<NewsListModel> models = new ArrayList<>();
    TextView category;
    String str, sendResult;
    Button camera;

    String userUrl = "http://20.194.21.177:8000/insert/" + LoginActivity.userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        camera = findViewById(R.id.camera1);
        listView = findViewById(R.id.listview);
        category = findViewById(R.id.category);

        Intent intent = getIntent();
        str = intent.getExtras().getString("value");
        category.setText(str);

        models.clear();

        if(str.equals("My")){
            camera.setVisibility(View.VISIBLE);
            models = MyContentsDAO.contentsModels;
        }
        else{
            for(int i=0; i<NewsListDAO.allmodels.size(); i++){
                if(NewsListDAO.allmodels.get(i).getCategory().equals(str)){
                    models.add(NewsListDAO.allmodels.get(i));
                }
            }
        }

        adapter = new NewsListAdapter(models, str);
        listView.setAdapter(adapter);
    }

    public void camera1(View v){
        String title, content;

        title = "안녕하세요";
        content = "이것은 테스트 입니다.";
        String result = sendContents(title, content);

        if(result.equals("success"))
            Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getApplicationContext(), "Failed to Connect to Server. Please try again later.", Toast.LENGTH_LONG).show();
    }

    public String sendContents(String title, String content){
        JSONObject reqForm = new JSONObject();
        try {
            reqForm.put("title", title);
            reqForm.put("content", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), reqForm.toString());

        postRequest(userUrl, body);

        while(sendResult==null) {
            System.out.println(".....ing");
        }
        System.out.println("==================" + sendResult + "==================");
        return sendResult;
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

                sendResult = "failure";
            }

            @Override
            public void onResponse(Call call, final Response response) {
                try {
                    final String responseString = response.body().string().trim();
                    if (responseString.equals("failure")) {

                        sendResult = "failure";
                    }
                    else if(responseString.equals("success")){
                        sendResult = "success";
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}