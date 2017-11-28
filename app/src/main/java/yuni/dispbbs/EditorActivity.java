package yuni.dispbbs;


import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import cz.msebera.android.httpclient.Header;

public class EditorActivity extends AppCompatActivity {
    //會用到的成員變數
    private int mBoardId = 0; //看板ID，要從發文的看板傳過來
    private EditText mTitleEditText;
    private EditText mTextEditText;
    private ProgressDialog mLoadingDialog;
    private final int REQUEST_PICK_IMAGE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        // 設定要顯示回上一頁的按鈕
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //接收intent傳來的資料
        Bundle args = this.getIntent().getExtras();
        if(args != null)
            mBoardId = args.getInt("bi",0);

        mTitleEditText = (EditText) findViewById(R.id.titleEditText);
        mTextEditText = (EditText) findViewById(R.id.textEditText);
    }

    private void sendText(){
        //送出文章的程式
        //Log.d("editor","click send button");

        String titleString = mTitleEditText.getText().toString();
        String textString = mTextEditText.getText().toString();
        String urlString = "http://disp.cc/api/editor.php?act=save&type=new&bi="+mBoardId;
        showLoadingDialog();

        AsyncHttpClient client = new AsyncHttpClient();
        PersistentCookieStore cookieStore = new PersistentCookieStore(this);
        client.setCookieStore(cookieStore);
        RequestParams params = new RequestParams();
        params.put("title", titleString);
        params.put("text", textString);
        client.post(urlString, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                dismissLoadingDialog();
                if (!response.optBoolean("isSuccess")) {
                    Toast.makeText(getApplicationContext(), "Error:" + response.optString("errorMessage"), Toast.LENGTH_LONG).show();
                    return;
                }
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                //送出文章成功
                finish();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject error) {
                dismissLoadingDialog();
                Toast.makeText(getApplicationContext(), "Error: " + statusCode + " " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_send:
                Toast.makeText(getApplicationContext(),"送出文章",Toast.LENGTH_SHORT).show();
                //sendText();
                return true;
            case R.id.action_attimg:
                textInsertImage();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        dismissLoadingDialog();
        super.onDestroy();
    }

    private void showLoadingDialog(){
        if(mLoadingDialog==null){
            mLoadingDialog = new ProgressDialog(this);
            mLoadingDialog.setMessage("傳送中...");
        }
        mLoadingDialog.show();
    }

    private void dismissLoadingDialog(){
        if(mLoadingDialog!=null) {
            mLoadingDialog.dismiss();
        }
    }

    private void textInsertImage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) { return; }

        switch(requestCode){
            case REQUEST_PICK_IMAGE:
                getSelectImage(data); //處理選取圖片的程式 寫在後面
                break;
        }
    }

    //選擇了要插入的圖片後，在onActivityResult會執行這個
    private void getSelectImage(Intent data){
        //從 onActivityResult 傳入的data中，取得圖檔路徑
        Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };

        Cursor cursor = getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        if(cursor==null){ return; }
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String imagePath = cursor.getString(columnIndex);
        cursor.close();
        //Log.d("editor","image:"+imagePath);

        //使用圖檔路徑產生調整過大小的Bitmap
        Bitmap bitmap = getResizedBitmap(imagePath); //程式寫在後面

        //將 Bitmap 轉為 base64 字串
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] bitmapData = bos.toByteArray();
        String imageBase64 = Base64.encodeToString(bitmapData, Base64.DEFAULT);
        //Log.d("editor",imageBase64);

        //將圖檔上傳至 Imgur，將取得的圖檔網址插入文字輸入框
        //imgurUpload(imageBase64); //程式寫在後面
        textInsertString("[img]"+imagePath+"[img]");
    }

    private Bitmap getResizedBitmap(String imagePath) {
        final int MAX_WIDTH = 1024; // 新圖的寬要小於等於這個值

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; //只讀取寬度和高度
        BitmapFactory.decodeFile(imagePath, options);
        int width = options.outWidth, height = options.outHeight;

        // 求出要縮小的 scale 值，必需是2的次方，ex: 1,2,4,8,16...
        int scale = 1;
        while(width > MAX_WIDTH*2){
            width /= 2;
            height /= 2;
            scale *= 2;
        }

        // 使用 scale 值產生縮小的圖檔
        BitmapFactory.Options scaledOptions = new BitmapFactory.Options();
        scaledOptions.inSampleSize = scale;
        Bitmap scaledBitmap = BitmapFactory.decodeFile(imagePath, scaledOptions);

        float resize = 1; //縮小值 resize 可為任意小數
        if(width>MAX_WIDTH){
            resize = ((float) MAX_WIDTH) / width;
        }

        Matrix matrix = new Matrix(); // 產生縮圖需要的參數 matrix
        matrix.postScale(resize, resize); // 設定寬與高的縮放比例

        // 產生縮小後的圖
        return Bitmap.createBitmap(scaledBitmap, 0, 0, width, height, matrix, true);
    }

    private void textInsertString(String insertString){
        int start = mTextEditText.getSelectionStart();
        mTextEditText.getText().insert(start, insertString);
    }

}
