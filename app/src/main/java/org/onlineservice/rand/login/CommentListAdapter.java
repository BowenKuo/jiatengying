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
public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.ViewHolder> {

    private ArrayList<HashMap<String, String>> dataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView comment_user, comment_content, comment_time;
        public ViewHolder(View v) {
            super(v);
            comment_user    = (TextView) v.findViewById(R.id.comment_name   );
            comment_content = (TextView) v.findViewById(R.id.comment_content);
            comment_time    = (TextView) v.findViewById(R.id.comment_time   );
        }
    }

    public CommentListAdapter(ArrayList<HashMap<String, String>> dataset) {
        this.dataset = dataset;
    }

    @Override
    public CommentListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_listview, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CommentListAdapter.ViewHolder holder, int position) {
        HashMap<String, String> node = this.dataset.get(position);

        holder.comment_user.setText(node.get("USER"));
        holder.comment_content.setText(node.get("COMMENT"));

        try {
            holder.comment_time.setText(
                    new SimpleDateFormat("yyyy年M月d日 h:mm a").format(
                            new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(node.get("TIME"))) +
                            "  ·  " + node.get("STAR") + " ★"
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
