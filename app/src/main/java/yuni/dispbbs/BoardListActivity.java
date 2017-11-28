package yuni.dispbbs;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class BoardListActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    ListView mListView;
    BoardListAdapter mAdapter;
    private int mPageNum = 0;
    SwipeRefreshLayout mSwipeLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fresco.initialize(this);
        setContentView(R.layout.activity_boardlist);


        mListView = (ListView) findViewById(R.id.board_listview);
        //add listview footer
        View footerView = getLayoutInflater().inflate(R.layout.row_footer, mListView, false);
        mListView.addFooterView(footerView);
        footerView.setOnClickListener(this);

        mAdapter = new BoardListAdapter(this);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        Button btnHotText = (Button) findViewById(R.id.button_HotText);
        btnHotText.setOnClickListener(this);
        Button btnBoardList = (Button) findViewById(R.id.button_BoardList);
        btnBoardList.setOnClickListener(this);
        Button btnSearchBoard = (Button) findViewById(R.id.button_SearchBoard);
        btnSearchBoard.setOnClickListener(this);
        btnBoardList.setBackgroundColor(Color.LTGRAY);
        btnBoardList.setTextColor(Color.BLACK);

        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mSwipeLayout.setColorSchemeColors(Color.BLUE);
        mSwipeLayout.setOnRefreshListener(this);

        mPageNum = 0;
        loadData();

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.button_HotText: //回到熱門文章頁
                Intent mainIntent = new Intent(this, MainActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);
                overridePendingTransition(0,0);//關閉換頁動畫
                break;
            case R.id.button_BoardList:
                //之後要加上重整資料的程式
                break;
            case R.id.button_SearchBoard:
                Intent searchIntent = new Intent(this, SearchActivity.class);
                searchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(searchIntent);
                overridePendingTransition(0,0);//關閉換頁動畫
                break;
            case R.id.row_footer: //點擊的是listView的footer
                Log.d("BoardList","footer click");
                loadData();
                break;
        }
    }


    private void loadData(){

        String urlString = "https://disp.cc/api/board.php?act=blist";
        if(mPageNum!=0){ urlString += "&pageNum=" + mPageNum; }

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(urlString,new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                if( response.has("err") && response.optInt("err")!=0 ){
                    Toast.makeText(getApplicationContext(),"Data error", Toast.LENGTH_LONG).show();
                }
                JSONArray list = response.optJSONObject("data").optJSONArray("blist");
                if(list==null){
                    Toast.makeText(getApplicationContext(),"Data error", Toast.LENGTH_LONG).show();
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
                Toast.makeText(getApplicationContext(),
                        "Error: " + statusCode + " " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
                // Log error message
                Log.e("Hot Text:", statusCode + " " + e.getMessage());
                mSwipeLayout.setRefreshing(false); //結束更新動畫
            }
        });
    }

    @Override
    public void onRefresh() {
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
        Intent textIntent = new Intent(this, TextActivity.class);
        // 將要傳遞的資料放進 Intent
        textIntent.putExtra("name",name);

        // 使用準備好的 Intent 來開啟新的頁面
        startActivity(textIntent);
    }
}
