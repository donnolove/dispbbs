package yuni.dispbbs;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

import android.app.SearchManager;
import android.widget.SearchView.OnQueryTextListener;

public class SearchActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener, OnQueryTextListener {

    ListView mListView;
    ArrayAdapter mSearchAdapter;
    ArrayList<String> mSearchList = new ArrayList<>();
    HashMap mBoardId = new HashMap();

    ArrayList<String> mHistoryList;
    ArrayAdapter mHistoryAdapter;

    boolean mIsSearch = false;
    TextView mHeaderTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

        android.widget.SearchView searchView = (android.widget.SearchView) findViewById(R.id.searchview);
        searchView.setOnQueryTextListener(this);
        searchView.setIconifiedByDefault(false); //是否要點選搜尋圖示後再打開輸入框
        searchView.setFocusable(false);
        searchView.requestFocusFromTouch(); //要點選後才會開啟鍵盤輸入
        searchView.setSubmitButtonEnabled(false);//輸入框後是否要加上送出的按鈕
        searchView.setQueryHint("請輸入看板名稱");
        searchView.setBackgroundColor(Color.WHITE);

        Button btnHotText = (Button) findViewById(R.id.button_HotText);
        btnHotText.setOnClickListener(this);
        Button btnBoardList = (Button) findViewById(R.id.button_BoardList);
        btnBoardList.setOnClickListener(this);
        Button btnSearchBoard = (Button) findViewById(R.id.button_SearchBoard);
        btnSearchBoard.setOnClickListener(this);
        btnSearchBoard.setBackgroundColor(Color.LTGRAY);
        btnSearchBoard.setTextColor(Color.BLACK);

        mListView = (ListView)findViewById(R.id.listview);
        View headerView = getLayoutInflater().inflate(R.layout.row_boardsearchheader, mListView, false);
        mListView.addHeaderView(headerView);
        mHeaderTextView = (TextView) findViewById(R.id.header_boardsearch);

        mSearchAdapter = new ArrayAdapter(this,R.layout.row_boardsearch, mSearchList);

        BoardHistoryDB boardHistoryDB = new BoardHistoryDB(this);
        mHistoryList = boardHistoryDB.getArrayList();
        mHistoryAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, mHistoryList);
        mListView.setAdapter(mHistoryAdapter);
        boardHistoryDB.close();

//        mSearchAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, mSearchList){
//            @NonNull
//            @Override
//            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//                View view = super.getView(position, convertView, parent);
//                TextView text = (TextView) view.findViewById(android.R.id.text1);
//                text.setTextColor(Color.BLACK);
//                return view;
//            }
//        };

        mListView.setOnItemClickListener(this);

        loadData();
    }

    private void loadData(){
        String urlString = "http://disp.cc/api/get.php?act=bSearchList";
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(urlString, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray list = response.optJSONArray("list");
                JSONObject board;
                for(int i = 0; i < list.length(); i++){
                    board = list.optJSONObject(i);
                    mSearchList.add(board.optString("name") + " " + board.optString("title").replace(" ",""));
                    mBoardId.put(board.optString("name"),String.valueOf(board.optInt("bi")));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject error) {
                Toast.makeText(getApplicationContext(),
                        "Error: " + statusCode + " " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Toast.makeText(this, "輸入的是：" + query, Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(!mIsSearch && newText.length() != 0){//搜尋框有值時
            mListView.setAdapter(mSearchAdapter);
            mHeaderTextView.setText(R.string.boardSearchHeader);
            mIsSearch = true;
        }else if(mIsSearch && newText.length() == 0){//搜尋框是空的時
            //mListView.setAdapter(null);
            mListView.setAdapter(mHistoryAdapter);//搜尋框是空的顯示歷史搜尋
            mHeaderTextView.setText(R.string.boardHistoryHeader);
            mIsSearch = false;
        }
        if(mIsSearch){ //過濾Adapter的內容
            Filter filter = mSearchAdapter.getFilter();
            filter.filter(newText);
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //從ArrayAdapter中用getItem取出第position項的資料
        String itemString = (String) parent.getAdapter().getItem(position);
        Pattern pattern = Pattern.compile("^(\\S+) (.*)$");
        Matcher matcher = pattern.matcher(itemString);
        String boardName="", boardTitle="";
        if(matcher.find()){
            boardName = matcher.group(1);
            boardTitle = matcher.group(2);
            //Log.e("debug","1:"+boardName+"__2:"+boardTitle + "__3:"+mBoardId.get(boardName));
        }
//        Intent intent = new Intent(this, TextListActivity.class);
//        intent.putExtra("boardName", boardName);
//        intent.putExtra("boardTitle", boardTitle);
//        startActivity(intent);

        BoardHistoryDB boardHistoryDB = new BoardHistoryDB(this);
        boardHistoryDB.add(Integer.valueOf(String.valueOf(mBoardId.get(boardName))), boardName, boardTitle);
        boardHistoryDB.close();

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.button_HotText:
                Intent mainIntent = new Intent(this, MainActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);
                overridePendingTransition(0,0);//關閉換頁動畫
                break;
            case R.id.button_BoardList:
                //連到熱門看板頁的程式
                Intent boardListIntent = new Intent(this, BoardListActivity.class);
                boardListIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(boardListIntent);
                overridePendingTransition(0, 0);
                break;
            case R.id.button_SearchBoard:
                loadData();
                break;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
