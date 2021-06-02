package com.example.oss_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

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
    ImageButton modebtn;

    String userUrl = "http://20.194.21.177:8000/insert/" + LoginActivity.userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity.safebar=100;
        MainActivity.pagenum=1;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);

        MainActivity.viewPoint = findViewById(R.id.view_point);

        if(MainActivity.mode == 0)
            MainActivity.viewPoint.setVisibility(View.INVISIBLE);
        else
            MainActivity.viewPoint.setVisibility(View.VISIBLE);

        modebtn = findViewById(R.id.mode);
        camera = findViewById(R.id.camera);
        listView = findViewById(R.id.listview);
        category = findViewById(R.id.category);

        Intent intent = getIntent();
        str = intent.getExtras().getString("value");
        category.setText(str);

        models.clear();

        if(OcrView.saveState){
            String result = sendContents(OcrView.title, OcrView.contents);

            if(result.equals("success"))
                Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_LONG).show();
            else
                Toast.makeText(getApplicationContext(), "Failed to Connect to Server. Please try again later.", Toast.LENGTH_LONG).show();
        }

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

        modebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.mode == 0) {
                    MainActivity.mode = 1;
                    modebtn.setBackground(ContextCompat.getDrawable(NewsList.this, R.drawable.black_eye));
                    MainActivity.viewPoint.setVisibility(View.VISIBLE);
                }
                else if(MainActivity.mode == 1) {
                    MainActivity.mode = 0;
                    modebtn.setBackground(ContextCompat.getDrawable(NewsList.this, R.drawable.black_eye_2));
                    MainActivity.viewPoint.setVisibility(View.GONE);
                }
            }
        });
    }

    public void cameraOCR(View v){
        Intent intent = new Intent(this, OcrView.class);
        startActivity(intent);
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

    public void pageback(View v){
        Intent intent = new Intent(this, CategoryPage.class);
        startActivity(intent);
    }
}