package yuni.dispbbs;


import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.DraweeView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BoardListAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private JSONArray mJsonArray;
    private Context mContext;

    private static class ViewHolder{
        //ImageView thumbImageView;
        SimpleDraweeView thumbImageView;
        TextView titleTextView;
        TextView descTextView;
    }

    public BoardListAdapter(Context context){
        mInflater = LayoutInflater.from(context);
        mJsonArray = new JSONArray();
        mContext = context;
    }

    // 輸入JSON資料
    public void updateData(JSONArray jsonArray, int pageNum) throws JSONException {
        if(pageNum == 0) {
            mJsonArray = jsonArray;
        }else{
            for(int i=0; i<jsonArray.length(); i++){
                mJsonArray.put(jsonArray.get(i));
            }
        }
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

        if(convertView == null){
            convertView = mInflater.inflate(R.layout.row_boardlist,parent,false);
            holder = new ViewHolder();
            holder.thumbImageView = (SimpleDraweeView) convertView.findViewById(R.id.img_thumb);//(ImageView) convertView.findViewById(R.id.img_thumb);
            holder.titleTextView = (TextView) convertView.findViewById(R.id.text_title);
            holder.descTextView = (TextView) convertView.findViewById(R.id.text_desc);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        JSONObject jsonObject = (JSONObject) getItem(position);
        String imageUrl = "";
        if(jsonObject.has("icon")){
            imageUrl = jsonObject.optString("icon");
        }
        if (!imageUrl.equals("")) {
            // 使用 Picasso 來載入網路上的圖片
            // 圖片載入前先用placeholder顯示預設圖片
            //Picasso.with(mContext).load(imageUrl).placeholder(R.drawable.displogo300).into(holder.thumbImageView);

            Uri uri = Uri.parse(imageUrl);
            holder.thumbImageView.setImageURI(uri);

        } else { // 沒有縮圖的話放 disp logo
            //holder.thumbImageView.setImageResource(R.drawable.displogo300);

            Uri uri = Uri.parse("res:///" + R.drawable.displogo300);
            holder.thumbImageView.setImageURI(uri);
        }
        String title = "";
        String desc = "";
        if (jsonObject.has("name")) {
            title = jsonObject.optString("name");
        }
        if (jsonObject.has("title")) {
            desc = jsonObject.optString("title");
        }
        holder.titleTextView.setText(title);
        holder.descTextView.setText(desc);

        return convertView;
    }
}
