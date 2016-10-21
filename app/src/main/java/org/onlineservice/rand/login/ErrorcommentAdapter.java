package org.onlineservice.rand.login;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.Response;

import java.util.List;

/**
 * Created by leoGod on 2016/10/21.
 */

public class ErrorcommentAdapter extends BaseAdapter {
    private LayoutInflater myInflater;
    private List<Errorcomment> errorcomment;
    public ErrorcommentAdapter(Context context, List<Errorcomment> errorcomment){
        myInflater = LayoutInflater.from(context);
        this.errorcomment = errorcomment;
    }
    @Override
    public int getCount() {
        return errorcomment.size();
    }

    @Override
    public Object getItem(int arg0) {
        return errorcomment.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return errorcomment.indexOf(getItem(position));
    }
    private class ViewHolder {
        TextView txtname;
        TextView txtinfo;
        TextView txtTime;
        public ViewHolder(TextView txtname, TextView txtinfo, TextView txtTime){
            this.txtname = txtname;
            this.txtinfo = txtinfo;
            this.txtTime = txtTime;
        }
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ErrorcommentAdapter.ViewHolder holder = null;
        if(convertView==null){
            convertView = myInflater.inflate(R.layout.error_comment_list, null);
            holder = new ErrorcommentAdapter.ViewHolder(
                    (TextView) convertView.findViewById(R.id.comment_name),
                    (TextView) convertView.findViewById(R.id.comment_content),
                    (TextView) convertView.findViewById(R.id.comment_time)
            );
            convertView.setTag(holder);
        }else{
            holder = (ErrorcommentAdapter.ViewHolder) convertView.getTag();
        }
        Errorcomment errorcomment = (Errorcomment) getItem(position);

        holder.txtname.setText(errorcomment.getmid());
        holder.txtinfo.setText(errorcomment.getinfo());
        holder.txtTime.setText(errorcomment.getTime());


        return convertView;
    }
}
