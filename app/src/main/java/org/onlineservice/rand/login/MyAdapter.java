package org.onlineservice.rand.login;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by leoGod on 2016/10/20.
 */

public class MyAdapter extends BaseAdapter {
    private LayoutInflater myInflater;
    private List<errorcodelist> errorcode;
    public MyAdapter(Context context, List<errorcodelist> errorcode){
        myInflater = LayoutInflater.from(context);
        this.errorcode = errorcode;
    }
    @Override
    public int getCount() {
        return errorcode.size();
    }

    @Override
    public Object getItem(int arg0) {
        return errorcode.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return errorcode.indexOf(getItem(position));
    }
    private class ViewHolder {
        TextView txtec;
        TextView txtinfo;
        TextView txtTime;
        public ViewHolder(TextView txtec, TextView txtinfo, TextView txtTime){
            this.txtec = txtec;
            this.txtinfo = txtinfo;
            this.txtTime = txtTime;
        }
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView==null){
            convertView = myInflater.inflate(R.layout.errorcode_list_view, null);
            holder = new ViewHolder(
                    (TextView) convertView.findViewById(R.id.ecid),
                    (TextView) convertView.findViewById(R.id.ecinfo),
                    (TextView) convertView.findViewById(R.id.ectime)
            );
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        errorcodelist errorcodelist = (errorcodelist) getItem(position);

        holder.txtec.setText(errorcodelist.getid());
        holder.txtinfo.setText(errorcodelist.getinfo());
        holder.txtTime.setText(errorcodelist.getTime());


        return convertView;
    }
}
