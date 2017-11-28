package yuni.dispbbs;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;


public class TextActivity extends AppCompatActivity {
//    TextView urlTextView;
    WebView mWebView;
    Menu mOptionsMenu;
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 設定這個頁面XML的layout名稱
        //setContentView(R.layout.activity_text);
        //加入右滑動回上一頁
        SwipeBack.attach(this, Position.LEFT)
                .setContentView(R.layout.activity_text)
                .setSwipeBackView(R.layout.swipeback_default);


        // 設定要顯示回上一頁的按鈕
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // 取得 Intent 附帶的資料，改成文章網址存為 url
        Bundle args = this.getIntent().getExtras();
        String url = "";
        if(args.getString("name") == null) {
            url = "http://disp.cc/b/" + args.getString("bi") + "-" + args.getString("ti");
        }else{
            url = "http://disp.cc/b/" + args.getString("name");
        }

        // 取得XML中的TextView，設定文字為 url
//        urlTextView = (TextView) findViewById(R.id.url_textview);
//        urlTextView.setText(url);
        // 取得XML中的WebView
        mWebView = (WebView) findViewById(R.id.webview);
        //hardware();
        // WebView的設定選項
        WebSettings webSettings = mWebView.getSettings();
        // Enable Javascript
        webSettings.setJavaScriptEnabled(true);
        // Enable LocalStorage
        webSettings.setDomStorageEnabled(true);
        // 加這行以避免跳出APP用瀏覽器開啟
        //mWebView.setWebViewClient(new WebViewClient());
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            //等 shouldOverrideUrlLoading(WebView view, String url) 不能用時再改下面這個
            //public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //Log.d("text","url loading:"+url);
                //顯示回上頁按鈕
                mOptionsMenu.getItem(0).setVisible(true);
                return false;
            }
        });
        // 設定背景為黑色，避免載入網頁前顯示白色
        mWebView.setBackgroundColor(Color.BLACK);
        // 載入網址
        mWebView.loadUrl(url);
    }

    private void hardware(){
        if (Build.VERSION.SDK_INT >= 19) {
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    @Override
    public void onBackPressed() {
        if(mWebView.canGoBack()){
            mWebView.goBack();
        }else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mOptionsMenu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "要分享的標題");
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "要分享的內文");
        mShareActionProvider.setShareIntent(shareIntent);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_back: //點擊回上頁
                webViewGoBack(); //自訂的函式
                return true;
            case R.id.action_refresh: //點擊重整
                mWebView.reload();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void webViewGoBack(){
        mWebView.goBack();
        if(!mWebView.canGoBack()){
            mOptionsMenu.getItem(0).setVisible(false);
        }
    }
}
