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
 * Created by davidkuo on 25/09/2016.
 */

public class MaintenanceListAdapter extends RecyclerView.Adapter<MaintenanceListAdapter.ViewHolder> {

    private ArrayList<HashMap<String, String>> dataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView maintain_text, maintain_time, maintain_place;
        public ViewHolder(View v) {
            super(v);
            maintain_text  = (TextView) v.findViewById(R.id.maintain_history   );
            maintain_time  = (TextView) v.findViewById(R.id.maintain_time_label);
            maintain_place = (TextView) v.findViewById(R.id.maintain_place     );
        }
    }

    public MaintenanceListAdapter(ArrayList<HashMap<String, String>> dataset) {
        this.dataset = dataset;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.maintain_history, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HashMap<String, String> node = this.dataset.get(position);
        holder.maintain_text.setText(node.get("CONTENT"));
        holder.maintain_place.setText(node.get("PLACE"));

        try {
            holder.maintain_time.setText( "於 " +
                    new SimpleDateFormat("yyyy 年 M 月 d 日").format(
                            new SimpleDateFormat("yyyy-MM-dd").parse(node.get("TIME"))
                    ) + "保養"
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
