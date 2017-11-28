package yuni.dispbbs;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements /*AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener,*/ View.OnClickListener, ViewPager.OnPageChangeListener {

//    private ProgressDialog mDlg;
//    ListView mListView;
//    MainAdapter mAdapter;
//    SwipeRefreshLayout mSwipeLayout;
    Button mHotTextBtn, mBoardListBtn, mBoardSearchBtn;
    //刪除改用 ViewPager
    //Fragment mHotTextFragment,mBoardListFragment,mBoardSearchFragment;
    //加上 ViewPager 與 FragmentPagerAdapter
    ViewPager mViewPager;
    FragmentPagerAdapter mAdapterViewPager;

    final int TAB_HOTTEXT=0, TAB_BOARDLIST=1, TAB_BOARDSEARCH=2;
    int mTabSelect; //記錄目前選取的是哪個TAB

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);


        //綁定 Button 的點擊事件
        mHotTextBtn = (Button) findViewById(R.id.button_HotText);
        mHotTextBtn.setOnClickListener(this);
        mBoardListBtn = (Button) findViewById(R.id.button_BoardList);
        mBoardListBtn.setOnClickListener(this);
        mBoardSearchBtn = (Button) findViewById(R.id.button_SearchBoard);
        mBoardSearchBtn.setOnClickListener(this);

//        mHotTextFragment = new HotTextFragment();
//        mBoardListFragment = new BoardListFragment();
//        mBoardSearchFragment = new BoardSearchFragment();
//        if (savedInstanceState == null) { //避免轉方向時重覆載入
//            getFragmentManager().beginTransaction()
//                    .add(R.id.frameLayout, mHotTextFragment)
//                    .commit();
//            //將新選取的Tab按鈕改為已選取的顏色
//            mHotTextBtn.setBackgroundColor(Color.LTGRAY);
//            mHotTextBtn.setTextColor(Color.BLACK);
//        }

        mHotTextBtn.setBackgroundColor(Color.LTGRAY);
        mHotTextBtn.setTextColor(Color.BLACK);
        //加上
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mAdapterViewPager = new MainPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapterViewPager);
        mViewPager.addOnPageChangeListener(this);

        mTabSelect = TAB_HOTTEXT; //一開始選取的TAB




//        mListView = (ListView) findViewById(R.id.main_listview);
//        mAdapter = new MainAdapter(this);
//        mListView.setAdapter(mAdapter);
//        mListView.setOnItemClickListener(this);
//
//        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
//        mSwipeLayout.setOnRefreshListener(this);
//        mSwipeLayout.setColorSchemeColors(Color.BLUE);

//        Button btnHotText = (Button) findViewById(R.id.button_HotText);
//        btnHotText.setOnClickListener(this);
//        Button btnBoardList = (Button) findViewById(R.id.button_BoardList);
//        btnBoardList.setOnClickListener(this);
//        Button btnSearchBoard = (Button) findViewById(R.id.button_SearchBoard);
//        btnSearchBoard.setOnClickListener(this);
//        //將目前頁面的按鈕設成已選取的樣式
//        btnHotText.setBackgroundColor(Color.LTGRAY);
//        btnHotText.setTextColor(Color.BLACK);

//        mDlg = new ProgressDialog(this);
//        mDlg.setMessage("Loding Data...");

