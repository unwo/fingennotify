package com.unwo.FingenNotify;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

public class Main extends Activity
{
    Button button;
    private RecyclerView mRecyclerViewApplications;
    private RecyclerView mRecyclerViewNotify;

    public RecyclerView.Adapter mAdapter;
    public RecyclerView.Adapter mAdapterNotify;
    public List<Notify> notify;
    public List<Package> applications;
    Button addApp;

    Db db;
    BroadcastReceiver br;
    String sPackageName;
    String sSenderName;

    public void onCreate(Bundle savedInstanceState)
    {
        //https://www.youtube.com/watch?v=y7gNVZ0JGOg
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mRecyclerViewApplications = findViewById(R.id.recyclerView);
        mRecyclerViewApplications.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewApplications.setHasFixedSize(true);

        mRecyclerViewNotify = findViewById(R.id.recyclerViewNotify);
        mRecyclerViewNotify.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewNotify.setHasFixedSize(true);

        db=new Db(this);
        applications=db.getApplications();
        mAdapter = new AdapterPackage(this,applications);
        mRecyclerViewApplications.setAdapter(mAdapter);

        br = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                notify=db.getNotify();
                mAdapterNotify = new AdapterNotify(context,notify);
                mRecyclerViewNotify.setAdapter(mAdapterNotify);
            }
        };
        IntentFilter intentFilter = new IntentFilter(Constants.BROADCAST_ACTION);
        registerReceiver(br, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(br);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.more_tab_menu, menu);

        menu.findItem(R.id.save_notify).setChecked(db.getPreferenceValueBool(Constants.PREFERENCE_SAVE_NOTIFY,true));
        menu.findItem(R.id.send_fingen).setChecked(db.getPreferenceValueBool(Constants.PREFERENCE_SEND_FINGEN,true));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Boolean value;
        switch(item.getItemId())
        {
            case R.id.add_package:
                showDialog(false,-1);
                break;
            case R.id.save_notify:
                value=!item.isChecked();
                db.setPreferenceValueBool(Constants.PREFERENCE_SAVE_NOTIFY,value);
                item.setChecked(value);
                break;
            case R.id.send_fingen:
                value=!item.isChecked();
                db.setPreferenceValueBool(Constants.PREFERENCE_SEND_FINGEN,value);
                item.setChecked(value);
                break;
        }
        return true;
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        super.onContextItemSelected(item);
        int id = item.getItemId();

        switch (id)
        {
            case Constants.EDIT_PACKAGE:
                showDialog(true,item.getGroupId());
                break;
            case Constants.DELETE_PACKAGE:
                db.deleteApplication(applications.get(item.getGroupId()).getId());
                applications.remove(item.getGroupId());
                mAdapter.notifyDataSetChanged();
                break;
            case Constants.SEND_NOTIFY_FINGEN:
                SendFingen sf=new SendFingen(this,notify.get(item.getGroupId()).getSender(),notify.get(item.getGroupId()).getMessage());
                break;
            case Constants.DELETE_NOTIFY:
                db.deleteMessage(notify.get(item.getGroupId()).getId());
                notify.remove(item.getGroupId());
                mAdapterNotify.notifyDataSetChanged();
                break;
            case Constants.DELETE_ALL_NOTIFY:
                db.deleteAllMessage();
                notify.clear();
                mAdapterNotify.notifyDataSetChanged();
                break;
        }

        return true;
    }

    private void showDialog(boolean edit,int id)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.title_dialog_package_name));

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog, null);
        builder.setView(dialogView);
        EditText packageName = dialogView.findViewById(R.id.packageName);
        EditText senderName = dialogView.findViewById(R.id.senderName);
        if (edit) {
            packageName.setText(applications.get(id).getName());
            senderName.setText(applications.get(id).getSender());
        }

        builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sPackageName = packageName.getText().toString();
                sSenderName = senderName.getText().toString();
                if (!edit) {
                    long id = db.addApplication(sPackageName,sSenderName);
                    Package a = new Package();
                    a.setName(sPackageName);
                    a.setSender(sSenderName);
                    a.setId(id);
                    applications.add(a);
                }
                else
                {
                    db.editApplication(applications.get(id).getId(), sPackageName,sSenderName);
                    applications.get(id).setName(sPackageName);
                    applications.get(id).setSender(sSenderName);

                }
                mAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        notify=db.getNotify();
        mAdapterNotify = new AdapterNotify(this,notify);
        mRecyclerViewNotify.setAdapter(mAdapterNotify);
    }

}