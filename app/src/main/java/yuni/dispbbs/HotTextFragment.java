package yuni.dispbbs;

import android.app.Activity;
//import android.app.ListFragment;
import android.support.v4.app.ListFragment;//改用 support.v4 支援 viewpager 左右滑動
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class HotTextFragment extends ListFragment implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    Activity mContext;
    ListView mListView;
    MainAdapter mAdapter;
    ProgressDialog mDialog;
    SwipeRefreshLayout mSwipeLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity();
        mListView = getListView();

        mAdapter = new MainAdapter(mContext);
        setListAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        mSwipeLayout = (SwipeRefreshLayout) mContext.findViewById(R.id.swiperefresh);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeColors(Color.BLUE);

        mDialog = new ProgressDialog(mContext);
        mDialog.setMessage("Loading Data...");

        loadData();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //從成員mainAdapter中用getItem取出第position項的資料，存成jsonObject
        JSONObject jsonObject = (JSONObject) parent.getAdapter().getItem(position);
        //取出我們要的兩個資料 bi 和 ti
        String bi = jsonObject.optString("bi","");
        String ti = jsonObject.optString("ti","");

        // 建立一個 Intent 用來表示要從現在這頁跳到文章閱讀頁 TextActivity
        Intent textIntent = new Intent(mContext, TextActivity.class);
        // 將要傳遞的資料放進 Intent
        textIntent.putExtra("bi",bi);
        textIntent.putExtra("ti",ti);

        // 使用準備好的 Intent 來開啟新的頁面
        startActivity(textIntent);
        //mContext.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onRefresh() {
        loadData(); //下載並更新列表的資料
    }

    private void loadData(){
        mDialog.show();
        String urlString = "http://disp.cc/api/hot_text.json";

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(urlString, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(mContext,
                        "Success!", Toast.LENGTH_LONG).show();
                Log.d("Hot Text:", response.toString());
                mDialog.dismiss();
                mSwipeLayout.setRefreshing(false); //結束更新動畫

                if( response.has("err") && response.optInt("err")!=0 ){
                    Toast.makeText(mContext,"Data error", Toast.LENGTH_LONG).show();
                }
                JSONArray list = response.optJSONArray("list");
                if(list==null){
                    Toast.makeText(mContext,"Data error", Toast.LENGTH_LONG).show();
                }
                mAdapter.updateData(list);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject error) {
                Toast.makeText(mContext,
                        "Error: " + statusCode + " " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
                // Log error message
                Log.e("Hot Text:", statusCode + " " + e.getMessage());
                mDialog.dismiss();
                mSwipeLayout.setRefreshing(false); //結束更新動畫
                Toast.makeText(mContext, "Error: " + statusCode + " " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
