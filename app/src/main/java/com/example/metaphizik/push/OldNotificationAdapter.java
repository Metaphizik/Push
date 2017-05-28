package com.example.metaphizik.push;


import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class OldNotificationAdapter extends RecyclerView.Adapter<OldNotificationAdapter.NotificationViewHolder> {

    private ArrayList<NotificationSample> data;

    public OldNotificationAdapter(ArrayList<NotificationSample> data) {
        this.data = data;
    }

    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false);

        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotificationViewHolder holder, int position) {
        NotificationSample item = data.get(position);
        holder.author.setText(item.getAuthor());
        holder.date.setText(item.getDate());
        holder.text.setText(item.getText());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView author, date, text;

        public NotificationViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            author = (TextView) itemView.findViewById(R.id.author);
            date = (TextView) itemView.findViewById(R.id.date);
            text = (TextView) itemView.findViewById(R.id.text);

        }
    }

    public void setData(ArrayList<NotificationSample> data) {
        this.data = data;
    }

}
