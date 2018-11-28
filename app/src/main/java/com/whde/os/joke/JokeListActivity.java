package com.whde.os.joke;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class JokeListActivity extends AppCompatActivity {
    ListView jokelistview = null;
    int currentPage = 1;
    int pageSize = 20;
    String url = "http://192.168.0.142:8080/JokeWebServer/jokelist?pageSize=20&currentPage=";
    List<JokeBean> jokeBeanList = new ArrayList<JokeBean>();
    OkHttpClient client = new OkHttpClient();
    JokeListAdapter adapter = null;
    public Handler h = new Handler();
    JokeSwipeRefreshLayout refreshLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joke_list);
        refreshLayout = findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        // 设置下拉加载更多
        refreshLayout.setOnLoadMoreListener(new JokeSwipeRefreshLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadMore();
            }
        });
        refreshLayout.setColorSchemeColors(Color.RED,Color.BLUE,Color.GREEN);
        jokelistview = findViewById(R.id.jokelistview);
        adapter = new JokeListAdapter(
                JokeListActivity.this,
                R.layout.joke_list_item,
                jokeBeanList);
        jokelistview.setAdapter(adapter);
        jokelistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JokeBean jokeBean = jokeBeanList.get(position);
                String herf = jokeBean.getHerf();
                Intent intent =new Intent(
                        JokeListActivity.this,
                        JokeDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("herf", herf);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        refresh();
    }

    void refresh() {
        currentPage = 1;
        try {
            httpGet(url,currentPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void loadMore() {
        currentPage++;
        try {
            httpGet(url,currentPage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void httpGet(String url, int currentPage) throws IOException {
        Request request = new Request.Builder().url(url+currentPage).build();
        Call call1 = client.newCall(request);
        final int curPage = currentPage;
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
                        List<JokeBean> list = null;
                        if (curPage==1) {
                            jokeBeanList.clear();
                        }
                        try {
                            JSONObject jsonObject = JSONObject.parseObject(responseStr);
                            Object object = jsonObject.get("data");
                            list = (List<JokeBean>) JSONArray.parseArray(object.toString(), JokeBean.class);
                        } catch (Exception e) {
                            list = new ArrayList();
                        }
                        jokeBeanList.addAll(list);
                        h.post(new Runnable() {      //跳到主线程中更新UI
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                adapter.notifyDataSetChanged();
                                if (refreshLayout.isRefreshing()) {
                                    refreshLayout.setRefreshing(false);
                                }
                                if (refreshLayout.mListView != null) {
                                    refreshLayout.setLoading(false);
                                }
                            }
                        });
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