//        loadData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_refresh: //點了重新整理
//                loadData();
                return true;
            case R.id.action_settings: //點了settings
                Log.d("item","click settings");
                return true;
            case R.id.action_edit:
                startActivity(new Intent(this,EditorActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    private void loadData(){
//        mDlg.show();
//        String urlString = "http://disp.cc/api/hot_text.json";
//
//        AsyncHttpClient client = new AsyncHttpClient();
//        client.get(urlString, new JsonHttpResponseHandler(){
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                Toast.makeText(getApplicationContext(),
//                        "Success!", Toast.LENGTH_LONG).show();
//                Log.d("Hot Text:", response.toString());
//                mDlg.dismiss();
//                mSwipeLayout.setRefreshing(false); //結束更新動畫
//
//                if( response.has("err") && response.optInt("err")!=0 ){
//                    Toast.makeText(getApplicationContext(),"Data error", Toast.LENGTH_LONG).show();
//                }
//                JSONArray list = response.optJSONArray("list");
//                if(list==null){
//                    Toast.makeText(getApplicationContext(),"Data error", Toast.LENGTH_LONG).show();
//                }
//                mAdapter.updateData(list);
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject error) {
//                Toast.makeText(getApplicationContext(),
//                        "Error: " + statusCode + " " + e.getMessage(),
//                        Toast.LENGTH_LONG).show();
//                // Log error message
//                Log.e("Hot Text:", statusCode + " " + e.getMessage());
//                mDlg.dismiss();
//                mSwipeLayout.setRefreshing(false); //結束更新動畫
//            }
//        });
//    }

//    @Override
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        //從成員mainAdapter中用getItem取出第position項的資料，存成jsonObject
//        JSONObject jsonObject = (JSONObject) parent.getAdapter().getItem(position);
//        //取出我們要的兩個資料 bi 和 ti
//        String bi = jsonObject.optString("bi","");
//        String ti = jsonObject.optString("ti","");
//
//        // 建立一個 Intent 用來表示要從現在這頁跳到文章閱讀頁 TextActivity
//        Intent textIntent = new Intent(this, TextActivity.class);
//        // 將要傳遞的資料放進 Intent
//        textIntent.putExtra("bi",bi);
//        textIntent.putExtra("ti",ti);
//
//        // 使用準備好的 Intent 來開啟新的頁面
//        startActivity(textIntent);
//    }

//    @Override
//    public void onRefresh() {
//        loadData(); //下載並更新列表的資料
//    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.button_HotText:
//                loadData(); //重整熱門文章列表
                //changeTab(TAB_HOTTEXT);
                mViewPager.setCurrentItem(TAB_HOTTEXT);
                break;
            case R.id.button_BoardList:
                //連到熱門看板頁的程式
//                Intent boardListIntent = new Intent(this, BoardListActivity.class);
//                boardListIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(boardListIntent);
//                overridePendingTransition(0, 0);
                //changeTab(TAB_BOARDLIST);
                mViewPager.setCurrentItem(TAB_BOARDLIST);
                break;
            case R.id.button_SearchBoard:
//                Intent searchIntent = new Intent(this, SearchActivity.class);
//                searchIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                startActivity(searchIntent);
//                overridePendingTransition(0,0);//關閉換頁動畫
                //changeTab(TAB_BOARDSEARCH);
                mViewPager.setCurrentItem(TAB_BOARDSEARCH);
                break;
        }
    }

    private void changeTab(int tabNew){
        Button btnSelect = mHotTextBtn;
        switch(mTabSelect){
            case TAB_BOARDLIST:
                btnSelect = mBoardListBtn;
                break;
            case TAB_BOARDSEARCH:
                btnSelect = mBoardSearchBtn;
                break;
        }
        //將原本選取的Tab按鈕改為未選取的顏色
        btnSelect.setBackgroundColor(Color.BLACK);
        btnSelect.setTextColor(Color.parseColor("#99CCFF"));

        Button btnNew = mHotTextBtn;
        //Fragment fragmentNew = mHotTextFragment;
        switch(tabNew){
            case TAB_BOARDLIST:
                btnNew = mBoardListBtn;
                //fragmentNew = mBoardListFragment;
                break;
            case TAB_BOARDSEARCH:
                btnNew = mBoardSearchBtn;
                //fragmentNew = mBoardSearchFragment;
        }
        //將新選取的Tab按鈕改為已選取的顏色
        btnNew.setBackgroundColor(Color.LTGRAY);
        btnNew.setTextColor(Color.BLACK);

//        if(mTabSelect==tabNew){ //點擊的相同的Tab按鈕時，重整 Fragment
//            getFragmentManager().beginTransaction()
//                    .detach(fragmentNew).attach(fragmentNew).commit();
//        }else { //切換<FrameLayout>的 Fragment
//            getFragmentManager().beginTransaction()
//                    .replace(R.id.frameLayout, fragmentNew).commit();
//        }
        mTabSelect = tabNew;

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        changeTab(position);
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    public static class MainPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 3;

        MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch(position){
                case 0:
                    return new HotTextFragment();
                case 1:
                    return new BoardListFragment();
                case 2:
                    return new BoardSearchFragment();
                default:
                    return null;

            }
        }

    }

}
