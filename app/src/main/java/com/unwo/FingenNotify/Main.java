package com.unwo.FingenNotify;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Main extends Activity
{
    private static final int REQUEST_CODE_EXPORT = 1001;
    private static final int REQUEST_CODE_IMPORT = 1002;
    private static final int REQUEST_CODE_EXPORT_PACKAGE = 1003;
    private static final int REQUEST_CODE_IMPORT_PACKAGE = 1004;

    Button button;
    private RecyclerView mRecyclerViewApplications;
    private RecyclerView mRecyclerViewNotify;

    public RecyclerView.Adapter mAdapter;
    public RecyclerView.Adapter mAdapterNotify;
    public List<Notify> notify;
    public List<Package> applications;
    Button addApp;

    Button btnDateFilter;
    Button btnDateReset;
    long filterDateMs;

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

        notify=db.getNotify();
        mAdapterNotify = new AdapterNotify(this,notify);
        mRecyclerViewNotify.setAdapter(mAdapterNotify);

        btnDateFilter = findViewById(R.id.btnDateFilter);
        btnDateReset = findViewById(R.id.btnDateReset);

        btnDateFilter.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            if (filterDateMs != 0) cal.setTimeInMillis(filterDateMs);
            new DatePickerDialog(Main.this, (view, year, month, dayOfMonth) -> {
                Calendar selected = Calendar.getInstance();
                selected.set(year, month, dayOfMonth, 0, 0, 0);
                selected.set(Calendar.MILLISECOND, 0);
                filterDateMs = selected.getTimeInMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                btnDateFilter.setText(sdf.format(selected.getTime()));
                refreshNotify();
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnDateReset.setOnClickListener(v -> {
            filterDateMs = 0;
            btnDateFilter.setText(R.string.filter_all);
            refreshNotify();
        });

        br = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                refreshNotify();
            }
        };
        IntentFilter intentFilter = new IntentFilter(Constants.BROADCAST_ACTION);
        registerReceiver(br, intentFilter, Context.RECEIVER_NOT_EXPORTED);
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
        int id = item.getItemId();
        if (id == R.id.add_package) {
            showDialog(false, -1);
        } else if (id == R.id.save_notify) {
            value = !item.isChecked();
            db.setPreferenceValueBool(Constants.PREFERENCE_SAVE_NOTIFY, value);
            item.setChecked(value);
        } else if (id == R.id.send_fingen) {
            value = !item.isChecked();
            db.setPreferenceValueBool(Constants.PREFERENCE_SEND_FINGEN, value);
            item.setChecked(value);
        } else if (id == R.id.export_db) {
            Intent exportIntent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            exportIntent.addCategory(Intent.CATEGORY_OPENABLE);
            exportIntent.setType("application/octet-stream");
            exportIntent.putExtra(Intent.EXTRA_TITLE, "FingenNotify.db");
            startActivityForResult(exportIntent, REQUEST_CODE_EXPORT);
        } else if (id == R.id.import_db) {
            Intent importIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            importIntent.addCategory(Intent.CATEGORY_OPENABLE);
            importIntent.setType("*/*");
            startActivityForResult(importIntent, REQUEST_CODE_IMPORT);
        } else if (id == R.id.export_package) {
            Intent exportIntent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            exportIntent.addCategory(Intent.CATEGORY_OPENABLE);
            exportIntent.setType("application/json");
            exportIntent.putExtra(Intent.EXTRA_TITLE, "FingenNotify_packages.json");
            startActivityForResult(exportIntent, REQUEST_CODE_EXPORT_PACKAGE);
        } else if (id == R.id.import_package) {
            Intent importIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            importIntent.addCategory(Intent.CATEGORY_OPENABLE);
            importIntent.setType("*/*");
            startActivityForResult(importIntent, REQUEST_CODE_IMPORT_PACKAGE);
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) return;
        Uri uri = data.getData();
        if (uri == null) return;

        File dbFile = getDatabasePath(Constants.DB_NAME);

        if (requestCode == REQUEST_CODE_EXPORT) {
            try (InputStream in = new FileInputStream(dbFile);
                 OutputStream out = getContentResolver().openOutputStream(uri)) {
                byte[] buf = new byte[4096];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                Toast.makeText(this, R.string.export_success, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, R.string.export_error, Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_IMPORT) {
            try (InputStream in = getContentResolver().openInputStream(uri);
                 OutputStream out = new FileOutputStream(dbFile)) {
                byte[] buf = new byte[4096];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                db = new Db(this);
                applications = db.getApplications();
                mAdapter = new AdapterPackage(this, applications);
                mRecyclerViewApplications.setAdapter(mAdapter);
                refreshNotify();
                Toast.makeText(this, R.string.import_success, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, R.string.import_error, Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_EXPORT_PACKAGE) {
            try {
                JSONArray jsonArray = new JSONArray();
                for (Package p : applications) {
                    JSONObject obj = new JSONObject();
                    obj.put(Constants.TABLE_PACKAGES_COLUMN_PACKAGE, p.getName());
                    obj.put(Constants.TABLE_PACKAGES_COLUMN_SENDER, p.getSender());
                    jsonArray.put(obj);
                }
                try (OutputStream out = getContentResolver().openOutputStream(uri)) {
                    out.write(jsonArray.toString().getBytes("UTF-8"));
                }
                Toast.makeText(this, R.string.export_package_success, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, R.string.export_package_error, Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_CODE_IMPORT_PACKAGE) {
            try {
                StringBuilder sb = new StringBuilder();
                try (InputStream in = getContentResolver().openInputStream(uri);
                     BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                }
                JSONArray jsonArray = new JSONArray(sb.toString());
                showImportPackageDialog(jsonArray);
            } catch (Exception e) {
                Toast.makeText(this, R.string.import_package_error, Toast.LENGTH_SHORT).show();
            }
        }
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

    @Override
    public void onContextMenuClosed(Menu menu) {
        super.onContextMenuClosed(menu);
        ((AdapterNotify) mAdapterNotify).clearSelection();
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

    private void showImportPackageDialog(JSONArray jsonArray) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.import_package_mode_title);
        builder.setPositiveButton(R.string.import_package_mode_add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                importPackages(jsonArray, false);
            }
        });
        builder.setNegativeButton(R.string.import_package_mode_replace, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                importPackages(jsonArray, true);
            }
        });
        builder.show();
    }

    private void importPackages(JSONArray jsonArray, boolean replace) {
        try {
            if (replace) {
                db.deleteAllApplications();
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String packageName = obj.getString(Constants.TABLE_PACKAGES_COLUMN_PACKAGE);
                String senderName = obj.optString(Constants.TABLE_PACKAGES_COLUMN_SENDER, "");
                db.addApplication(packageName, senderName);
            }
            applications = db.getApplications();
            mAdapter = new AdapterPackage(this, applications);
            mRecyclerViewApplications.setAdapter(mAdapter);
            Toast.makeText(this, R.string.import_package_success, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, R.string.import_package_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshNotify();
    }

    private void refreshNotify() {
        if (filterDateMs == 0) {
            notify = db.getNotify();
        } else {
            Calendar start = Calendar.getInstance();
            start.setTimeInMillis(filterDateMs);
            Calendar end = (Calendar) start.clone();
            end.add(Calendar.DAY_OF_MONTH, 1);
            notify = db.getNotifyByDate(start.getTimeInMillis(), end.getTimeInMillis());
        }
        ((AdapterNotify) mAdapterNotify).setData(notify);
        mAdapterNotify.notifyDataSetChanged();
    }

}