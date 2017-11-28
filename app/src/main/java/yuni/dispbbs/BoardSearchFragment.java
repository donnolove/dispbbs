package yuni.dispbbs;

import android.app.Activity;
//import android.app.ListFragment;
import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;//改用 support.v4 支援 viewpager 左右滑動
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
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


public class BoardSearchFragment extends ListFragment implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener {

    Activity mContext;
    ListView mListView;
    ArrayAdapter mSearchAdapter;
    ArrayList<String> mSearchList = new ArrayList<>();
    HashMap mBoardId = new HashMap();

    ArrayList<String> mHistoryList;
    ArrayAdapter mHistoryAdapter;

    boolean mIsSearch = false;
    TextView mHeaderTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);//打開menu功能
        return inflater.inflate(R.layout.fragment_boardsearch, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity();
        mListView = getListView();

        android.widget.SearchView searchView = (android.widget.SearchView) mContext.findViewById(R.id.searchview);
        searchView.setOnQueryTextListener(this);
        searchView.setIconifiedByDefault(false); //是否要點選搜尋圖示後再打開輸入框
        searchView.setFocusable(false);
        searchView.requestFocusFromTouch(); //要點選後才會開啟鍵盤輸入
        searchView.setSubmitButtonEnabled(false);//輸入框後是否要加上送出的按鈕
        //searchView.setQueryHint("請輸入看板名稱");
        searchView.setQueryHint(Html.fromHtml("<font color = #b0b1b6>" + "請輸入看板名稱" + "</font>"));
        searchView.setBackgroundColor(Color.BLACK);
        searchView.setVisibility(View.VISIBLE);
        //((EditText)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text)).setHintTextColor(Color.WHITE);

        int searchInputId = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) searchView.findViewById(searchInputId);
        textView.setTextColor(Color.WHITE);
        //((EditText)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text)).setTextColor(Color.WHITE);

        int searchIconId = searchView.getContext().getResources().getIdentifier("android:id/search_mag_icon",null, null);
        ImageView searchIcon = (ImageView) searchView.findViewById(searchIconId);
        searchIcon.setImageResource(R.drawable.ic_search_white_48dp);

        mSearchAdapter = new ArrayAdapter(mContext,R.layout.row_boardsearch, mSearchList);
        mListView.setAdapter(mSearchAdapter);

        View headerView = mContext.getLayoutInflater().inflate(R.layout.row_boardsearchheader, mListView, false);
        mListView.addHeaderView(headerView);
        mHeaderTextView = (TextView) mContext.findViewById(R.id.header_boardsearch);

        mSearchAdapter = new ArrayAdapter(mContext,R.layout.row_boardsearch, mSearchList){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.WHITE);
                return view;
            }
        };

        BoardHistoryDB boardHistoryDB = new BoardHistoryDB(mContext);
        mHistoryList = boardHistoryDB.getArrayList();
        mHistoryAdapter = new ArrayAdapter(mContext,android.R.layout.simple_list_item_1, mHistoryList){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextColor(Color.WHITE);
                return view;
            }
        };
        mListView.setAdapter(mHistoryAdapter);

        boardHistoryDB.close();

        mListView.setOnItemClickListener(this);
        mListView.setBackgroundColor(Color.BLACK);
        loadData();
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
        }

        BoardHistoryDB boardHistoryDB = new BoardHistoryDB(mContext);
        boardHistoryDB.add(Integer.valueOf(String.valueOf(mBoardId.get(boardName))), boardName, boardTitle);
        boardHistoryDB.close();
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
                Toast.makeText(mContext,
                        "Error: " + statusCode + " " + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        Toast.makeText(mContext, "輸入的是：" + s, Toast.LENGTH_SHORT).show();
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_boardsearch,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_clear: //點了清除瀏覽過的看板
                BoardHistoryDB boardHistoryDB = new BoardHistoryDB(mContext);
                boardHistoryDB.clear();
                boardHistoryDB.close();
                mHistoryAdapter.clear();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
