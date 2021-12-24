package com.unwo.FingenNotify;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AdapterNotify extends RecyclerView.Adapter<AdapterNotify.ViewHolder> {
    static Context context;

    private List<Notify> Data=new ArrayList<>();


    public AdapterNotify(Context context, List<Notify> list) {
        this.context=context;
        Data = list;
    }

    AdapterNotify() {
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        public TextView name;
        public TextView message;
        public TextView datetime;
        public TextView sender;

        public ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            message = v.findViewById(R.id.message);
            datetime = v.findViewById(R.id.datetime);
            sender = v.findViewById(R.id.sender);
            v.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(getAdapterPosition(),Constants.SEND_NOTIFY_FINGEN,0,context.getResources().getString(R.string.send_notify));
            menu.add(getAdapterPosition(),Constants.DELETE_NOTIFY,1,context.getResources().getString(R.string.delete_notify));
            menu.add(getAdapterPosition(),Constants.DELETE_ALL_NOTIFY,2,context.getResources().getString(R.string.delete_all_notify));
        }

    }

    public List<Notify> getData() {
        return Data;
    }


    @Override
    public long getItemId(int position) {

        return super.getItemId(position);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_notify, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.name.setText(Data.get(position).getName());
        holder.message.setText(Data.get(position).getMessage());
        holder.datetime.setText(Data.get(position).getDateTime());
        holder.sender.setText(Data.get(position).getSender());
    }


    @Override
    public int getItemCount() {
        return Data.size();
    }

    private int position;

    public int getPosition() {

        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

}