package com.unwo.FingenNotify;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AdapterPackage extends RecyclerView.Adapter<AdapterPackage.ViewHolder> {
    static Context context;

    private List<Package> Data=new ArrayList<>();


    public AdapterPackage(Context context, List<Package> list) {
        Data = list;
        this.context = context;

    }

    AdapterPackage() {
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        public TextView name;
        public TextView sender;

        public ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            sender = v.findViewById(R.id.sender);
            v.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(getAdapterPosition(),Constants.EDIT_PACKAGE,0, context.getResources().getString(R.string.edit_package));
            menu.add(getAdapterPosition(),Constants.DELETE_PACKAGE,1,context.getResources().getString(R.string.delete_package));
        }
    }


    public List<Package> getData() {
        return Data;
    }


    @Override
    public long getItemId(int position) {

        return super.getItemId(position);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_package, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.name.setText(Data.get(position).getName());
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