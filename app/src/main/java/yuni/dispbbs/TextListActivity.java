package yuni.dispbbs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;


public class TextListActivity extends AppCompatActivity {
    private int mBoardId; //看板ID，要從發文的看板傳過來
    //requestCode 用來分辨是從哪個 Activity 回來
    final int EDITOR_ACTIVITY = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_textlist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_post:
                textPost();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //發表文章
    private void textPost() {
        Intent editorIntent = new Intent(this, EditorActivity.class);
        editorIntent.putExtra("bi", mBoardId);
        //startActivity(editorIntent);
        startActivityForResult(editorIntent, EDITOR_ACTIVITY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) { return; }
        switch(requestCode) {
            case EDITOR_ACTIVITY:
                //onRefresh(); //從 EditorActivity 回來時重新整理
                break;
        }
    }
}
