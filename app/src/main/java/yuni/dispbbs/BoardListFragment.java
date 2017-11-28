package yuni.dispbbs;

import android.app.Activity;
//import android.app.ListFragment;
import android.support.v4.app.ListFragment;//改用 support.v4 支援 viewpager 左右滑動
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

import com.facebook.drawee.backends.pipeline.Fresco;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class BoardListFragment extends ListFragment implements AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    Activity mContext;
    ListView mListView;
    BoardListAdapter mAdapter;
    private int mPageNum = 0;
    SwipeRefreshLayout mSwipeLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_boardlist, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        mContext = getActivity();
        mListView = getListView();

        Fresco.initialize(mContext);
        mAdapter = new BoardListAdapter(mContext);
        setListAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        //add listview footer
        View footerView = mContext.getLayoutInflater().inflate(R.layout.row_footer, mListView, false);
        mListView.addFooterView(footerView);
        footerView.setOnClickListener(this);

        mSwipeLayout = (SwipeRefreshLayout) mContext.findViewById(R.id.sf_blist);//在 Fragment 用 ViewPager 搭配 SwipeRefreshLayout 時, SwipeRefreshLayout ID 要不一樣, 不然 findViewById 會抓不到
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeColors(Color.BLUE);

        mPageNum = 0;
        loadData();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //從成員mainAdapter中用getItem取出第position項的資料，存成jsonObject
        JSONObject jsonObject = (JSONObject) parent.getAdapter().getItem(position);
        //取出我們要的兩個資料 bi 和 ti
        String name = jsonObject.optString("name","");

        // 建立一個 Intent 用來表示要從現在這頁跳到文章閱讀頁 TextActivity
        Intent textIntent = new Intent(mContext, TextActivity.class);
        // 將要傳遞的資料放進 Intent
        textIntent.putExtra("name",name);

        // 使用準備好的 Intent 來開啟新的頁面
        startActivity(textIntent);
    }

    @Override
    public void onRefresh() {
        mPageNum = 0;
        loadData();
    }


    private void loadData(){

        String urlString = "https://disp.cc/api/board.php?act=blist";
        if(mPageNum!=0){ urlString += "&pageNum=" + mPageNum; }

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(urlString,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                if( response.has("err") && response.optInt("err")!=0 ){
                    Toast.makeText(mContext,"Data error", Toast.LENGTH_LONG).show();
                }
                JSONArray list = response.optJSONObject("data").optJSONArray("blist");
                if(list==null){
                    Toast.makeText(mContext,"Data error", Toast.LENGTH_LONG).show();
                }
                try {
                    mAdapter.updateData(list,mPageNum);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mPageNum++;
                mSwipeLayout.setRefreshing(false); //結束更新動畫
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject error) {
                Toast.makeText(mContext,
                        "Error: " + statusCode + " " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
                // Log error message
                Log.e("Hot Text:", statusCode + " " + e.getMessage());
                mSwipeLayout.setRefreshing(false); //結束更新動畫
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.row_footer: //點擊的是listView的footer
                Log.d("BoardList", "footer click");
                loadData();
                break;
        }
    }
}
