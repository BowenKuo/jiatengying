package org.onlineservice.rand.login;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Lillian Wu on 2016/8/16.
 */
public class GarageListAdapter extends BaseAdapter {
    private final static String LOG_TAG = "GarageListAdapter";
    private Context context;
    private LayoutInflater mLayInf;
    List<HashMap<String, String>> mList;

    public GarageListAdapter(Context context, List<HashMap<String, String>> list_map) {
        this.context = context;
        this.mLayInf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mList = list_map;
    }

    @Override
    public int getCount() {
        return this.mList.size();
    }

    @Override
    public HashMap<String, String> getItem(int position) {
        return this.mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayInf.inflate(R.layout.garage_list_view, parent, false);
            holder = new ViewHolder();

            holder.tv1 = (TextView) convertView.findViewById(R.id.textView1);
            holder.tv2 = (TextView) convertView.findViewById(R.id.textView2);
            holder.tv3 = (TextView) convertView.findViewById(R.id.textView3);
            holder.button = (Button) convertView.findViewById(R.id.button);

            convertView.setTag(holder);
        }
        else holder = (ViewHolder) convertView.getTag();

        holder.tv1.setText(getItem(position).get("NAME"));
        holder.tv2.setText(getItem(position).get("ADDRESS"));
        holder.tv3.setText(new DecimalFormat("##.#").format(Double.parseDouble(getItem(position).get("DIST"))) + " 公里");

        holder.button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String phone = getItem(position).get("PHONE");
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                Log.d(LOG_TAG, "Dial: Phone number: " + phone);

                if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Log.e(LOG_TAG, "Permisson Denied");
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.CALL_PHONE)) { }
                    else {
                        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, 11);
                        Log.d(LOG_TAG, "Request Phone-Call Permission");
                    }
                    return;
                }
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        TextView tv1;
        TextView tv2;
        TextView tv3;
        Button button;
    }

}
