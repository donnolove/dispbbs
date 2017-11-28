package yuni.dispbbs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;


public class MainAdapter extends BaseAdapter{

    private Context mContext;
    private LayoutInflater mInflater;
    private JSONArray mJsonArray;

    // 用來儲存row裡每個view的id，以免每次都要取一次
    private static class ViewHolder {
        ImageView thumbImageView;
        TextView titleTextView;
        TextView descTextView;
    }

    // 類別的建構子
    MainAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mJsonArray = new JSONArray();
    }

    // 輸入JSON資料
    void updateData(JSONArray jsonArray) {
        mJsonArray = jsonArray;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mJsonArray.length();
    }

    @Override
    public Object getItem(int i) {
        return mJsonArray.optJSONObject(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        // 檢查view是否已存在，如果已存在就不用再取一次id
        if(convertView == null){
            // Inflate the custom row layout from your XML.
            convertView = mInflater.inflate(R.layout.row_main, parent, false);
            // create a new "Holder" with subviews
            holder = new ViewHolder();
            holder.thumbImageView = (ImageView) convertView.findViewById(R.id.img_thumb);
            holder.titleTextView = (TextView) convertView.findViewById(R.id.text_title);
            holder.descTextView = (TextView) convertView.findViewById(R.id.text_desc);
            // hang onto this holder for future recyclage
            convertView.setTag(holder);
        }else{
            // skip all the expensive inflation/findViewById
            // and just get the holder you already made
            holder = (ViewHolder) convertView.getTag();
        }

        // 取得目前這個Row的JSON資料
        JSONObject jsonObject = (JSONObject) getItem(position);

        String imageUrl = "";
        if (jsonObject.has("img_list")) {
            JSONArray img_list = jsonObject.optJSONArray("img_list");
            if(img_list.length()!=0) {
                imageUrl = img_list.optString(0);
            }
        }

        if (!imageUrl.equals("")) {
            // 使用 Picasso 來載入網路上的圖片
            // 圖片載入前先用placeholder顯示預設圖片
            Picasso.with(mContext).load(imageUrl).placeholder(R.drawable.displogo300).into(holder.thumbImageView);
        } else { // 沒有縮圖的話放 disp logo
            holder.thumbImageView.setImageResource(R.drawable.displogo300);
        }

        // 從JSON資料取得標題和摘要
        String title = "";
        String desc = "";
        if (jsonObject.has("title")) {
            title = jsonObject.optString("title");
        }
        if (jsonObject.has("desc")) {
            desc = jsonObject.optString("desc");
        }

        // 將標題和摘要顯示在TextView上
        holder.titleTextView.setText(title);
        holder.descTextView.setText(desc);

        return convertView;
    }
}
