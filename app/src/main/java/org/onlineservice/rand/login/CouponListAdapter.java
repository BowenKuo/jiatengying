package org.onlineservice.rand.login;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by davidkuo on 8/27/16.
 */
public class CouponListAdapter extends RecyclerView.Adapter<CouponListAdapter.ViewHolder> {

    private ArrayList<HashMap<String, String>> dataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView coupon_content, coupon_time;
        public ViewHolder(View v) {
            super(v);
            coupon_content = (TextView) v.findViewById(R.id.coupon_content);
            coupon_time    = (TextView) v.findViewById(R.id.coupon_time  );
        }
    }

    public CouponListAdapter(ArrayList<HashMap<String, String>> dataset) {
        this.dataset = dataset;
    }

    @Override
    public CouponListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.coupon_listview, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CouponListAdapter.ViewHolder holder, int position) {
        HashMap<String, String> node = this.dataset.get(position);
        holder.coupon_content.setText(node.get("COUPON"));

        try {
            holder.coupon_time.setText(
                    new SimpleDateFormat("M月d日").format(
                            new SimpleDateFormat("yyyy-MM-dd").parse(node.get("START"))
                    ) + " 到 " +
                            new SimpleDateFormat("M月d日").format(
                                    new SimpleDateFormat("yyyy-MM-dd").parse(node.get("END"))
                            )
            );
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return this.dataset.size();
    }
}
