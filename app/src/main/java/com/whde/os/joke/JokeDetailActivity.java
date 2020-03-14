package com.whde.os.joke;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class JokeDetailActivity extends AppCompatActivity {
    TextView textView = null;
    String url = "http://192.168.0.200:8080/JokeWebServer/detail?herf=";
    Handler h = new Handler();

    public String herf = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joke_detail);
        textView = findViewById(R.id.jokeDetailTextView);
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        herf = bundle.getString("herf");
        try {
            httpGet(herf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void httpGet(String herf) throws IOException {
        Request request = new Request.Builder().url(url+herf).build();
        OkHttpClient client = new OkHttpClient();
        Call call1 = client.newCall(request);
        call1.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.d("OKHttp",call.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if(response.isSuccessful()){
                        String responseStr = response.body().string();
                        Log.d("OKHttp",responseStr);
                        JokeBean jokeBean = null;
                        try {
                            JSONObject jsonObject = JSONObject.parseObject(responseStr);
                            Object object = jsonObject.get("data");
                            jokeBean = (JokeBean) JSONObject.parseObject(object.toString(), JokeBean.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (jokeBean != null){
                            final JokeBean innerBean = jokeBean;
                            h.post(new Runnable() {      //跳到主线程中更新UI
                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    textView.setText(innerBean.getDetail());
                                }
                            });
                        }
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
