package com.example.oss_app;

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

public class MyContentsDAO {

    static String getResult;
    String getUrl = "http://20.194.21.177:8000/getdata/" + LoginActivity.userid;
    static List<NewsListModel> contentsModels = new ArrayList<>();
    NewsListModel newsListModel = new NewsListModel();

    public void LoadData(){
        contentsModels.clear();

        String result = getContent();
        result = result.replaceAll("\\{\\{", "");
        result = result.replaceAll("\\}\\}", "\n");

        String[] text = result.split("\n");

        System.out.println(text.length);
        System.out.println("======================================================================================");
        for(int i=0;i<text.length; i++){
            System.out.println(text[i]);
        }

        try{
            for(int i=0; i< text.length; i++){
                String[] content = text[i].split("//");
                newsListModel = new NewsListModel("My", "0", content[0], content[1], content[2], content[4], content[3]);
                contentsModels.add(newsListModel);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public String getContent() {

        JSONObject reqForm = new JSONObject();
        try {
            reqForm.put("subject", "request");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), reqForm.toString());

        postRequest(getUrl, body);

        while(getResult==null) {
            System.out.println(".....ing");
        }
        return getResult;
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
                getResult = "failure";
            }

            @Override
            public void onResponse(Call call, final Response response) {
                try {
                    final String responseString = response.body().string().trim();
                    if (responseString.equals("failure")) {
                        getResult = "failure";
                    }
                    else {   // 성공했을 때
                        getResult = responseString;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}