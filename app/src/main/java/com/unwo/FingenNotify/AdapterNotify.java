package com.unwo.FingenNotify;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
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
    private int selectedPosition = -1;


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
            int pos = getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                AdapterNotify adapter = (AdapterNotify) ((RecyclerView) v.getParent()).getAdapter();
                if (adapter != null) {
                    adapter.setSelectedPosition(pos);
                }
            }
            menu.add(pos,Constants.SEND_NOTIFY_FINGEN,0,context.getResources().getString(R.string.send_notify));
            menu.add(pos,Constants.DELETE_NOTIFY,1,context.getResources().getString(R.string.delete_notify));
            menu.add(pos,Constants.DELETE_ALL_NOTIFY,2,context.getResources().getString(R.string.delete_all_notify));
        }

    }

    public List<Notify> getData() {
        return Data;
    }

    public void setData(List<Notify> list) {
        Data = list;
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

        if (position == selectedPosition) {
            holder.itemView.setBackgroundColor(Color.parseColor("#1A000000"));
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }
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

    public void setSelectedPosition(int pos) {
        int prev = selectedPosition;
        selectedPosition = pos;
        if (prev != -1) notifyItemChanged(prev);
        if (pos != -1) notifyItemChanged(pos);
    }

    public void clearSelection() {
        setSelectedPosition(-1);
    }

}